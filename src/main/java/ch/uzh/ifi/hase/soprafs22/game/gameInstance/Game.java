package ch.uzh.ifi.hase.soprafs22.game.gameInstance;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.board.Board;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.CardStack;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;


import java.util.*;

public class Game {
    private Player _currentTurn;
    private List<Boolean> _playerHasValidTurns;
    private List<Player> _players;
    private CardStack _cardStack;
    private String _gameToken;
    private GameManager _manager;
    private Board _board;

    public Game(/*Lobby users,*/ GameManager manager){

        this._players= Arrays.asList(new Player[4]);;

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

    private int getPlayerPositionInList(Player player){
        int position= _players.indexOf(player);
        return position;
    }

    public Boolean getPlayerValidTurn(Player player){
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

    public CardStack get_cardStack(){
        // id don't know if it is usefull
        return null;
    }

    public String get_gameToken(){
        return this._gameToken;
    }

}
