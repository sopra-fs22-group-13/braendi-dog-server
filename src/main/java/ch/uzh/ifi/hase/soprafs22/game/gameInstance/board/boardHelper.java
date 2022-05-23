package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import java.util.ArrayList;

import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.constants.MARBLE;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.NoMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;

//contains the board helper functions
public class boardHelper {

    private ArrayList<MARBLE> _mainCircle = new ArrayList<>();

    private ArrayList<MARBLE> _redGoal = new ArrayList<>();
    private ArrayList<MARBLE> _greenGoal = new ArrayList<>();
    private ArrayList<MARBLE> _blueGoal = new ArrayList<>();
    private ArrayList<MARBLE> _yellowGoal = new ArrayList<>();

    private int _redBase = 4;
    private int _greenBase = 4;
    private int _blueBase = 4;
    private int _yellowBase = 4;

    private boolean REDBLOCKED = false;
    private boolean GREENBLOCKED = false;
    private boolean BLUEBLOCKED = false;
    private boolean YELLOWBLOCKED = false;

    private final int REDINTERSECT = 0;
    private final int BLUEINTERSECT = 48;
    private final int GREENINTERSECT = 32;
    private final int YELLOWINTERSECT = 16;

    public boardHelper(
            ArrayList<MARBLE> mainCircle,
            ArrayList<MARBLE> redGoal,
            ArrayList<MARBLE> greenGoal,
            ArrayList<MARBLE> blueGoal,
            ArrayList<MARBLE> yellowGoal,
            int redBase,
            int greenBase,
            int blueBase,
            int yellowBase,
            boolean redBlocked,
            boolean greenBlocked,
            boolean blueBlocked,
            boolean yellowBlocked) {
        this._mainCircle = mainCircle;
        this._redGoal = redGoal;
        this._greenGoal = greenGoal;
        this._blueGoal = blueGoal;
        this._yellowGoal = yellowGoal;
        this._redBase = redBase;
        this._greenBase = greenBase;
        this._blueBase = blueBase;
        this._yellowBase = yellowBase;
        this.REDBLOCKED = redBlocked;
        this.GREENBLOCKED = greenBlocked;
        this.BLUEBLOCKED = blueBlocked;
        this.YELLOWBLOCKED = yellowBlocked;
    }

    //setting up the getters
    public ArrayList<MARBLE> getMainCircle() {
        return _mainCircle;
    }

    public ArrayList<MARBLE> getRedGoal() {
        return _redGoal;
    }

    public ArrayList<MARBLE> getGreenGoal() {
        return _greenGoal;
    }

    public ArrayList<MARBLE> getBlueGoal() {
        return _blueGoal;
    }

    public ArrayList<MARBLE> getYellowGoal() {
        return _yellowGoal;
    }

    public int getRedBase() {
        return _redBase;
    }

    public int getGreenBase() {
        return _greenBase;
    }

    public int getBlueBase() {
        return _blueBase;
    }

    public int getYellowBase() {
        return _yellowBase;
    }

    public boolean getREDBLOCKED() {
        return REDBLOCKED;
    }

    public boolean getGREENBLOCKED() {
        return GREENBLOCKED;
    }

    public boolean getBLUEBLOCKED() {
        return BLUEBLOCKED;
    }

    public boolean getYELLOWBLOCKED() {
        return YELLOWBLOCKED;
    }

