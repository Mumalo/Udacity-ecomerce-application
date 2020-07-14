package com.example.demo.controllers;


import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemControllerSpec {


    @Autowired
    private ItemController itemController;

    @Autowired
    private ItemRepository itemRepository;



    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    private Item getItemObject(){
        Item item = new Item();
        item.setName(TestUtils.getRandomString());
        item.setPrice(BigDecimal.valueOf(TestUtils.getRandomLong().shortValue()));
        item.setDescription(TestUtils.getRandomString());
        return item;
    }

    private void createItems(int count){
        for (int i = 0; i <= count; i++){
           itemRepository.save(getItemObject());
        }
    }

    @Test
    public void test_get_items_happy_path(){
        createItems(10);
        ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertNotEquals(responseEntity.getBody().size(), 0);
    }

    @Test
    public void test_get_item_by_id_happy_path(){
        Item item = itemRepository.save(getItemObject());
        ResponseEntity<Item> responseEntity = itemController.getItemById(item.getId());
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody().getId(), item.getId());

    }

    @Test
    public void test_get_item_by_id_failure(){
        ResponseEntity<Item> responseEntity = itemController.getItemById(1000L);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void test_get_items_by_name_happy_path(){
        Item item = itemRepository.save(getItemObject());
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName(item.getName());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody().size(), 1);
        assertEquals(responseEntity.getBody().get(0).getName(), item.getName());
    }

    @Test
    public void test_get_items_by_name_failure_path(){
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName(TestUtils.getRandomString());
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

}
