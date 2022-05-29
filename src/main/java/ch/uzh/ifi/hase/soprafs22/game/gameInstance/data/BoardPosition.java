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

package ch.uzh.ifi.hase.soprafs22.game.gameInstance.data;

public class BoardPosition {
    private int index = 0;
    private boolean isInGoal = false;

    public BoardPosition(int index, boolean isInGoal)
    {
        if(isInGoal)
        {
            if(index < 0 || index > 3) throw new IndexOutOfBoundsException();
        }else
        {
            if(index < -1 || index > 63) throw new IndexOutOfBoundsException();
        }

        this.index = index;
        this.isInGoal = isInGoal;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        if(isInGoal)
        {
            if(index < 0 || index > 3) throw new IndexOutOfBoundsException();
        }else
        {
            if(index < -1 || index > 63) throw new IndexOutOfBoundsException();
        }

        this.index = index;
    }

    public boolean isInGoal() {
        return isInGoal;
    }

    public void setInGoal(boolean inGoal) {

        if(inGoal)
        {
            if(index < 0 || index > 3) throw new IndexOutOfBoundsException();
        }

        isInGoal = inGoal;
    }

    public boolean equalsContent(BoardPosition other)
    {
        return other.getIndex() == this.getIndex() && other.isInGoal() == this.isInGoal();
    }
}
