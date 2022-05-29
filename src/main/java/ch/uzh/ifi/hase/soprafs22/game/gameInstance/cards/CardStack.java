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

package ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards;

import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardStack {

    private final List<Card> deck = new ArrayList<>();

    public CardStack() {
        for (CARDSUITE suite: CARDSUITE.values()) {
            for (CARDVALUE value: CARDVALUE.values()) {
                deck.add(new Card(value, CARDTYPE.DEFAULT, suite));
            }
        }

        shuffle();
    }

    public Card getNextCard() {
        Card c =  this.deck.remove(0);
        if(this.deck.size() == 0)
        {
            restock();
        }

        return c;
    }

    public void shuffle() {
        Collections.shuffle(this.deck);
    }

    public List<Card> getDeck() {
        return new ArrayList<Card>(deck);
    }

    private void restock()
    {
        deck.clear();
        for (CARDSUITE suite: CARDSUITE.values()) {
            for (CARDVALUE value: CARDVALUE.values()) {
                deck.add(new Card(value, CARDTYPE.DEFAULT, suite));
            }
        }
        shuffle();
    }
}
