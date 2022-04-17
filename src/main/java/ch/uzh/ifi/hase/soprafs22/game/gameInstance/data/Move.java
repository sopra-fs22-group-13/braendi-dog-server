package ch.uzh.ifi.hase.soprafs22.game.gameInstance.data;

import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;

import java.util.ArrayList;

    /*
    * Move
    * This class contains all information of a move
    * */

public class Move {
    private ArrayList<Integer> _fromPos = new ArrayList<>();
    private ArrayList<Integer> _toPos = new ArrayList<>();

    private ArrayList<Boolean> _fromPosInGoal = new ArrayList<>();
    private ArrayList<Boolean> _toPosInGoal = new ArrayList<>();

    private Card _card;
    private COLOR _color;

    private String _token;

    public Move(ArrayList<Integer> fromPos, ArrayList<Integer> toPos, ArrayList<Boolean> fromPosInGoal, ArrayList<Boolean> toPosInGoal, Card c, String token, COLOR color ){
        this._fromPos = fromPos;
        this._toPos = toPos;
        this._fromPosInGoal = fromPosInGoal;
        this._toPosInGoal = toPosInGoal;
        this._card = c;
        this._token = token;
        this._color=color;
    }

    // for testing purposes
    public Move(){}

    public ArrayList<Integer> get_fromPos() {
        return _fromPos;
    }

    public void set_fromPos(ArrayList<Integer> _fromPos) {
        this._fromPos = _fromPos;
    }

    public ArrayList<Integer> get_toPos() {
        return _toPos;
    }

    public void set_toPos(ArrayList<Integer> _toPos) {
        this._toPos = _toPos;
    }

    public ArrayList<Boolean> get_fromPosInGoal() {
        return _fromPosInGoal;
    }

    public void set_fromPosInGoal(ArrayList<Boolean> _fromPosInGoal) {
        this._fromPosInGoal = _fromPosInGoal;
    }

    public ArrayList<Boolean> get_toPosInGoal() {
        return _toPosInGoal;
    }

    public void set_toPosInGoal(ArrayList<Boolean> _toPosInGoal) {
        this._toPosInGoal = _toPosInGoal;
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
        return _fromPos.size() == _toPos.size() && _toPos.size() == _fromPosInGoal.size() && _fromPosInGoal.size() == _toPosInGoal.size();
    }

    public boolean checkIfComplete(){
        if (isWellFormed()){
            return !_fromPos.isEmpty() && !_toPos.isEmpty() && !_fromPosInGoal.isEmpty() && !_toPosInGoal.isEmpty() && _card != null && _color != null;
        }
        return false;
    }

    public String getToken() {return _token;}

}
