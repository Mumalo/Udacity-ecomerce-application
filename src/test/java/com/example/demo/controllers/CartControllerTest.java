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
import org.springframework.data.crossstore.ChangeSetPersister;
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
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
@WebAppConfiguration
public class CartControllerTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    public Item createItem(){
        itemRepository.deleteAll();
        Item item = new Item();
        item.setName("Book");
        item.setPrice(new BigDecimal("20.0"));
        item.setDescription("A book");
        return itemRepository.save(item);
    }

    public User createUserAndCart() {
        userRepository.deleteAll();
        cartRepository.deleteAll();
        String username = "randomUser";
        User user = new User();
        user.setPassword(UUID.randomUUID().toString());
        user.setUsername(username);
        User savedUser = userRepository.save(user);
        Cart userCart = new Cart();
        userCart.setUser(savedUser);
        cartRepository.save(userCart);
        user.setCart(userCart);
        return userRepository.save(user);
    }

    @Test
    @WithMockUser(value = "spring")
    public void testAddToCart() throws Exception {
        User user = createUserAndCart();
        Item item = createItem();
        String addToCartJson = "{ \"username\":\""+user.getUsername()+ "\", \"itemId\": \""+ item.getId()  +"\", \"quantity\": 1 }";
        mvc.perform(
                post(new URI("/api/cart/addToCart"))
                        .content(addToCartJson)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getCart().getId().intValue()));
    }
}
