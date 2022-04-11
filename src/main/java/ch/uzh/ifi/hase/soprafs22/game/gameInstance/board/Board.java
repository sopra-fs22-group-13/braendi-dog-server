package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.MARBLE;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.MoveBlockedByMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.NoMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;

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

    private boolean setMarbleAtPosition(int position, MARBLE marble, COLOR goalColor)
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

    private boolean isEmptyAt(int position)
    {
        MARBLE m = _mainCircle.get(position);
        return m == MARBLE.NONE;
    }

    /**
     * Gets the inbetweeners in the main circle in the FORWARDS direction! (exclusive, inclusive)
     * IMPORTANT: NEVER USE THIS METHOD WHEN TRYING TO MOVE BACKWARDS
     */
    private ArrayList<Integer> getInbetweeners(int pos1, int pos2) throws IndexOutOfBoundsException
    {
        ArrayList<Integer> importantInbetweeners = new ArrayList<>();
        if(pos1 < 0 || pos1 >= 64 || pos2 < 0 || pos2 >= 64) throw new IndexOutOfBoundsException("the positions have to be in range 0-63 (inclusive)");

        if(pos1 < pos2)
        {
            //no loop
            for (int i = pos1 + 1; i <= pos2; i++) {
                if(_mainCircle.get(i) != MARBLE.NONE)
                {
                    importantInbetweeners.add(i);
                }
            }
        }else
        {
            //get to half way
            for (int i = pos1 + 1; i < 64; i++) {
                if(_mainCircle.get(i) != MARBLE.NONE)
                {
                    importantInbetweeners.add(i);
                }
            }
            //rest
            for (int i = 0; i <= pos2 ; i++) {
                if(_mainCircle.get(i) != MARBLE.NONE)
                {
                    importantInbetweeners.add(i);
                }
            }
        }

        return importantInbetweeners;
    }

    /**
     * Resets a marble on the main circle back to the base
     */
    private void resetMarble(int pos) throws NoMarbleException, IndexOutOfBoundsException
    {
        if(pos < 0 || pos >= 64) throw new IndexOutOfBoundsException("the position has to be in range 0-63 (inclusive)");

        if(_mainCircle.get(pos) == MARBLE.NONE) throw new NoMarbleException();

        MARBLE m = _mainCircle.get(pos);

        setMarbleAtPosition(pos, MARBLE.NONE);
        switch (m)
        {
            case RED:
                _redBase ++;
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
    private void resetInbetweeners(int pos1, int pos2) throws NoMarbleException
    {
        ArrayList<Integer> relevantInbetweeners = getInbetweeners(pos1, pos2);

        for (Integer inb: relevantInbetweeners) {
            resetMarble(inb);
        }
    }

    /**
     * Get the TurnColor from a marble at a position x on the main circle
     * @throws NoMarbleException if there is no marble at x
     */
    private COLOR getColorFromPosition(int pos) throws NoMarbleException
    {
        if(pos < 0 || pos >= 64) throw new IndexOutOfBoundsException("the position has to be in range 0-63 (inclusive)");
        MARBLE m = _mainCircle.get(pos);

        switch (m)
        {
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
     * @param removeInbetweeners IMPORTANT: NEVER SET THIS TO TRUE WHEN TRYING TO MOVE BACKWARDS
     * @throws NoMarbleException no marble was at pos1
     * @throws MoveBlockedByMarbleException there was already a marble at pos2
     */
    private void movePositions(int pos1, int pos2, boolean removeInbetweeners)
        throws InvalidMoveException, IndexOutOfBoundsException
    {
        if(pos1 < 0 || pos1 >= 64 || pos2 < 0 || pos2 >= 64) throw new IndexOutOfBoundsException("the positions have to be in range 0-63 (inclusive)");

        MARBLE m1 = _mainCircle.get(pos1);
        MARBLE m2 = _mainCircle.get(pos2);

        if(m1 == MARBLE.NONE) throw new NoMarbleException();
        if(m2 != MARBLE.NONE) {
            //reset the problem marble
            resetMarble(pos2);
        }

        //all good, make the move
        if(removeInbetweeners)
        {
            resetInbetweeners(pos1, pos2);
        }

        setMarbleAtPosition(pos2, m1);
        setMarbleAtPosition(pos1, MARBLE.NONE);
    }


    /**
     * Moves the marble from position 1 to position 2 where position 2 is in the respective goal.
     * @param goalColor the goal color to consider
     * @param startInGoal defines if position 1 is already in the goal. (ex: move 1 forward in the goal)
     * @param removeInbetweeners IMPORTANT: NEVER SET THIS TO TRUE WHEN TRYING TO MOVE BACKWARDS
     * @throws NoMarbleException no marble was at pos1
     * @throws MoveBlockedByMarbleException there was already a marble at pos2
     */
    private void movePositions(int pos1, int pos2, COLOR goalColor, boolean startInGoal, boolean removeInbetweeners)
            throws InvalidMoveException, IndexOutOfBoundsException
    {
        MARBLE m1;
        MARBLE m2;

        int colorintersect = 0;

        ArrayList<MARBLE> coloredGoalList = _redGoal;
        //get the respective color
        switch (goalColor)
        {
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
                colorintersect =YELLOWINTERSECT;
                break;
            case GREEN:
                coloredGoalList = _greenGoal;
                colorintersect = GREENINTERSECT;
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
        if(m2 != MARBLE.NONE) {
            //reset the problem marble
            resetMarble(pos2);
        }

        //all good, make the move

        if(removeInbetweeners)
        {
            resetInbetweeners(pos1, colorintersect);
        }

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
    private int getDistanceInBetween(int startPosition, int endPosition, COLOR goalColor, boolean startInGoal) throws IndexOutOfBoundsException
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

    /**
     * Makes one or more moves specified by the move object.
     * IMPORTANT: This does NOT check if the move is allowed depending on the rules (eg: move cost 12, card value: 8)
     * @param move the move object that holds the move
     * @throws InvalidMoveException if the move cannot be completed because of the board state
     */
    public void makeMove(Move move) throws InvalidMoveException
    {
        //expects the move to be valid
        if(move == null || !move.isWellFormed())
        {
            throw new InvalidMoveException("BAD_STRUCTURE", "Bad move structure");
        }

        //For each move, make it
        for (int i = 0; i < move.get_fromPos().size(); i++) {
            int fromPos = move.get_fromPos().get(i);
            int toPos = move.get_toPos().get(i);
            boolean startsInGoal = move.get_fromPosInGoal().get(i);
            boolean endsInGoal = move.get_toPosInGoal().get(i);

            //do the move
            if(endsInGoal)
            {
                movePositions(fromPos, toPos, move.get_color(), startsInGoal, move.get_card() != null? move.get_card().isSeven() : false);
            }
            else
            {
                movePositions(fromPos, toPos, move.get_card() != null? move.get_card().isSeven() : false);
            }

        }
    }

    /**
     * Makes a starting move (from the base to the starting intersect position)
     * IMPORTANT: This does NOT check if the move is allowed depending on the rules (eg: card is not Ace/Joker/King)
     * @param color the color we want to move out
     * @throws InvalidMoveException if the move cannot be completed because of the board state
     */
    public void makeStartingMove(COLOR color) throws InvalidMoveException
    {
        switch (color)
        {
            case RED:
                if(_redBase > 0)
                {
                    if(!isEmptyAt(REDINTERSECT)) resetMarble(REDINTERSECT);

                    _redBase = _redBase-1;
                    setMarbleAtPosition(REDINTERSECT, MARBLE.RED);
                }else
                {
                    throw new InvalidMoveException("NOTHING_LEFT", "there are no marbles left to start with");
                }
                break;
            case YELLOW:
                if(_yellowBase > 0)
                {
                    if(!isEmptyAt(YELLOWINTERSECT)) resetMarble(YELLOWINTERSECT);

                    _yellowBase = _yellowBase-1;
                    setMarbleAtPosition(YELLOWINTERSECT, MARBLE.YELLOW);
                }else
                {
                    throw new InvalidMoveException("NOTHING_LEFT", "there are no marbles left to start with");
                }
                break;
            case GREEN:
                if(_greenBase > 0)
                {
                    if(!isEmptyAt(GREENINTERSECT)) resetMarble(GREENINTERSECT);

                    _greenBase = _greenBase-1;
                    setMarbleAtPosition(GREENINTERSECT, MARBLE.GREEN);
                }else
                {
                    throw new InvalidMoveException("NOTHING_LEFT", "there are no marbles left to start with");
                }
                break;
            case BLUE:
                if(_blueBase > 0)
                {
                    if(!isEmptyAt(BLUEINTERSECT)) resetMarble(BLUEINTERSECT);

                    _blueBase = _blueBase-1;
                    setMarbleAtPosition(BLUEINTERSECT, MARBLE.BLUE);
                }else
                {
                    throw new InvalidMoveException("NOTHING_LEFT", "there are no marbles left to start with");
                }
                break;
        }
    }

    /**
     * Makes a switch specified by the two positions on the board
     * IMPORTANT: This does NOT check if the move is allowed depending on the rules (eg: card is not a joker/jack)
     * @throws InvalidMoveException if the move cannot be completed because of the board state or if the card does not allow the move
     */
    public void makeSwitch(int start, int end) throws InvalidMoveException
    {
        try{
            changePositions(start, end);
        }catch (IndexOutOfBoundsException e)
        {
            throw new InvalidMoveException("OUT_OF_BOUNDS", "one of the positions was out of bounds (0-63)");
        }
    }

    /**
     * makes move based on the card of the move object
     * @param move
     * @throws InvalidMoveException
     */
    public void makeCardMove(Move move) throws InvalidMoveException {
        if(isValidMove(move)){  
            Card moveCard = move.get_card();
            switch(moveCard.getValue()){
                case ACE:
                case KING:
                case JACK:
                case JOKER:
                default:
            }
        }else{
            throw new InvalidMoveException();
        }
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
            switch(marbleColor){
                case RED:
                    return endPos == REDINTERSECT;
                case YELLOW:
                    return endPos == YELLOWINTERSECT;
                case GREEN:
                    return endPos == GREENINTERSECT;
                case BLUE:
                    return endPos == BLUEINTERSECT;
                default:
                    return false;
            }
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
        }else{
            // TODO jack behaviour unclear yet: unclear on how front-end passes the move
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
            switch(marbleColor){
                case RED:
                    return endPos == REDINTERSECT;
                case YELLOW:
                    return endPos == YELLOWINTERSECT;
                case GREEN:
                    return endPos == GREENINTERSECT;
                case BLUE:
                    return endPos == BLUEINTERSECT;
                default:
                    return false;
            }
        }
        int moveDist = getDistanceInBetween(startPos, endPos);
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
            switch(marbleColor){
                case RED:
                    return endPos == REDINTERSECT;
                case YELLOW:
                    return endPos == YELLOWINTERSECT;
                case GREEN:
                    return endPos == GREENINTERSECT;
                case BLUE:
                    return endPos == BLUEINTERSECT;
                default:
                    return false;
            }
        }
        int moveDist = getDistanceInBetween(startPos, endPos);
        if(moveDist != 13) {
            return false;
        }
        return true;
    }

    // check if the move is valid for a 7 card
    private boolean isValidSevenMove(Move move) throws NoMarbleException {
        ArrayList<Integer> startPos = move.get_fromPos();
        ArrayList<Integer> endPos = move.get_toPos();
        ArrayList<COLOR> marbleColor = new ArrayList<>();
        ArrayList<MARBLE> curMarble = new ArrayList<>();
        try{
            for (int i = 0; i < startPos.size(); i++) {
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
        return true;
    }

    // check if the move is valid for a 4 card
    private boolean isValidFourMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        COLOR marbleColor;
        MARBLE curMarble;
        try{
            marbleColor = this.getColorFromPosition(startPos);
            curMarble = this._mainCircle.get(startPos);
        } catch(Exception e) {
            throw new NoMarbleException();
        }
        // check if marble color is the same as move color
        if(marbleColor != move.get_color()) {
            return false;
        }
        //TODO check for the behavior of 4
        return true;
    }

    // check if the move is valid for a regular card
    private boolean isValidRegularMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        Card moveCard = move.get_card();
        COLOR marbleColor;
        MARBLE curMarble;
        try{
            marbleColor = this.getColorFromPosition(startPos);
            curMarble = this._mainCircle.get(startPos);
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
            case FOUR: return moveDist == 4;
            case FIVE: return moveDist == 5;
            case SIX: return moveDist == 6;
            case EIGHT: return moveDist == 8;
            case NINE: return moveDist == 9;
            case TEN: return moveDist == 10;
            case QUEEN: return moveDist == 12;
            default: return false;
        }
    }
}
