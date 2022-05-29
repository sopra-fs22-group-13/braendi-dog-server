/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen, Simona Borghi, Sandro Vonlanthen, Anton Crazzolara, Shitao Zeng

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void getPlayedGames_Test() {
        GameHistory gh1 = new GameHistory(); gh1.setStartDate(10L);
        GameHistory gh2 = new GameHistory(); gh2.setStartDate(5L);
        GameHistory gh3 = new GameHistory(); gh3.setStartDate(15L);
        List<GameHistory> providedHistory = new ArrayList<>(List.of(gh1, gh2, gh3));

        Mockito.when(gameHistoryRepository.findPlayedGames(Mockito.any())).thenReturn(providedHistory);

        assertEquals(List.of(gh1, gh2, gh3), gameHistoryService.getPlayedGames(randomUser()));
    }

    private User randomUser() {
        User newUser = new User();

        newUser.setPassword(RandomString.make(5));
        newUser.setUsername(RandomString.make(8));
        newUser.setToken(RandomString.make(16));
        newUser.setStatus(UserStatus.OFFLINE);
        newUser.setWins(0);
        newUser.setGoals(0);

        return newUser;
    }
}