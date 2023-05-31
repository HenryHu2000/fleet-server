package uk.ac.ic.doc.fleet.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import uk.ac.ic.doc.fleet.dao.UserDao;

@Path("/api/users")
public class UserController {
    @Inject
    private UserDao userDao;

    @GET
    @RolesAllowed("user")
    @Path("/login")
    public Long login(@Context SecurityContext securityContext) {
        return userDao.findUserByUsername(securityContext.getUserPrincipal().getName()).getId();
    }
}
