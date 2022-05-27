package ch.uzh.ifi.hase.soprafs22.rest.repository;

import ch.uzh.ifi.hase.soprafs22.rest.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    User findByPassword(String password);

    User findByUsername(String username);

    User findByToken(String token);

    Optional<User> findById(Long id);

    @Query(value = "SELECT * FROM users us ORDER BY us.wins DESC, us.goals DESC LIMIT 10", nativeQuery = true)
    List<User> getTopTenUsers();

}