package somnus.auth.Dream.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import somnus.auth.Dream.model.Dream;

import java.util.List;
import java.util.Optional;

public interface DreamRepository extends JpaRepository<Dream, Long> {
    public Optional<Dream> findFirstByOrderByIdDesc();

    public Optional<List<Dream>> findDreamByAuthorId(long id);

}
