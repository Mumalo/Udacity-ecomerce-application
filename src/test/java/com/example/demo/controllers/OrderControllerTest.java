package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
@WebAppConfiguration
public class OrderControllerTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;


    @Autowired
    UserRepository userRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ItemRepository itemRepository;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    public User createUserAndCart() {
        userRepository.deleteAll();
        String username = "randomUser";
        User user = new User();
        user.setPassword(UUID.randomUUID().toString());
        user.setUsername(username);
        User savedUser = userRepository.save(user);
        Item item = new Item();
        item.setName("Book");
        item.setPrice(new BigDecimal("50.0"));
        item.setDescription(UUID.randomUUID().toString());
        itemRepository.save(item);
        Cart userCart = new Cart();
        userCart.addItem(item);
        userCart.setUser(savedUser);
        cartRepository.save(userCart);
        user.setCart(userCart);
        return userRepository.save(user);
    }

    @Test
    @WithMockUser(value = "spring")
    public void testSubmit() throws Exception {

        User user = createUserAndCart();

        mvc.perform(
                post(new URI("/api/order/submit/" + user.getUsername()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(user.getCart().getItems().get(0).getPrice().doubleValue())
                );
    }

    @Test
    @WithMockUser(value = "spring")
    public void testgetOrdersForUser() throws Exception {

        User user = createUserAndCart();

        mvc.perform(
                get(new URI("/api/order/history/" + user.getUsername()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk()

                );
    }

}
