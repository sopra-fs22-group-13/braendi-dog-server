package ch.uzh.ifi.hase.soprafs22.rest.entity;

import ch.uzh.ifi.hase.soprafs22.rest.constant.UserStatus;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String username;

  @Column
  private Integer avatar = 0;

  @Column
  private String description = "";

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private UserStatus status;

  @Column(nullable = false)
  private Integer wins;

  @Column(nullable = false)
  private Integer goals;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Integer getAvatar() { return avatar; }

  public void setAvatar(Integer avatar) { this.avatar = avatar; }

  public String getDescription() { return description; }

  public void setDescription(String description) { this.description = description; }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public Integer getWins(){ return  wins;}

  public void setWins(Integer wins) { this.wins = wins;}

  public Integer getGoals() { return goals; }

  public void setGoals(Integer goals) {this.goals = goals;}

}
