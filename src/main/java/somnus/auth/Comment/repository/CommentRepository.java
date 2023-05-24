package somnus.auth.Comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import somnus.auth.Comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDreamId(long id);
}
