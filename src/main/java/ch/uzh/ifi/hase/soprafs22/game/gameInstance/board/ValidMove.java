package ch.uzh.ifi.hase.soprafs22.game.gameInstance.board;

public class ValidMove {
    private boolean valid;
    private String errorMessage;

    public ValidMove(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    public ValidMove(boolean valid){
        this.valid = valid;
        this.errorMessage = "";
    }

    public boolean getValid(){
        return valid;
    }

    public String getError(){
        return errorMessage;
    }

}
