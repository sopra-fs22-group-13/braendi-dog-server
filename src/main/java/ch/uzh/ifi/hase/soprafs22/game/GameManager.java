package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;

import java.util.ArrayList;

public class GameManager {

    private ArrayList<Game> _games;
    private static GameManager gameManager = null;

    private GameManager(){
        this._games= new ArrayList<>();
    }

    public static GameManager getInstance(){
        if(gameManager==null){
            gameManager= new GameManager();
        }
        return gameManager;
    }

    public void addGame(Game newGame){
        _games.add(newGame);
    }
    public Game getGameByToken(String token){
        for (Game game:_games){
            if (game.getGameToken().equals(token)){
                return game;
            }
        }
        return null;
    }

    public Game getGameByPlayer(String token){
        for (Game game:_games){
            if (game.getPlayerByToken(token) != null){
                return game;
            }
        }
        return null;
    }

    public Player getPlayerByToken(String token){
        for (Game game:_games){
            Player player= game.getPlayerByToken(token);
            if (player != null){
                return player;
            }
        }
        return null;
    }

    public void deleteGame(Game gameToDelete){
        String gameTokenToDelete = gameToDelete.getGameToken();
        for (Game game:_games){
            String token= game.getGameToken();
            if(gameTokenToDelete.equals(token)){
                _games.remove(game);
            }
        }
    }
}
