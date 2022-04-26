package ch.uzh.ifi.hase.soprafs22.rest.data.dto;

import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;

import java.util.ArrayList;

public class MovePutDTO {
    private ArrayList<Integer> _fromPos;
    private ArrayList<Integer> _toPos;

    private ArrayList<Boolean> _fromPosInGoal;
    private ArrayList<Boolean> _toPosInGoal;

    private String card;
    private COLOR color;

    private String token;

    public MovePutDTO(ArrayList<Integer> fromPos, ArrayList<Integer> toPos, ArrayList<Boolean> fromPosInGoal, ArrayList<Boolean> toPosInGoal, String c, String token, COLOR color) {
        this._fromPos = fromPos;
        this._toPos = toPos;
        this._fromPosInGoal = fromPosInGoal;
        this._toPosInGoal = toPosInGoal;
        this.card = c;
        this.color = color;
        this.token = token;
    }

    public ArrayList<Integer> get_fromPos() {
        return _fromPos;
    }

    public ArrayList<Integer> get_toPos() {
        return _toPos;
    }

    public ArrayList<Boolean> get_fromPosInGoal() {
        return _fromPosInGoal;
    }

    public ArrayList<Boolean> get_toPosInGoal() {
        return _toPosInGoal;
    }

    public String getCard() {
        return card;
    }

    public COLOR getColor() {
        return color;
    }

    public String getToken() {
        return token;
    }

    public void set_fromPos(ArrayList<Integer> _fromPos) {
        this._fromPos = _fromPos;
    }

    public void set_toPos(ArrayList<Integer> _toPos) {
        this._toPos = _toPos;
    }

    public void set_fromPosInGoal(ArrayList<Boolean> _fromPosInGoal) {
        this._fromPosInGoal = _fromPosInGoal;
    }

    public void set_toPosInGoal(ArrayList<Boolean> _toPosInGoal) {
        this._toPosInGoal = _toPosInGoal;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public void setColor(COLOR color) {
        this.color = color;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
