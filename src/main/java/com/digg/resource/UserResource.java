package com.digg.resource;

import com.digg.entity.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    public List<User> getAllUsers() {
        return User.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        User user = User.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"User not found\"}")
                    .build();
        }
        return Response.ok(user).build();
    }

    @GET
    @Path("/search")
    public List<User> searchUsersByName(@QueryParam("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            return User.listAll();
        }
        return User.findByName(name);
    }

    @GET
    @Path("/email/{email}")
    public Response getUserByEmail(@PathParam("email") String email) {
        User user = User.findByEmail(email);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"User not found\"}")
                    .build();
        }
        return Response.ok(user).build();
    }

    @POST
    @Transactional
    public Response createUser(@Valid User user) {
        // Check if email already exists
        User existing = User.findByEmail(user.email);
        if (existing != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Email already exists\"}")
                    .build();
        }

        user.persist();
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, @Valid User updatedUser) {
        User user = User.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"User not found\"}")
                    .build();
        }

        // Check if email is being changed to an existing one
        if (!user.email.equals(updatedUser.email)) {
            User emailCheck = User.findByEmail(updatedUser.email);
            if (emailCheck != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Email already exists\"}")
                        .build();
            }
        }

        user.name = updatedUser.name;
        user.address = updatedUser.address;
        user.email = updatedUser.email;
        user.telephone = updatedUser.telephone;

        return Response.ok(user).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        User user = User.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"User not found\"}")
                    .build();
        }

        user.delete();
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/count")
    public Response getUserCount() {
        long count = User.count();
        return Response.ok("{\"count\": " + count + "}").build();
    }
}