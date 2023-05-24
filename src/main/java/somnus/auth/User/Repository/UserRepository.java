package somnus.auth.User.Repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import somnus.auth.authorization.domain.Role;
import somnus.auth.authorization.domain.User;

import java.util.Collections;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLogin(String login);
    User findByLogin(String login);

}
