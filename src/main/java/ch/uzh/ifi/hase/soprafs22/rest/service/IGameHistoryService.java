package ch.uzh.ifi.hase.soprafs22.rest.service;

import ch.uzh.ifi.hase.soprafs22.game.gameInstance.GameResults;
import ch.uzh.ifi.hase.soprafs22.rest.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;

import java.util.List;

public interface IGameHistoryService {
    public void savePlayedGame(GameResults gameResults);
    public List<GameHistory> getPlayedGames(User user);
    public List<GameHistory> getWonGames(User user);
}
