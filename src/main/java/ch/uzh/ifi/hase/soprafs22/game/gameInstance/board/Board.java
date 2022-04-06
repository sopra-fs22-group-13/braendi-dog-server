package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.MARBLE;
import ch.uzh.ifi.hase.soprafs22.game.constants.TURN;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.MoveBlockedByMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.NoMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import org.jboss.jandex.Index;
import org.springframework.data.annotation.CreatedDate;

import java.util.ArrayList;

public class Board {
    private ArrayList<MARBLE> _mainCircle = new ArrayList<>();

    private ArrayList<MARBLE> _redGoal = new ArrayList<>();
    private ArrayList<MARBLE> _greenGoal = new ArrayList<>();
    private ArrayList<MARBLE> _blueGoal = new ArrayList<>();
    private ArrayList<MARBLE> _yellowGoal = new ArrayList<>();

    private int _redBase = 4;
    private int _greenBase = 4;
    private int _blueBase = 4;
    private int _yellowBase = 4;

    private Card _lastPlayedCard;

    private final int REDINTERSECT = 0;
    private final int BLUEINTERSECT = 16;
    private final int GREENINTERSECT = 32;
    private final int YELLOWINTERSECT = 48;

    public Board()
    {
        //set all marbles to the start pos.
        _redBase = 4;
        _greenBase = 4;
        _blueBase = 4;
        _yellowBase = 4;

        //create the board list
        while(_mainCircle.size() < 64) _mainCircle.add(MARBLE.NONE);

        //create the goal lists
        while(_redGoal.size() < 4) _redGoal.add(MARBLE.NONE);
        while(_greenGoal.size() < 4) _greenGoal.add(MARBLE.NONE);
        while(_blueGoal.size() < 4) _blueGoal.add(MARBLE.NONE);
        while(_yellowGoal.size() < 4) _yellowGoal.add(MARBLE.NONE);
    }

    private boolean setMarbleAtPosition(int position, MARBLE marble)
    {
        _mainCircle.set(position, marble);
        return true;
    }

