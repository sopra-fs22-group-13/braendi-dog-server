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

import java.security.cert.TrustAnchor;
import java.time.Year;
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

    public void makeFinishigMove(int i){
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


    @Test
    public void moveToBlockedMarble(){

        try{
            _b.makeStartingMove(COLOR.RED);
            _b.makeStartingMove(COLOR.BLUE);
            makeMove(0,14);

        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.TWO, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));

    }


    @Test   //todo This should work
    public void moveToTheSameMarble(){

        try{
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,13);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,9);

        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.TWO, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(9,10);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
    }

    @Test
    public void noStartingMovePossible()
    {
        try{
            makeFinishigMove(3);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,3);
            _b.makeStartingMove(COLOR.BLUE);
        }
        catch (InvalidMoveException e) {
            fail("Cannot generate StartBoardState");
        }

        assertFalse(_b.isAnyMovePossible(king, COLOR.RED));
    }


    @Test
    public void movementAce11(){
        try{
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,5);
            makeFinishigMove(3);
        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }

    @Test
    public void movementAce1() {
        try{
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,15);
            makeFinishigMove(3);
        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.ACE, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }



    @Test
    public void movement2(){
        try{
            makeFinishigMove(3); //red puts in goal 3 marbles
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,14);

        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.TWO, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(16,17);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));

    }

    @Test
    public void movement3(){
        try{
            makeFinishigMove(3); //red puts in goal 3 marbles
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,13);

        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.THREE, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(16,17);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }
    @Test
    public void movement5(){
        try{
            makeFinishigMove(3); //red puts in goal 3 marbles
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,11);

        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(16,17);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }
    @Test
    public void movement6(){
        try{
            makeFinishigMove(3); //red puts in goal 3 marbles
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,10);

        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.SIX, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(16,17);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }

    @Test
    public void movement8(){
        try{
            makeFinishigMove(3); //red puts in goal 3 marbles
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,8);

        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.EIGHT, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(16,17);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }
    @Test
    public void movement9(){
        try{
            makeFinishigMove(3); //red puts in goal 3 marbles
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,7);

        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.NINE, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(16,17);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }
    @Test
    public void movement10(){
        try{
            makeFinishigMove(3); //red puts in goal 3 marbles
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,6);

        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.TEN, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(16,17);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }

    @Test
    public void movementQueen(){
        try{
            makeFinishigMove(3); //red puts in goal 3 marbles
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,4);

        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.QUEEN, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(16,17);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }

    @Test
    public void movement7Normal(){
        try{
            makeFinishigMove(3); //red puts in goal 3 marbles
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,9);

        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(16,17);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }    @Test
    public void movement7Separate2(){
        //the marble are putted behind other marbles so the sum is less the 7, then it should fail, moving one of the bloking marble whould then help
        try{
            makeFinishigMove(2);
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.GREEN);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,9);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,31);

        }
        catch (InvalidMoveException e) {
            fail();
        }

        Card card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(9,8);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));;
    }

    @Test
    public void movement7Separate2SecondMarbleMoves(){
        //the marble are putted behind other marbles so the sum is less the 7, then it should fail, moving one of the bloking marble whould then help
        try{
            makeFinishigMove(2);
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.GREEN);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,10);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,30);

        }
        catch (InvalidMoveException e) {
            fail();
        }

        Card card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(10,9);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));;
    }

    @Test
    public void movement7Separate2WithSameColorMarbleInFront(){
        //the marble are putted behind other marbles so the sum is less the 7, then it should fail, moving one of the bloking marble whould then help
        try{
            makeFinishigMove(2);
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,8);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,15);

        }
        catch (InvalidMoveException e) {
            fail();
        }

        Card card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(8,7);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));;
    }
    @Test
    public void movement7Separate3(){
        try{
            makeFinishigMove(1);
            _b.makeStartingMove(COLOR.GREEN);
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,15);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,14);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,27);

        }
        catch (InvalidMoveException e) {
            fail();
        }
        Card card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(27,26);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));;
    }

    @Test
    public void movement7Separate3SecondMarbleMoves(){
        try{
            makeFinishigMove(1);
            _b.makeStartingMove(COLOR.GREEN);
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,15);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,13);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,28);

        }
        catch (InvalidMoveException e) {
            fail();
        }
        Card card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(28,27);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));;
    }
    @Test
    public void movement7Separate3SecondAndThirdMarbleMoves(){
        try{
            makeFinishigMove(1);
            _b.makeStartingMove(COLOR.GREEN);
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,14);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,13);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,27);

        }
        catch (InvalidMoveException e) {
            fail();
        }
        Card card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(27,26);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));;
    }

    @Test
    public void movement7Separate3WithSameMarbleInFront(){
        try{
            makeFinishigMove(1);
            _b.makeStartingMove(COLOR.GREEN);
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,15);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,8);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,31);

        }
        catch (InvalidMoveException e) {
            fail();
        }
        Card card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(8,7);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }


    @Test
    public void movement7Separate4(){
        try{
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.GREEN);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,15);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,14);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,25);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,13);

        }
        catch (InvalidMoveException e) {
            fail();
        }

        Card card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(25,24);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }

    //@Test
    public void movement7Separate1Eat(){
        try{
            makeFinishigMove(3);
            _b.makeStartingMove(COLOR.BLUE);
            makeMove(16,8);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,1);
        }
        catch (InvalidMoveException e) {
            fail();
        }

        Card card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }

    @Test
    public void movement7Separate7Eat(){
        try{
            makeFinishigMove(3);
            _b.makeStartingMove(COLOR.BLUE);
            makeMove(16,2);
            _b.makeStartingMove(COLOR.BLUE);
            makeMove(16,3);
            _b.makeStartingMove(COLOR.BLUE);
            makeMove(16,4);
            _b.makeStartingMove(COLOR.BLUE);
            makeMove(16,5);
            _b.makeStartingMove(COLOR.GREEN);
            makeMove(32,6);
            _b.makeStartingMove(COLOR.GREEN);
            makeMove(32,7);
            _b.makeStartingMove(COLOR.GREEN);
            makeMove(32,8);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,1);
        }
        catch (InvalidMoveException e) {
            fail();
        }

        Card card = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }


    @Test
    public void movement4(){
        try{
            _b.makeStartingMove(COLOR.BLUE);
            _b.makeStartingMove(COLOR.RED);
            makeMove(0,20);

        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.FOUR, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(20,19);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(19,21);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
    }


    @Test
    public void  swapJack(){
        try{
            _b.makeStartingMove(COLOR.RED);

        }catch (InvalidMoveException e){
            fail();
        }
        Card card = new Card(CARDVALUE.JACK, CARDTYPE.DEFAULT,CARDSUITE.CLUBS);
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED)); // no other marble in main circle

        try{
            _b.makeStartingMove(COLOR.BLUE);

        }catch (InvalidMoveException e){
            fail();
        }
        assertFalse(_b.isAnyMovePossible(card, COLOR.RED));// Blue is blocked not possible to the swap

        makeMove(16,17);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));
        makeMove(0,1);
        assertTrue(_b.isAnyMovePossible(card, COLOR.RED));


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



    @Test
    public void winningConditionRightTest(){
        assertFalse(_b.checkWinningCondition(COLOR.RED));
        assertFalse(_b.checkWinningCondition(COLOR.YELLOW));
        makeFinishigMove(4);
        assertTrue(_b.checkWinningCondition(COLOR.RED));
        assertFalse(_b.checkWinningCondition(COLOR.YELLOW));

    }

}
