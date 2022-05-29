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

package ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    String[] _cards = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "K", "A", "Q"};
    String[] _suits = {"C", "D", "H", "S"};

    @Test
    void equalsContent() {
        Card c = new Card("AC");
        Card c2 = new Card("AC");
        assertTrue(c.equalsContent(c2));
    }

    @Test
    void getFormatted() {
        for (String c :_cards) {
            for (String s:_suits) {
                assertEquals(c+s, new Card(c+s).getFormatted());
            }
        }
        assertEquals("Joker", new Card("Joker").getFormatted());
    }
}