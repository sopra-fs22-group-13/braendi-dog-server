package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import java.util.ArrayList;

import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.constants.MARBLE;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.NoMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;

//contains the board helper functions
public class boardHelper {

    

    private final int REDINTERSECT = 0;
    private final int BLUEINTERSECT = 48;
    private final int GREENINTERSECT = 32;
    private final int YELLOWINTERSECT = 16;

    public void makeMove(Move move, ArrayList<MARBLE> _mainCircle, 
                        boardState bState) throws InvalidMoveException {
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
                        bState.REDBLOCKED = false;
                        break;
                    case GREENINTERSECT:
                        bState.GREENBLOCKED = false;
                        break;
                    case BLUEINTERSECT:
                        bState.BLUEBLOCKED = false;
                        break;
                    case YELLOWINTERSECT:
                        bState.YELLOWBLOCKED = false;
                        break;
                }
            }
            // do the move
            if (endsInGoal) {
                movePositions(fromPos, toPos, move.get_color(), startsInGoal,
                        move.get_card() != null ? move.get_card().isSeven() : false, _mainCircle, bState);
            } else {
                movePositions(fromPos, toPos, move.get_card() != null ? move.get_card().isSeven() : false, _mainCircle, bState);
            }

        }
    }

    public void movePositions(int pos1, int pos2, boolean removeInbetweeners, ArrayList<MARBLE> _mainCircle,
            boardState bState)
            throws InvalidMoveException, IndexOutOfBoundsException {
        if (pos1 < 0 || pos1 >= 64 || pos2 < 0 || pos2 >= 64)
            throw new IndexOutOfBoundsException("the positions have to be in range 0-63 (inclusive)");

        MARBLE m1 = _mainCircle.get(pos1);
        MARBLE m2 = _mainCircle.get(pos2);

        if (m1 == MARBLE.NONE)
            throw new NoMarbleException();
        if (m2 != MARBLE.NONE) {
            // reset the problem marble
            resetMarble(pos2, _mainCircle, bState);
        }

        // all good, make the move
        if (removeInbetweeners) {
            resetInbetweeners(pos1, pos2, _mainCircle, bState);
        }

        setMarbleAtPosition(pos2, m1, _mainCircle);
        setMarbleAtPosition(pos1, MARBLE.NONE, _mainCircle);
    }

    public void resetMarble(int pos, ArrayList<MARBLE> _mainCircle,
            boardState bState) throws NoMarbleException, IndexOutOfBoundsException {
        if (pos < 0 || pos >= 64)
            throw new IndexOutOfBoundsException("the position has to be in range 0-63 (inclusive)");

        if (_mainCircle.get(pos) == MARBLE.NONE)
            throw new NoMarbleException();

        MARBLE m = _mainCircle.get(pos);

        setMarbleAtPosition(pos, MARBLE.NONE, _mainCircle);
        switch (m) {
            case RED:
                bState._redBase++;
                break;
            case BLUE:
                bState._blueBase++;
                break;
            case GREEN:
                bState._greenBase++;
                break;
            case YELLOW:
                bState._yellowBase++;
                break;
        }
    }

    public void movePositions(int pos1, int pos2, COLOR goalColor, boolean startInGoal, boolean removeInbetweeners,
            ArrayList<MARBLE> _mainCircle,
            boardState bState)
            throws InvalidMoveException, IndexOutOfBoundsException {
        MARBLE m1;
        MARBLE m2;

        int colorintersect = 0;

        ArrayList<MARBLE> coloredGoalList = bState._redGoal;
        // get the respective color
        switch (goalColor) {
            case RED:
                coloredGoalList = bState._redGoal;
                colorintersect = REDINTERSECT;
                break;
            case BLUE:
                coloredGoalList = bState._blueGoal;
                colorintersect = BLUEINTERSECT;
                break;
            case YELLOW:
                coloredGoalList = bState._yellowGoal;
                colorintersect = YELLOWINTERSECT;
                break;
            case GREEN:
                coloredGoalList = bState._greenGoal;
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
            resetMarble(pos2, _mainCircle, bState);
        }

        // all good, make the move

        if (removeInbetweeners && !startInGoal) {
            resetInbetweeners(pos1, colorintersect, _mainCircle, bState);
        }

        setMarbleAtPosition(pos2, m1, goalColor, bState);

        if (startInGoal) {
            setMarbleAtPosition(pos1, MARBLE.NONE, goalColor, bState);
        } else {
            setMarbleAtPosition(pos1, MARBLE.NONE, _mainCircle);
        }
    }

    public boolean setMarbleAtPosition(int position, MARBLE marble, ArrayList<MARBLE> _mainCircle) {
        _mainCircle.set(position, marble);
        return true;
    }

    public boolean setMarbleAtPosition(int position, MARBLE marble, COLOR goalColor,
            boardState bState) {
        switch (goalColor) {
            case RED:
                if (marble != MARBLE.RED && marble != MARBLE.NONE)
                    return false;
                    bState._redGoal.set(position, marble);
                break;
            case BLUE:
                if (marble != MARBLE.BLUE && marble != MARBLE.NONE)
                    return false;
                    bState._blueGoal.set(position, marble);
                break;
            case YELLOW:
                if (marble != MARBLE.YELLOW && marble != MARBLE.NONE)
                    return false;
                    bState._yellowGoal.set(position, marble);
                break;
            case GREEN:
                if (marble != MARBLE.GREEN && marble != MARBLE.NONE)
                    return false;
                    bState._greenGoal.set(position, marble);
                break;
            default:
                return false;
        }
        return true;
    }

    public COLOR getColorFromPosition(int pos, ArrayList<MARBLE> _mainCircle) throws NoMarbleException {
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
    public void resetInbetweeners(int pos1, int pos2, ArrayList<MARBLE> _mainCircle,
            boardState bState) throws NoMarbleException {
        ArrayList<Integer> relevantInbetweeners = getInbetweeners(pos1, pos2, _mainCircle);

        for (Integer inb : relevantInbetweeners) {
            resetMarble(inb, _mainCircle, bState);
        }
    }

    public ArrayList<Integer> getInbetweenersBackwards(int pos1, int pos2, ArrayList<MARBLE> _mainCircle)
            throws IndexOutOfBoundsException {
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

        return getInbetweeners(newPos1, newPos2, _mainCircle);
    }

    public boolean blockedIntersect(Move move, int startPos, int endPos, boolean forward, ArrayList<MARBLE> _mainCircle,
            boardState bState) {
        if (forward) {
            ArrayList<Integer> blockPos = getInbetweeners(startPos, endPos, _mainCircle);
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
                            if (bState.REDBLOCKED) {
                                return true;
                            }
                            break;
                        case YELLOWINTERSECT:
                            if (bState.YELLOWBLOCKED) {
                                return true;
                            }
                            break;
                        case GREENINTERSECT:
                            if (bState.GREENBLOCKED) {
                                return true;
                            }
                            break;
                        case BLUEINTERSECT:
                            if (bState.BLUEBLOCKED) {
                                return true;
                            }
                            break;
                    }
                }
            }
        } else {
            ArrayList<Integer> blockPos = getInbetweenersBackwards(startPos, endPos, _mainCircle);
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
                            if (bState.REDBLOCKED) {
                                return true;
                            }
                            break;
                        case YELLOWINTERSECT:
                            if (bState.YELLOWBLOCKED) {
                                return true;
                            }
                            break;
                        case GREENINTERSECT:
                            if (bState.GREENBLOCKED) {
                                return true;
                            }
                            break;
                        case BLUEINTERSECT:
                            if (bState.BLUEBLOCKED) {
                                return true;
                            }
                            break;
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<Integer> getInbetweeners(int pos1, int pos2, ArrayList<MARBLE> _mainCircle)
            throws IndexOutOfBoundsException {
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

    public int getDistanceInBetween(int startPosition, int endPosition) throws IndexOutOfBoundsException {
        return getDistanceInBetween(startPosition, endPosition, true);
    }

    /**
     * gets the next position when moving a certain distance on the main field,
     * either forward or backwards
     */
    public int getDistanceInBetween(int startPosition, int endPosition, boolean forward)
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

    public int getDistanceInBetween(int startPosition, int endPosition, COLOR goalColor, boolean startInGoal)
            throws IndexOutOfBoundsException {
        if (!startInGoal) // normal move into a goal from outside
        {
            int intersect = 0;
            // get the respective intersect
            switch (goalColor) {
                case RED:
                    intersect = REDINTERSECT;
                    break;
                case BLUE:
                    intersect = BLUEINTERSECT;
                    break;
                case YELLOW:
                    intersect = YELLOWINTERSECT;
                    break;
                case GREEN:
                    intersect = GREENINTERSECT;
            }

            // distance from the startposition to the respective INTERSECT
            int distanceToIntersect = getDistanceInBetween(startPosition, intersect);

            if (endPosition < 0 || endPosition >= 4)
                throw new IndexOutOfBoundsException("endposition must be between 0 and 3 (inclusive)");

            // distance between the 2 positions
            return distanceToIntersect + 1 + endPosition;

        } else // from goal to goal, more simple
        {
            if (endPosition < startPosition)
                throw new IndexOutOfBoundsException("cannot move backwards in the goal");
            if (endPosition < 0 || endPosition >= 4)
                throw new IndexOutOfBoundsException("endposition must be between 0 and 3 (inclusive)");
            if (startPosition < 0 || startPosition >= 4)
                throw new IndexOutOfBoundsException("startposition must be between 0 and 3 (inclusive)");

            return endPosition - startPosition;
        }
    }

    public int getMoveDist(Move move) {
        int startPos = move.get_fromPos().get(0).getIndex();
        int endPos = move.get_toPos().get(0).getIndex();
        COLOR marbleColor = move.get_color();
        int moveDist;
        // abstract the check for the goal move
        if (move.get_fromPos().get(0).isInGoal()) {
            moveDist = getDistanceInBetween(startPos, endPos, marbleColor, true);
        } else if (move.get_toPos().get(0).isInGoal()) {
            moveDist = getDistanceInBetween(startPos, endPos, marbleColor, false);
        } else {
            moveDist = getDistanceInBetween(startPos, endPos);
        }
        return moveDist;
    }

    public ArrayList<COLOR> getMarbleColor(Move move, ArrayList<Integer> startPos, ArrayList<MARBLE> _mainCircle)
            throws NoMarbleException {
        ArrayList<COLOR> marbleColor = new ArrayList<>();
        try {
            for (int i = 0; i < startPos.size(); i++) {
                marbleColor.add(this.getColorFromPosition(startPos.get(i), _mainCircle));
            }
        } catch (Exception e) {
            throw new NoMarbleException();
        }
        return marbleColor;
    }

}
