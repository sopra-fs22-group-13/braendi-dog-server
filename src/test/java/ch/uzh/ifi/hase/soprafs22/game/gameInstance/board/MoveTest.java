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
        assertFalse(move.isWellFormed());
        assertFalse(move.checkIfComplete());

        fromPos.add(0);
        _color = null;
        assertTrue(move.isWellFormed());
        assertFalse(move.checkIfComplete());

        _color = COLOR.RED;
        fromPos.remove(0);
        toPos.remove(0);
        fromPosInGoal.remove(0);
        toPosInGoal.remove(0);
        assertTrue(move.isWellFormed());
        assertFalse(move.checkIfComplete());
    }

    // Testing ace moves
    void validAceMove(){
        //TODO
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
        //TODO

        // test nromal moves fromPos and toPos in goal
        //TODO

    }

    @Test
    void invalidAceMove(){
        //TODO
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
