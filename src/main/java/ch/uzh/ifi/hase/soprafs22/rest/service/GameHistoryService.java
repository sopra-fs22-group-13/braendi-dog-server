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
import ch.uzh.ifi.hase.soprafs22.rest.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.GameHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class GameHistoryService implements IGameHistoryService{

    private final GameHistoryRepository gameHistoryRepository;

    @Autowired
    public GameHistoryService(@Qualifier("gameHistoryRepository") GameHistoryRepository gameHistoryRepository) {
        this.gameHistoryRepository = gameHistoryRepository;
    }

    public void savePlayedGame(GameResults gameResults) {
        GameHistory gameHistory = new GameHistory();

        gameHistory.setStartDate(gameResults.startingTime);
        gameHistory.setWinner(gameResults.winner);

        gameHistory.setUser1(gameResults.getPlayer1());
        gameHistory.setUser2(gameResults.getPlayer2());
        gameHistory.setUser3(gameResults.getPlayer3());
        gameHistory.setUser4(gameResults.getPlayer4());

        gameHistory.setUser1_goals(gameResults.getResultPlayer1());
        gameHistory.setUser2_goals(gameResults.getResultPlayer2());
        gameHistory.setUser3_goals(gameResults.getResultPlayer3());
        gameHistory.setUser4_goals(gameResults.getResultPlayer4());

        gameHistoryRepository.save(gameHistory);
        gameHistoryRepository.flush();
    }

    public List<GameHistory> getPlayedGames(User user) {
        List<GameHistory> playedGames = gameHistoryRepository.findPlayedGames(user);
        return playedGames;
    }

    public List<GameHistory> getWonGames(User user) {
        return gameHistoryRepository.findWonGames(user);
    }
}
