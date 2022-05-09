package ch.uzh.ifi.hase.soprafs22.rest.service;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IUserService {
    public List<User> getUsers();
    public User createUser(User newUser);
    public User loginUser(User user);
    public User getUserByToken(String token);
    public User getUserById(Long id);
    public void addWins (User user);
    public void addNumberInGoal(User user, int marbleInGoal);
    public User checkIfLoggedIn(HttpServletRequest request);
    public void CheckIfLoggedInAsUser(HttpServletRequest request, Long userId);
    public void updateUser(User reqUser);
}
