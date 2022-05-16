package ch.uzh.ifi.hase.soprafs22.game.gameInstance;

import ch.uzh.ifi.hase.soprafs22.game.GameManager;

import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;

import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.board.Board;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.board.IBoard;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.board.ValidMove;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.CardStack;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardPosition;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.PlayerData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.rest.data.dto.PossibleMovesGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.IUserService;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import ch.uzh.ifi.hase.soprafs22.springContext.SpringContext;
import ch.uzh.ifi.hase.soprafs22.voicechat.VoiceChatCreator;
import ch.uzh.ifi.hase.soprafs22.websocket.constant.UpdateType;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.*;

public class Game {
    private int _indexWithCurrentTurn;
    private ArrayList<Boolean> _playersWithValidTurns;
    private ArrayList<Player> _players;
    private CardStack _cardStack;
    private String _gameToken;
    private GameManager _manager;
    private IBoard _board;
    private UserManager _userManager;
    private int[] _numberOfCardsInTurns = {6,5,4,3,2};
    private int _indexOfHowManyCardToDeal;
    private final long creationTime = new Date().getTime();

    private Card _lastPlayedCard;

    private IUserService _userService = SpringContext.getBean(UserService.class);

    public Game(ArrayList<User> users){
        setup(users);
        makeFirstMoveValid();
    }

    public long getCreationTime()
    {
        return creationTime;
    }

    private void setup(ArrayList<User> users){
        this._players= new ArrayList<>();
        this._players.add(new Player(COLOR.RED));
        this._players.add(new Player(COLOR.YELLOW));
        this._players.add(new Player(COLOR.GREEN));
        this._players.add(new Player(COLOR.BLUE));
        this._cardStack= new CardStack();
        this._board= new Board();
        this._indexOfHowManyCardToDeal =0;
        this.removeAndDealNewCards();
        this._indexWithCurrentTurn= 2;
        this._playersWithValidTurns = new ArrayList();
        for (int i = 0; i < 4; i++) {
            _playersWithValidTurns.add(false);
        }
        this.updateValidTurnAllPlayers();
        this._gameToken= UUID.randomUUID().toString();
        this._manager= GameManager.getInstance();

        this._userManager= new UserManager(_players, users);
    }

    private void makeFirstMoveValid()
    {
        //play some invalid move. like this we update the turns etc.
        // check if someone has a valid turn
        updateValidTurnAllPlayers();

        //TODO this is cheating, as green will now always have a valid starting turn, but who cares.

        // deal new cards until someone has a possible move, worst case.
        while(!_playersWithValidTurns.get(_indexWithCurrentTurn)) {
            removeAndDealNewCards();
            updateValidTurnAllPlayers();
        }

        removeInvalidTurnCards();
    }

    public Game(ArrayList<User> users, IBoard boardObj)
    {
        setup(users);
        this._board = boardObj;
        makeFirstMoveValid();
    }

    public Game(){
        // for test purpose
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
            throw new InvalidMoveException("NO_TOKEN", "No token");
        }

        // catch if token is not null but also not of a user
        try{
            playerWantToMove = _userManager.getPlayerFromUserToken(move.getToken());
        }
        catch (NullPointerException e){
            throw new InvalidMoveException("BAD_TOKEN", "Bad token");}

        if (!checkValidTurns(move, playerWantToMove)) {
            throw new InvalidMoveException("WRONG_COLOR_TURN", "Current turn is not this color");
        }

        if (move.get_color()!=playerWantToMove.getColor()) {
            throw new InvalidMoveException("WRONG_COLOR_MARBLE", "You want to move the wrong marble color");
        }


        if (!playerWantToMove.isCardAvailable(move.get_card()) && !(move.get_isJoker() && playerWantToMove.isCardAvailable(new Card("Joker")))){
            throw new InvalidMoveException("WRONG_CARD", "Selected card is not in hand");
        }

        // checks if the player can do something
        if  (_playersWithValidTurns.get(_indexWithCurrentTurn)) {
            //checks if move is logical right
            ValidMove validMove = _board.isValidMove(move);
            if (validMove.getValid()) {
                if (move.get_fromPos().get(0).getIndex() == -1) {
                    _board.makeStartingMove(move.get_color());
                }
                else if (move.get_card().getValue() == CARDVALUE.JACK) {
                    _board.makeSwitch(move.get_fromPos().get(0).getIndex(), move.get_toPos().get(0).getIndex());
                }
                else {
                    _board.makeMove(move);
                }
                // last played card &
                //remove card from player hand
                if(move.get_isJoker())
                {
                    //the sent card is a pseudocard. we should actually remove the joker
                    _lastPlayedCard = new Card("Joker");
                    _players.get(_indexWithCurrentTurn).removeCard(new Card("Joker"));
                }else
                {
                    //just remove the card
                    _lastPlayedCard = move.get_card();
                    _players.get(_indexWithCurrentTurn).removeCard(move.get_card());
                }
                _userManager.sendUpdateToAll(new UpdateDTO(UpdateType.BOARD,""));

            }
            else {
                throw new InvalidMoveException("RULE_VIOLATION", validMove.getError());
            }
        }else
        {
            //should not happen
            throw new InvalidMoveException("UNEXPECTED", "Unexpected: You cannot move");
        }

