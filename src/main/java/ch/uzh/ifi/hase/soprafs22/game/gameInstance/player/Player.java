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

package ch.uzh.ifi.hase.soprafs22.game.gameInstance.player;

import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import com.sun.istack.NotNull;

import java.util.ArrayList;

public class Player {

    private final ArrayList<Card> _hand = new ArrayList<Card>();
    private final COLOR _turn;

    public Player(COLOR playerColor) throws IllegalArgumentException{
        this._turn = playerColor;
    }

    public void removeCard(Card reqCard) {
        for (Card card: _hand) {
            if (reqCard.equalsContent(card)) {
                _hand.remove(card);
                return;
            }
        }
    }

    public boolean isCardAvailable(Card reqCard) {
        for (Card card: _hand) {
            if (reqCard.equalsContent(card)) return true;
        }

        return false;
    }

    public void addCard(Card card) {
        _hand.add(card);
    }

    public int getCardCount() {
        return _hand.size();
    }

    public ArrayList<String> getFormattedCards() {
        ArrayList<String> formattedCards = new ArrayList<>();
        for (Card card: _hand) formattedCards.add(card.getFormatted());
        return formattedCards;
    }

    public Card getCartValueInIndexHand(int i){
        try {
            return _hand.get(i);
        } catch (Exception e) {
            return null;
        }
    }

     public void removeAllCard(){
        _hand.clear();
     }

     public COLOR getColor(){
        return _turn;
     }
}
