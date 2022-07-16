package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private static final String username = "test";
    private static final String password = "testPassword";
    private static final String hashedPassword = "thisIsHashed";

    @Before
    public void setup() {
        userController = new UserController(userRepository, cartRepository, encoder);
        //TODO: TestUtils is not working
        //TestUtils.injectObjects(userController, "userRepository", userRepository);
        //TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        //TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_should_succeed() {
        mockEncoding(password, hashedPassword);
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
    public void find_user_by_id_should_succeed() {

    }

    private CreateUserRequest createUserRequest() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername(username);
        r.setPassword(password);
        r.setConfirmPassword(password);
        return r;
    }

    private void mockEncoding(String input, String output) {
        when(encoder.encode(input)).thenReturn(output);
    }

}
