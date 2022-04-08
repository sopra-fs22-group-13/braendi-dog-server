package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.MARBLE;
import ch.uzh.ifi.hase.soprafs22.game.constants.TURN;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.MoveBlockedByMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.NoMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    Board _b;

    ArrayList<String> actualBoard;
    ArrayList<String> actualgreen;
    ArrayList<String> actualred;
    ArrayList<String> actualyellow;
    ArrayList<String> actualblue;


    @BeforeEach
    void setupBoard()
    {
        _b = new Board();

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


    }

    void moveRedOut()
    {
        try{
            _b.makeStartingMove(TURN.RED);
        }catch (InvalidMoveException e)
        {
            fail("Cannot move out RED");
        }
    }

    @Test
    void getFormattedBoardState() {

        //get after creation.
        BoardData bd = _b.getFormattedBoardState();

        //everything else given by BeforeEach
        BoardData expected = new BoardData(actualBoard, actualred, actualgreen, actualblue, actualyellow, 4, 4, 4, 4);

        assertEquals(expected.getBoard(), bd.getBoard());
        assertEquals(expected.getBlueGoal(), bd.getBlueGoal());
        assertEquals(expected.getGreenGoal(), bd.getGreenGoal());
        assertEquals(expected.getRedGoal(), bd.getRedGoal());
        assertEquals(expected.getYellowGoal(), bd.getYellowGoal());
        assertEquals(expected.getGreenBase(), bd.getGreenBase());
        assertEquals(expected.getBlueBase(), bd.getBlueBase());
        assertEquals(expected.getYellowBase(), bd.getYellowBase());
        assertEquals(expected.getRedBase(), bd.getRedBase());

        assertEquals(null,  bd.getColorMapping());
        assertEquals(null, bd.getLastPlayedCard());
    }

    @Test
    void makeStartingMoveValid()
    {
        try
        {
            _b.makeStartingMove(TURN.RED);
        }catch (InvalidMoveException e)
        {
            fail(e.getMessage());
        }
        ArrayList<String> newBoard = new ArrayList<>(actualBoard);
        newBoard.set(0, "RED");

        BoardData actual = _b.getFormattedBoardState();

        assertEquals(newBoard, actual.getBoard());
        assertEquals(3, actual.getRedBase());
    }

    @Test
    void makeStartingMoveValid2()
    {
        try
        {
            _b.makeStartingMove(TURN.YELLOW);
        }catch (InvalidMoveException e)
        {
            fail(e.getMessage());
        }

        try
        {
            _b.makeStartingMove(TURN.RED);
        }catch (InvalidMoveException e)
        {
            fail(e.getMessage());
        }

        ArrayList<String> newBoard = new ArrayList<>(actualBoard);
        newBoard.set(0, "RED");
        newBoard.set(48, "YELLOW");

        BoardData actual = _b.getFormattedBoardState();

        assertEquals(newBoard, actual.getBoard());
        assertEquals(3, actual.getRedBase());
    }

    @Test
    void makeStartingMoveInvalid()
    {
        try
        {
            _b.makeStartingMove(TURN.RED);
        }catch (InvalidMoveException e)
        {
            fail(e.getMessage());
        }

        assertThrows(InvalidMoveException.class, () -> {_b.makeStartingMove(TURN.RED);}, "InvalidMove expected");
    }

    @Test
    void makeMoveValid1()
    {
        //move red out, so we can do a move
        moveRedOut();

        //create a simple move
        ArrayList<Integer> from = new ArrayList<>();
        ArrayList<Integer> to = new ArrayList<>();

        ArrayList<Boolean> from_base = new ArrayList<>();
        ArrayList<Boolean> to_base = new ArrayList<>();

        from.add(0);
        to.add(1);
        from_base.add(false);
        to_base.add(false);

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);
        move.set_fromPosInGoal(from_base);
        move.set_toPosInGoal(to_base);

        //make the move
        try{
            _b.makeMove(move);
        }catch (InvalidMoveException e)
        {
            if(e instanceof NoMarbleException)
            {
                fail("No Marble");
            }
            if(e instanceof MoveBlockedByMarbleException)
            {
                fail("Blocked");
            }
            fail("Invalid Move");
        }

        //new expected board state
        ArrayList<String> newBoard = new ArrayList<>(actualBoard);
        newBoard.set(1, "RED");

        //actual
        BoardData bd = _b.getFormattedBoardState();

        assertEquals(newBoard, bd.getBoard());
    }

    @Test
    void makeMoveValid2()
    {
        //move red out, so we can do a move
        moveRedOut();

        //create a simple move
        ArrayList<Integer> from = new ArrayList<>();
        ArrayList<Integer> to = new ArrayList<>();

        ArrayList<Boolean> from_base = new ArrayList<>();
        ArrayList<Boolean> to_base = new ArrayList<>();

        from.add(0);
        to.add(3);
        from_base.add(false);
        to_base.add(true); //move it into a goal

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);
        move.set_fromPosInGoal(from_base);
        move.set_toPosInGoal(to_base);
        move.set_color(TURN.RED); //need to set the color.

        //make the move
        try{
            _b.makeMove(move);
        }catch (InvalidMoveException e)
        {
            if(e instanceof NoMarbleException)
            {
                fail("No Marble");
            }
            if(e instanceof MoveBlockedByMarbleException)
            {
                fail("Blocked");
            }
            fail("Invalid Move");
        }

        //new expected board state
        ArrayList<String> newGoal = new ArrayList<>(actualred);
        newGoal.set(3, "RED");

        //actual
        BoardData bd = _b.getFormattedBoardState();

        assertEquals(actualBoard, bd.getBoard());
        assertEquals(newGoal, bd.getRedGoal());
    }

    @Test
    void makeMoveInvalid1()
    {
        //invalid move: invalid move composition
        //move red out, so we can do a move
        moveRedOut();

        //create a simple move
        ArrayList<Integer> from = new ArrayList<>();
        ArrayList<Integer> to = new ArrayList<>();

        ArrayList<Boolean> from_base = new ArrayList<>();
        ArrayList<Boolean> to_base = new ArrayList<>();

        from.add(0);
        to.add(3);
        to.add(1); //added 1 too many. move is now not well formed
        from_base.add(false);
        to_base.add(false); //move it into a goal

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);
        move.set_fromPosInGoal(from_base);
        move.set_toPosInGoal(to_base);

        //make the move
        boolean success = false;
        try{
            _b.makeMove(move);
        }catch (InvalidMoveException e)
        {
            if(e instanceof NoMarbleException)
            {
                fail("No Marble");
            }
            if(e instanceof MoveBlockedByMarbleException)
            {
                fail("Blocked");
            }

            success = true;
        }
        assertTrue(success);
    }

    @Test
    void makeMoveInvalid_Blocked()
    {
        //move red out, so we can do a move
        moveRedOut();

        //create a simple move
        ArrayList<Integer> from = new ArrayList<>();
        ArrayList<Integer> to = new ArrayList<>();

        ArrayList<Boolean> from_base = new ArrayList<>();
        ArrayList<Boolean> to_base = new ArrayList<>();

        from.add(0);
        to.add(1);
        from_base.add(false);
        to_base.add(false);

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);
        move.set_fromPosInGoal(from_base);
        move.set_toPosInGoal(to_base);

        //make the first move
        try{
            _b.makeMove(move);
        }catch (InvalidMoveException e)
        {
            fail("Invalid Move");
        }

        //move out one more marble
        moveRedOut();

        //make the second move, to the same position
        //this should fail
        assertThrows(MoveBlockedByMarbleException.class, () -> {_b.makeMove(move);});
    }

    @Test
    void makeMoveInvalid_NoMarble()
    {
        //DO NOT move red out, so we can not do a move

        //create a simple move
        ArrayList<Integer> from = new ArrayList<>();
        ArrayList<Integer> to = new ArrayList<>();

        ArrayList<Boolean> from_base = new ArrayList<>();
        ArrayList<Boolean> to_base = new ArrayList<>();

        from.add(0);
        to.add(1);
        from_base.add(false);
        to_base.add(false);

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);
        move.set_fromPosInGoal(from_base);
        move.set_toPosInGoal(to_base);

        //make the move
        assertThrows(NoMarbleException.class, ()->{_b.makeMove(move);});
    }
}