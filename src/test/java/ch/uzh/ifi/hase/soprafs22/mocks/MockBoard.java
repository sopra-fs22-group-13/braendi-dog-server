package ch.uzh.ifi.hase.soprafs22.mocks;

import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;
import ch.uzh.ifi.hase.soprafs22.game.exceptions.InvalidMoveException;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.board.IBoard;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.cards.Card;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.BoardData;
import ch.uzh.ifi.hase.soprafs22.game.gameInstance.data.Move;

public abstract class MockBoard implements IBoard {
    @Override
    public BoardData getFormattedBoardState() {
        return null;
    }

    @Override
    public void makeMove(Move move) throws InvalidMoveException {

    }

    @Override
    public void makeStartingMove(COLOR color) throws InvalidMoveException {

    }

    @Override
    public boolean checkWinningCondition(COLOR color) {
        return false;
    }

    @Override
    public void makeSwitch(int start, int end) throws InvalidMoveException {

    }

    @Override
    public boolean isAnyMovePossible(Card card, COLOR col) {
        return false;
    }

    @Override
    public boolean isValidMove(Move move) throws InvalidMoveException {
        return false;
    }

    @Override
    public int getNumberInBase(COLOR color){
        return 0;
    }

    public int getCountStartingMove() {
        return 0;
    }


    public int getCountJackMove() {
        return 0;
    }


    public int getCountNormalMove() {
        return 0;
    }

}
