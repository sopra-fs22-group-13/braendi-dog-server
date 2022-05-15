package ch.uzh.ifi.hase.soprafs22.rest.entity;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "gamehistory")
public class GameHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    User user1;

    @ManyToOne
    User user2;

    @ManyToOne
    User user3;

    @ManyToOne
    User user4;

    @ManyToOne
    User winner;

    @Column(nullable = false)
    private Integer user1_goals;

    @Column(nullable = false)
    private Integer user2_goals;

    @Column(nullable = false)
    private Integer user3_goals;

    @Column(nullable = false)
    private Integer user4_goals;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public User getUser3() {
        return user3;
    }

    public void setUser3(User user3) {
        this.user3 = user3;
    }

    public User getUser4() {
        return user4;
    }

    public void setUser4(User user4) {
        this.user4 = user4;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public Integer getUser1_goals() {
        return user1_goals;
    }

    public void setUser1_goals(Integer user1_goals) {
        this.user1_goals = user1_goals;
    }

    public Integer getUser2_goals() {
        return user2_goals;
    }

    public void setUser2_goals(Integer user2_goals) {
        this.user2_goals = user2_goals;
    }

    public Integer getUser3_goals() {
        return user3_goals;
    }

    public void setUser3_goals(Integer user3_goals) {
        this.user3_goals = user3_goals;
    }

    public Integer getUser4_goals() {
        return user4_goals;
    }

    public void setUser4_goals(Integer user4_goals) {
        this.user4_goals = user4_goals;
    }
}
