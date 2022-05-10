package ch.uzh.ifi.hase.soprafs22.game.gameInstance.data;

public class BoardPosition {
    private int index = 0;
    private boolean isInGoal = false;

    public BoardPosition(int index, boolean isInGoal)
    {
        if(isInGoal)
        {
            if(index < 0 || index > 3) throw new IndexOutOfBoundsException();
        }else
        {
            if(index < -1 || index > 63) throw new IndexOutOfBoundsException();
        }

        this.index = index;
        this.isInGoal = isInGoal;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        if(isInGoal)
        {
            if(index < 0 || index > 3) throw new IndexOutOfBoundsException();
        }else
        {
            if(index < -1 || index > 63) throw new IndexOutOfBoundsException();
        }

        this.index = index;
    }

    public boolean isInGoal() {
        return isInGoal;
    }

    public void setInGoal(boolean inGoal) {

        if(inGoal)
        {
            if(index < 0 || index > 3) throw new IndexOutOfBoundsException();
        }

        isInGoal = inGoal;
    }

    public boolean equalsContent(BoardPosition other)
    {
        return other.getIndex() == this.getIndex() && other.isInGoal() == this.isInGoal();
    }
}
