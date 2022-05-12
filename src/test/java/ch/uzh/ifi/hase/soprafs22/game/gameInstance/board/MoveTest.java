package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.*;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.MoveBlockedByMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.NoMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardPosition;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.MARBLE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class MoveTest {

    private Board board;
    private Move move;
    private ArrayList<BoardPosition> fromPos;
    private ArrayList<BoardPosition> toPos;
    private Card _card;
    private COLOR _color;

    @BeforeEach
    void setup() {
        board = new Board();
        fromPos = new ArrayList<>();
        toPos = new ArrayList<>();
    }

    void moveComplete() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
        toPos = new ArrayList<>(Arrays.asList(new BoardPosition(1, false)));

        move = new Move(fromPos, toPos, _card, "token", _color);

        assertTrue(move.isWellFormed());
        assertTrue(move.checkIfComplete());

        fromPos.remove(0);
        move = new Move(fromPos, toPos, _card, "token", _color);
        assertFalse(move.isWellFormed());
        assertFalse(move.checkIfComplete());

        fromPos.add(new BoardPosition(0, false));
        _color = null;
        move = new Move(fromPos, toPos, _card, "token", _color);
        assertTrue(move.isWellFormed());
        assertFalse(move.checkIfComplete());

        _color = COLOR.RED;
        fromPos.remove(0);
        toPos.remove(0);
        move = new Move(fromPos, toPos, _card, "token", _color);
        assertTrue(move.isWellFormed());
        assertFalse(move.checkIfComplete());
    }

    @Test
    void validAceStart() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(-1, false)));
        toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));


        move = new Move(fromPos, toPos, _card, "token", _color);
        // test starting move
        try {
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void invalidAceStart() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        // wrong intersect
        fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(-1, false)));
        toPos = new ArrayList<>(Arrays.asList(new BoardPosition(16, false)));

        move = new Move(fromPos, toPos, _card, "token", _color);
        try {
            assertFalse(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void validAceMove() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start

            // test for 1
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(1, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 11

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(11, false)));

            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (Exception e) {
            fail(String.format("Should not throw exception | startPos: %s, endPos: %s", fromPos, toPos));
        }
    }

    @Test
    void invalidAceMove() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start

            // test for move too far
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(12, false)));

            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for move too short
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));

            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void validAceToGoal() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start

            //move marble --> to unblock the intersect
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(1, false)));

            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move);

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(1, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));

            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move);

            // test for 1
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));

            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 11
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(56, false)));

            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(56, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(2, true)));

            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    //@Test
    void invalidAceToGoal() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start

            //todo @Shitao, this should fail, but because you cannot reach a goal from a blocked
            // goal too far --> wrong move distance
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(2, true)));

            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // goal too close --> wrong move distance

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(56, false)));

            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble to right position

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(56, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));

            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void validAceFromToGoal() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));

            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble into goal

            // test for 1
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(1, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (Exception e) {
            System.out.println(e + " | should not throw this exception");
        }
    }

    @Test
    void invalidAceFromToGoal() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble into goal

            // wrong move distance (test both too far and too short)
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(2, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // test king moves
    @Test
    void validKingStart() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(-1, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            // test starting move
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void invalidKingStart() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            // wrong intersect
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(-1, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(16, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            // test starting move
            assertFalse(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void validKingMove() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start

            // test for 13
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(13, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void invalidKingMove() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start

            // test for distance below 13
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(6, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for distance above 13
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(16, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void validKingToGoal() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(52, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble into position

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(52, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void invalidKingToGoal() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(53, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble into position

            // goal too far
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(53, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(3, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));
            // goal too close
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(53, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

        } catch (Exception e) {
            System.out.println(e + " | should not throw this exception");
        }
    }

    @Test
    void invalidKingFromToGoal() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble into position

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(3, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);

            try{
                board.isValidMove(move);
                //or if it does catch the error, we should at least have a false
                assertFalse(board.isValidMove(move));

            } catch (IndexOutOfBoundsException | InvalidMoveException e)
            {
                //good
            }
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }

    // testing joker moves
    // behaviour unclear yet, therefore nothing tested
    @Test
    void validJokerMove() {
        // TODO
    }

    // testing valid jack moves
    @Test
    void validJackMove() {
        _card = new Card(CARDVALUE.JACK, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        COLOR _colorOther = COLOR.BLUE;

        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start
            board.makeStartingMove(COLOR.BLUE); // move second marble to start
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(20, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move second marble into position

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(16, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(35, false)));
            move = new Move(fromPos, toPos, _card, "token", COLOR.BLUE);
            board.makeMove(move); // move second marble into position

            // switch the two marbles
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(20, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(35, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (Exception e) {
            System.out.println(e + " Should not throw exception");
        }
    }

    // @Test
    void invalidJackMove() {
        // TODO
    }

    // testing seven moves
    @Test
    void validSevenMove() {
        _card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start

            // test for one marble
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(7, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move)); // one move

            // test for two marbles
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(7, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move first marble into position

            board.makeStartingMove(COLOR.RED); // move second marble to start
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false), new BoardPosition(7, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(6, false), new BoardPosition(8, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move)); // two moves for two marbles
        } catch (Exception e) {
            fail(e +" Should not throw exception");
        }
    }

    //todo revert this once it is implemented
    @Test
    void validSevenToGoal() {
        _card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start

            // test for one marble
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(61, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move first marble into position

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(61, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(3, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for two marbles
            board.makeStartingMove(COLOR.RED); // move second marble to start
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(61, false), new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(63, false), new BoardPosition(61, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); //move second marble into position
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(63, false), new BoardPosition(61, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(1, true), new BoardPosition(0, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void validSevenFromToGoal() {
        _card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start

            // test for one marble in goal
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move first marble into goal

            board.makeStartingMove(COLOR.RED); // move second marble to start

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true), new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(3, true), new BoardPosition(4, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void validSevenTwoInGoal() {
        _card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start
            moveMarble(0, 1, false, true);
            board.makeStartingMove(COLOR.RED); // move first marble to start
            moveMarble(0, 0, false, true);
            board.makeStartingMove(COLOR.BLUE);
            board.makeStartingMove(COLOR.RED); // move first marble to start
            moveMarble(0, 13, false, false);

            fromPos = new ArrayList<>(Arrays.asList(
                    new BoardPosition(1, true),
                    new BoardPosition(0, true),
                    new BoardPosition(13, false)));
            toPos = new ArrayList<>(Arrays.asList(
                    new BoardPosition(3, true),
                    new BoardPosition(2, true),
                    new BoardPosition(15, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            moveMarble(13, 12, false, false); //this should work
            fromPos = new ArrayList<>(Arrays.asList(
                    new BoardPosition(1, true),
                    new BoardPosition(0, true),
                    new BoardPosition(12, false)));
            toPos = new ArrayList<>(Arrays.asList(
                    new BoardPosition(3, true),
                    new BoardPosition(2, true),
                    new BoardPosition(15, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void validSevenOneInGoalOneToGoalOneNormal() {
        _card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start
            moveMarble(0, 1, false, true);
            board.makeStartingMove(COLOR.BLUE);
            board.makeStartingMove(COLOR.RED); // move first marble to start
            moveMarble(0, 13, false, false);
            board.makeStartingMove(COLOR.RED); // move first marble to start
            moveMarble(0, 62, false, false);

            fromPos = new ArrayList<>(Arrays.asList(
                    new BoardPosition(1, true),
                    new BoardPosition(62, false),
                    new BoardPosition(13, false)));
            toPos = new ArrayList<>(Arrays.asList(
                    new BoardPosition(3, true),
                    new BoardPosition(0, true),
                    new BoardPosition(15, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // testing four moves
    @Test
    void validFourMove() {
        _card = new Card(CARDVALUE.FOUR, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(4, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move)); // forward move

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(60, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move)); // backward move
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    //@Test
    void validFourBack(){
        //TODO: test for valid backwards move with four
    }

    @Test
    void validFourToGoal() {
        _card = new Card(CARDVALUE.FOUR, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(63, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move);


            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(63, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(2, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // testing regular moves
    @Test
    void validRegularMove() {
        _color = COLOR.RED;
        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start

            // test for 2
            _card = new Card(CARDVALUE.TWO, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(2, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 3
            _card = new Card(CARDVALUE.THREE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(3, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 5
            _card = new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(5, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 6
            _card = new Card(CARDVALUE.SIX, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(6, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 8
            _card = new Card(CARDVALUE.EIGHT, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(8, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 9
            _card = new Card(CARDVALUE.NINE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(9, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 10
            _card = new Card(CARDVALUE.TEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(10, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for queen
            _card = new Card(CARDVALUE.QUEEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(12, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for ace 1
            _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(1, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for ace 2
            _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(11, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for king
            _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(13, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void invalidRegularBackwardsMove() {
        _color = COLOR.RED;
        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start

            // test for 2
            _card = new Card(CARDVALUE.TWO, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(62, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for 3
            _card = new Card(CARDVALUE.THREE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(61, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for 5
            _card = new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(59, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for 6
            _card = new Card(CARDVALUE.SIX, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(58, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for 8
            _card = new Card(CARDVALUE.EIGHT, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(56, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for 9
            _card = new Card(CARDVALUE.NINE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(55, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for 10
            _card = new Card(CARDVALUE.TEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(54, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for queen
            _card = new Card(CARDVALUE.QUEEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(52, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for ace 1
            _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(63, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for ace 2
            _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(53, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for king
            _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(51, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for wrong distance
            _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(52, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void validRegularToGoal() {
        _color = COLOR.RED;
        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start
            _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(63, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move);

            // test for 2
            _card = new Card(CARDVALUE.TWO, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(63, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 3
            _card = new Card(CARDVALUE.THREE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(2, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 5
            _card = new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(63, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(60, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(60, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 6
            _card = new Card(CARDVALUE.SIX, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(60, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(59, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble to right position

            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(59, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 8
            _card = new Card(CARDVALUE.EIGHT, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(59, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(57, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(57, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 9
            _card = new Card(CARDVALUE.NINE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(57, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(56, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(56, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 10
            _card = new Card(CARDVALUE.TEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(56, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(55, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(55, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for queen
            _card = new Card(CARDVALUE.QUEEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(55, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(54, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(54, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(1, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    void validRegularFromToGoal() {
        _color = COLOR.RED;
        try {
            board.makeStartingMove(COLOR.YELLOW);
            board.makeStartingMove(COLOR.RED);
            board.makeStartingMove(COLOR.BLUE);
            board.makeStartingMove(COLOR.GREEN);


            // test for 2
            _card = new Card(CARDVALUE.TWO, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            board.makeMove(move); // move marble to right position in goal
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(2, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 3
            _card = new Card(CARDVALUE.THREE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, true)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(3, true)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail(e + "Should not throw exception");
        }
    }

    //@Test
    void blockedAce(){
        //TODO: test for blocked intersection with ace move
    }

    //@Test
    void blockedKing(){
        //TODO: test for blocked intersection with king move
    }

    //@Test
    void blockedJack(){
        //TODO: test for blocked intersection with jack move
    }

    //@Test
    void blockedJoker(){
        //TODO: test for blocked intersection with joker move
    }

    //@Test
    void blockedSeven(){
        //TODO: test for blocked intersection with seven move
    }

    //@Test
    void blockedFour(){
        //TODO: test for blocked intersection with four move
    }

    //@Test
    void blockedRegular(){
        //TODO: test for blocked intersection with regular move
    }
    // testing awkward moves

    boolean createBasicMove(CARDVALUE cardvalue,Integer startIndex, Integer endIndex, boolean toGoal)
    {
        return createBasicMove(cardvalue, startIndex, endIndex, toGoal, false);
    }

    boolean createBasicMove(CARDVALUE cardvalue,Integer startIndex, Integer endIndex, boolean toGoal, boolean fromGoal)
    {
        _card = new Card(cardvalue, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(startIndex, fromGoal)));
        toPos = new ArrayList<>(Arrays.asList(new BoardPosition(endIndex, toGoal)));
        move = new Move(fromPos, toPos, _card, "token", _color);

        boolean success = false;
        try{
            success = board.isValidMove(move);
        }catch (InvalidMoveException e)
        {
            fail("Should not throw an exception");
        }

        return success;
    }

    void moveMarble(Integer from, Integer to, boolean fromGoal, boolean toGoal)
    {
        fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(from, fromGoal)));
        toPos = new ArrayList<>(Arrays.asList(new BoardPosition(to, toGoal)));
        move = new Move(fromPos, toPos, null, "token", _color);
        try{
            board.makeMove(move);
        } catch (Exception e)
        {
            fail("Exception while making a move " + e);
        }
    }

    int getCardDistance(CARDVALUE cardvalue){
        switch (cardvalue) {
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FIVE:
                return 5;
            case SIX:
                return 6;
            case EIGHT:
                return 8;
            case NINE:
                return 9;
            case TEN:
                return 10;
            case QUEEN:
                return 12;
            case KING:
                return 13;
            case ACE:
                return 1;
            default:
                return -1;
                //the 7, 4 and Joker are differently tested) {
        }
    }

    //Blocked
    @Test
    void testCannotOvertakeBlocked()
    {
        try{
            _color = COLOR.RED;
            board.makeStartingMove(COLOR.RED);

            //move back
            moveMarble(0, 60, false, false);

            //block again
            board.makeStartingMove(COLOR.RED);


            //move should not be possible now
            boolean success = createBasicMove(CARDVALUE.KING, 60, 9, false);
            assertFalse(success);

            //unblock
            moveMarble(0, 1, false, false);

            //now the move should work
            boolean successUnblocked = createBasicMove(CARDVALUE.KING, 60, 9, false);
            assertTrue(successUnblocked);
        }catch (Exception e)
        {
            fail("Should not throw here.");
        }
    }

    @Test
    void testCannotOvertakeBlockedBackwards()
    {
        try{
            _color = COLOR.RED;
            board.makeStartingMove(COLOR.RED);

            //move back
            moveMarble(0, 2, false, false);

            //block again
            board.makeStartingMove(COLOR.RED);

            //move should not be possible now
            boolean success = createBasicMove(CARDVALUE.FOUR, 2, 62, false);
            assertFalse(success);

            //unblock
            moveMarble(0, 1, false, false);

            //now the move should work
            boolean successUnblocked = createBasicMove(CARDVALUE.FOUR, 2, 62, false);
            assertTrue(successUnblocked);

            //this block should not impair
            board.makeStartingMove(COLOR.GREEN);
            //move should still work
            boolean successUnblockedIrrelevant = createBasicMove(CARDVALUE.FOUR, 2, 62, false);
            assertTrue(successUnblockedIrrelevant);

        }catch (Exception e)
        {
            fail("Should not throw here.");
        }
    }

    @Test
    void testCannotOvertakeBlockedToGoal()
    {
        try{
            _color = COLOR.RED;
            board.makeStartingMove(COLOR.RED);

            //move back
            moveMarble(0, 60, false, false);

            //block again
            board.makeStartingMove(COLOR.RED);


            //move should not be possible now
            boolean success = createBasicMove(CARDVALUE.EIGHT, 60, 3, true);
            assertFalse(success);

            //unblock
            moveMarble(0, 1, false, false);

            //now the move should work
            boolean successUnblocked = createBasicMove(CARDVALUE.EIGHT, 60, 3, true);
            assertTrue(successUnblocked);
        }catch (Exception e)
        {
            fail("Should not throw here.");
        }
    }

    @Test
    void testCannotLandOnBlocked()
    {
        try{
            _color = COLOR.GREEN;
            board.makeStartingMove(COLOR.GREEN);

            //move back
            moveMarble(32, 30, false, false);

            //block again
            board.makeStartingMove(COLOR.GREEN);

            //this should not impair
            board.makeStartingMove(COLOR.BLUE);

            //move should not be possible now
            boolean success = createBasicMove(CARDVALUE.TWO, 30, 32, false);
            assertFalse(success);

            //unblock
            moveMarble(32, 31, false, false);

            //now the move should work
            boolean successUnblocked = createBasicMove(CARDVALUE.TWO, 30, 32, false);
            assertTrue(successUnblocked);
        }catch (Exception e)
        {
            fail("Should not throw here.");
        }
    }

    @Test
    void cannotRunBackwardsIntoGoal()
    {
        try{
            _color = COLOR.RED;
            board.makeStartingMove(COLOR.RED);

            //move back
            moveMarble(0, 2, false, false);

            //just doubleCheck, this should work
            board.makeStartingMove(COLOR.RED);
            moveMarble(0, 62, false, false);
            boolean successPre1 = createBasicMove(CARDVALUE.FOUR, 62, 2, true); //wrong index
            assertFalse(successPre1);
            boolean successPre2 = createBasicMove(CARDVALUE.FOUR, 62, 1, true);
            assertTrue(successPre2);

            //but this should not be possible
            boolean success = createBasicMove(CARDVALUE.FOUR, 2, 1, true);
            assertFalse(success);
        }catch (Exception e)
        {
            fail("Should not throw here.");
        }
    }

    @Test
    void cannotSwitchBlockedOfOthers()
    {
        try{
            _color = COLOR.RED;
            board.makeStartingMove(COLOR.RED);
            board.makeStartingMove(COLOR.YELLOW);

            //move back
            moveMarble(0, 2, false, false);

            //this should not be possible
            _card = new Card(CARDVALUE.JACK, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(2, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(16, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            moveMarble(16, 18, false, false);

            //this should now be possible
            _card = new Card(CARDVALUE.JACK, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(2, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(18, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        }catch (Exception e)
        {
            fail("Should not throw here.");
        }
    }

    @Test
    void resetsCorrectlyAfterSwitchFromBlocked()
    {
        try{
            _color = COLOR.RED;
            board.makeStartingMove(COLOR.RED);
            board.makeStartingMove(COLOR.YELLOW);
            moveMarble(0, 60, false, false);
            board.makeStartingMove(COLOR.RED);
            moveMarble(16, 18, false, false);


            //this should not be possible
            _card = new Card(CARDVALUE.EIGHT, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(60, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(4, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            //this should work
            _card = new Card(CARDVALUE.JACK, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(18, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
            board.makeSwitch(0, 18);


            //this should now be possible
            _card = new Card(CARDVALUE.EIGHT, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(60, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(4, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

        }catch (Exception e)
        {
            fail("Should not throw here.");
        }
    }

    @Test
    void canSwitchBlockedOfMe()
    {
        try{
            _color = COLOR.RED;
            board.makeStartingMove(COLOR.RED);
            board.makeStartingMove(COLOR.YELLOW);


            //this should not be possible
            _card = new Card(CARDVALUE.JACK, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(16, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            moveMarble(16, 18, false, false);

            //this should be possible
            _card = new Card(CARDVALUE.JACK, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(18, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));

        }catch (Exception e)
        {
            fail("Should not throw here.");
        }
    }

    @Test
    void cannotRunDirectlyIntoGoal()
    {
        try{
            _color = COLOR.RED;
            board.makeStartingMove(COLOR.RED);

            //do NOT move back
            //moveMarble(0, 2, false, false);

            //this should not be possible
            boolean success1 = createBasicMove(CARDVALUE.ACE, 0, 0, true);
            assertFalse(success1);

            boolean success2 = createBasicMove(CARDVALUE.TWO, 0, 1, true);
            assertFalse(success2);

            boolean success3 = createBasicMove(CARDVALUE.FOUR, 0, 3, true);
            assertFalse(success3);

        }catch (Exception e)
        {
            fail("Should not throw here.");
        }
    }

    //Overtaking in goal
    @Test
    void cannotOvertakeInGoal()
    {
        try{
            _color = COLOR.RED;

            //move back
            board.makeStartingMove(COLOR.RED);
            moveMarble(0, 62, false, false);

            //move should be possible
            boolean success = createBasicMove(CARDVALUE.SIX, 62, 3, true);
            assertTrue(success);

            //move blocker to goal
            board.makeStartingMove(COLOR.RED);
            moveMarble(0, 2, false, true);

            //now the move should not work
            boolean successBlocked = createBasicMove(CARDVALUE.SIX, 62, 3, true);
            assertFalse(successBlocked);
        }catch (Exception e)
        {
            fail("Should not throw here.");
        }
    }

    @Test
    void cannotMoveBackInGoal()
    {
        try{
            _color = COLOR.RED;

            //move back
            board.makeStartingMove(COLOR.RED);
            moveMarble(0, 3, false, true);

            //move should not be possible
            boolean success = createBasicMove(CARDVALUE.FOUR, 3, 0, true, true);
            assertFalse(success);
        }catch (Exception e)
        {
            fail("Should not throw here.");
        }
    }

    //Joker

    //not so sure about this here
    //@RepeatedTest(13)
    void validAllMovesForAJoker(RepetitionInfo repetitionInfo)
    {
        CARDVALUE c = CARDVALUE.values()[repetitionInfo.getCurrentRepetition()];

        int dist = getCardDistance(c);
        if(dist != -1)
        {
            //just a generic move should work
            boolean success = createBasicMove(c, 0, getCardDistance(c), false);
            assertTrue(success);

            //just a generic goal move should also work

            dist = dist - 1;
            //now go back
            int start = 64 - dist;
            if(start == 64) start = 0;

            boolean successGoal = createBasicMove(c, start, 0, true);
            assertTrue(successGoal);
        }
    }

    @Test
    void jokerAce(){
        //TODO test for joker ace move
        _card = new Card(CARDVALUE.JOKER, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try{
            board.makeStartingMove(COLOR.RED);
            boolean success = createBasicMove(CARDVALUE.JOKER, 0, 1, false);
            assertTrue(success);

            success = createBasicMove(CARDVALUE.JOKER, 0, 11, false);
            assertTrue(success);
        }catch(Exception e){
            fail("Should not throw Exception here " + e);
        }
    }

    @Test
    void validJokerStartMove()
    {
        _card = new Card(CARDVALUE.JOKER, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(-1, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(0, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            // test starting move
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }
    
    @Test
    void validJokerSevenMove(){
        _card = new Card(CARDVALUE.JOKER, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        //testing with 3 marbles: one in goal, one to goal, one regular
        try{
            board.makeStartingMove(COLOR.RED);
            moveMarble(0, 63, false, false);
            moveMarble(63, 2, false, true); //move marble to goal
            board.makeStartingMove(COLOR.RED);
            moveMarble(0, 63, false, false);
            board.makeStartingMove(COLOR.RED);
            moveMarble(0, 1, false, false);
            fromPos = new ArrayList<>(Arrays.asList(
                    new BoardPosition(2, true),
                    new BoardPosition(63, false),
                    new BoardPosition(1, false)));
            toPos = new ArrayList<>(Arrays.asList(
                    new BoardPosition(3, true),
                    new BoardPosition(2, true),
                    new BoardPosition(3, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        }catch(Exception e){
            fail("Should not throw this exception: " + e);
        }
    }
    
    @Test
    void validSevenInbetween(){
        _card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try{
            board.makeStartingMove(COLOR.RED);
            moveMarble(0, 1, false, false);
            board.makeStartingMove(COLOR.RED);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(1, false), new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(5, false), new BoardPosition(3, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        }catch(Exception e){
            fail("Should not throw this exception: " + e);
        }
    }

    @Test
    void validSevenInbetween2(){
        _card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try{
            board.makeStartingMove(COLOR.RED);
            moveMarble(0, 2, false, false);
            board.makeStartingMove(COLOR.RED);
            fromPos = new ArrayList<>(Arrays.asList(new BoardPosition(2, false), new BoardPosition(0, false)));
            toPos = new ArrayList<>(Arrays.asList(new BoardPosition(5, false), new BoardPosition(4, false)));
            move = new Move(fromPos, toPos, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        }catch(Exception e){
            fail("Should not throw this exception: " + e);
        }
    }
}
