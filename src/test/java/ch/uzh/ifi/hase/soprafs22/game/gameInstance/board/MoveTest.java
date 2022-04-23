package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.*;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.MoveBlockedByMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.NoMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.MARBLE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class MoveTest {

    private Board board;
    private Move move;
    private ArrayList<Integer> fromPos;
    private ArrayList<Integer> toPos;
    private ArrayList<Boolean> fromPosInGoal;
    private ArrayList<Boolean> toPosInGoal;

    private Card _card;
    private COLOR _color;
    

    @BeforeEach
    void setup(){
        board = new Board();
    }

    @Test
    void moveComplete(){
        fromPos = new ArrayList<>(Arrays.asList(0));
        toPos = new ArrayList<>(Arrays.asList(1));
        fromPosInGoal = new ArrayList<>(Arrays.asList(false));
        toPosInGoal = new ArrayList<>(Arrays.asList(false));
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;
        move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);

        assertTrue(move.isWellFormed());
        assertTrue(move.checkIfComplete());

        fromPos.remove(0);
        move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
        assertFalse(move.isWellFormed());
        assertFalse(move.checkIfComplete());

        fromPos.add(0);
        _color = null;
        move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
        assertTrue(move.isWellFormed());
        assertFalse(move.checkIfComplete());

        _color = COLOR.RED;
        fromPos.remove(0);
        toPos.remove(0);
        fromPosInGoal.remove(0);
        toPosInGoal.remove(0);
        move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
        assertTrue(move.isWellFormed());
        assertFalse(move.checkIfComplete());
    }

    // Testing ace moves
    @Test
    void validAceMove(){
        fromPos = new ArrayList<>(Arrays.asList(-1));
        toPos = new ArrayList<>(Arrays.asList(0));
        fromPosInGoal = new ArrayList<>(Arrays.asList(false));
        toPosInGoal = new ArrayList<>(Arrays.asList(false));
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;
        move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);

        // test starting move
        try{
            assertTrue(board.isValidMove(move));
        }catch(InvalidMoveException e){
            fail("Should not throw exception");
        }

        // test normal moves without goal
        try{
            // test for 1
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(1));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeStartingMove(COLOR.RED);
            assertTrue(board.isValidMove(move));

            //test for 11
            toPos = new ArrayList<>(Arrays.asList(11));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        }catch(InvalidMoveException e){
            fail("Should not throw exception");
        }

        // test normal moves toPos in goal
        try{
            // test for 1
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(0));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 11
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(56));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); //move marble to right position

            fromPos = new ArrayList<>(Arrays.asList(56));
            toPos = new ArrayList<>(Arrays.asList(2));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        }catch(InvalidMoveException e){
            fail("Should not throw exception");
        }

        // test nromal moves fromPos and toPos in goal
        try{
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(1));
            fromPosInGoal = new ArrayList<>(Arrays.asList(true));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        }catch(InvalidMoveException e){
            fail("Should not throw exception");
        }

    }

    @Test
    void invalidAceMove(){
        //TODO
        // test invalid starting move
        fromPos = new ArrayList<>(Arrays.asList(-1));
        toPos = new ArrayList<>(Arrays.asList(1));
        fromPosInGoal = new ArrayList<>(Arrays.asList(false));
        toPosInGoal = new ArrayList<>(Arrays.asList(false));
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;
        move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);

        // no intersect
        try{
            assertFalse(board.isValidMove(move));
        }catch(InvalidMoveException e){
            fail("Should not throw exception");
        }

        // wrong intersect for color
        toPos = new ArrayList<>(Arrays.asList(16));
        move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
        try{
            assertFalse(board.isValidMove(move));
        }catch(InvalidMoveException e){
            fail("Should not throw exception");
        }

        // test regular move distance
        fromPos = new ArrayList<>(Arrays.asList(0));
        toPos = new ArrayList<>(Arrays.asList(0));
        move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
        try{
            // move distance too low
            board.makeStartingMove(COLOR.RED);
            assertFalse(board.isValidMove(move));
            // move distance too high
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(15));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertFalse(board.isValidMove(move));
        }catch(InvalidMoveException e){
            fail("Should not throw exception");
        }

        // test regular moves in goal
        // 

    }

    @Test
    void startAceMove(){
        //TODO
    }

    @Test
    void goalAceMove(){
        //TODO
    }

    // test king moves
    @Test
    void validKingMove(){
        //TODO
    }

    @Test
    void invalidKingMove(){
        //TODO
    }

    @Test
    void startKingMove(){

    }

    @Test
    void goalKingMove(){

    }

    // testing joker moves
    // behaviour unclear yet, therefore nothing tested
    @Test
    void validJokerMove(){
        //TODO
    }

    // testing valid jack moves
    @Test
    void validJackMove(){
        //TODO
    }

    @Test
    void invalidJackMove(){
        //TODO
    }

    // testing seven moves
    @Test
    void validSevenMove(){
        //TODO
    }

    @Test
    void invalidSevenMove(){
        //TODO
    }

    @Test
    void goalSevenMove(){
        //TODO
    }

    // testing four moves
    @Test
    void validFourMove(){
        //TODO
    }

    @Test
    void invalidFourMove(){
        //TODO
    }

    @Test
    void goalFourMove(){
        //TODO
    }

    // testing regular moves
    @Test
    void validRegularMove(){
        //TODO
    }

    @Test
    void invalidRegularMove(){
        //TODO
    }

    @Test
    void goalRegularMove(){
        //TODO
    }

    // testing goal moves
    @Test
    void validGoalMove(){
        //TODO
    }

    @Test
    void invalidGoalMove(){
        //TODO
    }

}
