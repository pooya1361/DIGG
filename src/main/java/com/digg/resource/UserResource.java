package com.digg.resource;

import com.digg.entity.User;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    Logger log;

    @GET
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = User.listAll();
        log.debugf("Found %d users", users.size());
        return users;
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        log.infof("Fetching user by ID: %d", id);
        User user = User.findById(id);
        if (user == null) {
            log.warnf("User not found with ID: %d", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"User not found\"}")
                    .build();
        }
        log.debugf("Successfully found user: %s (ID: %d)", user.name, id);
        return Response.ok(user).build();
    }

    @GET
    @Path("/search")
    public List<User> searchUsersByName(@QueryParam("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            log.debug("Search called with empty name parameter, returning all users");
            return User.listAll();
        }

        log.infof("Searching users with name containing: '%s'", name.trim());
        List<User> results = User.findByName(name);

        if (results.isEmpty()) {
            log.infof("No users found for search term: '%s'", name.trim());
        } else {
            log.infof("Found %d users matching search term: '%s'", results.size(), name.trim());
        }

        return results;
    }

    @GET
    @Path("/email/{email}")
    public Response getUserByEmail(@PathParam("email") String email) {
        log.infof("Fetching user by email: %s", email);
        User user = User.findByEmail(email);
        if (user == null) {
            log.warnf("User not found with email: %s", email);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"User not found\"}")
                    .build();
        }
        log.debugf("Successfully found user: %s (email: %s)", user.name, email);
        return Response.ok(user).build();
    }

    @POST
    @Transactional
    public Response createUser(@Valid User user) {
        log.infof("Creating new user with email: %s", user.email);

        // Check if email already exists
        User existing = User.findByEmail(user.email);
        if (existing != null) {
            log.warnf("Attempt to create user with existing email: %s", user.email);
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Email already exists\"}")
                    .build();
        }

        user.persist();
        log.infof("Successfully created user: %s (ID: %d, email: %s)",
                user.name, user.id, user.email);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, @Valid User updatedUser) {
        log.infof("Updating user with ID: %d", id);

        User user = User.findById(id);
        if (user == null) {
            log.warnf("Attempt to update non-existent user ID: %d", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"User not found\"}")
                    .build();
        }

        log.debugf("Found existing user: %s (ID: %d)", user.name, id);

        // Check if email is being changed to an existing one
        if (!user.email.equals(updatedUser.email)) {
            log.debugf("Email change requested from %s to %s", user.email, updatedUser.email);
            User emailCheck = User.findByEmail(updatedUser.email);
            if (emailCheck != null) {
                log.warnf("Attempt to update user ID %d with existing email: %s", id, updatedUser.email);
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Email already exists\"}")
                        .build();
            }
        }

        // Log the changes
        log.debugf("Updating user fields - Name: '%s' -> '%s', Email: '%s' -> '%s', Address: '%s' -> '%s', Phone: '%s' -> '%s'",
                user.name, updatedUser.name,
                user.email, updatedUser.email,
                user.address, updatedUser.address,
                user.telephone, updatedUser.telephone);

        user.name = updatedUser.name;
        user.address = updatedUser.address;
        user.email = updatedUser.email;
        user.telephone = updatedUser.telephone;

        log.infof("Successfully updated user: %s (ID: %d)", user.name, id);
        return Response.ok(user).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        log.infof("Deleting user with ID: %d", id);

        User user = User.findById(id);
        if (user == null) {
            log.warnf("Attempt to delete non-existent user ID: %d", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"User not found\"}")
                    .build();
        }

        String userName = user.name; // Store name before deletion for logging
        user.delete();
        log.infof("Successfully deleted user: %s (ID: %d)", userName, id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/count")
    public Response getUserCount() {
        log.debug("Fetching total user count");
        long count = User.count();
        log.debugf("Total user count: %d", count);
        return Response.ok("{\"count\": " + count + "}").build();
    }
}