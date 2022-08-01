package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTests {
    private CartController cartController;
    final private UserRepository userRepository = mock(UserRepository.class);
    final private ItemRepository itemRepository = mock(ItemRepository.class);
    final private CartRepository cartRepository = mock(CartRepository.class);

    private User user;
    private Cart cart;
    private Item firstItem;
    private List<Item> items;
    private String username = "test";

    @Before
    public void setup() {
        cartController = new CartController(userRepository, cartRepository, itemRepository);
        initData();
        initMock();
    }

    public void initMock() {
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(firstItem));
    }

    @Test
    public void add_to_cart_unknown_username_should_return_not_found() {
        ResponseEntity<Cart> response = cartController.addTocart(new ModifyCartRequest("unknown username", 1L, 1));

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void add_to_cart_unknown_item_id_should_return_not_found() {
        ResponseEntity<Cart> response = cartController.addTocart(new ModifyCartRequest("unknown username", 1L, 1));

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void add_to_cart_should_succeed() {
        ResponseEntity<Cart> response = cartController.addTocart(new ModifyCartRequest(username, 1L, 2));

        Cart cart = response.getBody();

        Assert.assertNotNull(cart);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(cart.getItems().get(0).getName(), firstItem.getName());
        Assert.assertEquals(cart.getUser().getUsername(), username);

        BigDecimal totalOfItemsBeforeAddCart = items.stream().map(Item::getPrice).reduce(BigDecimal.valueOf(0), (a, b) -> a.add(b));
        Assert.assertEquals(cart.getTotal(), totalOfItemsBeforeAddCart.add(firstItem.getPrice()).add(firstItem.getPrice()));
    }

    private void initData() {
        user = new User();
        user.setUsername(username);

        cart = new Cart();
        cart.setUser(user);

        firstItem = createItem(1L,"Wilson racket", "For advanced players", BigDecimal.valueOf(180.0));

        Item second = createItem(2L, "Head racket", "Latest technology for professional players", BigDecimal.valueOf(250.0));
        Item third = createItem(3L, "Artengo junior", "Decathlon technology", BigDecimal.valueOf(80.0));
        items = new ArrayList<>();
        items.add(firstItem);
        cart.addItem(firstItem);
        items.add(second);
        cart.addItem(second);
        items.add(third);
        cart.addItem(third);
        user.setCart(cart);
    }

    private Item createItem(Long id, String name, String description, BigDecimal price) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);

        return item;
    }
}