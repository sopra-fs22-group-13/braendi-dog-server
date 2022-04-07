package ch.uzh.ifi.hase.soprafs22.rest.repository;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    User findByPassword(String password);

    User findByUsername(String username);

    User findByToken(String token);

    User findById(String id);

}