    private boolean setMarbleAtPosition(int position, MARBLE marble, TURN goalColor)
    {
        switch (goalColor)
        {
            case RED:
                if(marble != MARBLE.RED && marble != MARBLE.NONE) return false;
                _redGoal.set(position, marble);
                break;
            case BLUE:
                if(marble != MARBLE.BLUE && marble != MARBLE.NONE) return false;
                _blueGoal.set(position, marble);
                break;
            case YELLOW:
                if(marble != MARBLE.YELLOW && marble != MARBLE.NONE) return false;
                _yellowGoal.set(position, marble);
                break;
            case GREEN:
                if(marble != MARBLE.GREEN && marble != MARBLE.NONE) return false;
                _greenGoal.set(position, marble);
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Get the TurnColor from a marble at a position x on the main circle
     * @throws NoMarbleException if there is no marble at x
     */
    private TURN getColorFromPosition(int pos) throws NoMarbleException
    {
        if(pos < 0 || pos >= 64) throw new IndexOutOfBoundsException("the position has to be in range 0-63 (inclusive)");
        MARBLE m = _mainCircle.get(pos);

        switch (m)
        {
            case GREEN:
                return TURN.GREEN;
            case BLUE:
                return TURN.BLUE;
            case RED:
                return TURN.RED;
            case YELLOW:
                return TURN.YELLOW;
            default:
                throw new NoMarbleException();
        }
    }

    /**
     * Switches the marbles at pos1 to pos2, no checking is done
     * You can also change a NONE with something.
     */
    private void changePositions(int pos1, int pos2) throws IndexOutOfBoundsException
    {
        MARBLE marble1;
        MARBLE marble2;

        if(pos1 < 0 || pos1 >= 64 || pos2 < 0 || pos2 >= 64) throw new IndexOutOfBoundsException("the positions have to be in range 0-63 (inclusive)");

        marble1 = _mainCircle.get(pos1);
        marble2 = _mainCircle.get(pos2);

        setMarbleAtPosition(pos1, marble2);
        setMarbleAtPosition(pos2, marble1);
    }

    /**
     * Moves the marble from position 1 to position 2 in the main circle
     * @throws NoMarbleException no marble was at pos1
     * @throws MoveBlockedByMarbleException there was already a marble at pos2
     */
    private void movePositions(int pos1, int pos2)
        throws NoMarbleException, MoveBlockedByMarbleException, IndexOutOfBoundsException
    {
        if(pos1 < 0 || pos1 >= 64 || pos2 < 0 || pos2 >= 64) throw new IndexOutOfBoundsException("the positions have to be in range 0-63 (inclusive)");

        MARBLE m1 = _mainCircle.get(pos1);
        MARBLE m2 = _mainCircle.get(pos2);

        if(m1 == MARBLE.NONE) throw new NoMarbleException();
        if(m2 == MARBLE.NONE) throw new MoveBlockedByMarbleException();

        //all good, make the move
        setMarbleAtPosition(pos2, m1);
        setMarbleAtPosition(pos1, MARBLE.NONE);
    }


    /**
     * Moves the marble from position 1 to position 2 where position 2 is in the respective goal.
     * @param goalColor the goal color to consider
     * @param startInGoal defines if position 1 is already in the goal. (ex: move 1 forward in the goal)
     * @throws NoMarbleException no marble was at pos1
     * @throws MoveBlockedByMarbleException there was already a marble at pos2
     */
    private void movePositions(int pos1, int pos2, TURN goalColor, boolean startInGoal)
            throws NoMarbleException, MoveBlockedByMarbleException, IndexOutOfBoundsException
    {
        MARBLE m1;
        MARBLE m2;

        ArrayList<MARBLE> coloredGoalList = _redGoal;
        //get the respective color
        switch (goalColor)
        {
            case RED:
                coloredGoalList = _redGoal;
                break;
            case BLUE:
                coloredGoalList = _blueGoal;
                break;
            case YELLOW:
                coloredGoalList = _yellowGoal;
                break;
            case GREEN:
                coloredGoalList = _greenGoal;
        }

        if(startInGoal)
        {
            if(pos1 < 0 || pos2 >= 4) throw new IndexOutOfBoundsException("if startInGoal is true, the pos1 can be in the range 0-3 (inclusive)");
            m1 = coloredGoalList.get(pos1);
        }else
        {
            if(pos1 < 0 || pos2 >= 64) throw new IndexOutOfBoundsException("if startInGoal is false, the pos1 can be in the range 0-63 (inclusive)");
            m1 = _mainCircle.get(pos1);
        }

        if(pos2 < 0 || pos2 >= 4) throw new IndexOutOfBoundsException("the pos2 can be in the range 0-3 (inclusive)");
        m2 = coloredGoalList.get(pos2);

        //we now have the start and end position. we move from pos1 to pos2

        if(m1 == MARBLE.NONE) throw new NoMarbleException();
        if(m2 != MARBLE.NONE) throw new MoveBlockedByMarbleException();

        //all good, make the move
        setMarbleAtPosition(pos2, m1, goalColor);

        if(startInGoal)
        {
            setMarbleAtPosition(pos1, MARBLE.NONE, goalColor);
        }else
        {
            setMarbleAtPosition(pos1, MARBLE.NONE);
        }
    }

    /**
     * gets the next position when moving forward a certain distance on the main field
     */
    private int getDistanceInBetween(int startPosition, int endPosition) throws IndexOutOfBoundsException
    {
        return getDistanceInBetween(startPosition, endPosition, true);
    }

    /**
     * gets the next position when moving a certain distance on the main field, either forward or backwards
     */
    private int getDistanceInBetween(int startPosition, int endPosition, boolean forward) throws IndexOutOfBoundsException
    {
        if(startPosition < 0 || startPosition >= 64 || endPosition < 0 || endPosition >= 64) throw new IndexOutOfBoundsException("Index is wrong: the main ring has indices between 0 and 63 while goals reach from 0 to 3");
        if(forward)
        {
            if(startPosition <= endPosition) //we do not loop over
            {
                return endPosition-startPosition;
            }
            else //we loop over the edge
            {
                int rest = 63 - startPosition;
                return endPosition + rest;
            }
        }else //we move backwards
        {
            if(endPosition <= startPosition) //we do not loop over
            {
                return startPosition-endPosition;
            }
            else //we loop over the edge
            {
                int rest = 63 - endPosition;
                return startPosition + rest;
            }
        }
    }

    /**
     * gets the next position when moving a certain distance to a goal, forward only!
     * @param startPosition the start position (0-63 or 0-3, depending on startInGoal)
     * @param endPosition the end position (0-3)
     * @param goalColor the color of the goal we are considering for moving into
     * @param startInGoal if the marble already starts in its respective goal (eg: moving 1 forward in the goal)
     */
    private int getDistanceInBetween(int startPosition, int endPosition, TURN goalColor, boolean startInGoal) throws IndexOutOfBoundsException
    {
        if(!startInGoal) //normal move into a goal from outside
        {
            int intersect = 0;
            //get the respective intersect
            switch (goalColor)
            {
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

            //distance from the startposition to the respective INTERSECT
            int distanceToIntersect = getDistanceInBetween(startPosition, intersect);

            if(endPosition < 0 || endPosition >= 4) throw new IndexOutOfBoundsException("endposition must be between 0 and 3 (inclusive)");

            //distance between the 2 positions
            return distanceToIntersect + 1 + endPosition;

        }else        //from goal to goal, more simple
        {
            if(endPosition < startPosition) throw new IndexOutOfBoundsException("cannot move backwards in the goal");
            if(endPosition < 0 || endPosition >= 4) throw new IndexOutOfBoundsException("endposition must be between 0 and 3 (inclusive)");
            if(startPosition < 0 || startPosition >= 4) throw new IndexOutOfBoundsException("startposition must be between 0 and 3 (inclusive)");

            return  endPosition - startPosition;
        }
    }

    public BoardData getFormattedBoardState()
    {
        ArrayList<String> board = new ArrayList<>();
        for (MARBLE m : _mainCircle) {
            board.add(m.toString());
        }

        ArrayList<String> redGoal = new ArrayList<>();
        for (MARBLE m : _redGoal) {
            redGoal.add(m.toString());
        }
        ArrayList<String> greenGoal = new ArrayList<>();
        for (MARBLE m : _greenGoal) {
            greenGoal.add(m.toString());
        }
        ArrayList<String> blueGoal = new ArrayList<>();
        for (MARBLE m : _blueGoal) {
            blueGoal.add(m.toString());
        }
        ArrayList<String> yellowGoal = new ArrayList<>();
        for (MARBLE m : _yellowGoal) {
            yellowGoal.add(m.toString());
        }

        BoardData boardData = new BoardData(board, redGoal, greenGoal, blueGoal, yellowGoal, _redBase, _greenBase, _blueBase, _yellowBase);
        return boardData;
    }


}