    public void makeMove(Move move) throws InvalidMoveException {
        // expects the move to be valid
        if (move == null || !move.isWellFormed()) {
            throw new InvalidMoveException("BAD_STRUCTURE", "Bad move structure");
        }

        // For each move, make it
        for (int i = 0; i < move.get_fromPos().size(); i++) {
            int fromPos = move.get_fromPos().get(i).getIndex();
            int toPos = move.get_toPos().get(i).getIndex();
            boolean startsInGoal = move.get_fromPos().get(i).isInGoal();
            boolean endsInGoal = move.get_toPos().get(i).isInGoal();
            // unblock if it was on an intersection
            if (!startsInGoal) {
                switch (fromPos) {
                    case REDINTERSECT:
                        REDBLOCKED = false;
                        break;
                    case GREENINTERSECT:
                        GREENBLOCKED = false;
                        break;
                    case BLUEINTERSECT:
                        BLUEBLOCKED = false;
                        break;
                    case YELLOWINTERSECT:
                        YELLOWBLOCKED = false;
                        break;
                }
            }
            // do the move
            if (endsInGoal) {
                movePositions(fromPos, toPos, move.get_color(), startsInGoal,
                        move.get_card() != null ? move.get_card().isSeven() : false);
            } else {
                movePositions(fromPos, toPos, move.get_card() != null ? move.get_card().isSeven() : false);
            }

        }
    }

    private void movePositions(int pos1, int pos2, boolean removeInbetweeners)
            throws InvalidMoveException, IndexOutOfBoundsException {
        if (pos1 < 0 || pos1 >= 64 || pos2 < 0 || pos2 >= 64)
            throw new IndexOutOfBoundsException("the positions have to be in range 0-63 (inclusive)");

        MARBLE m1 = _mainCircle.get(pos1);
        MARBLE m2 = _mainCircle.get(pos2);

        if (m1 == MARBLE.NONE)
            throw new NoMarbleException();
        if (m2 != MARBLE.NONE) {
            // reset the problem marble
            resetMarble(pos2);
        }

        // all good, make the move
        if (removeInbetweeners) {
            resetInbetweeners(pos1, pos2);
        }

        setMarbleAtPosition(pos2, m1);
        setMarbleAtPosition(pos1, MARBLE.NONE);
    }

    private void resetMarble(int pos) throws NoMarbleException, IndexOutOfBoundsException {
        if (pos < 0 || pos >= 64)
            throw new IndexOutOfBoundsException("the position has to be in range 0-63 (inclusive)");

        if (_mainCircle.get(pos) == MARBLE.NONE)
            throw new NoMarbleException();

        MARBLE m = _mainCircle.get(pos);

        setMarbleAtPosition(pos, MARBLE.NONE);
        switch (m) {
            case RED:
                _redBase++;
                break;
            case BLUE:
                _blueBase++;
                break;
            case GREEN:
                _greenBase++;
                break;
            case YELLOW:
                _yellowBase++;
                break;
        }
    }

    private void movePositions(int pos1, int pos2, COLOR goalColor, boolean startInGoal, boolean removeInbetweeners)
            throws InvalidMoveException, IndexOutOfBoundsException {
        MARBLE m1;
        MARBLE m2;

        int colorintersect = 0;

        ArrayList<MARBLE> coloredGoalList = _redGoal;
        // get the respective color
        switch (goalColor) {
            case RED:
                coloredGoalList = _redGoal;
                colorintersect = REDINTERSECT;
                break;
            case BLUE:
                coloredGoalList = _blueGoal;
                colorintersect = BLUEINTERSECT;
                break;
            case YELLOW:
                coloredGoalList = _yellowGoal;
                colorintersect = YELLOWINTERSECT;
                break;
            case GREEN:
                coloredGoalList = _greenGoal;
                colorintersect = GREENINTERSECT;
        }

        if (startInGoal) {
            if (pos1 < 0 || pos2 >= 4)
                throw new IndexOutOfBoundsException(
                        "if startInGoal is true, the pos1 can be in the range 0-3 (inclusive)");
            m1 = coloredGoalList.get(pos1);
        } else {
            if (pos1 < 0 || pos2 >= 64)
                throw new IndexOutOfBoundsException(
                        "if startInGoal is false, the pos1 can be in the range 0-63 (inclusive)");
            m1 = _mainCircle.get(pos1);
        }

        if (pos2 < 0 || pos2 >= 4)
            throw new IndexOutOfBoundsException("the pos2 can be in the range 0-3 (inclusive)");
        m2 = coloredGoalList.get(pos2);

        // we now have the start and end position. we move from pos1 to pos2

        if (m1 == MARBLE.NONE)
            throw new NoMarbleException();
        if (m2 != MARBLE.NONE) {
            // reset the problem marble
            resetMarble(pos2);
        }

        // all good, make the move

        if (removeInbetweeners && !startInGoal) {
            resetInbetweeners(pos1, colorintersect);
        }

        setMarbleAtPosition(pos2, m1, goalColor);

        if (startInGoal) {
            setMarbleAtPosition(pos1, MARBLE.NONE, goalColor);
        } else {
            setMarbleAtPosition(pos1, MARBLE.NONE);
        }
    }

