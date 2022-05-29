/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen, Simona Borghi, Sandro Vonlanthen, Anton Crazzolara, Shitao Zeng

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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
    private boolean cardIsPartOfJoker;
    private COLOR color;

    private String token;


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

    public boolean getCardIsPartOfJoker() {
        return cardIsPartOfJoker;
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

    public void setCardIsPartOfJoker(boolean isJoker){
        this.cardIsPartOfJoker = isJoker;
    }
}
