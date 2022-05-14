package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.*;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.MoveBlockedByMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.NoMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardPosition;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;
import org.springframework.beans.factory.annotation.Autowired;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.board.validMove;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private final int REDINTERSECT = 0;
    private final int BLUEINTERSECT = 48;
    private final int GREENINTERSECT = 32;
    private final int YELLOWINTERSECT = 16;

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
            boolean YELLOWBLOCKED) {
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
    private validMove validStart(Move move, int startPos, int endPos){
        try {
            COLOR marbleColor = getMarbleColor(move, startPos);
            switch (marbleColor) {
                case RED:
                    if (_redBase == 0) {
                        return new validMove(false, "no red marble on base");
                    } else if (endPos == REDINTERSECT && !REDBLOCKED) {
                        return new validMove(true);
                    } else {
                        return new validMove(false, "red at wrong end position or intersection blocked");
                    }
                case YELLOW:
                    if (_yellowBase == 0) {
                        return new validMove(false, "no yellow marble on base");
                    } else if (endPos == YELLOWINTERSECT && !YELLOWBLOCKED) {
                        return new validMove(true);
                    } else {
                        return new validMove(false, "yellow at wrong end position or intersection blocked");
                    }
                case GREEN:
                    if (_greenBase == 0) {
                        return new validMove(false, "no green marble on base");
                    } else if (endPos == GREENINTERSECT && !GREENBLOCKED) {
                        return new validMove(true);
                    } else {
                        return new validMove(false, "green at wrong end position or intersection blocked");
                    }
                case BLUE:
                    if (_blueBase == 0) {
                        return new validMove(false, "no blue marble on base");
                    } else if (endPos == BLUEINTERSECT && !BLUEBLOCKED) {
                        return new validMove(true);
                    } else {
                        return new validMove(false, "blue at wrong end position or intersection blocked");
                    }
                default:
                    return new validMove(false, "unknown error with starting marble");
            }
        } catch (Exception e) {
            return new validMove(false, "unknown error with starting marble:" + e);
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
        int startPos = move.get_fromPos().get(0).getIndex();
        int endPos = move.get_toPos().get(0).getIndex();
        COLOR marbleColor = move.get_color();
        int moveDist;
        // abstract the check for the goal move
        if(move.get_fromPos().get(0).isInGoal()){
            moveDist = getDistanceInBetween(startPos, endPos, marbleColor, true);
        }else if(move.get_toPos().get(0).isInGoal()){
            moveDist = getDistanceInBetween(startPos, endPos, marbleColor, false);
        }else{
            moveDist = getDistanceInBetween(startPos, endPos);
        }
        return moveDist;
    }

    public validMove isValidMove(Move move) throws InvalidMoveException {
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
    private validMove isValidAceMove(Move move){
        int startPos = move.get_fromPos().get(0).getIndex();
        int endPos = move.get_toPos().get(0).getIndex();
        COLOR marbleColor;
        try{
            marbleColor = getMarbleColor(move, startPos);
        }catch(Exception e){
            return new validMove(false, "error at getMarbleColor: " + e);
        }
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return new validMove(false, "move color != marble color");
        }
        // validate for starting move
        if (startPos == -1) {
            return validStart(move, startPos, endPos);
        }
        // validate for goal move
        if (move.isGoalMove()) {
            boolean startInGoal = move.get_fromPos().get(0).isInGoal();
            if (!isValidGoalMove(startPos, endPos, startInGoal, marbleColor)) {
                return new validMove(false, "move is not valid for goal move");
            }
        }
        int moveDist = getMoveDist(move);
        if (moveDist != 1 && moveDist != 11) {
            return new validMove(false, "wrong move distance for ace card: " + moveDist);
        }

        // checking for blocked intersect
        if (blockedIntersect(move, startPos, endPos, true)) {
            return new validMove(false, "blocked intersect");
        }
        return new validMove(true);
    }

    // check if the move is valid for a jack card
    private validMove isValidJackMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0).getIndex();
        int endPos = move.get_toPos().get(0).getIndex();
        COLOR marbleColor = getMarbleColor(move, startPos);
        // validate for starting move
        if (startPos == -1) {
            return new validMove(false, "jack cannot start");
        }
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return new validMove(false, "move color != marble color");
        }
        if(move.get_toPos().get(0).isInGoal() == true || move.get_fromPos().get(0).isInGoal())
        {
            //cannot switch from or to goal
            return new validMove(false, "cannot switch from or to goal");
        }
        // jack behaviour unclear yet: unclear on how front-end passes the move
        if (this._mainCircle.get(startPos) == MARBLE.NONE || this._mainCircle.get(endPos) == MARBLE.NONE
                || marbleColor == this.getColorFromPosition(endPos)) {
            return new validMove(false, "no marble at start or end position");
        }
        // check for blocked intersect
        switch (endPos) {
            case REDINTERSECT:
                if (REDBLOCKED) {
                    return new validMove(false, "jack cannot switch to blocked red intersect");
                }
                break;
            case YELLOWINTERSECT:
                if (YELLOWBLOCKED) {
                    return new validMove(false, "jack cannot switch to blocked yellow intersect");
                }
                break;
            case GREENINTERSECT:
                if (GREENBLOCKED) {
                    return new validMove(false, "jack cannot switch to blocked green intersect");
                }
                break;
            case BLUEINTERSECT:
                if (BLUEBLOCKED) {
                    return new validMove(false, "jack cannot switch to blocked blue intersect");
                }
                break;
        }
        return new validMove(true);
    }

    // check if the move is valid for a joker card
    private validMove isValidJokerMove(Move move) {
        // check if the move is a valid move for another card, check this recursively
        int startPos = move.get_fromPos().get(0).getIndex();
        int endPos = move.get_toPos().get(0).getIndex();
        if (startPos == -1) {
            return validStart(move, startPos, endPos);
        }
        ArrayList<CARDVALUE> allValues = new ArrayList<>(Arrays.asList(CARDVALUE.values()));
        allValues.remove(CARDVALUE.JOKER);
        for (CARDVALUE value : allValues) {
            Card c = new Card(value, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
            Move newMove = new Move(move.get_fromPos(), move.get_toPos(), c, move.getToken(), move.get_color());
            try {
                if (isValidMove(newMove).getValid()) {
                    return new validMove(true);
                }
            } catch (Exception e) {
                return new validMove(false, "unknown error with joker move: " + e);
            }
        }
        return new validMove(false, "no valid move for joker");
    }

    // check if the move is valid for a king card
    private validMove isValidKingMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0).getIndex();
        int endPos = move.get_toPos().get(0).getIndex();
        COLOR marbleColor = getMarbleColor(move, startPos);
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return new validMove(false, "move color != marble color");
        }
        // validate for starting move
        if (startPos == -1) {
            return validStart(move, startPos, endPos);
        }
        // validate for goal move
        if (move.isGoalMove()) {
            boolean startInGoal = move.get_fromPos().get(0).isInGoal();
            if (!isValidGoalMove(startPos, endPos, startInGoal, marbleColor)) {
                return new validMove(false, "move is not valid for goal move");
            }
        }
        // check if distance is valid for the card
        int moveDist = getMoveDist(move);
        if (moveDist != 13) {
            return new validMove(false, "wrong move distance for king card: " + moveDist);
        }

        // checking for blocked intersect
        if (blockedIntersect(move, startPos, endPos, true)) {
            return new validMove(false, "king cannot move to blocked intersect");
        }
        return new validMove(true);
    }

    // check if the move is valid for a 7 card
    /**
     * checks if the move is valid for a 7 card
     * 
     * @param move
     * @return true if all moves are valid, a single invalid move will return false
     * @throws NoMarbleException
     */
    private validMove isValidSevenMove(Move move) {
        List<BoardPosition> startPos = move.get_fromPos();
        List<BoardPosition> endPos = move.get_toPos();
        ArrayList<COLOR> marbleColor = new ArrayList<>();
        try {
            for (int i = 0; i < startPos.size(); i++) {
                if (startPos.get(i).getIndex() == -1) {
                    return new validMove(false, "7 cannot start");
                } // seven cant make a starting move
                marbleColor.add(getMarbleColor(move, startPos.get(i).getIndex()));
            }
        } catch (Exception e) {
            return new validMove(false, "unknown error with 7 move: " + e);
        }
        // check if marble color is the same as move color
        for (int i = 0; i < marbleColor.size(); i++) {
            if (marbleColor.get(i) != move.get_color()) {
                return new validMove(false, "move color != marble color");
            }
        }
        // checking for correct movedist
        int moveDist = 0;
        if (move.isGoalMove()) {
            for (int i = 0; i < startPos.size(); i++) {
                if (move.get_fromPos().get(i).isInGoal()) {
                    moveDist += getDistanceInBetween(startPos.get(i).getIndex(), endPos.get(i).getIndex(), marbleColor.get(i), true);
                } else if (move.get_toPos().get(i).isInGoal()) {
                    moveDist += getDistanceInBetween(startPos.get(i).getIndex(), endPos.get(i).getIndex(), marbleColor.get(i), false);
                } else {
                    moveDist += getDistanceInBetween(startPos.get(i).getIndex(), endPos.get(i).getIndex());
                }
            }
        } else {
            for (int i = 0; i < startPos.size(); i++) {
                moveDist += getDistanceInBetween(startPos.get(i).getIndex(), endPos.get(i).getIndex());
            }
        }
        if (moveDist != 7) {
            return new validMove(false, "wrong move distance for 7 card: " + moveDist);
        }

        for (int i = 0; i < startPos.size(); i++) {
            int start = startPos.get(i).getIndex();
            int end = endPos.get(i).getIndex();

            if(move.get_toPos().get(i).isInGoal()){

                if (!isValidGoalMove(startPos.get(i).getIndex(), endPos.get(i).getIndex(), move.get_fromPos().get(i).isInGoal(), marbleColor.get(i))) {
                    return new validMove(false, "wrong seven move for goal move"); //checking for is valid goal move
                 }
            }else
            {
                if (blockedIntersect(move, start, end, true)) {
                    return new validMove(false, "seven cannot move to blocked intersect"); // checking for blocked intersects
                }
            }
            // make the move and check blocked again

            ArrayList<BoardPosition> partStartPos = new ArrayList<>(Arrays.asList(new BoardPosition(start, move.get_fromPos().get(i).isInGoal())));
            ArrayList<BoardPosition> partEndPos = new ArrayList<>(Arrays.asList(new BoardPosition(end, move.get_toPos().get(i).isInGoal())));

            Move partMove = new Move(
                    partStartPos,
                    partEndPos,
                    move.get_card(),
                    "token",
                    move.get_color());
            try {
                makeMove(partMove);
            } catch (Exception e) {
                return new validMove(false, "unknown error with 7 move: " + e);
            }

        }

        return new validMove(true);
    }

    // check if the move is valid for a 4 card
    private validMove isValidFourMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0).getIndex();
        int endPos = move.get_toPos().get(0).getIndex();
        COLOR marbleColor = getMarbleColor(move, startPos);
        boolean isBackwardMove = false;
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return new validMove(false, "move color != marble color");
        }
        // check if distance is valid for the card
        int moveDistForward = getDistanceInBetween(startPos, endPos, true);
        int moveDistBackward = getDistanceInBetween(startPos, endPos, false);
        if (move.isGoalMove()) {
            moveDistForward = getDistanceInBetween(startPos, endPos, marbleColor, false);
        }
        if (moveDistForward != 4 && moveDistBackward != 4) {
            return new validMove(false, "wrong move distance for 4 card: " + moveDistForward + " " + moveDistBackward);
        }
        if (moveDistBackward == 4 && moveDistForward != 4) {
            isBackwardMove = true;
        }
        // validate for goal move
        if (move.isGoalMove()) {
            if (isBackwardMove) {
                return new validMove(false, "move is not valid for goal move");
            }
            boolean startInGoal = move.get_fromPos().get(0).isInGoal();
            if (!isValidGoalMove(startPos, endPos, startInGoal, marbleColor)) {
                return new validMove(false, "move is not valid for goal move");
            }
        }

        // checking for blocked intersect
        if (blockedIntersect(move, startPos, endPos, !isBackwardMove)) {
            return new validMove(false, "four cannot move to blocked intersect");
        }
        return new validMove(true);
    }

    // check if the move is valid for a regular card
    private validMove isValidRegularMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0).getIndex();
        int endPos = move.get_toPos().get(0).getIndex();
        Card moveCard = move.get_card();
        COLOR marbleColor = getMarbleColor(move, startPos);

        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return new validMove(false, "move color != marble color");
        }
        if (startPos == -1) {
            return new validMove(false, "regular move cannot start");
        }
        int moveDist = getMoveDist(move);
        switch (moveCard.getValue()) {
            case TWO:
                if (moveDist != 2) {
                    return new validMove(false, "wrong move distance for 2 card: " + moveDist);
                }
                break;
            case THREE:
                if (moveDist != 3) {
                    return new validMove(false, "wrong move distance for 3 card: " + moveDist);
                }
                break;
            case FIVE:
                if (moveDist != 5) {
                    return new validMove(false, "wrong move distance for 5 card: " + moveDist);
                }
                break;
            case SIX:
                if (moveDist != 6) {
                    return new validMove(false, "wrong move distance for 6 card: " + moveDist);
                }
                break;
            case EIGHT:
                if (moveDist != 8) {
                    return new validMove(false, "wrong move distance for 8 card: " + moveDist);
                }
                break;
            case NINE:
                if (moveDist != 9) {
                    return new validMove(false, "wrong move distance for 9 card: " + moveDist);
                }
                break;
            case TEN:
                if (moveDist != 10) {
                    return new validMove(false, "wrong move distance for 10 card: " + moveDist);
                }
                break;
            case QUEEN:
                if (moveDist != 12) {
                    return new validMove(false, "wrong move distance for queen card: " + moveDist);
                }
                break;
        }
        // validate for goal move
        if (move.isGoalMove()) {
            boolean startInGoal = move.get_fromPos().get(0).isInGoal();
            if (!isValidGoalMove(startPos, endPos, startInGoal, marbleColor)) {
                return new validMove(false, "regular move is not valid for goal move");
            }
        }

        // checking for blocked intersect
        if (blockedIntersect(move, startPos, endPos, true)) {
            return new validMove(false, "regular cannot move to blocked intersect");
        }
        return new validMove(true);
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
