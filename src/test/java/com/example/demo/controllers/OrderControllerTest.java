package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    private User user;
    private Cart cart;
    private UserOrder order;
    private Item item;

    private final String username = "test";
    private final BigDecimal total = BigDecimal.valueOf(180.0);
    private final String productName = "Wilson Racket";
    private final String productDescription = "Tennis racket for advanced players";

    @Before
    public void setup() {
        orderController = new OrderController(orderRepository, userRepository);
        initData();
        when(userRepository.findByUsername(username)).thenReturn(user);
    }

    @Test
    public void should_submit_successfully() {
        final ResponseEntity<UserOrder> response = orderController.submit(username);

        UserOrder orderFromResponse = response.getBody();
        Assert.assertNotNull(orderFromResponse);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(username, orderFromResponse.getUser().getUsername());
        Assert.assertEquals(total, orderFromResponse.getTotal());
        Assert.assertEquals(productName, orderFromResponse.getItems().get(0).getName());
        Assert.assertEquals(productDescription, orderFromResponse.getItems().get(0).getDescription());
    }

    @Test
    public void should_return_not_found() {
        final ResponseEntity<UserOrder> response = orderController.submit("Unknown user");

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void should_return_user_orders() {
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order));

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);

        List<UserOrder> ordersFromResponse = response.getBody();

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(1, ordersFromResponse.size());
        Assert.assertEquals(total, ordersFromResponse.get(0).getTotal());
        Assert.assertEquals(productName, ordersFromResponse.get(0).getItems().get(0).getName());
        Assert.assertEquals(username, ordersFromResponse.get(0).getUser().getUsername());
    }

    @Test
    public void should_return_not_found_when_getting_orders() {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("unknown user");

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private void initData() {
        user = new User();
        user.setUsername("test");

        item = new Item();
        item.setName(productName);
        item.setPrice(BigDecimal.valueOf(180.0));
        item.setDescription(productDescription);

        cart = new Cart();
        cart.addItem(item);
        cart.setUser(user);

        order = new UserOrder();
        order.setUser(user);
        order.setTotal(total);
        order.setItems(Arrays.asList(item));
        user.setCart(cart);
    }
}
