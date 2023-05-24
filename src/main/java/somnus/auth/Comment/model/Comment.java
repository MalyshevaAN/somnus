package somnus.auth.Comment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import somnus.auth.Dream.model.Dream;
import somnus.auth.authorization.domain.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dream_id", nullable = false)
    @JsonIgnore
    private Dream dream;

    private String commentText;
    
    @Basic
    private final LocalDateTime timeCreation = LocalDateTime.now();

    public Comment(User author, Dream dream, String commentText) {
        this.user = author;
        this.dream = dream;
        this.commentText = commentText;
    }
}
