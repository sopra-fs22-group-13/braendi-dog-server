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

import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player player;

    @BeforeEach
    public void setup() {
        player = new Player(COLOR.RED);
    }

    /**
     * currently only checks if color is assigned correctly
     */
    @Test
    public void creationTest() {
        List<Player> players = List.of(player, new Player(COLOR.GREEN), new Player(COLOR.BLUE), new Player(COLOR.YELLOW));
        for (int i=0; i<4; i++) assertEquals(COLOR.values()[i], players.get(i).getColor());
    }

    /**
     * (1) checks preconditions
     * (2) checks if card is successfully removed
     * (3) checks what happens if we want to remove a card that isn't in hand
     */
    @Test
    void removeCard() {
        //adds a card and checks if it's there (just to be sure)
        player.addCard(new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS));
        assertEquals(1, player.getCardCount());
        assertTrue(player.isCardAvailable(new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS)));

        //removes the card again and checks if it's gone
        player.removeCard(new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS));
        assertEquals(0, player.getCardCount());
        assertFalse(player.isCardAvailable(new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS)));

        //removes a card that shouldn't be there anymore (shouldn't change anything)
        player.removeCard(new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS));
        assertEquals(0, player.getCardCount());
        assertFalse(player.isCardAvailable(new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS)));
    }

    /**
     * (1) tests if the card was successfully added
     * (2) tests if card is found by copy
     * (3) tests if second card is successfully added
     * (4) tests if first card is still there after adding a different card
     */
    @Test
    void handTest() {
        Card firstCard = new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
        Card firstCardCOPY = new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
        player.addCard(firstCard);
        assertTrue(player.isCardAvailable(firstCard));
        assertTrue(player.isCardAvailable(firstCardCOPY));

        Card secondCard = new Card(CARDVALUE.FOUR, CARDTYPE.DEFAULT, CARDSUITE.SPADES);
        player.addCard(secondCard);
        assertTrue(player.isCardAvailable(secondCard));
        assertTrue(player.isCardAvailable(firstCard));
    }

    /**
     * (1) test with empty hand
     * (2) test with one card in hand
     * (3) tests with multiple cards in hand
     */
    @Test
    void cardCountTest() {
        assertEquals(0, player.getCardCount());
        player.addCard(new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS));
        assertEquals(1, player.getCardCount());
        player.addCard(new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS));
        player.addCard(new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS));
        assertEquals(3, player.getCardCount());
    }

    @Test
    void handRemovalTest() {
        player.addCard(new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS));
        player.removeAllCard();
        assertEquals(0, player.getCardCount());
    }

    @Test
    void cardByIndexTest() {
        Card card1 = new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
        Card card2 = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
        player.addCard(card1);
        player.addCard(card2);

        assertEquals(card1, player.getCartValueInIndexHand(0));
        assertEquals(card2, player.getCartValueInIndexHand(1));
        assertNull(player.getCartValueInIndexHand(2));
        assertNull(player.getCartValueInIndexHand(-1));
    }

    @Test
    void formattedHandTest() {
        Card card1 = new Card(CARDVALUE.FIVE, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
        Card card2 = new Card(CARDVALUE.SEVEN, CARDTYPE.DEFAULT, CARDSUITE.CLUBS);
        player.addCard(card1);
        player.addCard(card2);

        ArrayList<String> formatted = new ArrayList<>();
        formatted.add(card1.getFormatted());
        formatted.add(card2.getFormatted());
        assertEquals(player.getFormattedCards(), formatted);
    }
}