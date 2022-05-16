package ch.uzh.ifi.hase.soprafs22.rest.repository;

import ch.uzh.ifi.hase.soprafs22.rest.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {

    @Query("select gh from GameHistory gh where gh.winner = ?1")
    public List<GameHistory> findWonGames(User winner);


    @Query("select gh from GameHistory gh where gh.user1 = ?1 OR gh.user2 = ?1 OR gh.user3 = ?1 OR gh.user4 = ?1")
    public List<GameHistory> findPlayedGames(User user);

}
