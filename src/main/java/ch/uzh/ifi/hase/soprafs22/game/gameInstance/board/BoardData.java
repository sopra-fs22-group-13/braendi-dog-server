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

    public BoardData(ArrayList<String> board, ArrayList<String> redGoal, ArrayList<String> greenGoal, ArrayList<String> blueGoal, ArrayList<String> yellowGoal, int redBase, int greenBase, int blueBase, int yellowBase) {
        this.board = new ArrayList<>(board);
        this.redGoal = new ArrayList<>(redGoal);
        this.greenGoal = new ArrayList<>(greenGoal);
        this.blueGoal = new ArrayList<>(blueGoal);
        this.yellowGoal = new ArrayList<>(yellowGoal);
        this.redBase = redBase;
        this.greenBase = greenBase;
        this.blueBase = blueBase;
        this.yellowBase = yellowBase;
    }

    public void setColorMapping(Map<Integer, String> colorMapping) {
        this.colorMapping = colorMapping;
    }

    public void setLastPlayedCard(String lastPlayedCard) {
        this.lastPlayedCard = lastPlayedCard;
    }

    public ArrayList<String> getBoard() {
        return new ArrayList<>(board);
    }

    public ArrayList<String> getRedGoal() {
        return new ArrayList<>(redGoal);
    }

    public ArrayList<String> getGreenGoal() {
        return new ArrayList<>(greenGoal);
    }

    public ArrayList<String> getBlueGoal() {
        return new ArrayList<>(blueGoal);
    }

    public ArrayList<String> getYellowGoal() {
        return new ArrayList<>(yellowGoal);
    }

    public int getRedBase() {
        return redBase;
    }

    public int getGreenBase() {
        return greenBase;
    }

    public int getBlueBase() {
        return blueBase;
    }

    public int getYellowBase() {
        return yellowBase;
    }

    public Map<Integer, String> getColorMapping() {
        return colorMapping;
    }

    public String getLastPlayedCard() {
        return lastPlayedCard;
    }
}
