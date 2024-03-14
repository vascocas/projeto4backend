package paj.project4vc.service;

import jakarta.persistence.Column;
import paj.project4vc.bean.TaskBean;
import paj.project4vc.bean.UserBean;
import paj.project4vc.dto.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.management.relation.Role;
import java.util.ArrayList;

@Path("/users")
public class UserService {

    private static final long serialVersionUID = 1L;

    @Inject
    UserBean userBean;

    // Makes Login (Return token)
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@HeaderParam("username") String username, @HeaderParam("password") String password) {
        LoginDto login = userBean.login(username, password);
        if (login != null) {
            return Response.status(200).entity(login).build();
        }
        return Response.status(403).entity("Wrong Username or Password!").build();
    }

    // Register new user
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(UserDto user) {
        // Validate the UserDto inputs
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return Response.status(401).entity("Username cannot be empty").build();
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return Response.status(401).entity("Password cannot be empty").build();
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return Response.status(401).entity("Email cannot be empty").build();
        }
        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            return Response.status(401).entity("First name cannot be empty").build();
        }
        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            return Response.status(401).entity("Last name cannot be empty").build();
        }
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            return Response.status(401).entity("Phone cannot be empty").build();
        }
        // Proceed with registering the user
        if (userBean.register(user)) {
            return Response.status(200).entity("The new user is registered").build();
        }
        return Response.status(401).entity("There is a user with the same username").build();
    }

    // Makes Logout
    @PUT
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logout(@HeaderParam("token") String token) {
        if (userBean.logout(token)) {
            return Response.status(200).entity("Logout Successful!").build();
        } else {
            return Response.status(401).entity("Invalid Token!").build();
        }
    }

    // Get user by Id
    @GET
    @Path("/user/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userById(@PathParam("id") int id, @HeaderParam("token") String token) {
        if (userBean.tokenExist(token)) {
            UserDto dto = userBean.userById(id, token);
            return Response.status(200).entity(dto).build();
        } else {
            userBean.logout(token);
            return Response.status(401).entity("Invalid Token!").build();
        }
    }

    // Get logged user
    @GET
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userByToken(@HeaderParam("token") String token) {
        if (userBean.tokenExist(token)) {
            UserDto dto = userBean.userByToken(token);
            return Response.status(200).entity(dto).build();
        } else {
            userBean.logout(token);
            return Response.status(401).entity("Invalid Token!").build();
        }
    }

    // Get list of usernames (Login dto)
    @GET
    @Path("/usernames")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsernameList(@HeaderParam("token") String token) {
        if (!userBean.tokenExist(token)) {
            userBean.logout(token);
            return Response.status(401).entity("Invalid token").build();
        }
        ArrayList<LoginDto> usernames = userBean.getAllUsernames();
        return Response.status(200).entity(usernames).build();
    }

    // Get list of active users (User dto)
    @GET
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@HeaderParam("token") String token) {
        if (userBean.tokenExist(token)) {
            ArrayList<UserDto> users = userBean.getAllUsers(token);
            return Response.status(200).entity(users).build();
        } else {
            userBean.logout(token);
            return Response.status(401).entity("Invalid Token!").build();
        }
    }

    // Get list of deleted users (User dto)
    @GET
    @Path("/deletedUsers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDeletedUsers(@HeaderParam("token") String token) {
        if (userBean.tokenExist(token)) {
            ArrayList<UserDto> users = userBean.getDeletedUsers(token);
            return Response.status(200).entity(users).build();
        } else {
            userBean.logout(token);
            return Response.status(401).entity("Invalid Token!").build();
        }
    }

    // Get user by username
    @GET
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@HeaderParam("token") String token, @PathParam("username") String username) {
        if (userBean.tokenExist(token)) {
            UserDto u = userBean.getProfile(username, token);
            return Response.status(200).entity(u).build();
        } else {
            userBean.logout(token);
            return Response.status(401).entity("Invalid Token!").build();
        }
    }

    // Edit user profile
    @PUT
    @Path("/profile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editProfile(@HeaderParam("token") String token, UserDto user) {
        if (userBean.tokenExist(token)) {
            userBean.editProfile(user, token);
            return Response.status(200).entity("Profile updated!").build();
        } else {
            userBean.logout(token);
            return Response.status(401).entity("Invalid Token!").build();
        }
    }

    // Get role of logged user
    @GET
    @Path("/role")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response roleByToken(@HeaderParam("token") String token) {
        if (userBean.tokenExist(token)) {
            LoginDto userRole = userBean.getRole(token);
            return Response.status(200).entity(userRole).build();
        } else {
            userBean.logout(token);
            return Response.status(401).entity("Invalid Token!").build();
        }
    }

    // Creates a new user
    @POST
    @Path("/createUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@HeaderParam("token") String token, UserDto user) {
        // Validate the UserDto inputs
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return Response.status(401).entity("Username cannot be empty").build();
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return Response.status(401).entity("Password cannot be empty").build();
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return Response.status(401).entity("Email cannot be empty").build();
        }
        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            return Response.status(401).entity("First name cannot be empty").build();
        }
        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            return Response.status(401).entity("Last name cannot be empty").build();
        }
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            return Response.status(401).entity("Phone cannot be empty").build();
        }
        if (user.getRole() == null) {
            return Response.status(401).entity("Role cannot be empty").build();
        }
        // Proceed with registering the user
        if (!userBean.tokenExist(token)) {
            userBean.logout(token);
            return Response.status(401).entity("Invalid Token!").build();
        } else {
            if (userBean.createUser(token, user)) {
                return Response.status(200).entity("New user created!").build();
            } else return Response.status(401).entity("Error").build();
        }
    }

    // Delete user by username (Recycle bin)
    @PUT
    @Path("/delete/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@HeaderParam("token") String token, @PathParam("username") String username) {
        if (userBean.tokenExist(token)) {
            if (userBean.deleteUser(token, username)) {
                return Response.status(200).entity("Profile deleted").build();
            } else {
                return Response.status(401).entity("Error").build();
            }
        } else {
            userBean.logout(token);
            return Response.status(401).entity("Invalid Token!").build();
        }
    }

    // Edit user role
    @PUT
    @Path("/role")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRole(LoginDto user, @HeaderParam("token") String token) {
        if (userBean.tokenExist(token)) {
            if (userBean.updateRole(user, token)) {
                return Response.status(200).entity("Role updated").build();
            } else {
                return Response.status(401).entity("Error").build();
            }
        } else {
            userBean.logout(token);
            return Response.status(401).entity("Invalid Token!").build();
        }
    }

    // Remove user by username (Permanently)
    @DELETE
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeUser(@HeaderParam("token") String token, @PathParam("userId") int userId) {
        if (userBean.tokenExist(token)) {
            if (userBean.removeUser(userId, token)) {
                return Response.status(200).entity("Profile removed").build();
            } else {
                return Response.status(401).entity("Error").build();
            }
        } else {
            userBean.logout(token);
            return Response.status(401).entity("Invalid Token!").build();
        }
    }
}