package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.MARBLE;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void getFormattedBoardState() {

        //get after creation.
        Board b = new Board();
        BoardData bd = b.getFormattedBoardState();


        ArrayList<String> actualBoard = new ArrayList<>();
        ArrayList<String> actualgreen = new ArrayList<>();
        ArrayList<String> actualred = new ArrayList<>();
        ArrayList<String> actualyellow = new ArrayList<>();
        ArrayList<String> actualblue = new ArrayList<>();

        while(actualBoard.size() < 64) actualBoard.add("NONE");

        while(actualgreen.size() < 4) actualgreen.add("NONE");
        while(actualred.size() < 4) actualred.add("NONE");
        while(actualyellow.size() < 4) actualyellow.add("NONE");
        while(actualblue.size() < 4) actualblue.add("NONE");

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
}