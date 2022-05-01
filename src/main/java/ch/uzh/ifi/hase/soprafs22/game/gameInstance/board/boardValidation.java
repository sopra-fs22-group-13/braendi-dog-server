package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.*;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.MoveBlockedByMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.NoMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.Arrays;

public class boardValidation {

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

    public boardValidation(
            ArrayList<MARBLE> _mainCircle,
            ArrayList<MARBLE> _redGoal,
            ArrayList<MARBLE> _greenGoal,
            ArrayList<MARBLE> _blueGoal,
            ArrayList<MARBLE> _yellowGoal,
            int _redBase,
            int _greenBase,
            int _blueBase,
            int _yellowBase,
            boolean REDBLOCKED,
            boolean GREENBLOCKED,
            boolean BLUEBLOCKED,
            boolean YELLOWBLOCKED,
            Card _lastPlayedCard) {
        super();
        this._mainCircle = _mainCircle;
        this._redGoal = _redGoal;
        this._greenGoal = _greenGoal;
        this._blueGoal = _blueGoal;
        this._yellowGoal = _yellowGoal;
        this._redBase = _redBase;
        this._greenBase = _greenBase;
        this._blueBase = _blueBase;
        this._yellowBase = _yellowBase;
        this.REDBLOCKED = REDBLOCKED;
        this.GREENBLOCKED = GREENBLOCKED;
        this.BLUEBLOCKED = BLUEBLOCKED;
        this.YELLOWBLOCKED = YELLOWBLOCKED;
        this._lastPlayedCard = _lastPlayedCard;
    }

