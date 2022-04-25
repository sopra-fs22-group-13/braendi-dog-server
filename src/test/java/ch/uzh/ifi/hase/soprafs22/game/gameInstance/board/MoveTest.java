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
    void setup() {
        board = new Board();
        fromPos = new ArrayList<>();
        toPos = new ArrayList<>();
        fromPosInGoal = new ArrayList<>();
        toPosInGoal = new ArrayList<>();
    }

    void moveComplete() {
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

    // @Test
    void validAceStart() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        fromPos = new ArrayList<>(Arrays.asList(-1));
        toPos = new ArrayList<>(Arrays.asList(0));
        fromPosInGoal = new ArrayList<>(Arrays.asList(false));
        toPosInGoal = new ArrayList<>(Arrays.asList(false));
        move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
        // test starting move
        try {
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void invalidAceStart() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        // wrong intersect
        fromPos = new ArrayList<>(Arrays.asList(-1));
        toPos = new ArrayList<>(Arrays.asList(16));
        fromPosInGoal = new ArrayList<>(Arrays.asList(false));
        toPosInGoal = new ArrayList<>(Arrays.asList(false));
        move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
        try {
            assertFalse(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void validAceMove() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start

            // test for 1
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(1));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 11
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(11));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void invalidAceMove() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start

            // test for move too far
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(12));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for move too short
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertFalse(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void validAceToGoal() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start

            // test for 1
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 11
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(56));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(56));
            toPos = new ArrayList<>(Arrays.asList(2));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void invalidAceToGoal() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start

            // goal too far --> wrong move distance
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(2));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // goal too close --> wrong move distance
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(56));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(56));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertFalse(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void validAceFromToGoal() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble into goal

            // test for 1
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(1));
            fromPosInGoal = new ArrayList<>(Arrays.asList(true));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void invalidAceFromToGoal() {
        _card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble into goal

            // wrong move distance (test both too far and too short)
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(2));
            fromPosInGoal = new ArrayList<>(Arrays.asList(true));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertFalse(board.isValidMove(move));

        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // test king moves
    // @Test
    void validKingStart() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            fromPos = new ArrayList<>(Arrays.asList(-1));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            // test starting move
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void invalidKingStart() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            // wrong intersect
            fromPos = new ArrayList<>(Arrays.asList(-1));
            toPos = new ArrayList<>(Arrays.asList(16));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            // test starting move
            assertFalse(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void validKingMove() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start

            // test for 13
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(13));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void invalidKingMove() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start

            // test for distance below 13
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(6));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertFalse(board.isValidMove(move));

            // test for distance above 13
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(16));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertFalse(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void validKingToGoal() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(52));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble into position

            fromPos = new ArrayList<>(Arrays.asList(52));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void invalidKingToGoal() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(53));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble into position

            // goal too far
            fromPos = new ArrayList<>(Arrays.asList(53));
            toPos = new ArrayList<>(Arrays.asList(3));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertFalse(board.isValidMove(move));
            // goal too close
            fromPos = new ArrayList<>(Arrays.asList(53));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertFalse(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void invalidKingFromToGoal() {
        _card = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move marble to start
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble into position

            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(4));
            fromPosInGoal = new ArrayList<>(Arrays.asList(true));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertFalse(board.isValidMove(move));
        } catch (InvalidMoveException e) {
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
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(20));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _colorOther);
            board.makeMove(move); // move second marble into position

            // switch the two marbles
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(20));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
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
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(7));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move)); // one move

            fromPos = new ArrayList<>(Arrays.asList(0, 3));
            toPos = new ArrayList<>(Arrays.asList(3, 7));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false, false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false, false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move)); // two moves

            // test for two marbles
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(7));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move first marble into position

            board.makeStartingMove(COLOR.RED); // move second marble to start
            fromPos = new ArrayList<>(Arrays.asList(0, 7));
            toPos = new ArrayList<>(Arrays.asList(6, 8));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false, false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false, false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move)); // two moves for two marbles
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void validSevenToGoal() {
        _card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start

            // test for one marble
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(61));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move first marble into position

            fromPos = new ArrayList<>(Arrays.asList(61));
            toPos = new ArrayList<>(Arrays.asList(3));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for two marbles
            board.makeStartingMove(COLOR.RED); // move second marble to start
            fromPos = new ArrayList<>(Arrays.asList(61, 0));
            toPos = new ArrayList<>(Arrays.asList(2, 0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false, false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true, true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void validSevenFromToGoal() {
        _card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start

            // test for one marble in goal
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move first marble into goal

            board.makeStartingMove(COLOR.RED); // move second marble to start
            fromPos = new ArrayList<>(Arrays.asList(0, 0));
            toPos = new ArrayList<>(Arrays.asList(3, 4));
            fromPosInGoal = new ArrayList<>(Arrays.asList(true, false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true, false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // testing four moves
    // @Test
    void validFourMove() {
        _card = new Card(CARDVALUE.FOUR, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start

            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(4));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move)); // forward move

            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(60));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move)); // backward move
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void validFourToGoal() {
        _card = new Card(CARDVALUE.FOUR, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
        _color = COLOR.RED;

        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start

            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(3));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // testing regular moves
    // @Test
    void validRegularMove() {
        _color = COLOR.RED;
        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start

            // test for 2
            _card = new Card(CARDVALUE.TWO, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(2));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 3
            _card = new Card(CARDVALUE.THREE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(3));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 5
            _card = new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(5));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 6
            _card = new Card(CARDVALUE.SIX, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(6));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 8
            _card = new Card(CARDVALUE.EIGHT, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(8));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 9
            _card = new Card(CARDVALUE.NINE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(9));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 10
            _card = new Card(CARDVALUE.TEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(10));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for queen
            _card = new Card(CARDVALUE.QUEEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(11));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void validRegularToGoal() {
        _color = COLOR.RED;
        try {
            board.makeStartingMove(COLOR.RED); // move first marble to start

            // test for 2
            _card = new Card(CARDVALUE.TWO, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(1));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 3
            _card = new Card(CARDVALUE.THREE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(2));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 5
            _card = new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(60));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(60));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 6
            _card = new Card(CARDVALUE.SIX, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(60));
            toPos = new ArrayList<>(Arrays.asList(59));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(59));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 8
            _card = new Card(CARDVALUE.EIGHT, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(59));
            toPos = new ArrayList<>(Arrays.asList(57));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(57));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 9
            _card = new Card(CARDVALUE.NINE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(57));
            toPos = new ArrayList<>(Arrays.asList(56));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(56));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 10
            _card = new Card(CARDVALUE.TEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(56));
            toPos = new ArrayList<>(Arrays.asList(55));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(55));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for queen
            _card = new Card(CARDVALUE.QUEEN, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(55));
            toPos = new ArrayList<>(Arrays.asList(54));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(false));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble to right position
            fromPos = new ArrayList<>(Arrays.asList(54));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }

    // @Test
    void validRegularFromToGoal() {
        _color = COLOR.RED;
        try {
            board.makeStartingMove(COLOR.RED);

            // test for 2
            _card = new Card(CARDVALUE.TWO, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(0));
            fromPosInGoal = new ArrayList<>(Arrays.asList(false));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            board.makeMove(move); // move marble to right position in goal
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(2));
            fromPosInGoal = new ArrayList<>(Arrays.asList(true));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));

            // test for 3
            _card = new Card(CARDVALUE.THREE, CARDTYPE.DEFAULT, CARDSUITE.HEARTS);
            fromPos = new ArrayList<>(Arrays.asList(0));
            toPos = new ArrayList<>(Arrays.asList(3));
            fromPosInGoal = new ArrayList<>(Arrays.asList(true));
            toPosInGoal = new ArrayList<>(Arrays.asList(true));
            move = new Move(fromPos, toPos, fromPosInGoal, toPosInGoal, _card, "token", _color);
            assertTrue(board.isValidMove(move));
        } catch (InvalidMoveException e) {
            fail("Should not throw exception");
        }
    }
}
