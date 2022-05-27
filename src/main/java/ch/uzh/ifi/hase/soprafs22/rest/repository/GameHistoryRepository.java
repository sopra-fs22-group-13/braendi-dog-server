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


    @Query(value = "select * from GameHistory gh where gh.user1_id = ?1 OR gh.user2_id = ?1 OR gh.user3_id = ?1 OR gh.user4_id = ?1 ORDER BY gh.start_date DESC LIMIT 15", nativeQuery = true)
    public List<GameHistory> findPlayedGames(User user);
}
