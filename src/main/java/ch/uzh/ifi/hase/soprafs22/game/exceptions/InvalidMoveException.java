package ch.uzh.ifi.hase.soprafs22.game.exceptions;

public class InvalidMoveException extends Exception{
    private String code;

    public InvalidMoveException(String code, String message) {
        super(message);
        this.setCode(code);
    }

    public InvalidMoveException(String code) {
        this.setCode(code);
    }

    public InvalidMoveException() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
