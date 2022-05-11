package ch.uzh.ifi.hase.soprafs22.game.gameInstance.data;

import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;

import java.util.ArrayList;
import java.util.List;

/*
    * Move
    * This class contains all information of a move
    * */

public class Move {
    private List<BoardPosition> _fromPos = new ArrayList<>();
    private List<BoardPosition> _toPos = new ArrayList<>();

    private Card _card;
    private COLOR _color;

    private String _token;
    private boolean _isJoker;

    public Move(List<BoardPosition> fromPos, List<BoardPosition> toPos, Card c, String token, COLOR color)
    {
        this._fromPos = fromPos;
        this._toPos = toPos;
        this._card = c;
        this._token = token;
        this._color = color;
        this._isJoker = false;
    }
    public Move(List<BoardPosition> fromPos, List<BoardPosition> toPos, Card c, String token, COLOR color, boolean isJoker)
    {
        this._fromPos = fromPos;
        this._toPos = toPos;
        this._card = c;
        this._token = token;
        this._color = color;
        this._isJoker = isJoker;
    }

    // for testing purposes
    public Move(){}

    public List<BoardPosition> get_fromPos() {
        return _fromPos;
    }

    public void set_fromPos(List<BoardPosition> _fromPos) {
        this._fromPos = _fromPos;
    }

    public List<BoardPosition> get_toPos() {
        return _toPos;
    }

    public void set_toPos(List<BoardPosition> _toPos) {
        this._toPos = _toPos;
    }

    public Card get_card() {
        return _card;
    }

    public void set_card(Card _card) {
        this._card = _card;
    }

    public COLOR get_color()
    {
        return _color;
    }

    public void set_color(COLOR color)
    {
        this._color = color;
    }

    public boolean isWellFormed()
    {
        return _fromPos.size() == _toPos.size();
    }

    public boolean checkIfComplete(){
        if (isWellFormed()){
            return !_fromPos.isEmpty() && !_toPos.isEmpty() && _card != null && _color != null;
        }
        return false;
    }

    public String getToken() {return _token;}

    public boolean is_isJoker()
    {
        return _isJoker;
    }

    public void setIsJoker(boolean isJoker)
    {
        this._isJoker = isJoker;
    }

    public boolean isGoalMove() {
        boolean isGoalMove = false;
        for(int i = 0; i < _fromPos.size(); i++) {
            isGoalMove = isGoalMove || _fromPos.get(i).isInGoal();
        }
        for(int i = 0; i < _toPos.size(); i++) {
            isGoalMove = isGoalMove || _toPos.get(i).isInGoal();
        }
        return isGoalMove;
    }

}
