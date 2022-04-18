package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.*;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.MoveBlockedByMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.NoMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.Arrays;

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

    private boolean REDBLOCKED = false;
    private boolean GREENBLOCKED = false;
    private boolean BLUEBLOCKED = false;
    private boolean YELLOWBLOCKED = false;

    private Card _lastPlayedCard;

    private final int REDINTERSECT = 0;
    private final int BLUEINTERSECT = 16;
    private final int GREENINTERSECT = 32;
    private final int YELLOWINTERSECT = 48;

    public Board() {
        //set all marbles to the start pos.
        _redBase = 4;
        _greenBase = 4;
        _blueBase = 4;
        _yellowBase = 4;

        //create the board list
        while (_mainCircle.size() < 64) _mainCircle.add(MARBLE.NONE);

        //create the goal lists
        while (_redGoal.size() < 4) _redGoal.add(MARBLE.NONE);
        while (_greenGoal.size() < 4) _greenGoal.add(MARBLE.NONE);
        while (_blueGoal.size() < 4) _blueGoal.add(MARBLE.NONE);
        while (_yellowGoal.size() < 4) _yellowGoal.add(MARBLE.NONE);
    }

    private boolean setMarbleAtPosition(int position, MARBLE marble) {
        _mainCircle.set(position, marble);
        return true;
    }

    private boolean setMarbleAtPosition(int position, MARBLE marble, COLOR goalColor) {
        switch (goalColor) {
            case RED:
                if (marble != MARBLE.RED && marble != MARBLE.NONE) return false;
                _redGoal.set(position, marble);
                break;
            case BLUE:
                if (marble != MARBLE.BLUE && marble != MARBLE.NONE) return false;
                _blueGoal.set(position, marble);
                break;
            case YELLOW:
                if (marble != MARBLE.YELLOW && marble != MARBLE.NONE) return false;
                _yellowGoal.set(position, marble);
                break;
            case GREEN:
                if (marble != MARBLE.GREEN && marble != MARBLE.NONE) return false;
                _greenGoal.set(position, marble);
                break;
            default:
                return false;
        }
        return true;
    }

    private boolean isEmptyAt(int position) {
        MARBLE m = _mainCircle.get(position);
        return m == MARBLE.NONE;
    }

    /**
     * Gets the inbetweeners in the main circle in the FORWARDS direction! (exclusive, inclusive)
     * IMPORTANT: NEVER USE THIS METHOD WHEN TRYING TO MOVE BACKWARDS
     */
    private ArrayList<Integer> getInbetweeners(int pos1, int pos2) throws IndexOutOfBoundsException {
        ArrayList<Integer> importantInbetweeners = new ArrayList<>();
        if (pos1 < 0 || pos1 >= 64 || pos2 < 0 || pos2 >= 64)
            throw new IndexOutOfBoundsException("the positions have to be in range 0-63 (inclusive)");

        if (pos1 < pos2) {
            //no loop
            for (int i = pos1 + 1; i <= pos2; i++) {
                if (_mainCircle.get(i) != MARBLE.NONE) {
                    importantInbetweeners.add(i);
                }
            }
        }
        else {
            //get to half way
            for (int i = pos1 + 1; i < 64; i++) {
                if (_mainCircle.get(i) != MARBLE.NONE) {
                    importantInbetweeners.add(i);
                }
            }
            //rest
            for (int i = 0; i <= pos2; i++) {
                if (_mainCircle.get(i) != MARBLE.NONE) {
                    importantInbetweeners.add(i);
                }
            }
        }

        return importantInbetweeners;
    }

    /**
     * Resets a marble on the main circle back to the base
     */
    private void resetMarble(int pos) throws NoMarbleException, IndexOutOfBoundsException {
        if (pos < 0 || pos >= 64)
            throw new IndexOutOfBoundsException("the position has to be in range 0-63 (inclusive)");

        if (_mainCircle.get(pos) == MARBLE.NONE) throw new NoMarbleException();

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

    /**
     * resets any inbetweeners (exclusive, inclusive)
     */
    private void resetInbetweeners(int pos1, int pos2) throws NoMarbleException {
        ArrayList<Integer> relevantInbetweeners = getInbetweeners(pos1, pos2);

        for (Integer inb : relevantInbetweeners) {
            resetMarble(inb);
        }
    }

    /**
     * Get the TurnColor from a marble at a position x on the main circle
     *
     * @throws NoMarbleException if there is no marble at x
     */
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
     * Switches the marbles at pos1 to pos2, no checking is done
     * You can also change a NONE with something.
     */
    private void changePositions(int pos1, int pos2) throws IndexOutOfBoundsException {
        MARBLE marble1;
        MARBLE marble2;

        if (pos1 < 0 || pos1 >= 64 || pos2 < 0 || pos2 >= 64)
            throw new IndexOutOfBoundsException("the positions have to be in range 0-63 (inclusive)");

        marble1 = _mainCircle.get(pos1);
        marble2 = _mainCircle.get(pos2);

        setMarbleAtPosition(pos1, marble2);
        setMarbleAtPosition(pos2, marble1);
    }

    /**
     * Moves the marble from position 1 to position 2 in the main circle
     *
     * @param removeInbetweeners IMPORTANT: NEVER SET THIS TO TRUE WHEN TRYING TO MOVE BACKWARDS
     * @throws NoMarbleException            no marble was at pos1
     * @throws MoveBlockedByMarbleException there was already a marble at pos2
     */
    private void movePositions(int pos1, int pos2, boolean removeInbetweeners)
            throws InvalidMoveException, IndexOutOfBoundsException {
        if (pos1 < 0 || pos1 >= 64 || pos2 < 0 || pos2 >= 64)
            throw new IndexOutOfBoundsException("the positions have to be in range 0-63 (inclusive)");

        MARBLE m1 = _mainCircle.get(pos1);
        MARBLE m2 = _mainCircle.get(pos2);

        if (m1 == MARBLE.NONE) throw new NoMarbleException();
        if (m2 != MARBLE.NONE) {
            //reset the problem marble
            resetMarble(pos2);
        }

        //all good, make the move
        if (removeInbetweeners) {
            resetInbetweeners(pos1, pos2);
        }

        setMarbleAtPosition(pos2, m1);
        setMarbleAtPosition(pos1, MARBLE.NONE);
    }


    /**
     * Moves the marble from position 1 to position 2 where position 2 is in the respective goal.
     *
     * @param goalColor          the goal color to consider
     * @param startInGoal        defines if position 1 is already in the goal. (ex: move 1 forward in the goal)
     * @param removeInbetweeners IMPORTANT: NEVER SET THIS TO TRUE WHEN TRYING TO MOVE BACKWARDS
     * @throws NoMarbleException            no marble was at pos1
     * @throws MoveBlockedByMarbleException there was already a marble at pos2
     */
    private void movePositions(int pos1, int pos2, COLOR goalColor, boolean startInGoal, boolean removeInbetweeners)
            throws InvalidMoveException, IndexOutOfBoundsException {
        MARBLE m1;
        MARBLE m2;

        int colorintersect = 0;

        ArrayList<MARBLE> coloredGoalList = _redGoal;
        //get the respective color
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
                throw new IndexOutOfBoundsException("if startInGoal is true, the pos1 can be in the range 0-3 (inclusive)");
            m1 = coloredGoalList.get(pos1);
        }
        else {
            if (pos1 < 0 || pos2 >= 64)
                throw new IndexOutOfBoundsException("if startInGoal is false, the pos1 can be in the range 0-63 (inclusive)");
            m1 = _mainCircle.get(pos1);
        }

        if (pos2 < 0 || pos2 >= 4) throw new IndexOutOfBoundsException("the pos2 can be in the range 0-3 (inclusive)");
        m2 = coloredGoalList.get(pos2);

        //we now have the start and end position. we move from pos1 to pos2

        if (m1 == MARBLE.NONE) throw new NoMarbleException();
        if (m2 != MARBLE.NONE) {
            //reset the problem marble
            resetMarble(pos2);
        }

        //all good, make the move

        if (removeInbetweeners) {
            resetInbetweeners(pos1, colorintersect);
        }

        setMarbleAtPosition(pos2, m1, goalColor);

        if (startInGoal) {
            setMarbleAtPosition(pos1, MARBLE.NONE, goalColor);
        }
        else {
            setMarbleAtPosition(pos1, MARBLE.NONE);
        }
    }

    /**
     * gets the next position when moving forward a certain distance on the main field
     */
    private int getDistanceInBetween(int startPosition, int endPosition) throws IndexOutOfBoundsException {
        return getDistanceInBetween(startPosition, endPosition, true);
    }

    /**
     * gets the next position when moving a certain distance on the main field, either forward or backwards
     */
    private int getDistanceInBetween(int startPosition, int endPosition, boolean forward) throws IndexOutOfBoundsException {
        if (startPosition < 0 || startPosition >= 64 || endPosition < 0 || endPosition >= 64)
            throw new IndexOutOfBoundsException("Index is wrong: the main ring has indices between 0 and 63 while goals reach from 0 to 3");
        if (forward) {
            if (startPosition <= endPosition) //we do not loop over
            {
                return endPosition - startPosition;
            }
            else //we loop over the edge
            {
                int rest = 63 - startPosition;
                return endPosition + rest + 1;
            }
        }
        else //we move backwards
        {
            if (endPosition <= startPosition) //we do not loop over
            {
                return startPosition - endPosition;
            }
            else //we loop over the edge
            {
                int rest = 63 - endPosition;
                return startPosition + rest + 1;
            }
        }
    }

    /**
     * gets the next position when moving a certain distance to a goal, forward only!
     *
     * @param startPosition the start position (0-63 or 0-3, depending on startInGoal)
     * @param endPosition   the end position (0-3)
     * @param goalColor     the color of the goal we are considering for moving into
     * @param startInGoal   if the marble already starts in its respective goal (eg: moving 1 forward in the goal)
     */
    private int getDistanceInBetween(int startPosition, int endPosition, COLOR goalColor, boolean startInGoal) throws IndexOutOfBoundsException {
        if (!startInGoal) //normal move into a goal from outside
        {
            int intersect = 0;
            //get the respective intersect
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

            //distance from the startposition to the respective INTERSECT
            int distanceToIntersect = getDistanceInBetween(startPosition, intersect);

            if (endPosition < 0 || endPosition >= 4)
                throw new IndexOutOfBoundsException("endposition must be between 0 and 3 (inclusive)");

            //distance between the 2 positions
            return distanceToIntersect + 1 + endPosition;

        }
        else        //from goal to goal, more simple
        {
            if (endPosition < startPosition) throw new IndexOutOfBoundsException("cannot move backwards in the goal");
            if (endPosition < 0 || endPosition >= 4)
                throw new IndexOutOfBoundsException("endposition must be between 0 and 3 (inclusive)");
            if (startPosition < 0 || startPosition >= 4)
                throw new IndexOutOfBoundsException("startposition must be between 0 and 3 (inclusive)");

            return endPosition - startPosition;
        }
    }

    public BoardData getFormattedBoardState() {
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

    /**
     * Makes one or more moves specified by the move object.
     * IMPORTANT: This does NOT check if the move is allowed depending on the rules (eg: move cost 12, card value: 8)
     *
     * @param move the move object that holds the move
     * @throws InvalidMoveException if the move cannot be completed because of the board state
     */
    public void makeMove(Move move) throws InvalidMoveException {
        //expects the move to be valid
        if (move == null || !move.isWellFormed()) {
            throw new InvalidMoveException("BAD_STRUCTURE", "Bad move structure");
        }

        //For each move, make it
        for (int i = 0; i < move.get_fromPos().size(); i++) {
            int fromPos = move.get_fromPos().get(i);
            int toPos = move.get_toPos().get(i);
            boolean startsInGoal = move.get_fromPosInGoal().get(i);
            boolean endsInGoal = move.get_toPosInGoal().get(i);

            //do the move
            if (endsInGoal) {
                movePositions(fromPos, toPos, move.get_color(), startsInGoal, move.get_card() != null ? move.get_card().isSeven() : false);
            }
            else {
                movePositions(fromPos, toPos, move.get_card() != null ? move.get_card().isSeven() : false);
            }

        }
    }

    /**
     * Makes a starting move (from the base to the starting intersect position)
     * IMPORTANT: This does NOT check if the move is allowed depending on the rules (eg: card is not Ace/Joker/King)
     *
     * @param color the color we want to move out
     * @throws InvalidMoveException if the move cannot be completed because of the board state
     */
    public void makeStartingMove(COLOR color) throws InvalidMoveException {
        switch (color) {
            case RED:
                if (_redBase > 0) {
                    if (!isEmptyAt(REDINTERSECT)) resetMarble(REDINTERSECT);

                    _redBase = _redBase - 1;
                    setMarbleAtPosition(REDINTERSECT, MARBLE.RED);
                }
                else {
                    throw new InvalidMoveException("NOTHING_LEFT", "there are no marbles left to start with");
                }
                break;
            case YELLOW:
                if (_yellowBase > 0) {
                    if (!isEmptyAt(YELLOWINTERSECT)) resetMarble(YELLOWINTERSECT);

                    _yellowBase = _yellowBase - 1;
                    setMarbleAtPosition(YELLOWINTERSECT, MARBLE.YELLOW);
                }
                else {
                    throw new InvalidMoveException("NOTHING_LEFT", "there are no marbles left to start with");
                }
                break;
            case GREEN:
                if (_greenBase > 0) {
                    if (!isEmptyAt(GREENINTERSECT)) resetMarble(GREENINTERSECT);

                    _greenBase = _greenBase - 1;
                    setMarbleAtPosition(GREENINTERSECT, MARBLE.GREEN);
                }
                else {
                    throw new InvalidMoveException("NOTHING_LEFT", "there are no marbles left to start with");
                }
                break;
            case BLUE:
                if (_blueBase > 0) {
                    if (!isEmptyAt(BLUEINTERSECT)) resetMarble(BLUEINTERSECT);

                    _blueBase = _blueBase - 1;
                    setMarbleAtPosition(BLUEINTERSECT, MARBLE.BLUE);
                }
                else {
                    throw new InvalidMoveException("NOTHING_LEFT", "there are no marbles left to start with");
                }
                break;
        }
    }

    public boolean checkWinningCondition(COLOR color){
        switch (color){
            case RED:
                for (MARBLE m: _redGoal){
                    if (m == MARBLE.NONE){return false;}
                }
                break;
            case BLUE:
                for (MARBLE m: _blueGoal){
                    if (m == MARBLE.NONE){return false;}
                }
                break;
            case GREEN:
                for (MARBLE m: _greenGoal){
                    if (m == MARBLE.NONE){return false;}
                }
                break;
            case YELLOW:
                for (MARBLE m: _yellowGoal){
                    if (m == MARBLE.NONE){return false;}
                }
                break;
        }
        return true;
    }

    /**
     * Makes a switch specified by the two positions on the board
     * IMPORTANT: This does NOT check if the move is allowed depending on the rules (eg: card is not a joker/jack)
     *
     * @throws InvalidMoveException if the move cannot be completed because of the board state or if the card does not allow the move
     */
    public void makeSwitch(int start, int end) throws InvalidMoveException {
        try {
            changePositions(start, end);
        }
        catch (IndexOutOfBoundsException e) {
            throw new InvalidMoveException("OUT_OF_BOUNDS", "one of the positions was out of bounds (0-63)");
        }
    }

    /**
     * This method is a mess. Keep your sanity and do not even try to look at this.
     * It checks if for a color and a card, any move is possible given the current board state.
     */
    public boolean isAnyMovePossible(Card card, COLOR col)
    {
        //marble color
        MARBLE searchedMarbleCol = MARBLE.NONE;
        ArrayList<MARBLE> colGoal = new ArrayList<>();

        switch (col)
        {
            case YELLOW:
                searchedMarbleCol = MARBLE.YELLOW;
                colGoal = new ArrayList<>(_yellowGoal);
                break;
            case GREEN:
                searchedMarbleCol = MARBLE.GREEN;
                colGoal = new ArrayList<>(_greenGoal);
                break;
            case RED:
                searchedMarbleCol = MARBLE.RED;
                colGoal = new ArrayList<>(_redGoal);
                break;
            case BLUE:
                searchedMarbleCol = MARBLE.BLUE;
                colGoal = new ArrayList<>(_blueGoal);
                break;
        }

        //get the marbles of card
        ArrayList<Integer> marblesOnMain = new ArrayList<>();
        ArrayList<Integer> marblesInGoal = new ArrayList<>();

        for (int i = 0; i < _mainCircle.size(); i++) {
            if(_mainCircle.get(i) == searchedMarbleCol) marblesOnMain.add(i);
        }

        for (int i = 0; i < colGoal.size(); i++) {
            if(colGoal.get(i) == searchedMarbleCol) marblesInGoal.add(i);
        }

        //make a new move with the card
        Move m = new Move();
        m.set_card(card);
        m.set_color(col);

        ArrayList<Integer> allMoveValues = new ArrayList<>();

        switch(card.getValue()) {
            case TWO:
                allMoveValues.add(2);
                break;
            case THREE:
                allMoveValues.add(3);
                break;
            case FIVE:
                allMoveValues.add(5);
                break;
            case SIX:
                allMoveValues.add(6);
                break;
            case EIGHT:
                allMoveValues.add(8);
                break;
            case NINE:
                allMoveValues.add(9);
                break;
            case TEN:
                allMoveValues.add(10);
                break;
            case QUEEN:
                allMoveValues.add(12);
                break;
            case KING:
                allMoveValues.add(13);
                break;
            case FOUR:
                allMoveValues.add(4);
                allMoveValues.add(-4);
                break;
            case ACE:
                allMoveValues.add(1);
                allMoveValues.add(11);
                break;
        }

        //try normal moves
        for (int i = 0; i < marblesOnMain.size(); i++) {

            for (int j = 0; j < allMoveValues.size(); j++) {

                //try a normal move
                m.set_fromPos(new ArrayList<>(Arrays.asList(marblesOnMain.get(i))));
                m.set_toPos(new ArrayList<>(Arrays.asList(getIndexAfterDistance(marblesOnMain.get(i), allMoveValues.get(j)))));
                m.set_fromPosInGoal(new ArrayList<>(Arrays.asList(false)));
                m.set_toPosInGoal(new ArrayList<>(Arrays.asList(false)));

                //always early return, saves this O(scary) algorithm to terminate a lot earlier most of the time
                try {
                    if(isValidMove(m)) return true;
                }
                catch (InvalidMoveException e) {
                    //do nothing here
                    //ik this is terrible, but our move is always well formed in this case
                }

                //try a goal move
                m.set_fromPos(new ArrayList<>(Arrays.asList(marblesOnMain.get(i))));
                try{
                    m.set_toPos(new ArrayList<>(Arrays.asList(getIndexInGoalAfterDistance(marblesOnMain.get(i), allMoveValues.get(j), col))));
                }catch (IndexOutOfBoundsException e)
                {
                    //a four cannot move in a goal backwards.
                    m.set_toPos(new ArrayList<>());
                }
                m.set_fromPosInGoal(new ArrayList<>(Arrays.asList(false)));
                m.set_toPosInGoal(new ArrayList<>(Arrays.asList(true)));

                // goal reachable:
                if(m.get_toPos().size() > 0 && m.get_toPos().get(0) != -1)
                {
                    //always early return, saves this O(scary) algorithm to terminate a lot earlier most of the time
                    try {
                        if(isValidMove(m)) return true;
                    }
                    catch (InvalidMoveException e) {
                        //do nothing here
                        //ik this is terrible, but our move is always well-formed in this case
                    }
                }

            }

        }

        //try goal to goal moves
        for (int i = 0; i < marblesInGoal.size(); i++) {
            for (int j = 0; j < allMoveValues.size(); j++) {

                //try a goal move from a goal
                if(allMoveValues.get(j) == 1 || allMoveValues.get(j) == 2 || allMoveValues.get(j) == 3)
                {
                    int toPos = marblesInGoal.get(i) + allMoveValues.get(j);
                    if(toPos < 3)
                    {
                        m.set_fromPos(new ArrayList<>(Arrays.asList(marblesInGoal.get(i))));
                        m.set_toPos(new ArrayList<>(Arrays.asList(toPos)));
                        m.set_fromPosInGoal(new ArrayList<>(Arrays.asList(true)));
                        m.set_toPosInGoal(new ArrayList<>(Arrays.asList(true)));

                        //try the move
                        //always early return, saves this O(scary) algorithm to terminate a lot earlier most of the time
                        try {
                            if(isValidMove(m)) return true;
                        }
                        catch (InvalidMoveException e) {
                            //do nothing here
                            //ik this is terrible, but our move is always well-formed in this case
                        }
                    }
                }
            }
        }

        //try jack move
        if(card.getValue() == CARDVALUE.JACK)
        {
            ArrayList<Integer> otherMarbles = new ArrayList<>();
            for (int i = 0; i < _mainCircle.size(); i++) {
                if(_mainCircle.get(i) != searchedMarbleCol && _mainCircle.get(i) != MARBLE.NONE) otherMarbles.add(i);
            }
            //can we make any switch?
            for (int i = 0; i < marblesOnMain.size(); i++) {
                for (int j = 0; j < otherMarbles.size(); j++) {

                    //try the switch move for each combination
                    m.set_fromPos(new ArrayList<>(Arrays.asList(marblesOnMain.get(i))));
                    m.set_toPos(new ArrayList<>(Arrays.asList(otherMarbles.get(j))));
                    m.set_fromPosInGoal(new ArrayList<>(Arrays.asList(false)));
                    m.set_toPosInGoal(new ArrayList<>(Arrays.asList(false)));

                    //always early return, saves this O(scary) algorithm to terminate a lot earlier most of the time
                    try {
                        if(isValidMove(m)) return true;
                    }
                    catch (InvalidMoveException e) {
                        //do nothing here
                        //ik this is terrible, but our move is always well-formed in this case
                    }
                }
            }
        }

        //try start move
        if(card.getValue() == CARDVALUE.ACE || card.getValue() == CARDVALUE.KING)
        {
            int intersect = 0;
            switch (col)
            {
                case YELLOW:
                    intersect = YELLOWINTERSECT;
                    break;
                case GREEN:
                    intersect = GREENINTERSECT;
                    break;
                case RED:
                    intersect = REDINTERSECT;
                    break;
                case BLUE:
                    intersect = BLUEINTERSECT;
                    break;
            }

            m.set_fromPos(new ArrayList<>(Arrays.asList(-1)));
            m.set_toPos(new ArrayList<>(Arrays.asList(intersect)));
            m.set_fromPosInGoal(new ArrayList<>(Arrays.asList(false)));
            m.set_toPosInGoal(new ArrayList<>(Arrays.asList(false)));

            try {
                if(isValidMove(m)) return true;
            }
            catch (InvalidMoveException e) {
                //do nothing here
                //ik this is terrible, but our move is always well-formed in this case
            }
        }

        //try 7 move. OH, GOD
        if(card.getValue() == CARDVALUE.SEVEN)
        {
            if(isAnySevenMovePossible(marblesOnMain, marblesInGoal, col)) return true;
        }

        //try joker move
        if(card.getValue() == CARDVALUE.JOKER)
        {

            ArrayList<CARDVALUE> allValues = new ArrayList<>(Arrays.asList(CARDVALUE.values()));
            allValues.remove(CARDVALUE.JOKER);

            for (CARDVALUE value : allValues) {
                Card c = new Card(value, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
                if(isAnyMovePossible(c, col)) return true; //THIS IS RECURSIVE: MAKE SURE THERE IS NO JOKER IN THE LIST!!!
                //oh great, O(5n^6) just turned into O(scary)
            }
        }

        return false;
    }

    /**
     * This method is an utter mess. Keep your sanity and do not even try to look at this.
     * The complexity is about O(5n^6), so if you want to improve this, feel free.
     * For better overview just add your hours wasted here
     * HoursWasted = 4;
     */
    private boolean isAnySevenMovePossible(ArrayList<Integer> marblesOnMain, ArrayList<Integer> marblesInGoal, COLOR col)
    {
        //try all possible splits
        ArrayList<Integer[]> allPossibilites = new ArrayList<>();

        //DONT EVEN SAY A WORD.

        //pyramid of dooooooooom.
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                for (int k = 0; k < 7; k++) {
                    for (int l = 0; l < 7; l++) {
                        if(i + j + k + l == 7)
                        {
                            //theoretically valid, so add
                            Integer[] movecombo = {i, j, k, l};
                            allPossibilites.add(movecombo);
                        }
                    }
                }
            }
        }

        //now we have all possible splits in all permutations possible

        //0 moves into a goal
        for (Integer[] movecombo : allPossibilites) {

            //move init
            Move sevenmove = new Move();
            sevenmove.set_color(col);
            Card c = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
            sevenmove.set_card(c);

            ArrayList<Integer> fromPos = new ArrayList<>();
            ArrayList<Integer> toPos = new ArrayList<>();
            ArrayList<Boolean> fromInGoal = new ArrayList<Boolean>();
            ArrayList<Boolean> toInGoal = new ArrayList<Boolean>();

            int marbleCount = 0;
            //assign to all marbles
            for (int i = 0; i < marblesOnMain.size(); i++) {
                fromPos.add(marblesOnMain.get(i));
                toPos.add(getIndexAfterDistance(marblesOnMain.get(i), movecombo[marbleCount]));
                fromInGoal.add(false);
                toInGoal.add(false);
                marbleCount++;
            }
            for (int i = 0; i < marblesInGoal.size(); i++) {
                fromPos.add(marblesInGoal.get(i));
                toPos.add(marblesInGoal.get(i) + movecombo[marbleCount]);
                fromInGoal.add(true);
                toInGoal.add(true);
                marbleCount++;
            }

            boolean addsTo7 = true;

            for (int i = marbleCount; i < 4; i++) {
                if(movecombo[i] != 0)
                {
                    //invalid, as it does not sum up to 7 then.
                    addsTo7 = false;
                }
            }

            if(addsTo7)
            {
                sevenmove.set_fromPos(fromPos);
                sevenmove.set_toPos(toPos);
                sevenmove.set_fromPosInGoal(fromInGoal);
                sevenmove.set_toPosInGoal(toInGoal);

                //try the move
                try {
                    if(isValidMove(sevenmove)) return true;
                }
                catch (InvalidMoveException e) {
                    //do nothing here
                    //ik this is terrible, but our move is always well-formed in this case
                }
            }
        }

        //1 move into a goal
        for (Integer[] movecombo : allPossibilites) {

            for (int maincount = 0; maincount < marblesOnMain.size(); maincount++) {

                //move init
                Move sevenmove = new Move();
                sevenmove.set_color(col);
                Card c = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
                sevenmove.set_card(c);

                ArrayList<Integer> fromPos = new ArrayList<>();
                ArrayList<Integer> toPos = new ArrayList<>();
                ArrayList<Boolean> fromInGoal = new ArrayList<Boolean>();
                ArrayList<Boolean> toInGoal = new ArrayList<Boolean>();

                int marbleCount = 0;

                //assign to all marbles
                for (int i = 0; i < marblesOnMain.size(); i++) {
                    fromPos.add(marblesOnMain.get(i));
                    toPos.add(getIndexAfterDistance(marblesOnMain.get(i), movecombo[marbleCount]));
                    fromInGoal.add(false);
                    toInGoal.add(i == maincount ? true : false);
                    marbleCount++;
                }
                for (int i = 0; i < marblesInGoal.size(); i++) {
                    fromPos.add(marblesInGoal.get(i));
                    toPos.add(marblesInGoal.get(i) + movecombo[marbleCount]);
                    fromInGoal.add(true);
                    toInGoal.add(true);
                    marbleCount++;
                }

                boolean addsTo7 = true;

                for (int i = marbleCount; i < 4; i++) {
                    if(movecombo[i] != 0)
                    {
                        //invalid, as it does not sum up to 7 then.
                        addsTo7 = false;
                    }
                }

                if(addsTo7)
                {
                    sevenmove.set_fromPos(fromPos);
                    sevenmove.set_toPos(toPos);
                    sevenmove.set_fromPosInGoal(fromInGoal);
                    sevenmove.set_toPosInGoal(toInGoal);

                    //try the move
                    try {
                        if(isValidMove(sevenmove)) return true;
                    }
                    catch (InvalidMoveException e) {
                        //do nothing here
                        //ik this is terrible, but our move is always well-formed in this case
                    }
                }
            }

        }

        //2 moves into a goal
        if(marblesOnMain.size() >= 2)
        {
            for (Integer[] movecombo : allPossibilites) {

                //permutations of what 2 marbles should go in a goal state
                //yes, you see correctly. more for loops
                for (int maincount = 0; maincount < marblesOnMain.size(); maincount++) {
                    for (int offcount = maincount + 1; offcount < marblesInGoal.size(); offcount++) {

                        //move init
                        Move sevenmove = new Move();
                        sevenmove.set_color(col);
                        Card c = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
                        sevenmove.set_card(c);

                        ArrayList<Integer> fromPos = new ArrayList<>();
                        ArrayList<Integer> toPos = new ArrayList<>();
                        ArrayList<Boolean> fromInGoal = new ArrayList<Boolean>();
                        ArrayList<Boolean> toInGoal = new ArrayList<Boolean>();

                        int marbleCount = 0;

                        //assign to all marbles
                        for (int i = 0; i < marblesOnMain.size(); i++) {
                            fromPos.add(marblesOnMain.get(i));
                            toPos.add(getIndexAfterDistance(marblesOnMain.get(i), movecombo[marbleCount]));
                            fromInGoal.add(false);
                            toInGoal.add(i == maincount || i == offcount ? true : false);
                            marbleCount++;
                        }
                        for (int i = 0; i < marblesInGoal.size(); i++) {
                            fromPos.add(marblesInGoal.get(i));
                            toPos.add(marblesInGoal.get(i) + movecombo[marbleCount]);
                            fromInGoal.add(true);
                            toInGoal.add(true);
                            marbleCount++;
                        }

                        boolean addsTo7 = true;

                        for (int i = marbleCount; i < 4; i++) {
                            if(movecombo[i] != 0)
                            {
                                //invalid, as it does not sum up to 7 then.
                                addsTo7 = false;
                            }
                        }

                        if(addsTo7)
                        {
                            sevenmove.set_fromPos(fromPos);
                            sevenmove.set_toPos(toPos);
                            sevenmove.set_fromPosInGoal(fromInGoal);
                            sevenmove.set_toPosInGoal(toInGoal);

                            //try the move
                            try {
                                if(isValidMove(sevenmove)) return true;
                            }
                            catch (InvalidMoveException e) {
                                //do nothing here
                                //ik this is terrible, but our move is always well-formed in this case
                            }
                        }
                    }
                }
            }
        }
        //more than 2 moves into a goal with a 7 is not possible

        return false;
    }

    private int getIndexAfterDistance(int start, int distance) throws IndexOutOfBoundsException
    {
        if(start < 0 || start >= 64 || distance <= -64 || distance >= 64) throw new IndexOutOfBoundsException();

        //normal
        int newPos = start + distance;

        //looping
        if(distance < 0)
        {
            if(newPos < 0)
            {
                newPos = 64 + newPos;
            }
        }else
        {
            if(newPos > 63)
            {
                newPos = newPos - 64;
            }
        }

        return newPos;
    }

    private int getIndexInGoalAfterDistance(int start, int distance, COLOR color) throws IndexOutOfBoundsException {
        if (start < 0 || start >= 64 || distance < 0 || distance >= 64) throw new IndexOutOfBoundsException();

        int intersect = 0;
        switch (color)
        {
            case BLUE:
                intersect = BLUEINTERSECT;
                break;
            case RED:
                intersect = REDINTERSECT;
                break;
            case GREEN:
                intersect = GREENINTERSECT;
                break;
            case YELLOW:
                intersect = YELLOWINTERSECT;
                break;
        }

        //only forwards in goal!
        int distToGoalStart = getDistanceInBetween(start, intersect);
        int restDistance = distance - distToGoalStart;

        //cannot reach a goal
        if(restDistance < 1 || restDistance > 4) return -1;

        return restDistance;

    }

    /**
     * returns true if the move is valid (ie: the move is allowed) based on the card and the rules
     * @param move the move object that contains all the information
     * @throws InvalidMoveException if the move object has any missing params or is not well-formed
     */
    public boolean isValidMove(Move move) throws InvalidMoveException
    {
        //throwing exceptions
        if(move.checkIfComplete()) {
            throw new InvalidMoveException("BAD_MOVE", "move is not complete");
        }
        
        // checking if the move is valid based on the card
        switch(move.get_card().getValue()) {
            case ACE:
                return isValidAceMove(move);
            case KING:
                return isValidKingMove(move);
            case JOKER:
                return isValidJokerMove(move);
            case JACK:
                return isValidJackMove(move);
            case SEVEN:
                return isValidSevenMove(move);
            case FOUR:
                return isValidFourMove(move);
            default:
                return isValidRegularMove(move);
        }
    }

    // check if the move is valid for an ace card
    /**
     * checks if the move is valid for an ace card
     * @param move the move object that contains all the information
     * @throws NoMarbleException if there are no marbles at the position
     * @return true if the move is valid
     */
    private boolean isValidAceMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        COLOR marbleColor;
        MARBLE curMarble;
        try{
            if(startPos != -1){
                marbleColor = this.getColorFromPosition(startPos);
            }else{
                marbleColor = move.get_color();
            }
            curMarble = this._mainCircle.get(startPos);
        } catch(Exception e) {
            throw new NoMarbleException();
        }
        // check if marble color is the same as move color
        if(marbleColor != move.get_color()) {
            return false;
        }
        // validate for starting move
        if(startPos == -1){
            switch(marbleColor){
                case RED:
                    if(endPos == REDINTERSECT && !REDBLOCKED){
                        REDBLOCKED = true;
                        return true;
                    }else{return false;}
                case YELLOW:
                    if(endPos == YELLOWINTERSECT && !YELLOWBLOCKED){
                        YELLOWBLOCKED = true;
                        return true;
                    }else{return false;}
                case GREEN:
                    if(endPos == GREENINTERSECT && !GREENBLOCKED){
                        GREENBLOCKED = true;
                        return true;
                    }else{return false;}
                case BLUE:
                    if(endPos == BLUEINTERSECT && !BLUEBLOCKED){
                        BLUEBLOCKED = true;
                        return true;
                    }else{return false;}
                default:
                    return false;
            }
        }
        //validate for goal move
        if(move.isGoalMove()) {
            boolean startInGoal = !move.get_fromPosInGoal().get(0);
            return isValidGoalMove(startPos, endPos, startInGoal, marbleColor);
        }
        int moveDist = getDistanceInBetween(startPos, endPos);
        if(moveDist != 1 || moveDist != 11) {
            return false;
        }
        return true;
    }

    // check if the move is valid for a jack card
    private boolean isValidJackMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        COLOR marbleColor;
        MARBLE curMarble;
        try{
            if(startPos == -1) {return false;}
            marbleColor = this.getColorFromPosition(startPos);
            curMarble = this._mainCircle.get(startPos);
        } catch(Exception e) {
            throw new NoMarbleException();
        }
        // check if marble color is the same as move color
        if(marbleColor != move.get_color()) {
            return false;
        }
        // validate for starting move
        if(startPos == -1){
            return false;
        }
        //TODO jack behaviour unclear yet: unclear on how front-end passes the move
        if(this._mainCircle.get(startPos) == MARBLE.NONE || this._mainCircle.get(endPos) == MARBLE.NONE || marbleColor == this.getColorFromPosition(endPos)) {
            return false;
        }
        return true;
    }

    // check if the move is valid for a joker card
    private boolean isValidJokerMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        COLOR marbleColor;
        MARBLE curMarble;
        try{
            if(startPos != -1){
                marbleColor = this.getColorFromPosition(startPos);
            }else{
                marbleColor = move.get_color();
            }
            curMarble = this._mainCircle.get(startPos);
        } catch(Exception e) {
            throw new NoMarbleException();
        }
        // check if marble color is the same as move color
        if(marbleColor != move.get_color()) {
            return false;
        }
        // validate for starting move
        if(startPos == -1){

            switch(marbleColor){
                case RED:
                    if(endPos == REDINTERSECT && !REDBLOCKED){
                        REDBLOCKED = true;
                        return true;
                    }else{return false;}
                case YELLOW:
                    if(endPos == YELLOWINTERSECT && !YELLOWBLOCKED){
                        YELLOWBLOCKED = true;
                        return true;
                    }else{return false;}
                case GREEN:
                    if(endPos == GREENINTERSECT && !GREENBLOCKED){
                        GREENBLOCKED = true;
                        return true;
                    }else{return false;}
                case BLUE:
                    if(endPos == BLUEINTERSECT && !BLUEBLOCKED){
                        BLUEBLOCKED = true;
                        return true;
                    }else{return false;}
                default:
                    return false;
            }
        }
        //validate for goal move
        if(move.isGoalMove()) {
            boolean startInGoal = !move.get_fromPosInGoal().get(0);
            return isValidGoalMove(startPos, endPos, startInGoal, marbleColor);
        }
        //TODO joker behaviour unclear yet, can be any card: need to define behaviour in front-end
        return true;
    }

    // check if the move is valid for a king card
    private boolean isValidKingMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        COLOR marbleColor;
        MARBLE curMarble;
        try{
            if(startPos != -1){
                marbleColor = this.getColorFromPosition(startPos);
            }else{
                marbleColor = move.get_color();
            }
            curMarble = this._mainCircle.get(startPos);
        } catch(Exception e) {
            throw new NoMarbleException();
        }
        // check if marble color is the same as move color
        if(marbleColor != move.get_color()) {
            return false;
        }
        // validate for starting move
        if(startPos == -1){
            switch(marbleColor){
                case RED:
                    if(endPos == REDINTERSECT && !REDBLOCKED){
                        REDBLOCKED = true;
                        return true;
                    }else{return false;}
                case YELLOW:
                    if(endPos == YELLOWINTERSECT && !YELLOWBLOCKED){
                        YELLOWBLOCKED = true;
                        return true;
                    }else{return false;}
                case GREEN:
                    if(endPos == GREENINTERSECT && !GREENBLOCKED){
                        GREENBLOCKED = true;
                        return true;
                    }else{return false;}
                case BLUE:
                    if(endPos == BLUEINTERSECT && !BLUEBLOCKED){
                        BLUEBLOCKED = true;
                        return true;
                    }else{return false;}
                default:
                    return false;
            }
        }
        //validate for goal move
        if(move.isGoalMove()) {
            boolean startInGoal = !move.get_fromPosInGoal().get(0);
            return isValidGoalMove(startPos, endPos, startInGoal, marbleColor);
        }
        //check if distance is valid for the card
        int moveDist = getDistanceInBetween(startPos, endPos);
        if(moveDist != 13) {
            return false;
        }
        return true;
    }

    // check if the move is valid for a 7 card
    /**
     * checks if the move is valid for a 7 card
     * @param move
     * @return true if all moves are valid, a single invalid move will return false
     * @throws NoMarbleException
     */
    private boolean isValidSevenMove(Move move) throws NoMarbleException {
        ArrayList<Integer> startPos = move.get_fromPos();
        ArrayList<Integer> endPos = move.get_toPos();
        ArrayList<COLOR> marbleColor = new ArrayList<>();
        ArrayList<MARBLE> curMarble = new ArrayList<>();
        try{
            for (int i = 0; i < startPos.size(); i++) {
                if(startPos.get(i) == -1){return false;} //seven cant make a starting move
                marbleColor.add(this.getColorFromPosition(startPos.get(i)));
                curMarble.add(this._mainCircle.get(startPos.get(i)));
            }
        } catch(Exception e) {
            throw new NoMarbleException();
        }
        // check if marble color is the same as move color
        for (int i = 0; i < marbleColor.size(); i++) {
            if (marbleColor.get(i) != move.get_color()) {
                return false;
            }
        }
        int moveDist = 0;
        for(int i = 0; i < startPos.size(); i++) {
            moveDist += getDistanceInBetween(startPos.get(i), endPos.get(i));
        }
        if(moveDist != 7) {
            return false;
        }
        //validate for goal move
        if(move.isGoalMove()) {
            for(int i = 0; i < startPos.size(); i++) {
                if(move.get_toPosInGoal().get(i)){
                    if(!isValidGoalMove(startPos.get(i), endPos.get(i), move.get_fromPosInGoal().get(i), marbleColor.get(i))){
                        return false;
                    }
                    
                }
            }
        }
        return true;
    }

    // check if the move is valid for a 4 card
    private boolean isValidFourMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        COLOR marbleColor;
        MARBLE curMarble;
        try{
            if(startPos == -1){return false;}
            marbleColor = this.getColorFromPosition(startPos);
            curMarble = this._mainCircle.get(startPos);
        } catch(Exception e) {
            throw new NoMarbleException();
        }
        // check if marble color is the same as move color
        if(marbleColor != move.get_color()) {
            return false;
        }
        int moveDistForward = getDistanceInBetween(startPos, endPos, true);
        int moveDistBackward = getDistanceInBetween(startPos, endPos, false);
        if(moveDistForward != 4 || moveDistBackward != 4) {
            return false;
        }
        //validate for goal move
        if(move.isGoalMove()) {
            boolean startInGoal = !move.get_fromPosInGoal().get(0);
            return isValidGoalMove(startPos, endPos, startInGoal, marbleColor);
        }
        return true;
    }

    // check if the move is valid for a regular card
    private boolean isValidRegularMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        Card moveCard = move.get_card();
        COLOR marbleColor;
        try{
            if(startPos == -1){return false;}
            marbleColor = this.getColorFromPosition(startPos);
        } catch(Exception e) {
            throw new NoMarbleException();
        }
        // check if marble color is the same as move color
        if(marbleColor != move.get_color()) {
            return false;
        }
        int moveDist = getDistanceInBetween(startPos, endPos);
        switch(moveCard.getValue()) {
            case TWO: return moveDist == 2;
            case THREE: return moveDist == 3;
            case FIVE: return moveDist == 5;
            case SIX: return moveDist == 6;
            case EIGHT: return moveDist == 8;
            case NINE: return moveDist == 9;
            case TEN: return moveDist == 10;
            case QUEEN: return moveDist == 12;
        }
        //validate for goal move
        if(move.isGoalMove()) {
            boolean startInGoal = !move.get_fromPosInGoal().get(0);
            return isValidGoalMove(startPos, endPos, startInGoal, marbleColor);
        }
        return true;
    }

    /**
     * checks if the move is valid to enter goal
     * assumes that endPos will end in goal, otherwise this methos is not called
     * @param move
     * @return true if the move can enter the goal
     */
    private boolean isValidGoalMove(int startPos, int endPos, boolean startPosInGoal, COLOR color) {
        int moveDist = getDistanceInBetween(startPos, endPos);
        //get intersect and goal depending on marble color
        int intersect = -1;
        ArrayList<MARBLE> goal = new ArrayList<>();
        switch(color){
            case RED:
                intersect = REDINTERSECT;
                goal = _redGoal;
                break;
            case YELLOW:
                intersect = YELLOWINTERSECT;
                goal = _yellowGoal;
                break;
            case GREEN:
                intersect = GREENINTERSECT;
                goal = _greenGoal;
                break;
            case BLUE:
                intersect = BLUEINTERSECT;
                goal = _blueGoal;
                break;
        }
        //check if intersect can be reached or startPos is in goal
        if(moveDist < getDistanceInBetween(startPos, intersect) && !startPosInGoal) {
            return false;
        }
        if(startPosInGoal) {
            //check if goal can be reached from inside goal
            if(moveDist < getDistanceInBetween(startPos, endPos, color, true)) {
                return false;
            }
            //check if there is a marble between startPos and endPos in goal
            for(int i = startPos; i <= endPos; i++){
                if(goal.get(i) != MARBLE.NONE) {
                    return false;
                }
            }
        }else{
            //check if goal can be reached from outside goal
            if(moveDist < getDistanceInBetween(startPos, endPos, color, false)) {
                return false;
            }
            //check if there is a marble between intersect and endPos in goal
            for(int i = 0; i <= endPos; i++){
                if(goal.get(i) != MARBLE.NONE) {
                    return false;
                }
            }
        }
        return true;
    }
}   
