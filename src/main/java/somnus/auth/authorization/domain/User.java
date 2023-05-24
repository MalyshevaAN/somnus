package somnus.auth.authorization.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.web.WebProperties;
import somnus.auth.Comment.model.Comment;
import somnus.auth.Dream.model.Dream;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "somnusUser")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true)
    private String login;
    private String password;
    @Transient
    private String passwordConfirm;
    private String firstName;
    private String lastName;
    private Set<Role> roles = Set.of(Role.USER);

    @JsonIgnore
    @OneToMany(mappedBy = "author", fetch = FetchType.EAGER)
    private Set<Dream> dreams = new HashSet<>();


    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Comment> comments = new HashSet<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_subscriptions",
            joinColumns = {@JoinColumn(name = "channel_id")},
            inverseJoinColumns = {@JoinColumn(name="subscriber_id")}
    )
    private Set<User> subscribers = new HashSet<>();


    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_subscriptions",
            joinColumns = {@JoinColumn(name = "subscriber_id")},
            inverseJoinColumns = {@JoinColumn(name="channel_id")}
    )
    private Set<User> subscribtions = new HashSet<>();

    public void addSubscription(User subscription){
        this.subscribtions.add(subscription);
    }

    public void deleteSubscription(User subscription){
        if (this.subscribtions.contains(subscription)){
            this.subscribtions.remove(subscription);
        }
    }
}
