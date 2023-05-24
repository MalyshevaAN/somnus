package somnus.auth.Dream.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import somnus.auth.Comment.exception.DreamNotExistsException;
import somnus.auth.Dream.exception.DreamNotFoundException;
import somnus.auth.Dream.exception.UserHaveNoRights;
import somnus.auth.Dream.model.Dream;
import somnus.auth.Dream.model.DreamView;
import somnus.auth.Dream.service.DreamService;
import somnus.auth.authorization.domain.JwtAuthentication;
import somnus.auth.authorization.service.AuthService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dream")
public class DreamController {

    AuthService authService;

    DreamService dreamService;

    public DreamController(AuthService authService, DreamService dreamService){
        this.authService = authService;
        this.dreamService = dreamService;
    }

    @GetMapping("read/{dreamId}")
    public ResponseEntity<Dream> getDream(@PathVariable long dreamId){
        Optional<Dream> dream =  dreamService.getDreamById(dreamId);
        return dream.map(value -> ResponseEntity.ok().body(value)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PostMapping("add")
    public ResponseEntity<Dream>  addDream(@RequestBody DreamView dreamView){
        final JwtAuthentication authInfo = authService.getAuthInfo();
        Optional<Dream> dream = dreamService.addDream(dreamView, authInfo.getCredentials());
        return dream.map(value -> ResponseEntity.ok().body(value)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Dream> updateDream(@RequestBody DreamView dreamUpdate, @PathVariable long id){
        final JwtAuthentication authInfo = authService.getAuthInfo();
        try {
            Optional<Dream> newDream = dreamService.updateDream(dreamUpdate, authInfo.getCredentials(), id);
            return ResponseEntity.ok().body(newDream.get());
        } catch (DreamNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (UserHaveNoRights e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteDream(@PathVariable Long id){
        final JwtAuthentication authInfo = authService.getAuthInfo();
        try{
            dreamService.deleteDream(id, authInfo.getCredentials());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(DreamNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (UserHaveNoRights e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("all")
    public ResponseEntity<List<Dream>> getAllDreams(){
        return ResponseEntity.ok().body(dreamService.getAllDreams());
    }

    @GetMapping("random")
    public ResponseEntity<Dream> getRandomDream(){
        Optional<Dream> dream = dreamService.getRandomDream();
        return dream.map(value -> ResponseEntity.ok().body(value)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("users/{userId}")
    public ResponseEntity<List<Dream>> getUsersDreams(@PathVariable long userId){
        Optional<List<Dream>> usersDreams = dreamService.getUserDreams(userId);
        return usersDreams.map(value -> ResponseEntity.ok().body(usersDreams.get())).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("my")
    public ResponseEntity<List<Dream>> getMyDreams(){
        final JwtAuthentication authInfo = authService.getAuthInfo();
        Optional<List<Dream>> myDreams = dreamService.getUserDreams(authInfo.getCredentials());
        return myDreams.map(value -> ResponseEntity.ok().body(myDreams.get())).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/like/{dreamId}")
    public ResponseEntity<Dream> likeDream(@PathVariable long dreamId){
        final JwtAuthentication authInfo = authService.getAuthInfo();
        try {
            Optional<Dream> likedDream = dreamService.likeDream(dreamId, authInfo.getCredentials(), true);
            return ResponseEntity.ok().body(likedDream.get());
        }catch (DreamNotExistsException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
