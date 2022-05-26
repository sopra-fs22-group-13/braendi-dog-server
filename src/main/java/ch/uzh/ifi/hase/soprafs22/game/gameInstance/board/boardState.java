package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import java.util.ArrayList;

import ch.uzh.ifi.hase.soprafs22.game.constants.MARBLE;

public class boardState {
    public ArrayList<MARBLE> _redGoal = new ArrayList<>();
    public ArrayList<MARBLE> _greenGoal = new ArrayList<>();
    public ArrayList<MARBLE> _blueGoal = new ArrayList<>();
    public ArrayList<MARBLE> _yellowGoal = new ArrayList<>();

    public int _redBase = 4;
    public int _greenBase = 4;
    public int _blueBase = 4;
    public int _yellowBase = 4;

    public boolean REDBLOCKED = false;
    public boolean GREENBLOCKED = false;
    public boolean BLUEBLOCKED = false;
    public boolean YELLOWBLOCKED = false;
}