    private boolean setMarbleAtPosition(int position, MARBLE marble) {
        _mainCircle.set(position, marble);
        return true;
    }

    private boolean setMarbleAtPosition(int position, MARBLE marble, COLOR goalColor) {
        switch (goalColor) {
            case RED:
                if (marble != MARBLE.RED && marble != MARBLE.NONE)
                    return false;
                _redGoal.set(position, marble);
                break;
            case BLUE:
                if (marble != MARBLE.BLUE && marble != MARBLE.NONE)
                    return false;
                _blueGoal.set(position, marble);
                break;
            case YELLOW:
                if (marble != MARBLE.YELLOW && marble != MARBLE.NONE)
                    return false;
                _yellowGoal.set(position, marble);
                break;
            case GREEN:
                if (marble != MARBLE.GREEN && marble != MARBLE.NONE)
                    return false;
                _greenGoal.set(position, marble);
                break;
            default:
                return false;
        }
        return true;
    }

    private COLOR getColorFromPosition(int pos) throws NoMarbleException {
        if (pos < 0 || pos >= 64)
            throw new IndexOutOfBoundsException("the position has to be in range 0-63 (inclusive)");
        MARBLE m = _mainCircle.get(pos);

        switch (m) {
            case GREEN:
                return COLOR.GREEN;
            case BLUE:
                return COLOR.BLUE;
            case RED:
                return COLOR.RED;
            case YELLOW:
                return COLOR.YELLOW;
            default:
                throw new NoMarbleException();
        }
    }

    /**
     * resets any inbetweeners (exclusive, inclusive)
     */
    private void resetInbetweeners(int pos1, int pos2) throws NoMarbleException {
        ArrayList<Integer> relevantInbetweeners = getInbetweeners(pos1, pos2);

        for (Integer inb : relevantInbetweeners) {
            resetMarble(inb);
        }
    }

    private ArrayList<Integer> getInbetweenersBackwards(int pos1, int pos2) throws IndexOutOfBoundsException {
        int newPos1 = pos2;
        int newPos2 = pos1;

        // manage exclusive/inclusive correctly
        newPos1 = newPos1 - 1;
        newPos2 = newPos2 - 1;

        // fix looping
        if (newPos1 < 0) {
            newPos1 = 64 + newPos1;
        }
        if (newPos2 < 0) {
            newPos2 = 64 + newPos2;
        }

        return getInbetweeners(newPos1, newPos2);
    }

