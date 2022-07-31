package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    private List<Item> items;
    private Item firstItem;

    private String name = "test";

    @Before
    public void setup() {
        itemController = new ItemController(itemRepository);
        firstItem = createItem("Wilson racket", "For advanced players", BigDecimal.valueOf(180.0));

        Item second = createItem("Head racket", "Latest technology for professional players", BigDecimal.valueOf(250.0));
        Item third = createItem("Artengo junior", "Decathlon technology", BigDecimal.valueOf(80.0));
        items = new ArrayList<>();
        items.add(firstItem);
        items.add(second);
        items.add(third);
    }

    @Test
    public void should_get_all_items() {
        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();

        List<Item> itemsFromResponse = response.getBody();

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(items.size(), itemsFromResponse.size());
        Assert.assertEquals(firstItem.getName(), itemsFromResponse.get(0).getName());
        Assert.assertEquals(firstItem.getDescription(), itemsFromResponse.get(0).getDescription());
        Assert.assertEquals(firstItem.getPrice(), itemsFromResponse.get(0).getPrice());
    }

    @Test
    public void should_get_items_by_name() {
        when(itemRepository.findByName(name)).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName(name);

        List<Item> itesmFromResponse = response.getBody();

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(items, itesmFromResponse);
    }

    @Test
    public void get_all_items_by_name_should_return_not_found() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("unknown name");
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private Item createItem(String name, String description, BigDecimal price) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);

        return item;
    }
}
