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

package ch.uzh.ifi.hase.soprafs22.rest.data.dto;

import ch.uzh.ifi.hase.soprafs22.rest.constant.UserStatus;

import javax.persistence.criteria.CriteriaBuilder;

public class UserGetDTO {

    private Long id;
    private String username;
    private UserStatus status;
    private Integer avatar;
    private String description;
    private Integer wins;
    private Integer gotInGoals;


    public Long getId() {
    return id;
    }

    public void setId(Long id) {
    this.id = id;
    }

    public String getUsername() {
    return username;
    }

    public void setUsername(String username) {
    this.username = username;
    }

    public UserStatus getStatus() {
    return status;
    }

    public void setStatus(UserStatus status) {
    this.status = status;
    }

    public Integer getAvatar() {
        return avatar;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getWins(){return wins;}

    public void setWins(Integer wins) {this.wins = wins;}

    public Integer getGotInGoals(){return gotInGoals;}

    public void setGotInGoals(Integer gotInGoals) {this.gotInGoals = gotInGoals;}
}

