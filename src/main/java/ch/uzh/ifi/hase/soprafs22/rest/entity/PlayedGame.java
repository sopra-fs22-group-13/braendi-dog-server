package ch.uzh.ifi.hase.soprafs22.rest.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PLAYED_GAME")
public class PlayedGame implements Serializable {

    private static final long serialGameId = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId1;

    @Column(nullable = false)
    private Long userId2;

    @Column(nullable = false)
    private Long userId3;

    @Column(nullable = false)
    private Long userId4;

    @Column(nullable = false)
    private Long winnerId;

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

    public Long getUserId1() {
        return userId1;
    }

    public void setUserId1(Long userId1) {
        this.userId1 = userId1;
    }

    public Long getUserId2() {
        return userId2;
    }

    public void setUserId2(Long userId2) {
        this.userId2 = userId2;
    }

    public Long getUserId3() {
        return userId3;
    }

    public void setUserId3(Long userId3) {
        this.userId3 = userId3;
    }

    public Long getUserId4() {
        return userId4;
    }

    public void setUserId4(Long userId4) {
        this.userId4 = userId4;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
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
