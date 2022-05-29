/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen

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

package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

import java.util.ArrayList;

import ch.uzh.ifi.hase.soprafs22.game.constants.MARBLE;

public class BoardState {
    public ArrayList<MARBLE> _redGoal = new ArrayList<>();
    public ArrayList<MARBLE> _greenGoal = new ArrayList<>();
    public ArrayList<MARBLE> _blueGoal = new ArrayList<>();
    public ArrayList<MARBLE> _yellowGoal = new ArrayList<>();

    public int _redBase = 4;
    public int _greenBase = 4;
    public int _blueBase = 4;
    public int _yellowBase = 4;

    public boolean REDBLOCKED = false;
    public boolean GREENBLOCKED = false;
    public boolean BLUEBLOCKED = false;
    public boolean YELLOWBLOCKED = false;

    public BoardState()
    {

    }

    public BoardState(BoardState bs)
    {
        this._redBase = bs._redBase;
        this._blueBase = bs._blueBase;
        this._greenBase =bs._greenBase;
        this._yellowBase = bs._yellowBase;
        this._redGoal = new ArrayList<>(bs._redGoal);
        this._blueGoal = new ArrayList<>(bs._blueGoal);
        this._greenGoal = new ArrayList<>(bs._greenGoal);
        this._yellowGoal = new ArrayList<>(bs._yellowGoal);
        this.REDBLOCKED = bs.REDBLOCKED;
        this.GREENBLOCKED = bs.GREENBLOCKED;
        this.BLUEBLOCKED = bs.BLUEBLOCKED;
        this.YELLOWBLOCKED = bs.YELLOWBLOCKED;
    }
}
