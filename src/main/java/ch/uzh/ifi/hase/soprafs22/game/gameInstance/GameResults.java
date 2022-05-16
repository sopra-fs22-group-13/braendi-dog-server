package ch.uzh.ifi.hase.soprafs22.game.gameInstance;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;

public class GameResults {
    public Long startingTime;

    public User winner;

    private User player1;
    private Integer resultPlayer1;
    private User player2;
    private Integer resultPlayer2;
    private User player3;
    private Integer resultPlayer3;
    private User player4;
    private Integer resultPlayer4;

    public void addPlayerResults(User player, Integer result) {
        if (player1 == null) {
            player1 = player;
            resultPlayer1 = result;
        } else if (player2 ==null) {
            player2 = player;
            resultPlayer2 = result;
        } else if (player3 ==null) {
            player3 = player;
            resultPlayer3 = result;
        } else if (player4 ==null) {
            player4 = player;
            resultPlayer4 = result;
        }
    }

    public User getWinner() {
        return winner;
    }

    public User getPlayer1() {
        return player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public User getPlayer3() {
        return player3;
    }

    public User getPlayer4() {
        return player4;
    }

    public Integer getResultPlayer1() {
        return resultPlayer1;
    }

    public Integer getResultPlayer2() {
        return resultPlayer2;
    }

    public Integer getResultPlayer3() {
        return resultPlayer3;
    }

    public Integer getResultPlayer4() {
        return resultPlayer4;
    }
}
