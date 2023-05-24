package somnus.auth.Dream.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import somnus.auth.Comment.exception.DreamNotExistsException;
import somnus.auth.Dream.exception.DreamNotFoundException;
import somnus.auth.Dream.exception.UserHaveNoRights;
import somnus.auth.Dream.model.Dream;
import somnus.auth.Dream.model.DreamView;
import somnus.auth.Dream.repository.DreamRepository;
import somnus.auth.User.service.UserService;
import somnus.auth.authorization.domain.User;

import java.security.SecureRandom;
import java.util.*;

@Service
public class DreamService {

    @Autowired
    DreamRepository dreamRepository;

    @Autowired
    UserService userService;

    public Optional<Dream> getDreamById(long dreamId){
        return dreamRepository.findById(dreamId);
    }

    public Optional<Dream> addDream(DreamView dreamView, Long authorId){
        Optional<User> author = userService.getUserById(authorId);
        return author.map(user -> Optional.of(dreamRepository.save(new Dream(dreamView.getDreamText(), user)))).orElse(null);
    }

    public Optional<Dream> updateDream(DreamView dreamUpdate, Long authorId, long dreamId) throws DreamNotFoundException, UserHaveNoRights {
        Optional<Dream> dream = dreamRepository.findById(dreamId);
        if (dream.isEmpty()){
            throw new DreamNotFoundException("dream is not found");
        }
        if (!Objects.equals(dream.get().getAuthor().getId(), authorId)){
            throw new UserHaveNoRights();
        }
        return Optional.ofNullable(dream
                .map(dreamOld -> {
                    dreamOld.setDreamText(dreamUpdate.getDreamText());
                    return dreamRepository.save(dreamOld);
                }).orElseThrow(() -> new DreamNotFoundException("Dream with this id does not found, " + dreamId)));
    }

    public void deleteDream(long dreamId, long userId) throws DreamNotFoundException, UserHaveNoRights {
        Optional<Dream> dream  = dreamRepository.findById(dreamId);
        if (dream.isEmpty()){
            throw new DreamNotFoundException();
        }
        if (dream.get().getAuthor().getId() != userId){
            throw new UserHaveNoRights();
        }
        dreamRepository.delete(dream.get());
    }

    public List<Dream> getAllDreams(){
        return dreamRepository.findAll();
    }


    public Optional<Dream> getRandomDream(){
        Random random = new SecureRandom();
        if (dreamRepository.count() == 0){
            return Optional.empty();
        }
        Optional<Dream> lastDream = dreamRepository.findFirstByOrderByIdDesc();
        if (lastDream.isPresent()) {
            long lastId = lastDream.get().getId();
            long randId = random.nextLong(lastId + 1);
            while (dreamRepository.findById(randId).isEmpty()) {
                randId = random.nextLong(lastId);
            }
            return dreamRepository.findById(randId);
        }
        return Optional.empty();
    }

    public Optional<List<Dream>> getUserDreams(long authorId){
        return dreamRepository.findDreamByAuthorId(authorId);
    }

    public Optional<Dream> likeDream(long dreamId, long userId, boolean like) throws DreamNotExistsException{
        Optional<Dream> dream = dreamRepository.findById(dreamId);
        if (dream.isEmpty()){
            throw new DreamNotExistsException();
        }

        Dream dreamLike = dream.get();
        Set likes = dreamLike.getLikes();
        if (likes.add(userId)){
            dreamLike.setLikes(likes);
            return Optional.of(dreamRepository.save(dreamLike));
        }else{
            likes.remove(userId);
            dreamLike.setLikes(likes);
            return Optional.of(dreamRepository.save(dreamLike));
        }
    }
}
