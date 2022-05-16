package ch.uzh.ifi.hase.soprafs22.rest.service;

import ch.uzh.ifi.hase.soprafs22.game.gameInstance.GameResults;
import ch.uzh.ifi.hase.soprafs22.rest.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.rest.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.GameHistoryRepository;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class GameHistoryServiceTest {
    User user1;
    User user2;
    User user3;
    User user4;

    @Mock
    private GameHistoryRepository gameHistoryRepository;

    @InjectMocks
    private GameHistoryService gameHistoryService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        user1 = randomUser();
        user2 = randomUser();
        user3 = randomUser();
        user4 = randomUser();

        GameHistory expectedHistory = new GameHistory();

        Mockito.when(gameHistoryRepository.save(Mockito.any())).thenReturn(expectedHistory);
    }

    @Test
    public void saveGame_Test() {
        //setup
        GameResults gameResults = new GameResults();
        gameResults.addPlayerResults(user1, 1);
        gameResults.addPlayerResults(user2, 2);
        gameResults.addPlayerResults(user3, 3);
        gameResults.addPlayerResults(user4, 4);
        gameResults.startingTime = 1000L;
        gameResults.winner = user4;

        // call method
        gameHistoryService.savePlayedGame(gameResults);

        //check
        Mockito.verify(gameHistoryRepository, Mockito.times(1)).save(Mockito.any());
    }

    private User randomUser() {
        User newUser = new User();

        newUser.setPassword(RandomString.make(5));
        newUser.setUsername(RandomString.make(8));
        newUser.setToken(RandomString.make(16));
        newUser.setStatus(UserStatus.OFFLINE);
        newUser.setWins(0);
        newUser.setGotGoal(0);

        return newUser;
    }

}