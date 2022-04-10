package ch.uzh.ifi.hase.soprafs22.game.gameInstance;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;

import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;

import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.board.Board;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.CardStack;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.PlayerData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.websocket.constant.UpdateType;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;
import org.springframework.http.HttpStatus;


import java.util.*;

public class Game {
    private Player _playerWithCurrentTurn;
    private ArrayList<Boolean> _playerHasValidTurns;
    private ArrayList<Player> _players;
    private CardStack _cardStack;
    private String _gameToken;
    private GameManager _manager;
    private Board _board;
    private UserManager _userManager;

    public Game(ArrayList<User> users, GameManager manager){

        /* Inizialize Game with already one player has first*/
        Random rand = new Random();
        this._playerWithCurrentTurn= _players.get(rand.nextInt(_players.size()));
        this._playerHasValidTurns= new ArrayList();
        this._cardStack= new CardStack();
        this._gameToken= UUID.randomUUID().toString();
        this._manager= manager;
        this._board= new Board();
        this._userManager= new UserManager(_players,users);

    }


    private boolean checkValidTurns(Move move, Player playerWantToMove){
        if (!move.isWellFormed()|| move== null ) {
            if (playerWantToMove == _playerWithCurrentTurn){
                return true;
            }
        }
        return false;
    }

    public boolean playerMove(Move move) throws InvalidMoveException {
        Player playerWantToMove=_userManager.getPlayerFromUserToken(move.getToken());
        if (!checkValidTurns(move, playerWantToMove)) {
            throw new InvalidMoveException("Move Not allowed", "Bad move logic");
        }
        _board.makeMove(move);


        // check for winning condition
        return true;
    }

    /**
     * Gets the games board state.
     */
    public BoardData gameState(){
        BoardData bd =  _board.getFormattedBoardState();

        //the color mapping of the users
        Map<Long, COLOR> cMap = new HashMap<>();
        ArrayList<COLOR> cols = new ArrayList<>(Arrays.asList(COLOR.RED, COLOR.BLUE, COLOR.GREEN, COLOR.YELLOW));

        for (Player p: _players) {
            User u = _userManager.getUserFromPlayer(p);
            cMap.put(u.getId(), cols.remove(0));
        }
        bd.setColorMapping(cMap);

        return bd;
    }

    public PlayerData getPlayerStates(String pointOfView){

        PlayerData pd = new PlayerData();

        ArrayList<Integer> hiddenCards = new ArrayList<>();

        boolean validPOV = false;

        for (Player p : _players) {

            User u = _userManager.getUserFromPlayer(p);

            if(Objects.equals(u.getToken(), pointOfView))
            {
                //we can see our own cards
                ArrayList<String> cards = p.getFormattedCards();
                pd.setVisibleCards(cards);
                validPOV = true;

            }else
            {
                int count = p.getCardCount();
                hiddenCards.add(count);
            }

        }

        pd.setHiddenCardCount(hiddenCards);

        if(!validPOV)
        {
            //the pointOfView token was not valid for any of the players.
            return null;
        }

        return pd;
    }

    public void dealNewCards(){

    }

    private void nextTurns(){

    }

    private void updateValidTurns(){

    }

    public Player getCurrentTurn(){
        return _playerWithCurrentTurn;
    }

    public Boolean getPlayerValidTurn(int i){

        // if a player has a valid turn must be checked in UpdateValidTurn()
        boolean valid = _playerHasValidTurns.get(i);
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

    public ArrayList<Player> getPlayers(){
        // I don't think that game should do it like that
        return _players;
    }

}
