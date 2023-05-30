package somnus.auth.Dream.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import somnus.auth.Comment.model.Comment;
import somnus.auth.authorization.domain.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dream")
public class Dream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String dreamText;

    @Basic
    @Column(name = "DateCreation")
    private final LocalDateTime timeCreation = LocalDateTime.now();


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    @JsonIgnore
    private User author;


    @OneToMany(mappedBy = "dream", fetch = FetchType.EAGER)
    private Set<Comment> comments = new HashSet<>();

    private Set<Long> likes = new HashSet<>();

    public Dream(String dreamText, User user) {
        this.dreamText = dreamText;
        this.author = user;
    }

}