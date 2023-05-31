package uk.ac.ic.doc.fleet.lifecycle;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import io.quarkus.runtime.StartupEvent;
import uk.ac.ic.doc.fleet.dao.UserDao;
import uk.ac.ic.doc.fleet.entity.User;


@Singleton
public class Startup {
    @Inject
    private UserDao userDao;
    @Transactional
    public void loadUsers(@Observes StartupEvent evt) {
        userDao.save(new User("admin", "admin", "admin"));
        userDao.save(new User("user", "user", "user"));
    }
}