    private boolean blockedIntersect(Move move, int startPos, int endPos, boolean forward) {
        if (forward) {
            ArrayList<Integer> blockPos = getInbetweeners(startPos, endPos);
            ArrayList<Integer> blockIntersect = new ArrayList<>();
            if (blockPos.size() > 0) {
                for (int i = 0; i < blockPos.size(); i++) {
                    if (blockPos.get(i) == REDINTERSECT || blockPos.get(i) == YELLOWINTERSECT
                            || blockPos.get(i) == GREENINTERSECT || blockPos.get(i) == BLUEINTERSECT) {
                        blockIntersect.add(blockPos.get(i));
                    }
                }
                for (int i = 0; i < blockIntersect.size(); i++) {
                    switch (blockIntersect.get(i)) {
                        case REDINTERSECT:
                            if (REDBLOCKED) {
                                return true;
                            }
                            break;
                        case YELLOWINTERSECT:
                            if (YELLOWBLOCKED) {
                                return true;
                            }
                            break;
                        case GREENINTERSECT:
                            if (GREENBLOCKED) {
                                return true;
                            }
                            break;
                        case BLUEINTERSECT:
                            if (BLUEBLOCKED) {
                                return true;
                            }
                            break;
                    }
                }
            }
        } else {
            ArrayList<Integer> blockPos = getInbetweenersBackwards(startPos, endPos);
            ArrayList<Integer> blockIntersect = new ArrayList<>();
            if (blockPos.size() > 0) {
                for (int i = 0; i < blockPos.size(); i++) {
                    if (blockPos.get(i) == REDINTERSECT || blockPos.get(i) == YELLOWINTERSECT
                            || blockPos.get(i) == GREENINTERSECT || blockPos.get(i) == BLUEINTERSECT) {
                        blockIntersect.add(blockPos.get(i));
                    }
                }
                for (int i = 0; i < blockIntersect.size(); i++) {
                    switch (blockIntersect.get(i)) {
                        case REDINTERSECT:
                            if (REDBLOCKED) {
                                return true;
                            }
                            break;
                        case YELLOWINTERSECT:
                            if (YELLOWBLOCKED) {
                                return true;
                            }
                            break;
                        case GREENINTERSECT:
                            if (GREENBLOCKED) {
                                return true;
                            }
                            break;
                        case BLUEINTERSECT:
                            if (BLUEBLOCKED) {
                                return true;
                            }
                            break;
                    }
                }
            }
        }
        return false;
    }

    private ArrayList<Integer> getInbetweeners(int pos1, int pos2) throws IndexOutOfBoundsException {
        ArrayList<Integer> importantInbetweeners = new ArrayList<>();
        if (pos1 < 0 || pos1 >= 64 || pos2 < 0 || pos2 >= 64)
            throw new IndexOutOfBoundsException("the positions have to be in range 0-63 (inclusive)");

        if (pos1 < pos2) {
            // no loop
            for (int i = pos1 + 1; i <= pos2; i++) {
                if (_mainCircle.get(i) != MARBLE.NONE) {
                    importantInbetweeners.add(i);
                }
            }
        } else {
            // get to half way
            for (int i = pos1 + 1; i < 64; i++) {
                if (_mainCircle.get(i) != MARBLE.NONE) {
                    importantInbetweeners.add(i);
                }
            }
            // rest
            for (int i = 0; i <= pos2; i++) {
                if (_mainCircle.get(i) != MARBLE.NONE) {
                    importantInbetweeners.add(i);
                }
            }
        }

        return importantInbetweeners;
    }

    private int getDistanceInBetween(int startPosition, int endPosition) throws IndexOutOfBoundsException {
        return getDistanceInBetween(startPosition, endPosition, true);
    }

    /**
     * gets the next position when moving a certain distance on the main field,
     * either forward or backwards
     */
    private int getDistanceInBetween(int startPosition, int endPosition, boolean forward)
            throws IndexOutOfBoundsException {
        if (startPosition < 0 || startPosition >= 64 || endPosition < 0 || endPosition >= 64)
            throw new IndexOutOfBoundsException(
                    "Index is wrong: the main ring has indices between 0 and 63 while goals reach from 0 to 3");
        if (forward) {
            if (startPosition <= endPosition) // we do not loop over
            {
                return endPosition - startPosition;
            } else // we loop over the edge
            {
                int rest = 63 - startPosition;
                return endPosition + rest + 1;
            }
        } else // we move backwards
        {
            if (endPosition <= startPosition) // we do not loop over
            {
                return startPosition - endPosition;
            } else // we loop over the edge
            {
                int rest = 63 - endPosition;
                return startPosition + rest + 1;
            }
        }
    }
}
