package uk.ac.ic.doc.fleet.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import uk.ac.ic.doc.fleet.dao.UserDao;
import uk.ac.ic.doc.fleet.entity.User;

@Path("/api/public")
public class PublicController {
    @Inject
    private UserDao userDao;

    @POST
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response register(@FormParam("username") String username, @FormParam("password") String password) throws CloneNotSupportedException {
        if (userDao.existsByUsername(username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        var user = new User(username, password, "user");
        userDao.save(user);
        var userDto = (User) user.clone();
        userDto.setPassword(null);
        return Response.ok(userDto).build();
   }
}
