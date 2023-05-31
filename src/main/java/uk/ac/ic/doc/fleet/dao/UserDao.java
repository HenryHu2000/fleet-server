package uk.ac.ic.doc.fleet.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ic.doc.fleet.entity.User;

public interface UserDao extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    User findUserByUsername(String username);
}