        //if we reach this, a valid move was done


        // check if somebody won
        for (Player player:_players){
            if (_board.checkWinningCondition(player.getColor())) {
                _userService.addWins(_userManager.getUserFromPlayer(player));
                for (Player playerGoal:_players){
                    int numberOfMarbleInGoals=  _board.getNumberInBase(playerGoal.getColor());
                   _userService.addNumberInGoal(_userManager.getUserFromPlayer(playerGoal), numberOfMarbleInGoals);
                }

                _userManager.sendUpdateToAll(new UpdateDTO(UpdateType.WIN, String.format("{\"win\": \"%s\"}", player.getColor())));
                VoiceChatCreator.getInstance().destroyRoomWithPlayers(_gameToken);
                _manager.deleteGame(_gameToken);
                return;
            }
        }


        // deal new cards until someone has a possible move
        updateValidTurnAllPlayers();
        while(!someoneValidTurn()) {
            removeAndDealNewCards();
            updateValidTurnAllPlayers();
        }

        _userManager.sendUpdateToAll(new UpdateDTO(UpdateType.CARD, ""));
        //Todo wait for a moment here or in the front end?

        //because the next player with the ability to do something is not strictly the next player it is necessary to loop it through

        do{
           nextTurns();
            //remove hand from this one if it cannot move
            removeInvalidTurnCards();
        }while(!_playersWithValidTurns.get(_indexWithCurrentTurn));

        _userManager.sendUpdateToAll(new UpdateDTO(UpdateType.CARD,""));

        _userManager.sendUpdateToAll(new UpdateDTO(UpdateType.TURN, String.format("{\"turn\": \"%s\"}", _players.get(_indexWithCurrentTurn).getColor())));
    }

    /**
     * Gets the games board state.
     */
    public BoardData gameState(){
        BoardData bd =  _board.getFormattedBoardState();

        //the color mapping of the users
        Map<Long, COLOR> cMap = new HashMap<>();
        ArrayList<COLOR> cols = new ArrayList<>(Arrays.asList(COLOR.RED, COLOR.YELLOW, COLOR.GREEN, COLOR.BLUE));

        for (Player p: _players) {
            User u = _userManager.getUserFromPlayer(p);
            cMap.put(u.getId(), cols.remove(0));
        }
        bd.setColorMapping(cMap);
        bd.setLastPlayedCard(_lastPlayedCard != null ? _lastPlayedCard.getFormatted() : null);
        return bd;
    }

    public PlayerData getPlayerStates(String pointOfView){

        PlayerData pd = new PlayerData();

        ArrayList<Integer> hiddenCards = new ArrayList<>();

        boolean validPOV = false;

        int correctStartIndex = -1;


        for (Player p : _players) {

            User u = _userManager.getUserFromPlayer(p);

            if(Objects.equals(u.getToken(), pointOfView))
            {
                //we can see our own cards
                ArrayList<String> cards = p.getFormattedCards();
                pd.setVisibleCards(cards);
                validPOV = true;
                correctStartIndex = hiddenCards.size();
                hiddenCards.add(p.getCardCount());

            }else
            {
                int count = p.getCardCount();
                hiddenCards.add(count);
            }

        }

        List<Integer> correctHiddenCards = new ArrayList<>();

        //fix hidden cards split
        if((correctStartIndex + 1) < 4)
        {
            correctHiddenCards = hiddenCards.subList(correctStartIndex + 1, 4);
        }
        //add rest
        for (int i = 0; i < correctStartIndex; i++) {
            correctHiddenCards.add(hiddenCards.get(i));
        }

        //fix dir
        Collections.reverse(correctHiddenCards);

        pd.setHiddenCardCount(new ArrayList<>(correctHiddenCards));

        if(!validPOV)
        {
            //the pointOfView token was not valid for any of the players.
            return null;
        }

        return pd;
    }

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

    public List<BoardPosition> getPossibleMoves(BoardPosition bp, Card card, COLOR color) {
        return _board.whatMovesPossible(bp, card, color);
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

    private void removeInvalidTurnCards()
    {
/*        for (int i = 0; i < 4; i++) {
            if(_playersWithValidTurns.get(i) == false){
                Player player= _players.get(i);
                player.removeAllCard();
                _userManager.sendUpdateToAll(new UpdateDTO(UpdateType.CARD,""));
            }
        }*/

        if(_playersWithValidTurns.get(_indexWithCurrentTurn) == false)
        {
            Player player = _players.get(_indexWithCurrentTurn);
            player.removeAllCard();
        }
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

    // needs to be passed to gamemanager in order to delete the players from the game
    public UserManager getUserManager(){
        return _userManager;
    }
}
