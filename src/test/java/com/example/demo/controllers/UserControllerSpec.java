package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerSpec {

    @Autowired
    private UserController userController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository carRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", carRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

    }

    private CreateUserRequest userRequest(String username, String password){
        CreateUserRequest request = new CreateUserRequest();
        request.setPassword(password);
        request.setUsername(username);
        request.setConfirmPassword(password);
        return request;
    }


    @Test
    public void create_user_happy_path() {
        when(encoder.encode("TestPassword")).thenReturn("encodedPassword"); //stubbing
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setPassword("TestPassword");
        userRequest.setConfirmPassword("TestPassword");
        userRequest.setUsername("Test");
        ResponseEntity<User> response = userController.createUser(userRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertEquals(0, u.getId());
        assertNotNull(u);
        verify(encoder, times(1)).encode("TestPassword");
        assertEquals("Test", u.getUsername());
    }

    @Test
    public void create_user_failure_path(){
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setPassword("TestPassword");
        userRequest.setConfirmPassword("differentPassword");
        userRequest.setUsername("Test");
        ResponseEntity<User> response = userController.createUser(userRequest);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        verifyZeroInteractions(encoder);
    }

    @Test
    public void find_by_username_happy_path(){
        ResponseEntity<User> response = userController.findByUserName("Test");
        assertEquals(404, response.getStatusCodeValue());

        response = userController.createUser(userRequest("testUser", "password"));
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void find_by_id_happy_path(){
        ResponseEntity<User> response = userController.findById(0L);
        assertEquals(404, response.getStatusCodeValue());
    }
}
