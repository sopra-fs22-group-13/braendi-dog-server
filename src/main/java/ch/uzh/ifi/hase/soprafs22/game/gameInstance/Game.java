package ch.uzh.ifi.hase.soprafs22.game.gameInstance;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.board.Board;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.CardStack;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;


import java.util.*;

public class Game {
    private Player _currentTurn;
    private List<Boolean> _playerHasValidTurns;
    private List<Player> _players;
    private CardStack _cardStack;
    private String _gameToken;
    private GameManager _manager;
    private Board _board;

    public Game(List<Player> players, GameManager manager){

        this._players= players;;

        /*
        * From how do I get the Users?? Lobby, UserManager, Manager Or God?
        * */

        /* Inizialize Game with already one player has first*/
        Random rand = new Random();
        this._currentTurn= _players.get(rand.nextInt(_players.size()));
        this._playerHasValidTurns= Arrays.asList(new Boolean[4]);;
        this._cardStack= new CardStack();
        this._gameToken= UUID.randomUUID().toString();
        this._manager= manager;
        this._board= new Board();
    }

    
    private void checkValidTurns(){
        // checks if every player has a valid turn with the cards
    }

    public boolean playerMove(String player, Move move){
        return false;
    }

    public String gameState(){
        return null;
    }

    public String getPlayerStates(String pointOFViey){
        return null;
    }

    public void dealNewCards(){

    }

    private void nextTurns(){

    }

    private void updateValidTurns(){

    }

    public Player getCurrentTurn(){
        return _currentTurn;
    }

    public int getPlayerPositionInList(Player player){
        int position= _players.indexOf(player);
        return position;
    }

    public Boolean getPlayerValidTurn(Player player){
        // if a player has a valid turn must be checked in UpdateValidTurn()
        boolean valid = _playerHasValidTurns.get(getPlayerPositionInList(player));
        return valid;
    }

    public Boolean getIfSomeoneValidTurn(){
        boolean valid = false;
        for (int i = 0; i<4; i++){
            valid = valid || _playerHasValidTurns.get(i);
        }
        return valid;
    }

    public CardStack getCardStack(){
        // id don't know if it is usefull
        return null;
    }

    public String getGameToken(){
        return this._gameToken;
    }

    public List<String> getUsersToken(){
        List<String> usersToken=null;
        for (int i =0; i<4; i++){
            usersToken.add(_players.get(i).get_token());
        }
        return usersToken;
    }

}
