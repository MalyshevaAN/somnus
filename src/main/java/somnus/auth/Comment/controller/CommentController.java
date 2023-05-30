package somnus.auth.Comment.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import somnus.auth.Comment.exception.CommentNotFound;
import somnus.auth.Comment.exception.DreamNotExistsException;
import somnus.auth.Comment.model.Comment;
import somnus.auth.Comment.model.CommentView;
import somnus.auth.Comment.service.CommentService;
import somnus.auth.Comment.exception.UserHaveNoRights;
import somnus.auth.authorization.domain.JwtAuthentication;
import somnus.auth.authorization.service.AuthService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    @Autowired
    AuthService authService;

    @PostMapping("add/{dreamId}")
    public ResponseEntity<Comment> addComment(@RequestBody CommentView comment, @PathVariable long dreamId){
        final JwtAuthentication authInfo = authService.getAuthInfo();
        try {
            Optional<Comment> newComment = commentService.addComment(comment, dreamId, authInfo.getCredentials());
            return newComment.map(value -> new ResponseEntity<>(newComment.get(), HttpStatus.CREATED)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        } catch(DreamNotExistsException | UsernameNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("read/{dreamId}")
    public ResponseEntity<List<Comment>> readComment(@PathVariable long dreamId){
        final JwtAuthentication authInfo = authService.getAuthInfo();
        try {
            Optional<List<Comment>> dreamComments = commentService.readCommentForPost(dreamId);
            return dreamComments.map(value -> ResponseEntity.ok().body(dreamComments.get())).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }catch (DreamNotExistsException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("edit/{commentId}")
    public ResponseEntity<Comment> editComment(@PathVariable long commentId, @RequestBody CommentView comment){
        final JwtAuthentication authInfo = authService.getAuthInfo();
        try {
            Optional<Comment> commentUpdated  = commentService.editComment(authInfo.getCredentials(), commentId, comment);
            return commentUpdated.map(value -> ResponseEntity.ok().body(commentUpdated.get())).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch(DreamNotExistsException e ){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (UserHaveNoRights e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable long commentId){
        final JwtAuthentication authInfo = authService.getAuthInfo();
        try {
            commentService.deleteComment(authInfo.getCredentials(), commentId);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (CommentNotFound e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (UserHaveNoRights e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
