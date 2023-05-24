package somnus.auth.Comment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import somnus.auth.Comment.exception.CommentNotFound;
import somnus.auth.Comment.exception.DreamNotExistsException;
import somnus.auth.Comment.exception.UserHaveNoRights;
import somnus.auth.Comment.model.Comment;
import somnus.auth.Comment.model.CommentView;
import somnus.auth.Comment.repository.CommentRepository;
import somnus.auth.Dream.model.Dream;
import somnus.auth.Dream.service.DreamService;
import somnus.auth.User.service.UserService;
import somnus.auth.authorization.domain.User;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserService userService;

    @Autowired
    DreamService dreamService;

    public Optional<Comment> addComment(CommentView commentView, long dreamId, long authId) throws DreamNotExistsException {

        Optional<User> author = userService.getUserById(authId);
        Optional<Dream> dream = dreamService.getDreamById(dreamId);

        if (author.isEmpty()){
            throw new UsernameNotFoundException("invalid user Id");
        }
        if (dream.isEmpty()){
            throw new DreamNotExistsException("dream with id " + dreamId + "does not exists");
        }

        return Optional.of(commentRepository.save(new Comment(author.get(), dream.get(), commentView.getCommentText())));
    }

    public Optional<List<Comment>> readCommentForPost(long dreamId) throws DreamNotExistsException {
        Optional<Dream> dream = dreamService.getDreamById(dreamId);

        if (dream.isEmpty()){
            throw new DreamNotExistsException("dream with id " + dreamId + "does not exists");
        }

        return Optional.ofNullable(commentRepository.findByDreamId(dreamId));

    }

    public Optional<Comment> editComment(long userId, long commentId, CommentView comment) throws UserHaveNoRights, DreamNotExistsException {
        Optional<Comment> oldComment = commentRepository.findById(commentId);
        if (oldComment.isPresent()){
            Comment old = oldComment.get();
            if (old.getUser().getId() == userId){
                old.setCommentText(comment.getCommentText());
                return Optional.of(commentRepository.save(old));
            }
            throw new UserHaveNoRights();
        }
        throw new DreamNotExistsException();
    }

    public void deleteComment(long userId, long commentId) throws UserHaveNoRights, CommentNotFound {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()){
            Comment comment1 = comment.get();
            if (comment1.getUser().getId() == userId){
                commentRepository.delete(comment1);
            }
            throw new UserHaveNoRights();
        }
        throw new CommentNotFound();
    }
}