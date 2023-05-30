package somnus.auth.User.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import somnus.auth.User.Exceptions.PasswordsAreDifferent;
import somnus.auth.User.Exceptions.UserCannotSubscribeOnHimself;
import somnus.auth.User.Exceptions.UserNotFound;
import somnus.auth.User.Repository.UserRepository;
import somnus.auth.User.Exceptions.UserAlreadyExists;
import somnus.auth.User.views.UserView;
import somnus.auth.authorization.domain.User;

import java.util.*;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    public Optional<User> getByLogin(@NonNull String login) {
        return userRepository.findAll().stream()
                .filter(user -> login.equals(user.getEmail()))
                .findFirst();
    }

    public UserView addUser(User user) throws UserAlreadyExists, PasswordsAreDifferent {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExists();
        }

        if (!Objects.equals(user.getPassword(), user.getPasswordConfirm())) {
            throw new PasswordsAreDifferent();
        }
        User newUser = new User();
        newUser.setEmail(user.getEmail());
//        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setPassword(user.getPassword());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        User returnUser =  userRepository.save(newUser);
        return returnUserView(returnUser);
    }

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    public UserView saveFollow(long clientId, long userId) throws UserCannotSubscribeOnHimself, UserNotFound {
        if (clientId != userId) {
            Optional<User> client = userRepository.findById(clientId);
            Optional<User> subscription = userRepository.findById(userId);

            if (client.isPresent() && subscription.isPresent()) {
                User currentClient = client.get();
                currentClient.addSubscription(subscription.get());
                User updatedUser = userRepository.save(currentClient);
                return returnUserView(updatedUser);
            }
            throw new UserNotFound();
        }
        throw new UserCannotSubscribeOnHimself();
    }

    public UserView deleteFollow(long clientId, long userId) throws UserCannotSubscribeOnHimself, UserNotFound {
        if (clientId != userId) {
            Optional<User> client = userRepository.findById(clientId);
            Optional<User> subscription = userRepository.findById(userId);

            if (client.isPresent() && subscription.isPresent()) {
                User currentClient = client.get();
                currentClient.deleteSubscription(subscription.get());
                User updatedUser = userRepository.save(currentClient);
                return returnUserView(updatedUser);
            }
            throw new UserNotFound();
        }
        throw new UserCannotSubscribeOnHimself();
    }

    public Set<User> getSubscriptions(long userId) {
        return userRepository.findById(userId).get().getSubscribtions();
    }

    public Set<User> getSubscribers(long userId) {
        return userRepository.findById(userId).get().getSubscribers();
    }

    protected UserView returnUserView (User user){
        UserView returnUpdatedUser = new UserView();
        returnUpdatedUser.setEmail(user.getEmail());
        returnUpdatedUser.setId(user.getId());
        returnUpdatedUser.setFirstName(user.getFirstName());
        returnUpdatedUser.setLastName(user.getLastName());
        return returnUpdatedUser;
    }
}