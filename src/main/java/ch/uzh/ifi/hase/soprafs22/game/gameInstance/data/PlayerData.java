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

package ch.uzh.ifi.hase.soprafs22.game.gameInstance.data;

import java.util.ArrayList;

/**
 * contains the cards of a player, along with the 3 hidden card amounts of the remaining players in order.
 */
public class PlayerData {
    private ArrayList<String> visibleCards;
    private ArrayList<Integer> hiddenCardCount;

    public ArrayList<String> getVisibleCards() {
        return visibleCards;
    }

    public void setVisibleCards(ArrayList<String> visibleCards) {
        this.visibleCards = visibleCards;
    }

    public ArrayList<Integer> getHiddenCardCount() {
        return hiddenCardCount;
    }

    public void setHiddenCardCount(ArrayList<Integer> hiddenCardCount) {
        this.hiddenCardCount = hiddenCardCount;
    }
}
