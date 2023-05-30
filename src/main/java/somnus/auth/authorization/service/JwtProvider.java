package somnus.auth.authorization.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret, //уберу в секреты
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret //уберу в секреты (переменные окружения)
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){

        final LocalDateTime now = LocalDateTime.now();
        final Instant tokenInstant = now.plusMinutes(60).atZone(ZoneId.systemDefault()).toInstant();
        final Date tokenExpiration = Date.from(tokenInstant);
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(tokenExpiration).signWith(jwtAccessSecret, SignatureAlgorithm.HS256)
                .compact();
    }
//
//    public String generateAccessToken(@NonNull User user, UserDetails userDetails) {
//        final LocalDateTime now = LocalDateTime.now();
//        final Instant accessExpirationInstant = now.plusMinutes(60).atZone(ZoneId.systemDefault()).toInstant();
//        final Date accessExpiration = Date.from(accessExpirationInstant);
//        return Jwts.builder()
//                .setSubject(user.getEmail())
//                .setExpiration(accessExpiration)
//                .signWith(jwtAccessSecret)
//                .claim("id", user.getId())
//                .claim("roles", user.getRoles())
//                .claim("firstName", user.getFirstName())
//                .compact();
//    }
//
//    public String generateRefreshToken(@NonNull User user, UserDetails userDetails) {
//        final LocalDateTime now = LocalDateTime.now();
//        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
//        final Date refreshExpiration = Date.from(refreshExpirationInstant);
//        return Jwts.builder()
//                .setSubject(user.getEmail())
//                .setExpiration(refreshExpiration)
//                .signWith(jwtRefreshSecret)
//                .compact();
//    }

//    public boolean validateAccessToken(@NonNull String accessToken) {
//        return validateToken(accessToken, jwtAccessSecret);
//    }
//
//    public boolean validateRefreshToken(@NonNull String refreshToken) {
//        return validateToken(refreshToken, jwtRefreshSecret, );
//    }

    private boolean validateToken(@NonNull String token, @NonNull Key secret, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(secret)
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (ExpiredJwtException expEx) {
//            log.error("Token expired", expEx);
//        } catch (UnsupportedJwtException unsEx) {
//            log.error("Unsupported jwt", unsEx);
//        } catch (MalformedJwtException mjEx) {
//            log.error("Malformed jwt", mjEx);
//        } catch (SignatureException sEx) {
//            log.error("Invalid signature", sEx);
//        } catch (Exception e) {
//            log.error("invalid token", e);
//        }
//        return false;
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        return validateToken(token, jwtAccessSecret, userDetails);
    }

    private boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = getClaims(token, jwtAccessSecret);
        return claimsResolver.apply(claims);
    }



    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
