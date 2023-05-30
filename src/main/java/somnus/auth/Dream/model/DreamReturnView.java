package somnus.auth.Dream.model;

import lombok.Getter;
import lombok.Setter;
import somnus.auth.Comment.model.Comment;
import somnus.auth.authorization.domain.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
public class DreamReturnView {
    private Long Id;
    private String dreamText;
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private User author;
    private Set<Comment> comments = new HashSet<>();
    private Set<Long> likes = new HashSet<>();
}
