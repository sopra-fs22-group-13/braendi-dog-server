package ch.uzh.ifi.hase.soprafs22.mocks;

import ch.uzh.ifi.hase.soprafs22.game.gameInstance.GameResults;
import ch.uzh.ifi.hase.soprafs22.rest.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.IGameHistoryService;

import java.util.List;

public abstract class MockGameHistoryService implements IGameHistoryService {
    @Override
    public void savePlayedGame(GameResults gameResults) {return;}

    @Override
    public List<GameHistory> getPlayedGames(User user) {return null;}

    @Override
    public List<GameHistory> getWonGames(User user) {return null;}
}
