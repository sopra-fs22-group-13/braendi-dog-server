/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.*;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.MoveBlockedByMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.NoMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardPosition;
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
            _b.makeStartingMove(COLOR.RED);
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
            _b.makeStartingMove(COLOR.RED);
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
            _b.makeStartingMove(COLOR.YELLOW);
        }catch (InvalidMoveException e)
        {
            fail(e.getMessage());
        }

        try
        {
            _b.makeStartingMove(COLOR.RED);
        }catch (InvalidMoveException e)
        {
            fail(e.getMessage());
        }

        ArrayList<String> newBoard = new ArrayList<>(actualBoard);
        newBoard.set(0, "RED");
        newBoard.set(16, "YELLOW");

        BoardData actual = _b.getFormattedBoardState();

        assertEquals(newBoard, actual.getBoard());
        assertEquals(3, actual.getRedBase());
    }

    @Test
    void makeStartingMoveInvalid()
    {
        try
        {
            _b.makeStartingMove(COLOR.RED);
            _b.makeSwitch(0, 12);
        }catch (InvalidMoveException e)
        {
            fail(e.getMessage());
        }
        try
        {
            _b.makeStartingMove(COLOR.RED);
            _b.makeSwitch(0, 14);

        }catch (InvalidMoveException e)
        {
            fail(e.getMessage());
        }
        try
        {
            _b.makeStartingMove(COLOR.RED);
            _b.makeSwitch(0, 15);

        }catch (InvalidMoveException e)
        {
            fail(e.getMessage());
        }
        try
        {
            _b.makeStartingMove(COLOR.RED);
            _b.makeSwitch(0, 16);

        }catch (InvalidMoveException e)
        {
            fail(e.getMessage());
        }

        //no marbles left, should not be able to move out
        assertThrows(InvalidMoveException.class, () -> {_b.makeStartingMove(COLOR.RED);}, "InvalidMove expected");
    }

    @Test
    void makeMoveValidRed()
    {
        //move red out, so we can do a move
        moveRedOut();

        //create a simple move
        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();

        ArrayList<Boolean> from_base = new ArrayList<>();
        ArrayList<Boolean> to_base = new ArrayList<>();

        from.add(new BoardPosition(0, false));
        to.add(new BoardPosition(1, false));

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);

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
    void makeMoveValidYellow()
    {
        //move yellow out, so we can do a move
        try{
            _b.makeStartingMove(COLOR.YELLOW);
        }catch (InvalidMoveException e)
        {
            fail("Cannot move out YELLOW");
        }

        //create a simple move
        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();

        ArrayList<Boolean> from_base = new ArrayList<>();
        ArrayList<Boolean> to_base = new ArrayList<>();

        from.add(new BoardPosition(16, false));
        to.add(new BoardPosition(17, false));

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);

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
        newBoard.set(17, "YELLOW");

        //actual
        BoardData bd = _b.getFormattedBoardState();

        assertEquals(newBoard, bd.getBoard());
    }

    @Test
    void makeMoveValidGreen()
    {
        //move green out, so we can do a move
        try{
            _b.makeStartingMove(COLOR.GREEN);
        }catch (InvalidMoveException e)
        {
            fail("Cannot move out GREEN");
        }

        //create a simple move
        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();


        from.add(new BoardPosition(32, false));
        to.add(new BoardPosition(33, false));

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);

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
        newBoard.set(33, "GREEN");

        //actual
        BoardData bd = _b.getFormattedBoardState();

        assertEquals(newBoard, bd.getBoard());
    }

    @Test
    void makeMoveValidBlue()
    {
        //move green out, so we can do a move
        try{
            _b.makeStartingMove(COLOR.BLUE);
        }catch (InvalidMoveException e)
        {
            fail("Cannot move out BLUE");
        }

        //create a simple move
        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();

        from.add(new BoardPosition(48, false));
        to.add(new BoardPosition(49, false));


        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);

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
        newBoard.set(49, "BLUE");

        //actual
        BoardData bd = _b.getFormattedBoardState();

        assertEquals(newBoard, bd.getBoard());
    }

    @Test
    void makeMoveValidToGoalRed()
    {
        //move red out, so we can do a move
        moveRedOut();

        //create a simple move
        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();


        from.add(new BoardPosition(0, false));
        to.add(new BoardPosition(3, true));//move it into a goal

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);
        move.set_color(COLOR.RED); //need to set the color.

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
    void makeMoveValidToGoalYellow()
    {
        //move yellow out, so we can do a move
        try{
            _b.makeStartingMove(COLOR.YELLOW);
        }catch (InvalidMoveException e)
        {
            fail("Cannot move out YELLOW");
        }

        //create a simple move
        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();

        from.add(new BoardPosition(16, false));
        to.add(new BoardPosition(3, true));//move it into a goal

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);
        move.set_color(COLOR.YELLOW); //need to set the color.

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
        ArrayList<String> newGoal = new ArrayList<>(actualyellow);
        newGoal.set(3, "YELLOW");

        //actual
        BoardData bd = _b.getFormattedBoardState();

        assertEquals(actualBoard, bd.getBoard());
        assertEquals(newGoal, bd.getYellowGoal());
    }

    @Test
    void makeMoveValidToGoalGreen()
    {
        //move yellow out, so we can do a move
        try{
            _b.makeStartingMove(COLOR.GREEN);
        }catch (InvalidMoveException e)
        {
            fail("Cannot move out GREEN");
        }

        //create a simple move
        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();

        from.add(new BoardPosition(32, false));
        to.add(new BoardPosition(3, true));//move it into a goal

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);
        move.set_color(COLOR.GREEN); //need to set the color.

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
        ArrayList<String> newGoal = new ArrayList<>(actualgreen);
        newGoal.set(3, "GREEN");

        //actual
        BoardData bd = _b.getFormattedBoardState();

        assertEquals(actualBoard, bd.getBoard());
        assertEquals(newGoal, bd.getGreenGoal());
    }

    @Test
    void makeMoveValidToGoalBlue()
    {
        //move yellow out, so we can do a move
        try{
            _b.makeStartingMove(COLOR.BLUE);
        }catch (InvalidMoveException e)
        {
            fail("Cannot move out BLUE");
        }

        //create a simple move
        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();

        from.add(new BoardPosition(48, false));
        to.add(new BoardPosition(3, true));//move it into a goal

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);
        move.set_color(COLOR.BLUE); //need to set the color.

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
        ArrayList<String> newGoal = new ArrayList<>(actualblue);
        newGoal.set(3, "BLUE");

        //actual
        BoardData bd = _b.getFormattedBoardState();

        assertEquals(actualBoard, bd.getBoard());
        assertEquals(newGoal, bd.getBlueGoal());
    }

    @Test
    void makeMoveInvalid_Composition()
    {
        //invalid move: invalid move composition
        //move red out, so we can do a move
        moveRedOut();

        //create a simple move
        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();

        from.add(new BoardPosition(0, false));
        to.add(new BoardPosition(3, false));
        to.add(new BoardPosition(1, false)); //added 1 too many. move is now not well formed


        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);


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
    void makeMoveValid_Blocked()
    {
        //move red out, so we can do a move
        moveRedOut();

        //create a simple move
        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();

        ArrayList<Boolean> from_base = new ArrayList<>();
        ArrayList<Boolean> to_base = new ArrayList<>();

        from.add(new BoardPosition(0, false));
        to.add(new BoardPosition(1, false));//move it into a goal

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);

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
        //this should reset the first marble
        try {
            _b.makeMove(move);
        }
        catch (InvalidMoveException e) {
            fail(e.getMessage());
        }

        BoardData bd = _b.getFormattedBoardState();
        ArrayList<String> newBoard = new ArrayList<>(actualBoard);
        newBoard.set(1, "RED");

        assertEquals(newBoard, bd.getBoard());
        assertEquals(3, bd.getRedBase());

    }

    @Test
    void makeMoveValidSkipSeven()
    {
        //move red out, so we can do a move
        moveRedOut();

        //create a simple move
        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();

        from.add(new BoardPosition(0, false));
        to.add(new BoardPosition(5, false));

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);

        //make the first move
        try{
            _b.makeMove(move);
        }catch (InvalidMoveException e)
        {
            fail("Invalid Move");
        }

        //move out one more marble
        moveRedOut();

        move.set_card(new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.CLUBS)); //with a 7!!!
        to.clear();
        to.add(new BoardPosition(7, false)); //move to 7

        //make the second move, skip the 5, this should reset the first marble
        //this should reset the first marble
        try {
            _b.makeMove(move);
        }
        catch (InvalidMoveException e) {
            fail(e.getMessage());
        }

        BoardData bd = _b.getFormattedBoardState();
        ArrayList<String> newBoard = new ArrayList<>(actualBoard);
        newBoard.set(7, "RED");

        assertEquals(newBoard, bd.getBoard());
        assertEquals(3, bd.getRedBase());
    }

    @Test
    void makeMoveInvalid_NoMarble()
    {
        //DO NOT move red out, so we can not do a move

        //create a simple move
        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();

        from.add(new BoardPosition(0, false));
        to.add(new BoardPosition(1, false));

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);

        //make the move
        assertThrows(NoMarbleException.class, ()->{_b.makeMove(move);});
    }

    @Test
    void makeMoveValidMultiple()
    {
        //list has more than 1 entry
        moveRedOut();

        //create a simple move
        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();

        from.add(new BoardPosition(0, false));
        to.add(new BoardPosition(10, false));

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);

        try{
            _b.makeMove(move);
        }catch (InvalidMoveException e)
        {
            fail(e.getMessage());
        }

        moveRedOut();

        //now 2 marbles on the field, move bot of them

        from.clear();
        to.clear();

        from.add(new BoardPosition(10, false));
        from.add(new BoardPosition(0, false));
        to.add(new BoardPosition(12, false));
        to.add(new BoardPosition(5, false));

        try{
            _b.makeMove(move);
        }catch (InvalidMoveException e)
        {
            fail();
        }

        //actual
        BoardData bd = _b.getFormattedBoardState();

        //expected
        ArrayList<String> newBoard = new ArrayList<>(actualBoard);
        newBoard.set(12, "RED");
        newBoard.set(5, "RED");

        assertEquals(newBoard, bd.getBoard());
        assertEquals(2, bd.getRedBase());
    }

    @Test
    void makeMoveValidInGoal()
    {
        //initial move, not important
        moveRedOut();

        //create a simple move
        //color is necessary

        ArrayList<BoardPosition> from = new ArrayList<>();
        ArrayList<BoardPosition> to = new ArrayList<>();

        from.add(new BoardPosition(0, false));
        to.add(new BoardPosition(2, true));

        Move move = new Move();
        move.set_fromPos(from);
        move.set_toPos(to);

        move.set_color(COLOR.RED);


        try{
            _b.makeMove(move);
        }catch (InvalidMoveException e)
        {
            fail(e.getMessage());
        }

        //now next move: move in the goal to a state in the goal
        from.clear();
        to.clear();

        from.add(new BoardPosition(2, true));
        to.add(new BoardPosition(3, true));


        try{
            _b.makeMove(move);
        }catch (InvalidMoveException e)
        {
            fail(e.getMessage());
        }

        //expected
        ArrayList<String> newRed = new ArrayList<>(actualred);
        newRed.set(3, "RED");

        //actual
        BoardData bd = _b.getFormattedBoardState();

        assertEquals(newRed, bd.getRedGoal());
    }

    @Test
    void makeSwitchValid()
    {

        //move red out
        moveRedOut();
        //move green out
        try{
            _b.makeStartingMove(COLOR.GREEN);
        }catch (InvalidMoveException e)
        {
            fail("Cannot move out GREEN");
        }

        //switch these two

        try {
            _b.makeSwitch(0, 32);
        }
        catch (InvalidMoveException e) {
            fail(e.getMessage());
        }

        //expected
        ArrayList<String> newBoard = new ArrayList<>(actualBoard);
        newBoard.set(0, "GREEN");
        newBoard.set(32, "RED");

        //actual
        BoardData bd = _b.getFormattedBoardState();

        assertEquals(newBoard, bd.getBoard());
    }

    @Test
    void makeSwitchOOB()
    {

        //move red out
        moveRedOut();
        //move green out
        try{
            _b.makeStartingMove(COLOR.GREEN);
        }catch (InvalidMoveException e)
        {
            fail("Cannot move out GREEN");
        }

        //switch these two, but one is out of bounds
        InvalidMoveException e = assertThrows(InvalidMoveException.class, () -> {_b.makeSwitch(64, 32);});
        assertEquals("OUT_OF_BOUNDS", e.getCode());
    }
}