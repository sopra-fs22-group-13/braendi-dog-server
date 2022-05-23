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

        playedGames.sort(new Comparator<GameHistory>() {
            @Override
            public int compare(GameHistory o1, GameHistory o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        Collections.reverse(playedGames);

        return playedGames;
    }

    public List<GameHistory> getWonGames(User user) {
        return gameHistoryRepository.findWonGames(user);
    }
}
