package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


public class BoardIsMovePossibleTest {

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



    ArrayList<String> actualBoard;
    ArrayList<String> actualgreen;
    ArrayList<String> actualred;
    ArrayList<String> actualyellow;
    ArrayList<String> actualblue;

    @BeforeEach
    public void beforeEach()
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

    public void blockedMovementForKing(){
        try{

            //create a simple move
            ArrayList<Integer> from = new ArrayList<>();
            ArrayList<Integer> to = new ArrayList<>();

            ArrayList<Boolean> from_base = new ArrayList<>();
            ArrayList<Boolean> to_base = new ArrayList<>();

            _b.makeStartingMove(COLOR.RED);

            from.add(0);
            to.add(3);
            from_base.add(false);
            to_base.add(false);

            Move move = new Move();
            move.set_fromPos(from);
            move.set_toPos(to);
            move.set_fromPosInGoal(from_base);
            move.set_toPosInGoal(to_base);

            _b.makeMove(move);

            _b.makeStartingMove(COLOR.RED);

            to.clear();
            to.add(54);
            _b.makeMove(move);

            _b.makeStartingMove(COLOR.RED);

            to.clear();
            to.add(41);
            _b.makeMove(move);

            _b.makeStartingMove(COLOR.RED);

            to.clear();
            to.add(28);
            _b.makeMove(move);

            _b.makeStartingMove(COLOR.BLUE);



        }
        catch (InvalidMoveException e) {
            fail("Cannot generate StartBoardState");
        }
    }
    public void makeStartBoardState()
    {
        try {
            _b.makeStartingMove(COLOR.RED);
            _b.makeStartingMove(COLOR.GREEN);
            _b.makeStartingMove(COLOR.YELLOW);
            _b.makeStartingMove(COLOR.BLUE);

            //create a simple move
            ArrayList<Integer> from = new ArrayList<>();
            ArrayList<Integer> to = new ArrayList<>();

            ArrayList<Boolean> from_base = new ArrayList<>();
            ArrayList<Boolean> to_base = new ArrayList<>();

            from.add(0);
            to.add(60);
            from_base.add(false);
            to_base.add(false);

            Move move = new Move();
            move.set_fromPos(from);
            move.set_toPos(to);
            move.set_fromPosInGoal(from_base);
            move.set_toPosInGoal(to_base);

            _b.makeMove(move);

            _b.makeStartingMove(COLOR.RED);

            from.clear();
            from.add(0);
            to.clear();
            to.add(63);

            _b.makeMove(move);

            from.clear();
            from.add(16);
            to.clear();
            to.add(13);

            _b.makeMove(move);

        }
        catch (InvalidMoveException e) {
            fail("Cannot generate StartBoardState");
        }
    }

    //this is a test for shitaos method, ignore
    @Test
    public void validStartingMove()
    {
        Move m = new Move();
        m.set_card(king);
        m.set_color(COLOR.RED);
        m.set_fromPos(new ArrayList<>(Arrays.asList(-1)));
        m.set_toPos(new ArrayList<>(Arrays.asList(0)));
        m.set_fromPosInGoal(new ArrayList<>(Arrays.asList(false)));
        m.set_toPosInGoal(new ArrayList<>(Arrays.asList(false)));

        //should work

        try {
            assertTrue(_b.isValidMove(m));
        }
        catch (InvalidMoveException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void startingMovePossible()
    {
        assertTrue(_b.isAnyMovePossible(king, COLOR.RED));
        assertTrue(_b.isAnyMovePossible(ace, COLOR.GREEN));
        //assertTrue(_b.isAnyMovePossible(joker, COLOR.BLUE)); //todo enable when joker works
    }

    //@Test
    public void noStartingMovePossibleAnymore()
    {
        blockedMovementForKing();
        assertFalse(_b.isAnyMovePossible(king, COLOR.RED));
    }

    //@Test
    public void movementAce11(){

    }
    //@Test
    public void movementAce1() {
    }

    //@Test
    public void movement2(){}

    //@Test
    public void movement3(){}
    //@Test
    public void movement5(){}
    //@Test
    public void movement6(){}
    //@Test
    public void movement8(){}
    //@Test
    public void movement9(){}
    //@Test
    public void movement10(){}
    //@Test
    public void movementQueen(){}

    //@Test
    public void movement7Normaleat(){}

    //@Test
    public void movement7Separate2(){}
    //@Test
    public void movement7Separate3(){}
    //@Test
    public void movement7Separate4(){}

    //@Test
    public void movement7Separate1Eat(){}

    //@Test
    public void movement7Separate2Eat(){}

    //@Test
    public void movement7Separate3Eat(){}

    //@Test
    public void movement7Separate4Eat(){}

    //@Test
    public void  swapJack(){}

    public void swapJackNotPossible(){}


    //@Test
    public void movementToEat1(){

    }






    @Test
    public void simpleMovePossible()
    {
        makeStartBoardState(); // we should have 2 options here to move with a 12
        assertTrue(_b.isAnyMovePossible(twelve, COLOR.RED));
        assertTrue(_b.isAnyMovePossible(eight, COLOR.RED));
        assertTrue(_b.isAnyMovePossible(king, COLOR.RED));
        assertTrue(_b.isAnyMovePossible(seven, COLOR.RED));
    }





    //@Test
    public void winningConditionRightTest(){

    }

    //@Test
    public void winningConditionWrongTest(){

    }


}
