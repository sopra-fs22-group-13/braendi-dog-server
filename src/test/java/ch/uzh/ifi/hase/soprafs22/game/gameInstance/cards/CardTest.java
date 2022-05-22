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