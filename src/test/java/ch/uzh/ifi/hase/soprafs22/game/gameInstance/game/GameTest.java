package ch.uzh.ifi.hase.soprafs22.game.gameInstance.game;


import ch.uzh.ifi.hase.soprafs22.game.GameManager;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.Game;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.UserService;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameTest {

    Game _g;

    ArrayList<User> users;


    @Autowired
    private UserService userService;

    @BeforeEach
    void setupGame()
    {
        GameManager manager= GameManager.getInstance();
        users = new ArrayList<>();

        for (int i = 0; i<4; i++){

            User user = new User();
            user.setUsername(RandomString.make(6));
            user.setPassword(RandomString.make(10));
            user = userService.createUser(user);

            users.add(user);
        }
        _g = new Game(users);


    }


    @Test
    void getGameTokenTest(){
        assertNotNull(_g.getGameToken());
    }

    @Test
    void getPlayersTest(){
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
    void getPlayerMoveEmptyTest() {

        Move move= new Move();

        Throwable exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(move));
        assertEquals("Move has no token", exception.getMessage());
    }

    @Test
    void playerMoveNullTokenTest() {
        //  setting up for a correct move if not for the token
        ArrayList<Integer> _fromPos = new ArrayList<>();
        ArrayList<Integer> _toPos = new ArrayList<>();
        ArrayList<Boolean> _fromPosInGoal = new ArrayList<>();
        ArrayList<Boolean> _toPosInGoal = new ArrayList<>();

        _fromPos.add(2);
        _toPos.add(3);
        _fromPosInGoal.add(false);
        _toPosInGoal.add(false);
        COLOR movecolor= COLOR.GREEN;

        Card _card= new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);

        String _token=null ;
        //

        Move  _move= new Move(_fromPos,_toPos,_fromPosInGoal,_toPosInGoal,_card,_token,movecolor);

        Throwable exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(_move));
        assertEquals("Move has no token", exception.getMessage());
    }

    @Test
    void playerMoveBadTokenTest() {
        //  setting up for a correct move if not for the token
        ArrayList<Integer> _fromPos = new ArrayList<>();
        ArrayList<Integer> _toPos = new ArrayList<>();
        ArrayList<Boolean> _fromPosInGoal = new ArrayList<>();
        ArrayList<Boolean> _toPosInGoal = new ArrayList<>();
        _fromPos.add(2);
        _toPos.add(3);
        _fromPosInGoal.add(false);
        _toPosInGoal.add(false);
        COLOR movecolor= COLOR.GREEN;

        Card _card= new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
        String _token=UUID.randomUUID().toString();


        Move  _move= new Move(_fromPos,_toPos,_fromPosInGoal,_toPosInGoal,_card,_token,movecolor);

        Throwable exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(_move));
        assertEquals("Bad token", exception.getMessage());
    }

    @Test
    void playerMoveMoveNotCompleteTest() {
        //  setting up for a correct move if not for _toPos

        ArrayList<Integer> _fromPos = new ArrayList<>();
        ArrayList<Integer> _toPos = new ArrayList<>();
        ArrayList<Boolean> _fromPosInGoal = new ArrayList<>();
        ArrayList<Boolean> _toPosInGoal = new ArrayList<>();
        _fromPos.add(2);
        _fromPosInGoal.add(false);
        _toPosInGoal.add(false);
        COLOR movecolor= COLOR.GREEN;

        //gets _token from user that is using
        Card _card= new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
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

        Move  _move= new Move(_fromPos,_toPos,_fromPosInGoal,_toPosInGoal,_card,_token,movecolor);

        Throwable exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(_move));
        assertEquals("Bad move logic", exception.getMessage());
    }


    @Test
    void playerMovePlayerToMoveWrongTest() {
        //  setting up for a correct move if not for the token
        ArrayList<Integer> _fromPos = new ArrayList<>();
        ArrayList<Integer> _toPos = new ArrayList<>();
        ArrayList<Boolean> _fromPosInGoal = new ArrayList<>();
        ArrayList<Boolean> _toPosInGoal = new ArrayList<>();
        _fromPos.add(2);
        _toPos.add(3);
        _fromPosInGoal.add(false);
        _toPosInGoal.add(false);
        COLOR movecolor= COLOR.GREEN;

        //gets _token from user that is using
        Card _card= new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
        COLOR colorOfCurrentPlayerTurn= _g.getCurrentTurn().getColor();
        int indexOfCurrentPlayer;
        if (colorOfCurrentPlayerTurn == COLOR.RED){
            indexOfCurrentPlayer=1;
        }else if (colorOfCurrentPlayerTurn == COLOR.YELLOW){
            indexOfCurrentPlayer=2;
        }else if (colorOfCurrentPlayerTurn == COLOR.GREEN){
            indexOfCurrentPlayer=3;
        }else {
            indexOfCurrentPlayer=0;
        }
        String _token=users.get(indexOfCurrentPlayer).getToken() ;



        Move  _move= new Move(_fromPos,_toPos,_fromPosInGoal,_toPosInGoal,_card,_token,movecolor);

        Throwable exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(_move));
        assertEquals("Bad move logic", exception.getMessage());
    }

    @Test
    void playerMoveWrongMarbelColorTest() {
        //  setting up for a correct move if not for the token
        ArrayList<Integer> _fromPos = new ArrayList<>();
        ArrayList<Integer> _toPos = new ArrayList<>();
        ArrayList<Boolean> _fromPosInGoal = new ArrayList<>();
        ArrayList<Boolean> _toPosInGoal = new ArrayList<>();
        _fromPos.add(2);
        _toPos.add(3);
        _fromPosInGoal.add(false);
        _toPosInGoal.add(false);

        //gets _token from user that is using
        Card _card= new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
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


        Move  _move= new Move(_fromPos,_toPos,_fromPosInGoal,_toPosInGoal,_card,_token,moveColor);

        Throwable exception = assertThrows(InvalidMoveException.class, () ->_g.playerMove(_move));
        assertEquals("Bad move logic", exception.getMessage());
    }

    //@Test
    void playerCallingMoveHasNotAValidMove() {
        //  setting up for a correct move if not for the token
        ArrayList<Integer> _fromPos = new ArrayList<>();
        ArrayList<Integer> _toPos = new ArrayList<>();
        ArrayList<Boolean> _fromPosInGoal = new ArrayList<>();
        ArrayList<Boolean> _toPosInGoal = new ArrayList<>();
        _fromPos.add(2);
        _toPos.add(3);
        _fromPosInGoal.add(false);
        _toPosInGoal.add(false);

        //gets _token from user that is using
        Card _card= new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
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


        Move  _move= new Move(_fromPos,_toPos,_fromPosInGoal,_toPosInGoal,_card,_token,colorOfCurrentPlayerTurn);
        Player playerNotAbleToMove = _g.getCurrentTurn();
        _g.getCurrentTurn().removeAllCard();
        //gets 1 player without cards so he can't move

        try{_g.playerMove(_move);}
        catch (InvalidMoveException e){
        };
        for (Player player: _g.getPlayers()){
            assertNotEquals(playerNotAbleToMove,_g.getCurrentTurn());
            // most of the time it is a new player that has to move because the
        }
    }

    void   playerMoveIsNotValidForThisUSer(){

    }


    void   playerMoveMakeStartingMove(){

    }


    void   playerMoveMakeSwitchMove(){

    }

    void   playerMoveMakeNormalMove(){

    }





}