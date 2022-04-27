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

public class boardValidation extends Board {

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

    private COLOR getMarbleColor(Move move, int startPos) throws NoMarbleException {
        COLOR marbleColor;
        try {
            if (startPos != -1 && !move.isGoalMove()) {
                marbleColor = this.getColorFromPosition(startPos);
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
                    if (endPos == REDINTERSECT && !REDBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                case YELLOW:
                    if (endPos == YELLOWINTERSECT && !YELLOWBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                case GREEN:
                    if (endPos == GREENINTERSECT && !GREENBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                case BLUE:
                    if (endPos == BLUEINTERSECT && !BLUEBLOCKED) {
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

    private boolean blockedIntersect(Move move, int startPos, int endPos) {
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
        return false;
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
            return isValidGoalMove(startPos, endPos, startInGoal, marbleColor);
        }
        int moveDist = getDistanceInBetween(startPos, endPos);
        if (moveDist != 1 && moveDist != 11) {
            return false;
        }

        // checking for blocked intersect
        if (blockedIntersect(move, startPos, endPos)) {
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
        // intersect
        return true;
    }

    // check if the move is valid for a joker card
    private boolean isValidJokerMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        COLOR marbleColor;
        try {
            if (startPos != -1) {
                marbleColor = this.getColorFromPosition(startPos);
            } else {
                marbleColor = move.get_color();
            }
        } catch (Exception e) {
            throw new NoMarbleException();
        }
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return false;
        }
        // validate for starting move
        if (startPos == -1) {

            switch (marbleColor) {
                case RED:
                    if (endPos == REDINTERSECT && !REDBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                case YELLOW:
                    if (endPos == YELLOWINTERSECT && !YELLOWBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                case GREEN:
                    if (endPos == GREENINTERSECT && !GREENBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                case BLUE:
                    if (endPos == BLUEINTERSECT && !BLUEBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                default:
                    return false;
            }
        }
        // validate for goal move
        if (move.isGoalMove()) {
            boolean startInGoal = move.get_fromPosInGoal().get(0);
            return isValidGoalMove(startPos, endPos, startInGoal, marbleColor);
        }
        // TODO joker behaviour unclear yet, can be any card: need to define behaviour
        // in front-end

        // checking for blocked intersect
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
                            return false;
                        }
                    case YELLOWINTERSECT:
                        if (YELLOWBLOCKED) {
                            return false;
                        }
                    case GREENINTERSECT:
                        if (GREENBLOCKED) {
                            return false;
                        }
                    case BLUEINTERSECT:
                        if (BLUEBLOCKED) {
                            return false;
                        }
                }
            }
        }
        return true;
    }

    // check if the move is valid for a king card
    private boolean isValidKingMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        COLOR marbleColor;
        try {
            if (startPos != -1) {
                marbleColor = this.getColorFromPosition(startPos);
            } else {
                marbleColor = move.get_color();
            }
        } catch (Exception e) {
            throw new NoMarbleException();
        }
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return false;
        }
        // validate for starting move
        if (startPos == -1) {
            switch (marbleColor) {
                case RED:
                    if (endPos == REDINTERSECT && !REDBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                case YELLOW:
                    if (endPos == YELLOWINTERSECT && !YELLOWBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                case GREEN:
                    if (endPos == GREENINTERSECT && !GREENBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                case BLUE:
                    if (endPos == BLUEINTERSECT && !BLUEBLOCKED) {
                        return true;
                    } else {
                        return false;
                    }
                default:
                    return false;
            }
        }
        // validate for goal move
        if (move.isGoalMove()) {
            boolean startInGoal = move.get_fromPosInGoal().get(0);
            return isValidGoalMove(startPos, endPos, startInGoal, marbleColor);
        }
        // check if distance is valid for the card
        int moveDist = getDistanceInBetween(startPos, endPos);
        if (moveDist != 13) {
            return false;
        }

        // checking for blocked intersect
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
                            return false;
                        }
                    case YELLOWINTERSECT:
                        if (YELLOWBLOCKED) {
                            return false;
                        }
                    case GREENINTERSECT:
                        if (GREENBLOCKED) {
                            return false;
                        }
                    case BLUEINTERSECT:
                        if (BLUEBLOCKED) {
                            return false;
                        }
                }
            }
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
                marbleColor.add(this.getColorFromPosition(startPos.get(i)));
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
                } else {
                    moveDist += getDistanceInBetween(startPos.get(i), endPos.get(i), marbleColor.get(i), false);
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
        ArrayList<Integer> blockPos = new ArrayList<>();
        for (int i = 0; i < startPos.size(); i++) {
            blockPos.addAll(getInbetweeners(startPos.get(i), endPos.get(i)));
        }
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
                            return false;
                        }
                    case YELLOWINTERSECT:
                        if (YELLOWBLOCKED) {
                            return false;
                        }
                    case GREENINTERSECT:
                        if (GREENBLOCKED) {
                            return false;
                        }
                    case BLUEINTERSECT:
                        if (BLUEBLOCKED) {
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
        try {
            if (startPos == -1) {
                return false;
            }
            marbleColor = this.getColorFromPosition(startPos);
        } catch (Exception e) {
            throw new NoMarbleException();
        }
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
        // validate for goal move
        if (move.isGoalMove()) {
            boolean startInGoal = move.get_fromPosInGoal().get(0);
            return isValidGoalMove(startPos, endPos, startInGoal, marbleColor);
        }

        // checking for blocked intersect
        // TODO getInbetweeners does not work for backwards moves yet --> switch
        // startPos and endPos when going backwards
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
                            return false;
                        }
                    case YELLOWINTERSECT:
                        if (YELLOWBLOCKED) {
                            return false;
                        }
                    case GREENINTERSECT:
                        if (GREENBLOCKED) {
                            return false;
                        }
                    case BLUEINTERSECT:
                        if (BLUEBLOCKED) {
                            return false;
                        }
                }
            }
        }
        return true;
    }

    // check if the move is valid for a regular card
    private boolean isValidRegularMove(Move move) throws NoMarbleException {
        int startPos = move.get_fromPos().get(0);
        int endPos = move.get_toPos().get(0);
        Card moveCard = move.get_card();
        COLOR marbleColor;
        try {
            if (startPos != -1 && !move.isGoalMove()) {
                marbleColor = this.getColorFromPosition(startPos);
            } else {
                marbleColor = move.get_color();
            }
        } catch (Exception e) {
            throw new NoMarbleException();
        }
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return false;
        }
        int moveDist = getDistanceInBetween(startPos, endPos);
        if (move.isGoalMove()) {
            if (move.get_fromPosInGoal().get(0)) {
                moveDist = getDistanceInBetween(startPos, endPos, marbleColor, true);
            } else {
                moveDist = getDistanceInBetween(startPos, endPos, marbleColor, false);
            }
        }
        switch (moveCard.getValue()) {
            case TWO:
                return moveDist == 2;
            case THREE:
                return moveDist == 3;
            case FIVE:
                return moveDist == 5;
            case SIX:
                return moveDist == 6;
            case EIGHT:
                return moveDist == 8;
            case NINE:
                return moveDist == 9;
            case TEN:
                return moveDist == 10;
            case QUEEN:
                return moveDist == 12;
        }
        // validate for goal move
        if (move.isGoalMove()) {
            boolean startInGoal = move.get_fromPosInGoal().get(0);
            return isValidGoalMove(startPos, endPos, startInGoal, marbleColor);
        }

        // checking for blocked intersect
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
                            return false;
                        }
                    case YELLOWINTERSECT:
                        if (YELLOWBLOCKED) {
                            return false;
                        }
                    case GREENINTERSECT:
                        if (GREENBLOCKED) {
                            return false;
                        }
                    case BLUEINTERSECT:
                        if (BLUEBLOCKED) {
                            return false;
                        }
                }
            }
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
