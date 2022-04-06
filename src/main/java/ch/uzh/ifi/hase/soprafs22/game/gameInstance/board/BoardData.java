package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import java.util.ArrayList;
import java.util.Map;

public class BoardData {
    private ArrayList<String> board;

    private ArrayList<String> redGoal;
    private ArrayList<String> greenGoal;
    private ArrayList<String> blueGoal;
    private ArrayList<String> yellowGoal;

    private int redBase;
    private int greenBase;
    private int blueBase;
    private int yellowBase;

    private Map<Integer, String> colorMapping;

    private String lastPlayedCard;

}
