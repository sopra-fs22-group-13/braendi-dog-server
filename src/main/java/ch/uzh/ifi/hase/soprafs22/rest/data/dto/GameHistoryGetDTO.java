package ch.uzh.ifi.hase.soprafs22.rest.data.dto;

public class GameHistoryGetDTO {
    private Long id;
    private Long startDate;
    private Boolean won;
    private Integer goals;

    public GameHistoryGetDTO(Long id, Long startDate, Boolean won, Integer goals) {
        this.id = id;
        this.startDate = startDate;
        this.won = won;
        this.goals = goals;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Boolean getWon() {
        return won;
    }

    public void setWon(Boolean won) {
        this.won = won;
    }

    public Integer getGoals() {
        return goals;
    }

    public void setGoals(Integer goals) {
        this.goals = goals;
    }
}
