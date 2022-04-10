package ch.uzh.ifi.hase.soprafs22.game.gameInstance.player;

import ch.uzh.ifi.hase.soprafs22.game.constants.CARDSUITE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDTYPE;
import ch.uzh.ifi.hase.soprafs22.game.constants.CARDVALUE;
import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        for (int i=0; i<4; i++) assertEquals(COLOR.values()[i], players.get(i).getTurn());
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
}