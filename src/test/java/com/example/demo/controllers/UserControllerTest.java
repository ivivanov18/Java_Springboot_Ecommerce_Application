package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private static final String username = "test";
    private static final String password = "testPassword";
    private static final String hashedPassword = "thisIsHashed";
    private static final long id = 1L;
    private static final String unknownUsername = "unknownUsername";

    @Before
    public void setup() {
        userController = new UserController(userRepository, cartRepository, encoder);
        mockEncoding(password, hashedPassword);
        //TODO: TestUtils is not working
        //TestUtils.injectObjects(userController, "userRepository", userRepository);
        //TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        //TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_should_succeed() {
        CreateUserRequest userRequest = createUserRequest();

        final ResponseEntity<User> response = userController.createUser(userRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        Assert.assertNotNull(u);
        Assert.assertEquals(0, u.getId());
        Assert.assertEquals(username, u.getUsername());
        Assert.assertEquals(hashedPassword, u.getPassword());
    }

    @Test
    public void create_user_with_no_password_should_return_400() {
        CreateUserRequest userRequest = createUserRequest();
        userRequest.setPassword(null);

        final ResponseEntity<User> response = userController.createUser(userRequest);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void create_user_with_no_confirm_password_should_return_400() {
        CreateUserRequest userRequest = createUserRequest();
        userRequest.setConfirmPassword(null);

        final ResponseEntity<User> response = userController.createUser(userRequest);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void create_user_with_password_less_than_7_characters_should_return_400() {
        CreateUserRequest userRequest = createUserRequest();
        userRequest.setPassword("123");

        final ResponseEntity<User> response = userController.createUser(userRequest);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void create_user_with_confirm_password_different_than_password_return_400() {
        CreateUserRequest userRequest = createUserRequest();
        userRequest.setPassword("123");
        userRequest.setConfirmPassword("1234");

        final ResponseEntity<User> response = userController.createUser(userRequest);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void find_user_by_id_should_succeed() {
        User user = createUser();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = userController.findById(user.getId());

        User foundUser = response.getBody();

        Assert.assertNotNull(foundUser);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(username, foundUser.getUsername());
        Assert.assertEquals(password, foundUser.getPassword());
    }

    @Test
    public void find_user_by_username_should_succeed() {
        User user = createUser();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName(user.getUsername());

        User foundUser = response.getBody();

        Assert.assertNotNull(foundUser);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(username, foundUser.getUsername());
        Assert.assertEquals(password, foundUser.getPassword());
    }

    @Test
    public void find_user_by_username_should_return_not_found() {
        final ResponseEntity<User> response = userController.findByUserName(unknownUsername);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private CreateUserRequest createUserRequest() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername(username);
        r.setPassword(password);
        r.setConfirmPassword(password);
        return r;
    }

    private User createUser() {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setPassword(password);
        return u;
    }

    private void mockEncoding(String input, String output) {
        when(encoder.encode(input)).thenReturn(output);
    }
}
