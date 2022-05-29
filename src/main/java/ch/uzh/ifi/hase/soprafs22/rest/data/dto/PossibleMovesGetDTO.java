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

package ch.uzh.ifi.hase.soprafs22.rest.data.dto;

import java.util.List;

public class PossibleMovesGetDTO {

    //provided by the client
    Integer index;
    Boolean inGoal;
    String card;

    public PossibleMovesGetDTO(int index, boolean inGoal) {
        this.index = index;
        this.inGoal = inGoal;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer marblePosition) {
        this.index = marblePosition;
    }

    public Boolean getInGoal() {
        return inGoal;
    }

    public void setInGoal(Boolean inGoal) {
        this.inGoal = inGoal;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