    public void makeMove(Move move) throws InvalidMoveException {
        // expects the move to be valid
        if (move == null || !move.isWellFormed()) {
            throw new InvalidMoveException("BAD_STRUCTURE", "Bad move structure");
        }

        // For each move, make it
        for (int i = 0; i < move.get_fromPos().size(); i++) {
            int fromPos = move.get_fromPos().get(i);
            int toPos = move.get_toPos().get(i);
            boolean startsInGoal = move.get_fromPosInGoal().get(i);
            boolean endsInGoal = move.get_toPosInGoal().get(i);
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

    private void resetInbetweeners(int pos1, int pos2) throws NoMarbleException {
        ArrayList<Integer> relevantInbetweeners = getInbetweeners(pos1, pos2);

        for (Integer inb : relevantInbetweeners) {
            resetMarble(inb);
        }
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

        if (removeInbetweeners) {
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

    private COLOR getMarbleColor(Move move, int startPos) throws NoMarbleException {
        COLOR marbleColor;
        try {
            if (startPos != -1 && !move.isGoalMove()) {
                marbleColor = getColorFromPosition(startPos);
            } else {
                marbleColor = move.get_color();
            }
        } catch (Exception e) {
            System.out.println(e);
            throw new NoMarbleException();
        }
        return marbleColor;
    }

    private ArrayList<COLOR> getMarbleColor(Move move, ArrayList<Integer> startPos) throws NoMarbleException {
        ArrayList<COLOR> marbleColor = new ArrayList<>();
        try {
            for (int i = 0; i < startPos.size(); i++) {
                marbleColor.add(this.getColorFromPosition(startPos.get(i)));
            }
        } catch (Exception e) {
            throw new NoMarbleException();
        }
        return marbleColor;
    }

    // checks if the start move is valid
    private boolean validStart(Move move, int startPos, int endPos) throws NoMarbleException {
        try {
            COLOR marbleColor = getMarbleColor(move, startPos);
            switch (marbleColor) {
                case RED:
                    if (_redBase == 0) {
                        return false;
                    } else if (endPos == REDINTERSECT && !REDBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                case YELLOW:
                    if (_yellowBase == 0) {
                        return false;
                    } else if (endPos == YELLOWINTERSECT && !YELLOWBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                case GREEN:
                    if (_greenBase == 0) {
                        return false;
                    } else if (endPos == GREENINTERSECT && !GREENBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                case BLUE:
                    if (_blueBase == 0) {
                        return false;
                    } else if (endPos == BLUEINTERSECT && !BLUEBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                default:
                    return false;
            }
        } catch (Exception e) {
            throw new NoMarbleException();
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

    /**
     * gets the next position when moving forward a certain distance on the main
     * field
     */
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

    /**
     * gets the next position when moving a certain distance to a goal, forward
     * only!
     *
     * @param startPosition the start position (0-63 or 0-3, depending on
     *                      startInGoal)
     * @param endPosition   the end position (0-3)
     * @param goalColor     the color of the goal we are considering for moving into
     * @param startInGoal   if the marble already starts in its respective goal (eg:
     *                      moving 1 forward in the goal)
     */
    private int getDistanceInBetween(int startPosition, int endPosition, COLOR goalColor, boolean startInGoal)
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

    private int getMoveDist(Move move) {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        COLOR marbleColor = move.get_color();
        int moveDist;
        // abstract the check for the goal move
        if(move.get_fromPosInGoal().get(0)){
            moveDist = getDistanceInBetween(startPos, endPos, marbleColor, true);
        }else if(move.get_toPosInGoal().get(0)){
            moveDist = getDistanceInBetween(startPos, endPos, marbleColor, false);
        }else{
            moveDist = getDistanceInBetween(startPos, endPos);
        }
        return moveDist;
    }

    public boolean isValidMove(Move move) throws InvalidMoveException {
        // throwing exceptions
        if (!move.checkIfComplete()) {
            throw new InvalidMoveException("BAD_MOVE", "move is not complete");
        }

        // checking if the move is valid based on the card
        switch (move.get_card().getValue()) {
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
     * 
     * @param move the move object that contains all the information
     * @throws NoMarbleException if there are no marbles at the position
     * @return true if the move is valid
     */
    private boolean isValidAceMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        COLOR marbleColor = getMarbleColor(move, startPos);
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return false;
        }
        // validate for starting move
        if (startPos == -1) {
            return validStart(move, startPos, endPos);
        }
        // validate for goal move
        if (move.isGoalMove()) {
            boolean startInGoal = move.get_fromPosInGoal().get(0);
            if (!isValidGoalMove(startPos, endPos, startInGoal, marbleColor)) {
                return false;
            }
        }
        int moveDist = getMoveDist(move);
        if (moveDist != 1 && moveDist != 11) {
            return false;
        }

        // checking for blocked intersect
        if (blockedIntersect(move, startPos, endPos, true)) {
            return false;
        }
        return true;
    }

    // check if the move is valid for a jack card
    private boolean isValidJackMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        COLOR marbleColor = getMarbleColor(move, startPos);
        if (startPos == -1) {
            return false;
        }
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return false;
        }
        // validate for starting move
        if (startPos == -1) {
            return false;
        }
        // jack behaviour unclear yet: unclear on how front-end passes the move
        if (this._mainCircle.get(startPos) == MARBLE.NONE || this._mainCircle.get(endPos) == MARBLE.NONE
                || marbleColor == this.getColorFromPosition(endPos)) {
            return false;
        }
        // TODO dont know if Jack can move when the other marble is on a blocked
        // check for blocked intersect
        switch (endPos) {
            case REDINTERSECT:
                if (REDBLOCKED) {
                    return false;
                }
                break;
            case YELLOWINTERSECT:
                if (YELLOWBLOCKED) {
                    return false;
                }
                break;
            case GREENINTERSECT:
                if (GREENBLOCKED) {
                    return false;
                }
                break;
            case BLUEINTERSECT:
                if (BLUEBLOCKED) {
                    return false;
                }
                break;
        }
        return true;
    }

    // check if the move is valid for a joker card
    private boolean isValidJokerMove(Move move) throws NoMarbleException {
        // check if the move is a valid move for another card, check this recursively
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        if (startPos == -1) {
            return validStart(move, startPos, endPos);
        }
        ArrayList<CARDVALUE> allValues = new ArrayList<>(Arrays.asList(CARDVALUE.values()));
        allValues.remove(CARDVALUE.JOKER);
        boolean validMove = false;
        for (CARDVALUE value : allValues) {
            Card c = new Card(value, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
            Move newMove = new Move(move.get_fromPos(), move.get_toPos(), move.get_fromPosInGoal(),
                    move.get_toPosInGoal(), c, move.getToken(), move.get_color());
            try {
                if (isValidMove(newMove)) {
                    validMove = true;
                }
            } catch (InvalidMoveException e) {
                System.out.println(e);
            }
        }

        return validMove;
    }

    // check if the move is valid for a king card
    private boolean isValidKingMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        COLOR marbleColor = getMarbleColor(move, startPos);
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return false;
        }
        // validate for starting move
        if (startPos == -1) {
            return validStart(move, startPos, endPos);
        }
        // validate for goal move
        if (move.isGoalMove()) {
            boolean startInGoal = move.get_fromPosInGoal().get(0);
            if (!isValidGoalMove(startPos, endPos, startInGoal, marbleColor)) {
                return false;
            }
        }
        // check if distance is valid for the card
        int moveDist = getMoveDist(move);
        if (moveDist != 13) {
            return false;
        }

        // checking for blocked intersect
        if (blockedIntersect(move, startPos, endPos, true)) {
            return false;
        }
        return true;
    }

    // check if the move is valid for a 7 card
    /**
     * checks if the move is valid for a 7 card
     * 
     * @param move
     * @return true if all moves are valid, a single invalid move will return false
     * @throws NoMarbleException
     */
    private boolean isValidSevenMove(Move move) throws NoMarbleException {
        ArrayList<Integer> startPos = move.get_fromPos();
        ArrayList<Integer> endPos = move.get_toPos();
        ArrayList<COLOR> marbleColor = new ArrayList<>();
        try {
            for (int i = 0; i < startPos.size(); i++) {
                if (startPos.get(i) == -1) {
                    return false;
                } // seven cant make a starting move
                marbleColor.add(getMarbleColor(move, startPos.get(i)));
            }
        } catch (Exception e) {
            throw new NoMarbleException();
        }
        // check if marble color is the same as move color
        for (int i = 0; i < marbleColor.size(); i++) {
            if (marbleColor.get(i) != move.get_color()) {
                return false;
            }
        }
        int moveDist = 0;
        if (move.isGoalMove()) {
            for (int i = 0; i < startPos.size(); i++) {
                if (move.get_fromPosInGoal().get(i)) {
                    moveDist += getDistanceInBetween(startPos.get(i), endPos.get(i), marbleColor.get(i), true);
                } else if (move.get_toPosInGoal().get(i)) {
                    moveDist += getDistanceInBetween(startPos.get(i), endPos.get(i), marbleColor.get(i), false);
                } else {
                    moveDist += getDistanceInBetween(startPos.get(i), endPos.get(i));
                }
            }
        } else {
            for (int i = 0; i < startPos.size(); i++) {
                moveDist += getDistanceInBetween(startPos.get(i), endPos.get(i));
            }
        }
        if (moveDist != 7) {
            return false;
        }
        // validate for goal move
        if (move.isGoalMove()) {
            for (int i = 0; i < startPos.size(); i++) {
                if (move.get_toPosInGoal().get(i)) {
                    if (!isValidGoalMove(startPos.get(i), endPos.get(i), move.get_fromPosInGoal().get(i),
                            marbleColor.get(i))) {
                        return false;
                    }
                }
            }
        }

        // checking for blocked intersect
        for (int i = 0; i < startPos.size(); i++) {
            int start = startPos.get(i);
            int end = endPos.get(i);
            if (blockedIntersect(move, start, end, true)) {
                return false;
            }
            // make the move and check blocked again
            ArrayList<Integer> partStartPos = new ArrayList<>(Arrays.asList(start));
            ArrayList<Integer> partEndPos = new ArrayList<>(Arrays.asList(end));
            ArrayList<Boolean> partStartInGoal = new ArrayList<>(Arrays.asList(move.get_fromPosInGoal().get(i)));
            ArrayList<Boolean> partEndInGoal = new ArrayList<>(Arrays.asList(move.get_toPosInGoal().get(i)));
            Move partMove = new Move(
                    partStartPos,
                    partEndPos,
                    partStartInGoal,
                    partEndInGoal,
                    move.get_card(),
                    "token",
                    move.get_color());
            try {
                makeMove(partMove);
            } catch (Exception e) {
                System.out.println(e);
            }

        }

        return true;
    }

    // check if the move is valid for a 4 card
    private boolean isValidFourMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        COLOR marbleColor = getMarbleColor(move, startPos);
        boolean isBackwardMove = false;
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return false;
        }
        // check if distance is valid for the card
        int moveDistForward = getDistanceInBetween(startPos, endPos, true);
        int moveDistBackward = getDistanceInBetween(startPos, endPos, false);
        if (move.isGoalMove()) {
            moveDistForward = getDistanceInBetween(startPos, endPos, marbleColor, false);
        }
        if (moveDistForward != 4 && moveDistBackward != 4) {
            return false;
        }
        if (moveDistBackward == 4 && moveDistForward != 4) {
            isBackwardMove = true;
        }
        // validate for goal move
        if (move.isGoalMove()) {
            if (isBackwardMove) {
                return false;
            }
            boolean startInGoal = move.get_fromPosInGoal().get(0);
            if (!isValidGoalMove(startPos, endPos, startInGoal, marbleColor)) {
                return false;
            }
        }

        // checking for blocked intersect
        if (blockedIntersect(move, startPos, endPos, !isBackwardMove)) {
            return false;
        }
        return true;
    }

    // check if the move is valid for a regular card
    private boolean isValidRegularMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        Card moveCard = move.get_card();
        COLOR marbleColor = getMarbleColor(move, startPos);

        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return false;
        }
        if (startPos == -1) {
            return false;
        }
        int moveDist = getMoveDist(move);
        switch (moveCard.getValue()) {
            case TWO:
                if (moveDist != 2) {
                    return false;
                }
                break;
            case THREE:
                if (moveDist != 3) {
                    return false;
                }
                break;
            case FIVE:
                if (moveDist != 5) {
                    return false;
                }
                break;
            case SIX:
                if (moveDist != 6) {
                    return false;
                }
                break;
            case EIGHT:
                if (moveDist != 8) {
                    return false;
                }
                break;
            case NINE:
                if (moveDist != 9) {
                    return false;
                }
                break;
            case TEN:
                if (moveDist != 10) {
                    return false;
                }
                break;
            case QUEEN:
                if (moveDist != 12) {
                    return false;
                }
                break;
        }
        // validate for goal move
        if (move.isGoalMove()) {
            boolean startInGoal = move.get_fromPosInGoal().get(0);
            if (!isValidGoalMove(startPos, endPos, startInGoal, marbleColor)) {
                return false;
            }
        }

        // checking for blocked intersect
        if (blockedIntersect(move, startPos, endPos, true)) {
            return false;
        }
        return true;
    }

    /**
     * checks if the move is valid to enter goal
     * assumes that endPos will end in goal, otherwise this methos is not called
     *
     * @return true if the move can enter the goal
     */
    private boolean isValidGoalMove(int startPos, int endPos, boolean startPosInGoal, COLOR color) {
        int moveDist = getDistanceInBetween(startPos, endPos, color, startPosInGoal);
        // get intersect and goal depending on marble color
        int intersect = -1;
        ArrayList<MARBLE> goal = new ArrayList<>();
        if (!startPosInGoal) {
            switch (color) {
                case RED:
                    if (REDBLOCKED) {
                        return false;
                    }
                    break;
                case YELLOW:
                    if (YELLOWBLOCKED) {
                        return false;
                    }
                    break;
                case GREEN:
                    if (GREENBLOCKED) {
                        return false;
                    }
                    break;
                case BLUE:
                    if (BLUEBLOCKED) {
                        return false;
                    }
                    break;
            }
        }
        switch (color) {
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
        // check if intersect can be reached or startPos is in goal
        if (!startPosInGoal) {
            if (moveDist < getDistanceInBetween(startPos, intersect)) {
                return false;
            }
        }
        if (startPosInGoal) {
            // check if goal can be reached from inside goal
            if (moveDist < getDistanceInBetween(startPos, endPos, color, true)) {
                return false;
            }
            // check if there is a marble between startPos and endPos in goal
            for (int i = startPos + 1; i <= endPos; i++) {
                if (goal.get(i) != MARBLE.NONE) {
                    return false;
                }
            }
        } else {
            // check if goal can be reached from outside goal
            if (moveDist < getDistanceInBetween(startPos, endPos, color, false)) {
                return false;
            }
            // check if there is a marble between intersect and endPos in goal
            for (int i = 0; i <= endPos; i++) {
                if (goal.get(i) != MARBLE.NONE) {
                    return false;
                }
            }
        }
        return true;
    }
}
