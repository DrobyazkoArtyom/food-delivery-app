//package ru.drobyazko.fooddeliveryservice.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//import ru.drobyazko.fooddeliveryservice.dtos.CartDto;
//import ru.drobyazko.fooddeliveryservice.dtos.requests.AddToCartRequest;
//import ru.drobyazko.fooddeliveryservice.dtos.requests.SetQuantityCartItemRequest;
//import ru.drobyazko.fooddeliveryservice.services.CartService;
//
//@RestController("/cart")
//public class CartController {
//    private final CartService cartService;
//
//    @Autowired
//    public CartController(CartService cartService) {
//        this.cartService = cartService;
//    }
//
//    @PostMapping
//    public CartDto getOrCreateCart(Long userId) {
//        return cartService.getOrCreateCart(userId);
//    }
//
//    @PostMapping
//    public void addToCart(AddToCartRequest addToCartRequest) {
//        cartService.addToCart(addToCartRequest);
//    }
//
//    @PostMapping
//    public void setQuantity(SetQuantityCartItemRequest setQuantityCartItemRequest) {
//        cartService.setQuantity(setQuantityCartItemRequest);
//    }
//
//    public void deleteFromCart() {
//
//    }
//
//    public void deleteCart() {
//
//    }
//}
