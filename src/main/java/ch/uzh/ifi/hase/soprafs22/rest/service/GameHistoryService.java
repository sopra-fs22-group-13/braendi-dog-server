package ch.uzh.ifi.hase.soprafs22.rest.service;

import ch.uzh.ifi.hase.soprafs22.game.gameInstance.GameResults;
import ch.uzh.ifi.hase.soprafs22.rest.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.GameHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
    }

    public List<GameHistory> getPlayedGames(User user) {
        return gameHistoryRepository.findPlayedGames(user);
    }

    public List<GameHistory> getWonGames(User user) {
        return gameHistoryRepository.findWonGames(user);
    }
}
