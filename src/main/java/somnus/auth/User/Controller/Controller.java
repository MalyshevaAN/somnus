package somnus.auth.User.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import somnus.auth.User.Exceptions.PasswordsAreDifferent;
import somnus.auth.User.Exceptions.UserAlreadyExists;
import somnus.auth.User.Exceptions.UserCannotSubscribeOnHimself;
import somnus.auth.User.Exceptions.UserNotFound;
import somnus.auth.User.service.UserService;
import somnus.auth.User.views.UserView;
import somnus.auth.authorization.domain.AuthenticationResponse;
import somnus.auth.authorization.domain.JwtAuthentication;
import somnus.auth.authorization.domain.RegisterRequest;
import somnus.auth.authorization.domain.User;
import somnus.auth.authorization.service.AuthService;

import java.util.Set;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class Controller {

    private final AuthService authService;

    @Autowired
    UserService userService;

    @PostMapping("register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequest request) {
        try {
            AuthenticationResponse newUser = authService.registerUser(request);
            return ResponseEntity.ok().body(newUser);
        }catch (UserAlreadyExists e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }catch (PasswordsAreDifferent e){
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

    @PutMapping("follow/{userId}")
    public ResponseEntity<UserView> addFollow(@PathVariable long userId) {
        final JwtAuthentication authInfo = authService.getAuthInfo();
        try {
            UserView  userFollow = userService.saveFollow(authInfo.getCredentials(), userId);
            return ResponseEntity.ok().body(userFollow);
        }catch (UserCannotSubscribeOnHimself e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (UserNotFound e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping("unfollow/{userId}")
    public ResponseEntity<UserView> removeFollow(@PathVariable long userId) {
        final JwtAuthentication authInfo = authService.getAuthInfo();
        try {
            UserView userUnfollow = userService.deleteFollow(authInfo.getCredentials(), userId);
            return ResponseEntity.ok().body(userUnfollow);
        }catch (UserCannotSubscribeOnHimself e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (UserNotFound e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("subscriptions")
    public ResponseEntity<Set<User>> getMySubscriptions() {
        final JwtAuthentication authInfo = authService.getAuthInfo();
        return ResponseEntity.ok().body(userService.getSubscriptions(authInfo.getCredentials()));
    }

    @GetMapping("subscribers")
    public ResponseEntity<Set<User>> getMySubscribers() {
        final JwtAuthentication authInfo = authService.getAuthInfo();
        return ResponseEntity.ok().body(userService.getSubscribers(authInfo.getCredentials()));
    }
}