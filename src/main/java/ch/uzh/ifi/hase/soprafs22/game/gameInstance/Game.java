package ch.uzh.ifi.hase.soprafs22.game.gameInstance;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;

import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;

import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.board.Board;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.CardStack;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.PlayerData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import ch.uzh.ifi.hase.soprafs22.websocket.constant.UpdateType;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.*;

public class Game {
    private int _indexWithCurrentTurn;
    private ArrayList<Boolean> _playersWithValidTurns;
    private ArrayList<Player> _players;
    private CardStack _cardStack;
    private String _gameToken;
    private GameManager _manager;
    private Board _board;
    private UserManager _userManager;
    private int[] _numberOfCardsInTurns = {7,6,5,4,3};
    private int _indexOfHowManyCardToDeal;

    @Autowired
    private UserService _user;

    public Game(ArrayList<User> users){

        Random rand = new Random();
        this._players= new ArrayList<>();
        this._players.add(new Player(COLOR.RED));
        this._players.add(new Player(COLOR.YELLOW));
        this._players.add(new Player(COLOR.GREEN));
        this._players.add(new Player(COLOR.BLUE));
        this._cardStack= new CardStack();
        this._board= new Board();
        this._indexOfHowManyCardToDeal =0;
        this.removeAndDealNewCards();
        this._indexWithCurrentTurn= rand.nextInt(4);
        this._playersWithValidTurns = new ArrayList();
        for (int i = 0; i < 4; i++) {
            _playersWithValidTurns.add(false);
        }
        this.updateValidTurnAllPlayers();
        this._gameToken= UUID.randomUUID().toString();
        this._manager= GameManager.getInstance();

        this._userManager= new UserManager(_players,users);


    }

    public Game(){
        // for test porpuse
    }

    private boolean checkValidTurns(Move move, Player playerWantToMove) {
        if (move.checkIfComplete()) {
            if (playerWantToMove == _players.get(_indexWithCurrentTurn)) {
                return true;
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
        Player playerWantToMove;

        // checks if token exist if not entire check of correctness of move failes
        if (move.getToken()== null){
            throw new InvalidMoveException("Move Not allowed", "Move has no token");
        }

        // catch if token is not null but also not of a user
        try{
            playerWantToMove = _userManager.getPlayerFromUserToken(move.getToken());
        }
        catch (NullPointerException e){
            throw new InvalidMoveException("Move Not allowed", "Bad token");}

        if (!checkValidTurns(move, playerWantToMove)||move.get_color()!=playerWantToMove.getColor()) {
            throw new InvalidMoveException("Move Not allowed", "Bad move logic");
        }

        // check if someone has a valid turn
        updateValidTurnAllPlayers();

        // checks if the player can do something
        if  (_playersWithValidTurns.get(_indexWithCurrentTurn)) {
            //checks if move is logical right
            if (_board.isValidMove(move)) {
                // for every special move it's called an other function move
                if (move.get_fromPos().get(0) == -1) {
                    _board.makeStartingMove(move.get_color());
                }
                else if (move.get_card().getValue() == CARDVALUE.JACK) {
                    _board.makeSwitch(move.get_fromPos().get(0), move.get_toPos().get(0));
                }
                else {
                    _board.makeMove(move);
                }
                //remove card from player hand
                _players.get(_indexWithCurrentTurn).removeCard(move.get_card());
                _userManager.sendUpdateToAll(new UpdateDTO(UpdateType.TURN, "TURN_FINISH"));
                nextTurns();
            }



            //TODO FOR ALL _userManager calls: make sure the message is valid JSON and not just text
            throw new InvalidMoveException("Move Not allowed", "Wrong move logic");

        }

        // check if somebody won
        for (Player player:_players){
            if (_board.checkWinningCondition(player.getColor())) {
                _userManager.sendUpdateToAll(new UpdateDTO(UpdateType.WIN, player.getColor()+" Won"));
                return;
            }
        }


        // deal new cards until someone has a possible move
        while(!someoneValidTurn()) {
            removeAndDealNewCards();
            updateValidTurnAllPlayers();
        }


        _userManager.sendUpdateToAll(new UpdateDTO(UpdateType.CARD, "New Cards"));
        //wait for a moment

        //because the next player with the ability to do something is not strictly the next player it is necessary to loop it through
        int indexNextPlayerTurn=-1;
        int i=_indexWithCurrentTurn;
        do{
            if(_playersWithValidTurns.get(i)==true){
                indexNextPlayerTurn=0;
            }
            else {
                 nextTurns();
            }
        }while(indexNextPlayerTurn!=-1);

        _userManager.sendUpdateToPlayer(_players.get(_indexWithCurrentTurn), new UpdateDTO(UpdateType.TURN, "Your Turn"));
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

    // does this part the same has updateValidTurnAllPlayers()  why do we need a constructor with strin? can't we send the card that we have? from Sandro todo @luca
    private boolean ifMoveIsPossible(Move move){

        for (int i =0; i<4;i++){
            boolean possibleTurn= false;
            for (String card: _players.get(i).getFormattedCards()) {
                //possibleTurn = _board.makePossibleMove(card,_players.get(i).getColor());
                Card c = new Card(CARDVALUE.NINE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS); //todo @shitao: card constructor with string
                _board.isAnyMovePossible(c, _players.get(i).getColor());
            }
            _playersWithValidTurns.set(i,possibleTurn);
        }
        return false;
    }

    private void removeAndDealNewCards(){
        for (Player player: _players){
            player.removeAllCard();
            for (int i = 0; i< _numberOfCardsInTurns[_indexOfHowManyCardToDeal]; i++) {
                player.addCard(_cardStack.getNextCard());
            }
        }
        _indexOfHowManyCardToDeal++;
        if (_indexOfHowManyCardToDeal ==4){
            _indexOfHowManyCardToDeal =0;
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
        boolean valid = _playersWithValidTurns.get(i);
        return valid;
    }

    private Boolean someoneValidTurn(){

        boolean valid = false;
        for (int i = 0; i<4; i++){
            valid = valid || _playersWithValidTurns.get(i);
        }
        return valid;
    }

    private void updateValidTurnAllPlayers(){
        for (int i = 0; i < 4; i++) {
            Player player= _players.get(i);
            boolean possibleMove = false;
            for (int j = 0; j < player.getCardCount(); j++) {
                possibleMove = possibleMove || _board.isAnyMovePossible(player.getCartValueInIndexHand(j), player.getColor());
            }
            _playersWithValidTurns.set(i, possibleMove);
        }
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
