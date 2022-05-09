package ch.uzh.ifi.hase.soprafs22.mocks;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.IUserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public abstract class MockUserService implements IUserService {
    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public User createUser(User newUser) {
        return null;
    }

    @Override
    public User loginUser(User user) {
        return null;
    }

    @Override
    public User getUserByToken(String token) {
        return null;
    }

    @Override
    public User getUserById(Long id) {
        return null;
    }

    @Override
    public void addWins(User user) {

    }

    @Override
    public void addNumberInGoal(User user, int marbleInGoal) {

    }

    @Override
    public User checkIfLoggedIn(HttpServletRequest request) {
        return null;
    }

    @Override
    public void CheckIfLoggedInAsUser(HttpServletRequest request, Long userId) {

    }

    @Override
    public void updateUser(User reqUser) {

    }
}
