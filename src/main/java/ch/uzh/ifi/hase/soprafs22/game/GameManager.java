package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.UserManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.springContext.SpringContext;
import ch.uzh.ifi.hase.soprafs22.websocket.constant.UpdateType;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.IUpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;

import java.util.ArrayList;
import java.util.Date;

public class GameManager {

    private ArrayList<Game> _games;
    private static GameManager gameManager = null;
    protected IUpdateController updateController = SpringContext.getBean(UpdateController.class);

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

    synchronized public void deleteGame(String tokenToDelete, boolean fromHeartBeat){
        for (Game game:_games){
            String token= game.getGameToken();
            if(tokenToDelete.equals(token) && (game.getCreationTime() + 5000 < new Date().getTime() || !fromHeartBeat)){
                UserManager userManager = game.getUserManager();
                updatePlayers(game, UpdateType.GAME_CLOSED, userManager);
                _games.remove(game);
                return;
            }
        }
    }

    synchronized public void deleteGame(String tokenToDelete){
        deleteGame(tokenToDelete, false);
    }

    private void updatePlayers(Game game, UpdateType updateType, UserManager userManager) {
        UpdateDTO updateDTO = new UpdateDTO(updateType, "");

        for (Player player: game.getPlayers()){
            updateController.sendUpdateToUser(userManager.getUserFromPlayer(player).getToken(), updateDTO);
        }
    }
}
