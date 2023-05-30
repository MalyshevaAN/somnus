package somnus.auth.authorization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import somnus.auth.User.Exceptions.PasswordsAreDifferent;
import somnus.auth.User.Exceptions.UserAlreadyExists;
import somnus.auth.User.Repository.UserRepository;
import somnus.auth.authorization.domain.*;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

//    public JwtResponse login(@NonNull JwtRequest authRequest) {
//        final User user = userService.getByLogin(authRequest.getEmail())
//                .orElseThrow(() -> new AuthException("Пользователь не найден"));
////        System.out.println(user.getPassword());
////        System.out.println(passwordEncoder.encode(authRequest.getPassword()));
//        if (user.getPassword().equals(authRequest.getPassword())) {
//            final String accessToken = jwtProvider.generateAccessToken(user);
//            final String refreshToken = jwtProvider.generateRefreshToken(user);
//            refreshStorage.put(user.getEmail(), refreshToken);
//            return new JwtResponse(accessToken, refreshToken);
//        } else {
//            throw new AuthException("Неправильный пароль");
//        }
//    }
//
//    public JwtResponse getAccessToken(@NonNull String refreshToken) {
//        if (jwtProvider.validateRefreshToken(refreshToken)) {
//            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
//            final String login = claims.getSubject();
//            final String saveRefreshToken = refreshStorage.get(login);
//            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
//                final User user = userService.getByLogin(login)
//                        .orElseThrow(() -> new AuthException("Пользователь не найден"));
//                final String accessToken = jwtProvider.generateAccessToken(user);
//                return new JwtResponse(accessToken, null);
//            }
//        }
//        return new JwtResponse(null, null);
//    }
//
//    public JwtResponse refresh(@NonNull String refreshToken) {
//        if (jwtProvider.validateRefreshToken(refreshToken)) {
//            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
//            final String login = claims.getSubject();
//            final String saveRefreshToken = refreshStorage.get(login);
//            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
//                final User user = userService.getByLogin(login)
//                        .orElseThrow(() -> new AuthException("Пользователь не найден"));
//                final String accessToken = jwtProvider.generateAccessToken(user);
//                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
//                refreshStorage.put(user.getEmail(), newRefreshToken);
//                return new JwtResponse(accessToken, newRefreshToken);
//            }
//        }
//        throw new AuthException("Невалидный JWT токен");
//    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    public AuthenticationResponse registerUser(RegisterRequest request) throws UserAlreadyExists, PasswordsAreDifferent {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtProvider.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse login(AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtProvider.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}