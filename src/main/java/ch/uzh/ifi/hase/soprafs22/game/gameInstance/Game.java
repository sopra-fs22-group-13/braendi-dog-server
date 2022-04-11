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
import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.springContext.SpringContext;
import ch.uzh.ifi.hase.soprafs22.websocket.constant.UpdateType;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.IUpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;
import org.springframework.http.HttpStatus;


import java.util.*;

public class Game {
    private int _indexWithCurrentTurn;
    private ArrayList<Boolean> _playerHasValidTurns;
    private ArrayList<Player> _players;
    private CardStack _cardStack;
    private String _gameToken;
    private GameManager _manager;
    private Board _board;
    private UserManager _userManager;
    private final IUpdateController updateController = SpringContext.getBean(UpdateController.class);

    public Game(ArrayList<User> users){

        Random rand = new Random();
        this._players= new ArrayList<>();
        this._players.add(new Player(COLOR.RED));
        this._players.add(new Player(COLOR.YELLOW));
        this._players.add(new Player(COLOR.GREEN));
        this._players.add(new Player(COLOR.BLUE));

        this._indexWithCurrentTurn= rand.nextInt(4);
        this._playerHasValidTurns= new ArrayList();
        this._cardStack= new CardStack();
        this._gameToken= UUID.randomUUID().toString();
        this._manager= GameManager.getInstance();
        this._board= new Board();
        this._userManager= new UserManager(_players,users);

    }

    public Player entireGame() throws InvalidMoveException {
        int[] numberOfCardsInTurns = {7,6,5,4,3};
        int howManyCardToDeal = 0;
        do{
            //if someone has a valid turn it continues, if not it ridistribuits the cards
            if(someoneValidTurn()){
                if  (_playerHasValidTurns.get(_indexWithCurrentTurn)) {
                    _userManager.sendUpdateToPlayer(_players.get(_indexWithCurrentTurn),new UpdateDTO(UpdateType.TURN,"Your move"));
                    Move move = null;//TODO
                    playerMove(move);
                }
                else{nextTurns();}
            }
            else {
                removeAndDealNewCards(numberOfCardsInTurns[howManyCardToDeal]);
                // refreshing of the cards
            }

        }while( true/*!board.winningCondition()*/);
        //if someone winns it stops the game
        //return board.winner();
    }

    private boolean checkValidTurns(Move move, Player playerWantToMove){

        if ( move!= null ) {
            if (move.checkIfComplete()){
                if (playerWantToMove == _players.get(_indexWithCurrentTurn)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * sequenz were the player does his move
     * @param move witch move wants do to the player
     * @return  if succesed true
     * @throws InvalidMoveException  if move isn't correct in the form
     */
    public void playerMove(Move move) throws InvalidMoveException {
        Player playerWantToMove=_userManager.getPlayerFromUserToken(move.getToken());
        if (!checkValidTurns(move, playerWantToMove)) {
            throw new InvalidMoveException("Move Not allowed", "Bad move logic");
        }
        _board.makeMove(move);
        _userManager.sendUpdateToAll(new UpdateDTO(UpdateType.TURN,"new turn"));
        ifMoveIsPossible();
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

    public boolean ifMoveIsPossible(){
        //TODO
        for (int i =1; i<4;i++){
            //_playerHasValidTurns=_board.ifMoveIsPossible(_players.get(i));
        }
        return false;
    }

    public void removeAndDealNewCards(int howMany){
        for (Player player: _players){
            player.removeAllCard();
            for (int i = 0; i>howMany;i++) {
                player.addCard(_cardStack.getNextCard());
            }
        }
    }

    private void nextTurns(){
        _indexWithCurrentTurn++;
        if (_indexWithCurrentTurn==4){
            _indexWithCurrentTurn=0;
        };
    }

    public Player getCurrentTurn(){

        return _players.get(_indexWithCurrentTurn);
    }

    public Boolean getPlayerValidTurn(int i){

        // if a player has a valid turn must be checked in UpdateValidTurn()
        boolean valid = _playerHasValidTurns.get(i);
        return valid;
    }

    public Boolean someoneValidTurn(){
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

    public Player getPlayerByToken(String token){
        return _userManager.getPlayerFromUserToken(token);
    }

}
