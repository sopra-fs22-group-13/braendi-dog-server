package ch.uzh.ifi.hase.soprafs22.rest.repository;

import ch.uzh.ifi.hase.soprafs22.rest.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.rest.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GameHistoryRepositoryTest {
    User user1;
    User user2;
    User user3;
    User user4;
    GameHistory gameHistory;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameHistoryRepository gameHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        //setup
        user1 = randomUser();
        user2 = randomUser();
        user3 = randomUser();
        user4 = randomUser();

        gameHistory = new GameHistory();
        gameHistory.setStartDate(new Random().nextLong());
        gameHistory.setWinner(user1);
        gameHistory.setUser1(user1);
        gameHistory.setUser2(user2);
        gameHistory.setUser3(user3);
        gameHistory.setUser4(user4);
        gameHistory.setUser1_goals(4);
        gameHistory.setUser2_goals(3);
        gameHistory.setUser3_goals(2);
        gameHistory.setUser4_goals(1);

        gameHistory = gameHistoryRepository.save(gameHistory);
        gameHistoryRepository.flush();
    }

    @Test
    public void findById_Test() {

        //retrieve
        Optional<GameHistory> foundHistory = gameHistoryRepository.findById(gameHistory.getId());

        //check
        assertEquals(gameHistory, foundHistory.get());
    }

    @Test
    public void findPlayedGames_Test() {
        User user5 = randomUser();

        //retrieve
        List<GameHistory> playedGames_User1 = gameHistoryRepository.findPlayedGames(user1);
        List<GameHistory> playedGames_User5 = gameHistoryRepository.findPlayedGames(user5);

        //check
        assertEquals(1, playedGames_User1.size()); //played 1
        assertEquals(0, playedGames_User5.size()); //played 0
    }

    @Test
    public void findWonGames_Test() {
        User user5 = randomUser();

        //retrieve
        List<GameHistory> wonGames_User1 = gameHistoryRepository.findWonGames(user1);
        List<GameHistory> wonGames_User2 = gameHistoryRepository.findWonGames(user2);
        List<GameHistory> wonGames_User5 = gameHistoryRepository.findWonGames(user5);

        //check
        assertEquals(1, wonGames_User1.size()); //played 1, won 1
        assertEquals(0, wonGames_User2.size()); //played 1, won 0
        assertEquals(0, wonGames_User5.size()); //played 0, won 0
    }

    private User randomUser() {
        User newUser = new User();

        newUser.setPassword(RandomString.make(5));
        newUser.setUsername(RandomString.make(8));
        newUser.setToken(RandomString.make(16));
        newUser.setStatus(UserStatus.OFFLINE);
        newUser.setWins(0);
        newUser.setGotGoal(0);

        User storedUser = userRepository.save(newUser);
        userRepository.flush();

        return storedUser;
    }
}