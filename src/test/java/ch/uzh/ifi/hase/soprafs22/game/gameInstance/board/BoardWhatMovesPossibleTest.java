package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardPosition;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class BoardWhatMovesPossibleTest {

    Board _b;

    Card joker = new Card(CARDVALUE.JOKER, CARDTYPE.JOKER, CARDSUITE.CLUBS);
    Card king = new Card(CARDVALUE.KING, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
    Card ace = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
    Card seven = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
    Card four = new Card(CARDVALUE.FOUR, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
    Card eight = new Card(CARDVALUE.EIGHT, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
    Card jack = new Card(CARDVALUE.JACK, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
    Card three = new Card(CARDVALUE.THREE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
    Card twelve = new Card(CARDVALUE.QUEEN, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);

    @BeforeEach
    public void beforeEach() {
        _b = new Board();
    }

    public void makeMove(int fromPos, int toPos){
        try{

            //create a simple move
            ArrayList<Integer> from = new ArrayList<>();
            ArrayList<Integer> to = new ArrayList<>();
            ArrayList<Boolean> from_base = new ArrayList<>();
            ArrayList<Boolean> to_base = new ArrayList<>();

            from.add(fromPos);
            to.add(toPos);
            from_base.add(false);
            to_base.add(false);

            Move move = new Move();
            move.set_fromPos(from);
            move.set_toPos(to);
            move.set_fromPosInGoal(from_base);
            move.set_toPosInGoal(to_base);

            _b.makeMove(move);

        }
        catch (InvalidMoveException e) {
            fail("Cannot generate StartBoardState");
        }
    }

    public void makeFinishingMove(int i){
        try{
            for (int j = 0;j<i; j++) {
                _b.makeStartingMove(COLOR.RED);
                //create a simple move
                ArrayList<Integer> from = new ArrayList<>();
                ArrayList<Integer> to = new ArrayList<>();
                ArrayList<Boolean> from_base = new ArrayList<>();
                ArrayList<Boolean> to_base = new ArrayList<>();

                from.add(0);
                to.add(j);
                from_base.add(false);
                to_base.add(true);

                Move move = new Move();
                move.set_fromPos(from);
                move.set_toPos(to);
                move.set_fromPosInGoal(from_base);
                move.set_toPosInGoal(to_base);
                move.set_color(COLOR.RED);

                _b.makeMove(move);
            }

        }
        catch (InvalidMoveException e) {
            fail("Cannot generate StartBoardState");
        }
    }

    private boolean boardPositionsEqual(List<BoardPosition> l1, List<BoardPosition> l2)
    {
        if(l1.size() != l2.size()) return false;

        boolean allEqual = true;
        for (int i = 0; i < l1.size(); i++) {
            if(!l1.get(i).equalsContent(l2.get(i))) allEqual = false;
        }

        return allEqual;
    }

    @Test
    public void startingExpected()
    {
        List<BoardPosition> expected = new ArrayList<>();
        expected.add(new BoardPosition(16, false));

        BoardPosition startPos = new BoardPosition(-1, false);

        assertTrue(boardPositionsEqual(expected, _b.whatMovesPossible(startPos, new Card("KD"), COLOR.YELLOW)));
    }

    @Test
    public void simpleNumber()
    {
        //setup
        try {
            _b.makeStartingMove(COLOR.YELLOW);
        }
        catch (InvalidMoveException e) {
            fail();
        }


        List<BoardPosition> expected = new ArrayList<>();
        expected.add(new BoardPosition(21, false));

        BoardPosition startPos = new BoardPosition(16, false);

        assertTrue(boardPositionsEqual(expected, _b.whatMovesPossible(startPos, new Card("5D"), COLOR.YELLOW)));
    }

    @Test
    public void four()
    {
        //setup
        try {
            _b.makeStartingMove(COLOR.YELLOW);
        }
        catch (InvalidMoveException e) {
            fail();
        }


        List<BoardPosition> expected = new ArrayList<>();
        expected.add(new BoardPosition(20, false));
        expected.add(new BoardPosition(12, false));


        BoardPosition startPos = new BoardPosition(16, false);

        assertTrue(boardPositionsEqual(expected, _b.whatMovesPossible(startPos, new Card("4D"), COLOR.YELLOW)));
    }

    @Test
    public void jack()
    {
        //setup
        try {
            _b.makeStartingMove(COLOR.YELLOW);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0, 2);
            _b.makeStartingMove(COLOR.GREEN);
            makeMove(32, 34);
            _b.makeStartingMove(COLOR.GREEN); //this should be blocked
        }
        catch (InvalidMoveException e) {
            fail();
        }


        List<BoardPosition> expected = new ArrayList<>();
        expected.add(new BoardPosition(2, false));
        expected.add(new BoardPosition(34, false));

        BoardPosition startPos = new BoardPosition(16, false);

        assertTrue(boardPositionsEqual(expected, _b.whatMovesPossible(startPos, new Card("JD"), COLOR.YELLOW)));
    }

    @Test
    public void goalAndNormal()
    {
        //setup
        try {
            _b.makeStartingMove(COLOR.YELLOW);
            makeMove(16, 14);
        }
        catch (InvalidMoveException e) {
            fail();
        }


        List<BoardPosition> expected = new ArrayList<>();
        expected.add(new BoardPosition(20, false));
        expected.add(new BoardPosition(3, true));

        BoardPosition startPos = new BoardPosition(14, false);

        assertTrue(boardPositionsEqual(expected, _b.whatMovesPossible(startPos, new Card("6D"), COLOR.YELLOW)));
    }

    @Test
    public void goalAndNormalFour()
    {
        //setup
        try {
            _b.makeStartingMove(COLOR.YELLOW);
            makeMove(16, 14);
        }
        catch (InvalidMoveException e) {
            fail();
        }


        List<BoardPosition> expected = new ArrayList<>();
        expected.add(new BoardPosition(18, false));
        expected.add(new BoardPosition(10, false));

        expected.add(new BoardPosition(1, true));

        BoardPosition startPos = new BoardPosition(14, false);

        assertTrue(boardPositionsEqual(expected, _b.whatMovesPossible(startPos, new Card("4D"), COLOR.YELLOW)));
    }

    @Test
    public void JokerShouldReturnNothing()
    {
        //setup
        try {
            _b.makeStartingMove(COLOR.YELLOW);
        }
        catch (InvalidMoveException e) {
            fail();
        }


        List<BoardPosition> expected = new ArrayList<>();

        BoardPosition startPos = new BoardPosition(16, false);

        assertTrue(boardPositionsEqual(expected, _b.whatMovesPossible(startPos, new Card("Joker"), COLOR.YELLOW)));
    }
}
