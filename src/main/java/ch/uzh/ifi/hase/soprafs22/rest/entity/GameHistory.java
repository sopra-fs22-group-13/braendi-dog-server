/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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

    @Column
    Long startDate;

    @ManyToOne(optional = false)
    User user1;

    @ManyToOne(optional = false)
    User user2;

    @ManyToOne(optional = false)
    User user3;

    @ManyToOne(optional = false)
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

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
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
