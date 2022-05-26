package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import ch.uzh.ifi.hase.soprafs22.game.constants.*;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.NoMarbleException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardPosition;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class boardValidation {

    private boardHelper helper = new boardHelper();
    private boardState bState = new boardState();

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

    private void updateFromBoardState(){
        _redGoal = bState._redGoal;
        _greenGoal = bState._greenGoal;
        _blueGoal = bState._blueGoal;
        _yellowGoal = bState._yellowGoal;
        _redBase = bState._redBase;
        _greenBase = bState._greenBase;
        _blueBase = bState._blueBase;
        _yellowBase = bState._yellowBase;
        REDBLOCKED = bState.REDBLOCKED;
        GREENBLOCKED = bState.GREENBLOCKED;
        BLUEBLOCKED = bState.BLUEBLOCKED;
        YELLOWBLOCKED = bState.YELLOWBLOCKED;
    }

    private void updateToBoardState(){
        bState._redGoal = _redGoal;
        bState._greenGoal = _greenGoal;
        bState._blueGoal = _blueGoal;
        bState._yellowGoal = _yellowGoal;
        bState._redBase = _redBase;
        bState._greenBase = _greenBase;
        bState._blueBase = _blueBase;
        bState._yellowBase = _yellowBase;
        bState.REDBLOCKED = REDBLOCKED;
        bState.GREENBLOCKED = GREENBLOCKED;
        bState.BLUEBLOCKED = BLUEBLOCKED;
        bState.YELLOWBLOCKED = YELLOWBLOCKED;
    }

    // checks if the start move is valid
    private ValidMove validStart(Move move, int startPos, int endPos){
        try {
            COLOR marbleColor = helper.getMarbleColor(move, startPos, _mainCircle);
            switch (marbleColor) {
                case RED:
                    if (_redBase == 0) {
                        return new ValidMove(false, "no red marble on base");
                    } else if (endPos == REDINTERSECT && !REDBLOCKED) {
                        return new ValidMove(true);
                    } else {
                        return new ValidMove(false, "red at wrong end position or intersection blocked");
                    }
                case YELLOW:
                    if (_yellowBase == 0) {
                        return new ValidMove(false, "no yellow marble on base");
                    } else if (endPos == YELLOWINTERSECT && !YELLOWBLOCKED) {
                        return new ValidMove(true);
                    } else {
                        return new ValidMove(false, "yellow at wrong end position or intersection blocked");
                    }
                case GREEN:
                    if (_greenBase == 0) {
                        return new ValidMove(false, "no green marble on base");
                    } else if (endPos == GREENINTERSECT && !GREENBLOCKED) {
                        return new ValidMove(true);
                    } else {
                        return new ValidMove(false, "green at wrong end position or intersection blocked");
                    }
                case BLUE:
                    if (_blueBase == 0) {
                        return new ValidMove(false, "no blue marble on base");
                    } else if (endPos == BLUEINTERSECT && !BLUEBLOCKED) {
                        return new ValidMove(true);
                    } else {
                        return new ValidMove(false, "blue at wrong end position or intersection blocked");
                    }
                default:
                    return new ValidMove(false, "unknown error with starting marble");
            }
        } catch (Exception e) {
            return new ValidMove(false, "unknown error with starting marble:" + e);
        }
    }

    public ValidMove isValidMove(Move move){
        // throwing exceptions
        if (!move.checkIfComplete()) {
            return new ValidMove(false, "Move is not complete");
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
    private ValidMove isValidAceMove(Move move){
        int startPos = move.get_fromPos().get(0).getIndex();
        int endPos = move.get_toPos().get(0).getIndex();
        COLOR marbleColor;
        try{
            marbleColor = helper.getMarbleColor(move, startPos, _mainCircle);
        }catch(Exception e){
            return new ValidMove(false, "error at getMarbleColor: " + e);
        }
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return new ValidMove(false, "move color != marble color");
        }
        // validate for starting move
        if (startPos == -1) {
            return validStart(move, startPos, endPos);
        }
        // validate for goal move
        if (move.isGoalMove()) {
            boolean startInGoal = move.get_fromPos().get(0).isInGoal();
            if (!isValidGoalMove(startPos, endPos, startInGoal, marbleColor)) {
                return new ValidMove(false, "move is not valid for goal move");
            }
        }
        int moveDist = helper.getMoveDist(move);
        if (moveDist != 1 && moveDist != 11) {
            return new ValidMove(false, "wrong move distance for ace card: " + moveDist);
        }

        // checking for blocked intersect
        if (!move.isGoalMove() && helper.blockedIntersect(move, startPos, endPos, true, _mainCircle, bState)) {
            return new ValidMove(false, "blocked intersect");
        }
        return new ValidMove(true);
    }

    // check if the move is valid for a jack card
    private ValidMove isValidJackMove(Move move){
        int startPos = move.get_fromPos().get(0).getIndex();
        int endPos = move.get_toPos().get(0).getIndex();
        COLOR marbleColor;
        try{
            marbleColor = helper.getMarbleColor(move, startPos, _mainCircle);
        }catch(Exception e){
            return new ValidMove(false, "error at getMarbleColor: " + e);
        }
        // validate for starting move
        if (startPos == -1) {
            return new ValidMove(false, "jack cannot start");
        }
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return new ValidMove(false, "move color != marble color");
        }
        if(move.get_toPos().get(0).isInGoal() == true || move.get_fromPos().get(0).isInGoal())
        {
            //cannot switch from or to goal
            return new ValidMove(false, "cannot switch from or to goal");
        }
        // jack behaviour unclear yet: unclear on how front-end passes the move
        if (this._mainCircle.get(startPos) == MARBLE.NONE || this._mainCircle.get(endPos) == MARBLE.NONE) {
            return new ValidMove(false, "no marble at start or end position");
        }
        // check for blocked intersect
        switch (endPos) {
            case REDINTERSECT:
                if (REDBLOCKED) {
                    return new ValidMove(false, "jack cannot switch to blocked red intersect");
                }
                break;
            case YELLOWINTERSECT:
                if (YELLOWBLOCKED) {
                    return new ValidMove(false, "jack cannot switch to blocked yellow intersect");
                }
                break;
            case GREENINTERSECT:
                if (GREENBLOCKED) {
                    return new ValidMove(false, "jack cannot switch to blocked green intersect");
                }
                break;
            case BLUEINTERSECT:
                if (BLUEBLOCKED) {
                    return new ValidMove(false, "jack cannot switch to blocked blue intersect");
                }
                break;
        }
        return new ValidMove(true);
    }

    // check if the move is valid for a joker card
    private ValidMove isValidJokerMove(Move move) {
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
                    return new ValidMove(true);
                }
            } catch (Exception e) {
                return new ValidMove(false, "unknown error with joker move: " + e);
            }
        }
        return new ValidMove(false, "no valid move for joker");
    }

    // check if the move is valid for a king card
    private ValidMove isValidKingMove(Move move){
        int startPos = move.get_fromPos().get(0).getIndex();
        int endPos = move.get_toPos().get(0).getIndex();
        COLOR marbleColor;
        try{
            marbleColor = helper.getMarbleColor(move, startPos, _mainCircle);
        }catch(Exception e){
            return new ValidMove(false, "error at getMarbleColor: " + e);
        }
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return new ValidMove(false, "move color != marble color");
        }
        // validate for starting move
        if (startPos == -1) {
            return validStart(move, startPos, endPos);
        }
        // validate for goal move
        if (move.isGoalMove()) {
            boolean startInGoal = move.get_fromPos().get(0).isInGoal();
            if (!isValidGoalMove(startPos, endPos, startInGoal, marbleColor)) {
                return new ValidMove(false, "move is not valid for goal move");
            }
        }
        // check if distance is valid for the card
        int moveDist = helper.getMoveDist(move);
        if (moveDist != 13) {
            return new ValidMove(false, "wrong move distance for king card: " + moveDist);
        }

        // checking for blocked intersect
        if (!move.isGoalMove() && helper.blockedIntersect(move, startPos, endPos, true, _mainCircle, bState)) {
            return new ValidMove(false, "king cannot move to blocked intersect");
        }
        return new ValidMove(true);
    }

    // check if the move is valid for a 7 card
    /**
     * checks if the move is valid for a 7 card
     * 
     * @param move
     * @return true if all moves are valid, a single invalid move will return false
     * @throws NoMarbleException
     */
    private ValidMove isValidSevenMove(Move move) {
        List<BoardPosition> startPos = move.get_fromPos();
        List<BoardPosition> endPos = move.get_toPos();
        ArrayList<COLOR> marbleColor = new ArrayList<>();
        try {
            for (int i = 0; i < startPos.size(); i++) {
                if (startPos.get(i).getIndex() == -1) {
                    return new ValidMove(false, "7 cannot start");
                } // seven cant make a starting move
                marbleColor.add(helper.getMarbleColor(move, startPos.get(i).getIndex(), _mainCircle));
            }
        } catch (Exception e) {
            return new ValidMove(false, "unknown error with 7 move: " + e);
        }
        // check if marble color is the same as move color
        for (int i = 0; i < marbleColor.size(); i++) {
            if (marbleColor.get(i) != move.get_color()) {
                return new ValidMove(false, "move color != marble color");
            }
        }
        // checking for correct movedist
        int moveDist = 0;
        if (move.isGoalMove()) {
            for (int i = 0; i < startPos.size(); i++) {
                if (move.get_fromPos().get(i).isInGoal()) {
                    moveDist += helper.getDistanceInBetween(startPos.get(i).getIndex(), endPos.get(i).getIndex(), marbleColor.get(i), true);
                } else if (move.get_toPos().get(i).isInGoal()) {
                    moveDist += helper.getDistanceInBetween(startPos.get(i).getIndex(), endPos.get(i).getIndex(), marbleColor.get(i), false);
                } else {
                    moveDist += helper.getDistanceInBetween(startPos.get(i).getIndex(), endPos.get(i).getIndex());
                }
            }
        } else {
            for (int i = 0; i < startPos.size(); i++) {
                moveDist += helper.getDistanceInBetween(startPos.get(i).getIndex(), endPos.get(i).getIndex());
            }
        }
        if (moveDist != 7) {
            return new ValidMove(false, "wrong move distance for 7 card: " + moveDist);
        }

        for (int i = 0; i < startPos.size(); i++) {
            int start = startPos.get(i).getIndex();
            int end = endPos.get(i).getIndex();

            if(move.get_toPos().get(i).isInGoal()){

                if (!isValidGoalMove(startPos.get(i).getIndex(), endPos.get(i).getIndex(), move.get_fromPos().get(i).isInGoal(), marbleColor.get(i))) {
                    return new ValidMove(false, "wrong seven move for goal move"); //checking for is valid goal move
                 }
            }else
            {
                if (helper.blockedIntersect(move, start, end, true, _mainCircle, bState)) {
                    return new ValidMove(false, "seven cannot move to blocked intersect"); // checking for blocked intersects
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
                helper.makeMove(partMove, _mainCircle, bState);
            } catch (Exception e) {
                return new ValidMove(false, "unknown error with 7 move: " + e);
            }

        }

        return new ValidMove(true);
    }

    // check if the move is valid for a 4 card
    private ValidMove isValidFourMove(Move move) {
        int startPos = move.get_fromPos().get(0).getIndex();
        int endPos = move.get_toPos().get(0).getIndex();
        COLOR marbleColor;
        try{
            marbleColor = helper.getMarbleColor(move, startPos, _mainCircle);
        }catch(Exception e){
            return new ValidMove(false, "error at getMarbleColor: " + e);
        }
        boolean isBackwardMove = false;
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return new ValidMove(false, "move color != marble color");
        }
        // check if distance is valid for the card
        int moveDistForward = helper.getDistanceInBetween(startPos, endPos, true);
        int moveDistBackward = helper.getDistanceInBetween(startPos, endPos, false);
        if (move.isGoalMove()) {
            moveDistForward = helper.getDistanceInBetween(startPos, endPos, marbleColor, false);
        }
        if (moveDistForward != 4 && moveDistBackward != 4) {
            return new ValidMove(false, "wrong move distance for 4 card: " + moveDistForward + " " + moveDistBackward);
        }
        if (moveDistBackward == 4 && moveDistForward != 4) {
            isBackwardMove = true;
        }
        // validate for goal move
        if (move.isGoalMove()) {
            if (isBackwardMove) {
                return new ValidMove(false, "move is not valid for goal move");
            }
            boolean startInGoal = move.get_fromPos().get(0).isInGoal();
            if (!isValidGoalMove(startPos, endPos, startInGoal, marbleColor)) {
                return new ValidMove(false, "move is not valid for goal move");
            }
        }

        // checking for blocked intersect
        if (!move.isGoalMove() && helper.blockedIntersect(move, startPos, endPos, !isBackwardMove, _mainCircle, bState)) {
            return new ValidMove(false, "four cannot move to blocked intersect");
        }
        return new ValidMove(true);
    }

    // check if the move is valid for a regular card
    private ValidMove isValidRegularMove(Move move){
        int startPos = move.get_fromPos().get(0).getIndex();
        int endPos = move.get_toPos().get(0).getIndex();
        Card moveCard = move.get_card();
        COLOR marbleColor;
        try{
            marbleColor = helper.getMarbleColor(move, startPos, _mainCircle);
        }catch(Exception e){
            return new ValidMove(false, "error at getMarbleColor: " + e);
        }
        // check if marble color is the same as move color
        if (marbleColor != move.get_color()) {
            return new ValidMove(false, "move color != marble color");
        }
        if (startPos == -1) {
            return new ValidMove(false, "regular move cannot start");
        }
        int moveDist = helper.getMoveDist(move);
        switch (moveCard.getValue()) {
            case TWO:
                if (moveDist != 2) {
                    return new ValidMove(false, "wrong move distance for 2 card: " + moveDist);
                }
                break;
            case THREE:
                if (moveDist != 3) {
                    return new ValidMove(false, "wrong move distance for 3 card: " + moveDist);
                }
                break;
            case FIVE:
                if (moveDist != 5) {
                    return new ValidMove(false, "wrong move distance for 5 card: " + moveDist);
                }
                break;
            case SIX:
                if (moveDist != 6) {
                    return new ValidMove(false, "wrong move distance for 6 card: " + moveDist);
                }
                break;
            case EIGHT:
                if (moveDist != 8) {
                    return new ValidMove(false, "wrong move distance for 8 card: " + moveDist);
                }
                break;
            case NINE:
                if (moveDist != 9) {
                    return new ValidMove(false, "wrong move distance for 9 card: " + moveDist);
                }
                break;
            case TEN:
                if (moveDist != 10) {
                    return new ValidMove(false, "wrong move distance for 10 card: " + moveDist);
                }
                break;
            case QUEEN:
                if (moveDist != 12) {
                    return new ValidMove(false, "wrong move distance for queen card: " + moveDist);
                }
                break;
        }
        // validate for goal move
        if (move.isGoalMove()) {
            boolean startInGoal = move.get_fromPos().get(0).isInGoal();
            if (!isValidGoalMove(startPos, endPos, startInGoal, marbleColor)) {
                return new ValidMove(false, "regular move is not valid for goal move");
            }
        }

        // checking for blocked intersect
        if (!move.isGoalMove() && helper.blockedIntersect(move, startPos, endPos, true, _mainCircle, bState)) {
            return new ValidMove(false, "regular cannot move to blocked intersect");
        }
        return new ValidMove(true);
    }

    /**
     * checks if the move is valid to enter goal
     * assumes that endPos will end in goal, otherwise this methos is not called
     *
     * @return true if the move can enter the goal
     */
    private boolean isValidGoalMove(int startPos, int endPos, boolean startPosInGoal, COLOR color) {
        int moveDist = helper.getDistanceInBetween(startPos, endPos, color, startPosInGoal);
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
            if (moveDist < helper.getDistanceInBetween(startPos, intersect)) {
                return false;
            }
        }
        if (startPosInGoal) {
            // check if goal can be reached from inside goal
            if (moveDist < helper.getDistanceInBetween(startPos, endPos, color, true)) {
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
            if (moveDist < helper.getDistanceInBetween(startPos, endPos, color, false)) {
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
