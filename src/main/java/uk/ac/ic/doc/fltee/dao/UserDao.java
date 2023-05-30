package uk.ac.ic.doc.fltee.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ic.doc.fltee.entity.User;

public interface UserDao extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
}
