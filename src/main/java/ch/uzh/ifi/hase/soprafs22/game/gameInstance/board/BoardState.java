package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import java.util.ArrayList;

import ch.uzh.ifi.hase.soprafs22.game.constants.MARBLE;

public class BoardState {
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

    public BoardState()
    {

    }

    public BoardState(BoardState bs)
    {
        this._redBase = bs._redBase;
        this._blueBase = bs._blueBase;
        this._greenBase =bs._greenBase;
        this._yellowBase = bs._yellowBase;
        this._redGoal = new ArrayList<>(bs._redGoal);
        this._blueGoal = new ArrayList<>(bs._blueGoal);
        this._greenGoal = new ArrayList<>(bs._greenGoal);
        this._yellowGoal = new ArrayList<>(bs._yellowGoal);
        this.REDBLOCKED = bs.REDBLOCKED;
        this.GREENBLOCKED = bs.GREENBLOCKED;
        this.BLUEBLOCKED = bs.BLUEBLOCKED;
        this.YELLOWBLOCKED = bs.YELLOWBLOCKED;
    }
}
