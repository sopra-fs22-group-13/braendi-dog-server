package ch.uzh.ifi.hase.soprafs22.rest.service;

import ch.uzh.ifi.hase.soprafs22.rest.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService implements IUserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }


    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        long i = 0;
        while (userRepository.findById(i).isPresent()) {
            i++;
        }

        newUser.setId(i);
        newUser.setWins(0);
        newUser.setGoals(0);
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setDescription("Hello there i'm playing dog");

        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }


    public User loginUser(User user) {

        checkIfPasswordIsCorrect(user);
        // need to throw an expection is not register

        User userAlreadyRegister = userRepository.findByUsername(user.getUsername());
        log.debug("Check Information for User: {}", user);
        userAlreadyRegister.setStatus(UserStatus.ONLINE);
        return userAlreadyRegister;
    }

    public User getUserByToken(String token) {
        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The provided Token was not found");
        }
        return user;
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        User newUser = user.isPresent() ? user.get() : null;
        if (newUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The provided Id was not found");
        }
        return newUser;
    }

    /**
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        }
    }

    public void addWins(User user) {
        User userWon = userRepository.findByUsername(user.getUsername());
        Integer wins = userWon.getWins();
        userWon.setWins(wins + 1);
    }

    public void addNumberInGoal(User user, int marbleInGoal) {
        User userToAddGoals = userRepository.findByUsername(user.getUsername());
        Integer goals = userToAddGoals.getGoals();
        userToAddGoals.setGoals(goals + marbleInGoal);
    }

    public List<User> getLeaderboard(){
        return userRepository.getTopTenUsers();
    }


    private void checkIfPasswordIsCorrect(User userToLoggedIn ) throws ResponseStatusException {
        User userByUsername = userRepository.findByUsername(userToLoggedIn.getUsername());

        String baseErrorMessageNotRegister = "The %s provided %s not reigister. Therefore, the user could not login";
        if (userByUsername == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(baseErrorMessageNotRegister, "username", "is"));
        }
        String baseErrorMessageWrongPassword = "The Password provided is for your %s %s wrong. Therefore, the user could not login";
        String temp = userByUsername.getPassword();
        String temp1 = userToLoggedIn.getPassword();
        if (!temp1.equals(temp)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    String.format(baseErrorMessageWrongPassword, "username", "is"));
        }

    }

    public User checkIfLoggedIn(HttpServletRequest request) {
        if (request.getHeader("Authorization") != null && request.getHeader("Authorization").length() > 6 && Objects.equals(request.getHeader("Authorization").substring(0, 5).toUpperCase(), HttpServletRequest.BASIC_AUTH)) {
            String auth = request.getHeader("Authorization");
            auth = auth.substring(6); //get the token part
            User us = getUserByToken(auth);

            if (us != null && Objects.equals(us.getToken(), auth)) {
                //logged in.
                return us;
            }
            else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, us == null ? "bad token" : "something went wrong");
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong Auth method, expected BASIC");
        }
    }


    public void CheckIfLoggedInAsUser(HttpServletRequest request, Long userId) {
        if (request.getHeader("Authorization") != null && request.getHeader("Authorization").length() > 6 && Objects.equals(request.getHeader("Authorization").substring(0, 5).toUpperCase(), HttpServletRequest.BASIC_AUTH)) {
            String auth = request.getHeader("Authorization");

            auth = auth.substring(6); //get the token part

            User us = getUserById(userId);

            if (us != null && Objects.equals(us.getToken(), auth)) {
                //logged in.
            }
            else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, us == null ? "no token" : "bad token");
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong Auth method, expected BASIC");
        }
    }

    public void updateUser(User reqUser) {
        User storedUser = getUserById(reqUser.getId());

        if (userRepository.findByUsername(reqUser.getUsername()) != null) { throw new ResponseStatusException(HttpStatus.CONFLICT, "The username you are trying to change to is already taken."); }
        if (reqUser.getUsername() != null ) { storedUser.setUsername(reqUser.getUsername()); }

        if (reqUser.getPassword() != null ) { storedUser.setPassword(reqUser.getPassword()); }
        if (reqUser.getAvatar() != null ) { storedUser.setAvatar(reqUser.getAvatar()); }
        if (reqUser.getDescription() != null) { storedUser.setDescription(reqUser.getDescription()); }
    }

    public void setUserOffline(User user){
        User storedUser = getUserById(user.getId());
        storedUser.setStatus(UserStatus.OFFLINE);
    }

    public void setUserOnline(User user){
        User storedUser = getUserById(user.getId());
        storedUser.setStatus(UserStatus.ONLINE);
    }
}
