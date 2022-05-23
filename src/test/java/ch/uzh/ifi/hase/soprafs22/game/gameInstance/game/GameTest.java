package ch.uzh.ifi.hase.soprafs22.game.gameInstance.game;


import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.constants.*;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.board.Board;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.board.IBoard;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.board.ValidMove;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardPosition;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.PlayerData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.mocks.*;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.service.GameHistoryService;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import ch.uzh.ifi.hase.soprafs22.springContext.SpringContext;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameTest {

    Game _g;
    MockBoard board;

    ArrayList<User> users;


    @BeforeEach
    void setupGame()
    {
        //mock the update controller in a similar matter
        MockUpdateController mockUpdateController = new MockUpdateController() {
            @Override
            public void sendUpdateToUser(String usertoken, UpdateDTO updateMessage) {
                super.sendUpdateToUser(usertoken, updateMessage);
            }
        };
        users = new ArrayList<>();
        for (int i = 0; i<4; i++){

            User user = new User();
            user.setUsername(RandomString.make(6));
            user.setPassword(RandomString.make(10));
            user.setToken(UUID.randomUUID().toString());
            user.setId((long)(Math.random()*100000));

            users.add(user);
        }


        //make a new mockSpringContext
        MockSpringContext mockSpringContext = new MockSpringContext();

        mockSpringContext.returnForClass(UpdateController.class, mockUpdateController);
        MockUserRepo mockUserRepo = new MockUserRepo() {
            @Override
            public User findByToken(String token) {
                for (User user:users)
                    {if (user.getToken().equals(token)){return user;}
                }
                return null ;
            }
        };

        MockUserService mockUserService = new MockUserService() {};

        mockSpringContext.returnForClass(UserService.class, mockUserService);

        mockSpringContext.returnForClass(UserRepository.class, mockUserRepo);

        //set the spring context object to the mocked one (instead of the default runtime one)
        SpringContext.setSpringContextObject(mockSpringContext);
        GameManager manager= GameManager.getInstance();

        MockGameHistoryService mockGameHistoryService = new MockGameHistoryService() {};

        mockSpringContext.returnForClass(GameHistoryService.class, mockGameHistoryService);

        //make a fake board
         board = new MockBoard() {
             private int countStartingMove = 0;
             private int countJackMove = 0;
             private int countNormalMove = 0;

             @Override
             public boolean isAnyMovePossible(Card card, COLOR col) {
                 return true;
             }

             @Override
             public ValidMove isValidMove(Move move){
                 if (move.get_card().getValue().equals(CARDVALUE.NINE)) {
                     return new ValidMove(false);
                 }
                 return new ValidMove(true);
             }

             @Override
             public void makeStartingMove(COLOR color) {
                 countStartingMove++;
             }

             @Override
             public void makeSwitch(int from, int to) {
                 countJackMove++;
             }

             @Override
             public void makeMove(Move move) {
                 countNormalMove++;

             }
             @Override
             public boolean checkWinningCondition(COLOR color){
                 if (getCountNormalMove()==4){return true;}
                 return false;
             }

             @Override
             public int getCountStartingMove() {
                 return countStartingMove;
             }

             @Override
             public int getCountJackMove() {
                 return countJackMove;
             }

             @Override
             public int getCountNormalMove() {
                 return countNormalMove;
             }

        };
        _g = new Game(users, board);


    }

    @AfterEach
    void clean(){

        //IMPORTANT: RESET THE SPRING CONTEXT FOR THE NEXT TESTS (in different files)

        SpringContext.resetSpringContextObject();
    }


    private Move generateMove(String token, COLOR moveColor, int from, Integer to, boolean fromGoal, boolean toGoal, CARDVALUE cardvalue,CARDTYPE cardtype, CARDSUITE suite, boolean isJoker){
        ArrayList<BoardPosition> _fromPos = new ArrayList<>();
        ArrayList<BoardPosition> _toPos = new ArrayList<>();

        _fromPos.add(new BoardPosition(from, fromGoal));
        if (to!=null) {
            _toPos.add(new BoardPosition(to, toGoal));
        }

        Card _card= new Card(cardvalue, cardtype, suite);


        return new Move(_fromPos,_toPos,_card, token,moveColor, isJoker);
    }

    @Test
    void getGame_TokenTest(){
        assertNotNull(_g.getGameToken());
    }

    @Test
    void getPlayers_Test(){
        assertEquals(COLOR.RED,_g.getPlayerByToken(users.get(0).getToken()).getColor());
        assertEquals(COLOR.YELLOW,_g.getPlayerByToken(users.get(1).getToken()).getColor());
        assertEquals(COLOR.GREEN,_g.getPlayerByToken(users.get(2).getToken()).getColor());
        assertEquals(COLOR.BLUE,_g.getPlayerByToken(users.get(3).getToken()).getColor());
    }

    @Test
    void getCurrentTurnTest(){
        assertNotNull(_g.getCurrentTurn());
    }

    @Test
    void getPlayer_MoveEmptyTest() {

        Move move= new Move();

        InvalidMoveException exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(move));
        assertEquals("NO_TOKEN", exception.getCode());
    }

    @Test
    void playerMove_NullTokenTest() {
        //  setting up for a correct move if not for the token

        Move _move = generateMove(null, COLOR.BLUE, 2,3,false,false,CARDVALUE.FIVE,CARDTYPE.DEFAULT,CARDSUITE.CLUBS,false);
        InvalidMoveException exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(_move));

        assertEquals("NO_TOKEN", exception.getCode());
    }

    @Test
    void playerMove_BadTokenTest() {

        Move  _move= generateMove(UUID.randomUUID().toString(), COLOR.BLUE, 2,3,false,false,CARDVALUE.FIVE,CARDTYPE.DEFAULT,CARDSUITE.CLUBS,false);
        InvalidMoveException exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(_move));

        assertEquals("BAD_TOKEN", exception.getCode());
    }

    @Test
    void playerMove_MoveNotCompleteTest() {
        //  setting up for a correct move if not for _toPos


        COLOR colorOfCurrentPlayerTurn= _g.getCurrentTurn().getColor();
        int indexOfCurrentPlayer;
        if (colorOfCurrentPlayerTurn == COLOR.RED){
            indexOfCurrentPlayer=0;
        }else if (colorOfCurrentPlayerTurn == COLOR.YELLOW){
            indexOfCurrentPlayer=1;
        }else if (colorOfCurrentPlayerTurn == COLOR.GREEN){
            indexOfCurrentPlayer=2;
        }else {
            indexOfCurrentPlayer=3;
        }
        String _token=users.get(indexOfCurrentPlayer).getToken() ;
        Integer noContent = null;

        Move  _move= generateMove(_token, COLOR.BLUE, 2, noContent,false,false,CARDVALUE.FIVE,CARDTYPE.DEFAULT,CARDSUITE.CLUBS,false);

        InvalidMoveException exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(_move));
        assertEquals("WRONG_COLOR_TURN", exception.getCode());
    }


    @Test
    void playerMove_PlayerToMoveWrong() {

        COLOR colorOfCurrentPlayerTurn= _g.getCurrentTurn().getColor();
        int indexOfCurrentPlayer;
        if (colorOfCurrentPlayerTurn == COLOR.RED){
            indexOfCurrentPlayer=1;
        }else if (colorOfCurrentPlayerTurn == COLOR.BLUE){
            indexOfCurrentPlayer=2;
        }else if (colorOfCurrentPlayerTurn == COLOR.GREEN){
            indexOfCurrentPlayer=3;
        }else {
            indexOfCurrentPlayer=0;
        }
        String _token=users.get(indexOfCurrentPlayer).getToken() ;



        Move  _move= generateMove(_token, COLOR.BLUE, 2,3,false,false,CARDVALUE.NINE,CARDTYPE.DEFAULT,CARDSUITE.CLUBS,false);

        InvalidMoveException exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(_move));
        assertEquals("WRONG_COLOR_TURN", exception.getCode());
    }

    @Test
    void playerMove_WrongMarbelColor() {

        COLOR colorOfCurrentPlayerTurn= _g.getCurrentTurn().getColor();
        int indexOfCurrentPlayer;
        if (colorOfCurrentPlayerTurn == COLOR.RED){
            indexOfCurrentPlayer=0;
        }else if (colorOfCurrentPlayerTurn == COLOR.YELLOW){
            indexOfCurrentPlayer=1;
        }else if (colorOfCurrentPlayerTurn == COLOR.GREEN){
            indexOfCurrentPlayer=2;
        }else {
            indexOfCurrentPlayer=3;
        }
        String _token=users.get(indexOfCurrentPlayer).getToken() ;

        // the marble  has not the same color has the player that has to play
        COLOR moveColor;
        if (colorOfCurrentPlayerTurn == COLOR.RED){
            moveColor= COLOR.BLUE;
        }else {moveColor= COLOR.RED;}


        Move  _move= generateMove(_token, moveColor, 2,3,false,false,CARDVALUE.FIVE,CARDTYPE.DEFAULT,CARDSUITE.CLUBS,false);
        InvalidMoveException exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(_move));
        assertEquals("WRONG_COLOR_MARBLE", exception.getCode());
    }

    @Test
    void   playerMove_IsNotValidForThisUser()  {
        Player playerWithCurrentTurn= _g.getCurrentTurn();
        COLOR colorOfCurrentPlayerTurn= playerWithCurrentTurn.getColor();
        int indexOfCurrentPlayer;
        if (colorOfCurrentPlayerTurn == COLOR.RED){
            indexOfCurrentPlayer=0;
        }else if (colorOfCurrentPlayerTurn == COLOR.YELLOW){
            indexOfCurrentPlayer=1;
        }else if (colorOfCurrentPlayerTurn == COLOR.GREEN){
            indexOfCurrentPlayer=2;
        }else {
            indexOfCurrentPlayer=3;
        }
        String _token=users.get(indexOfCurrentPlayer).getToken() ;

        playerWithCurrentTurn.addCard(new Card(CARDVALUE.NINE,CARDTYPE.DEFAULT,CARDSUITE.CLUBS));

        Move  _move= generateMove(_token, colorOfCurrentPlayerTurn, 2,3,false,false,CARDVALUE.NINE,CARDTYPE.DEFAULT,CARDSUITE.CLUBS,false);


        InvalidMoveException exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(_move));
        assertEquals("RULE_VIOLATION", exception.getCode());

    }

    @Test
    void   playerMove_MakeStartingMove(){
        Player withCurrentTurn= _g.getCurrentTurn();
        COLOR colorOfCurrentPlayerTurn= withCurrentTurn.getColor();
        int indexOfCurrentPlayer;
        if (colorOfCurrentPlayerTurn == COLOR.RED){
            indexOfCurrentPlayer=0;
        }else if (colorOfCurrentPlayerTurn == COLOR.YELLOW){
            indexOfCurrentPlayer=1;
        }else if (colorOfCurrentPlayerTurn == COLOR.GREEN){
            indexOfCurrentPlayer=2;
        }else {
            indexOfCurrentPlayer=3;
        }
        String _token=users.get(indexOfCurrentPlayer).getToken() ;

        Card firstCard;
        int i = 0;
        do {
           firstCard=withCurrentTurn.getCartValueInIndexHand(i);
           i++;
        }while(firstCard.getValue()== CARDVALUE.NINE);


        Move  _move= generateMove(_token, colorOfCurrentPlayerTurn, -1,3,false,false,firstCard.getValue(),firstCard.getType(),firstCard.getSuite(),false);
        int numberCards= withCurrentTurn.getCardCount();
        try {
            _g.playerMove(_move);
        }catch (InvalidMoveException e){
            fail();
        }
        assertEquals (numberCards-1 , withCurrentTurn.getCardCount());
        assertEquals(1,board.getCountStartingMove());

    }

    @Test
    void   playerMove_MakeSwitchMove(){
        Player playerWithCurrentTurn= _g.getCurrentTurn();
        COLOR colorOfCurrentPlayerTurn= playerWithCurrentTurn.getColor();
        int indexOfCurrentPlayer;
        if (colorOfCurrentPlayerTurn == COLOR.RED){
            indexOfCurrentPlayer=0;
        }else if (colorOfCurrentPlayerTurn == COLOR.YELLOW){
            indexOfCurrentPlayer=1;
        }else if (colorOfCurrentPlayerTurn == COLOR.GREEN){
            indexOfCurrentPlayer=2;
        }else {
            indexOfCurrentPlayer=3;
        }
        String _token=users.get(indexOfCurrentPlayer).getToken() ;

        playerWithCurrentTurn.addCard(new Card(CARDVALUE.JACK,CARDTYPE.DEFAULT,CARDSUITE.CLUBS));

        Card firstCard=playerWithCurrentTurn.getCartValueInIndexHand(playerWithCurrentTurn.getCardCount()-1);

        Move  _move= generateMove(_token, colorOfCurrentPlayerTurn, 2,3,false,false,firstCard.getValue(),firstCard.getType(),firstCard.getSuite(),false);
        int numberCards= playerWithCurrentTurn.getCardCount();
        try {
            _g.playerMove(_move);
        }catch (InvalidMoveException e){
            fail();
        }
        assertEquals (numberCards-1 , playerWithCurrentTurn.getCardCount());
        assertEquals(1,board.getCountJackMove());

    }

    @Test
    void   playerMove_MakeNormalMove(){

        Player playerWithCurrentTurn= _g.getCurrentTurn();
        COLOR colorOfCurrentPlayerTurn= playerWithCurrentTurn.getColor();
        int indexOfCurrentPlayer;
        if (colorOfCurrentPlayerTurn == COLOR.RED){
            indexOfCurrentPlayer=0;
        }else if (colorOfCurrentPlayerTurn == COLOR.YELLOW){
            indexOfCurrentPlayer=1;
        }else if (colorOfCurrentPlayerTurn == COLOR.GREEN){
            indexOfCurrentPlayer=2;
        }else {
            indexOfCurrentPlayer=3;
        }
        String _token=users.get(indexOfCurrentPlayer).getToken() ;

        playerWithCurrentTurn.addCard(new Card(CARDVALUE.TWO,CARDTYPE.DEFAULT,CARDSUITE.CLUBS));

        Card firstCard=playerWithCurrentTurn.getCartValueInIndexHand(playerWithCurrentTurn.getCardCount()-1);

        Move  _move= generateMove(_token, colorOfCurrentPlayerTurn, 2,3,false,false,firstCard.getValue(),firstCard.getType(),firstCard.getSuite(),false);
        int numberCards= playerWithCurrentTurn.getCardCount();
        try {
            _g.playerMove(_move);
        }catch (InvalidMoveException e){
            fail();
        }
        assertEquals (numberCards-1 , playerWithCurrentTurn.getCardCount());
        assertEquals(1,board.getCountNormalMove());
    }

    @Test
    void   playerMove_MakeNormalMoveWithJoker(){

        Player playerWithCurrentTurn= _g.getCurrentTurn();
        COLOR colorOfCurrentPlayerTurn= playerWithCurrentTurn.getColor();
        int indexOfCurrentPlayer;
        if (colorOfCurrentPlayerTurn == COLOR.RED){
            indexOfCurrentPlayer=0;
        }else if (colorOfCurrentPlayerTurn == COLOR.YELLOW){
            indexOfCurrentPlayer=1;
        }else if (colorOfCurrentPlayerTurn == COLOR.GREEN){
            indexOfCurrentPlayer=2;
        }else {
            indexOfCurrentPlayer=3;
        }
        String _token=users.get(indexOfCurrentPlayer).getToken() ;

        playerWithCurrentTurn.addCard(new Card(CARDVALUE.JOKER,CARDTYPE.JOKER,null));

        Card firstCard=playerWithCurrentTurn.getCartValueInIndexHand(playerWithCurrentTurn.getCardCount()-1);

        Move  _move= generateMove(_token, colorOfCurrentPlayerTurn, 2,3,false,false,firstCard.getValue(),firstCard.getType(),firstCard.getSuite(),true);
        int numberCards= playerWithCurrentTurn.getCardCount();
        try {
            _g.playerMove(_move);
        }catch (InvalidMoveException e){
            fail();
        }
        assertEquals (numberCards-1 , playerWithCurrentTurn.getCardCount());
        assertEquals(1,board.getCountNormalMove());
    }

    @Test
    void   playerMove_MakeNormalWithNoCardInHand(){

        Player playerWithCurrentTurn= _g.getCurrentTurn();
        COLOR colorOfCurrentPlayerTurn= playerWithCurrentTurn.getColor();
        int indexOfCurrentPlayer;
        if (colorOfCurrentPlayerTurn == COLOR.RED){
            indexOfCurrentPlayer=0;
        }else if (colorOfCurrentPlayerTurn == COLOR.YELLOW){
            indexOfCurrentPlayer=1;
        }else if (colorOfCurrentPlayerTurn == COLOR.GREEN){
            indexOfCurrentPlayer=2;
        }else {
            indexOfCurrentPlayer=3;
        }
        String _token=users.get(indexOfCurrentPlayer).getToken() ;

        playerWithCurrentTurn.addCard(new Card(CARDVALUE.JOKER,CARDTYPE.JOKER,null));


        Move  _move= generateMove(_token, colorOfCurrentPlayerTurn, 2,3,false,false,CARDVALUE.TWO,CARDTYPE.DEFAULT,CARDSUITE.CLUBS,false);
        int numberCards= playerWithCurrentTurn.getCardCount();

        InvalidMoveException exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(_move));
        assertEquals("WRONG_CARD", exception.getCode());

    }

    @Test
    void playerWin(){

        // this test checks if the winning condition are meet and if the game is then deleted
        // the winningcondition are actual mocked because and are: if a player does more the 4 of a normal move it gives true back
        
        GameManager gameManager= GameManager.getInstance();
        gameManager.addGame(_g);

        assertEquals(_g,gameManager.getGameByToken(_g.getGameToken()));

        Player playerWithCurrentTurn= _g.getCurrentTurn();
        COLOR colorOfCurrentPlayerTurn= playerWithCurrentTurn.getColor();

        String _token=users.get(2).getToken() ;

        playerWithCurrentTurn.addCard(new Card(CARDVALUE.TWO,CARDTYPE.DEFAULT,CARDSUITE.CLUBS));

        Card firstCard=playerWithCurrentTurn.getCartValueInIndexHand(playerWithCurrentTurn.getCardCount()-1);


        Move  _move= generateMove(_token, colorOfCurrentPlayerTurn, 0, 30,false,false,firstCard.getValue(),firstCard.getType(),firstCard.getSuite(),false);
        try{
            board.makeMove(_move);
        } catch (InvalidMoveException e) {
            fail();
        }

        try{
            board.makeMove(_move);
        } catch (InvalidMoveException e) {
            fail();
        }

        try{
            board.makeMove(_move);
        } catch (InvalidMoveException e) {
            fail();
        }

        try {
            _g.playerMove(_move);
        }catch (InvalidMoveException e){
            fail();
        }

        assertNull(gameManager.getGameByToken(_g.getGameToken()));

    }

    @Test
    void gameStateTest(){

        Game game= new Game(users);
        ArrayList<String> actualBoard;
        ArrayList<String> actualgreen;
        ArrayList<String> actualred;
        ArrayList<String> actualyellow;
        ArrayList<String> actualblue;

        actualBoard = new ArrayList<>();
        actualgreen = new ArrayList<>();
        actualred = new ArrayList<>();
        actualyellow = new ArrayList<>();
        actualblue = new ArrayList<>();

        while(actualBoard.size() < 64) actualBoard.add("NONE");
        while(actualgreen.size() < 4) actualgreen.add("NONE");
        while(actualred.size() < 4) actualred.add("NONE");
        while(actualyellow.size() < 4) actualyellow.add("NONE");
        while(actualblue.size() < 4) actualblue.add("NONE");

        //get after creation.
        BoardData bd = game.gameState();

        BoardData expected = new BoardData(actualBoard, actualred, actualgreen, actualblue, actualyellow, 4, 4, 4, 4);

        Map<Long, COLOR> cMap = new HashMap<>();
        ArrayList<COLOR> cols = new ArrayList<>(Arrays.asList(COLOR.RED, COLOR.YELLOW, COLOR.GREEN, COLOR.BLUE));



        assertEquals(expected.getBoard(), bd.getBoard());
        assertEquals(expected.getBlueGoal(), bd.getBlueGoal());
        assertEquals(expected.getGreenGoal(), bd.getGreenGoal());
        assertEquals(expected.getRedGoal(), bd.getRedGoal());
        assertEquals(expected.getYellowGoal(), bd.getYellowGoal());
        assertEquals(expected.getGreenBase(), bd.getGreenBase());
        assertEquals(expected.getBlueBase(), bd.getBlueBase());
        assertEquals(expected.getYellowBase(), bd.getYellowBase());
        assertEquals(expected.getRedBase(), bd.getRedBase());


    }

    @Test
    void getPlayerStatesWrongTokenTest(){

        PlayerData pd= _g.getPlayerStates("1");
        assertEquals(null,pd);

    }
    @Test
    void getPlayerStatesCheckRightCardsTest(){

        PlayerData pd0= _g.getPlayerStates(users.get(0).getToken());
        ArrayList<String> actualCards0=_g.getPlayers().get(0).getFormattedCards();
        PlayerData pd1= _g.getPlayerStates(users.get(1).getToken());
        ArrayList<String> actualCards1=_g.getPlayers().get(1).getFormattedCards();
        PlayerData pd2= _g.getPlayerStates(users.get(2).getToken());
        ArrayList<String> actualCards2=_g.getPlayers().get(2).getFormattedCards();
        PlayerData pd3= _g.getPlayerStates(users.get(3).getToken());
        ArrayList<String> actualCards3=_g.getPlayers().get(3).getFormattedCards();
        assertEquals(actualCards0,pd0.getVisibleCards());
        assertEquals(actualCards1,pd1.getVisibleCards());
        assertEquals(actualCards2,pd2.getVisibleCards());
        assertEquals(actualCards3,pd3.getVisibleCards());
        ArrayList<Integer> countOfCards= new ArrayList<>();
        countOfCards.add(actualCards0.size());
        countOfCards.add(actualCards1.size());
        countOfCards.add(actualCards2.size());
        countOfCards.add(actualCards3.size());

        assertEquals(countOfCards.get(1),pd0.getHiddenCardCount().get(0));
        assertEquals(countOfCards.get(2),pd0.getHiddenCardCount().get(1));
        assertEquals(countOfCards.get(3),pd0.getHiddenCardCount().get(2));
        System.out.println(pd0.getHiddenCardCount());
        System.out.println(countOfCards);


    }




}