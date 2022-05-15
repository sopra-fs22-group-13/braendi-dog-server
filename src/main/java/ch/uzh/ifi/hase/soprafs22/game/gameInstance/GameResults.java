package ch.uzh.ifi.hase.soprafs22.game.gameInstance;

public class GameResults {
    public Long startingTime;

    public Long winnerID;

    private Long player1;
    private Integer resultPlayer1;
    private Long player2;
    private Integer resultPlayer2;
    private Long player3;
    private Integer resultPlayer3;
    private Long player4;
    private Integer resultPlayer4;

    public void addPlayerResults(Long player, Integer result) {
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
}
