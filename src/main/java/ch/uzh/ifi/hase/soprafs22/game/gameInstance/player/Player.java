package ch.uzh.ifi.hase.soprafs22.game.gameInstance.player;

import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;

import java.util.ArrayList;

public class Player {

    private ArrayList<Card> _hand = new ArrayList<Card>();
    private COLOR _turn;

    public Player(){

    }
}
