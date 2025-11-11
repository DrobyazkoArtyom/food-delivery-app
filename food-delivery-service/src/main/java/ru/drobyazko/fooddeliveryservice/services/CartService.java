//package ru.drobyazko.fooddeliveryservice.services;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import ru.drobyazko.fooddeliveryservice.dtos.*;
//import ru.drobyazko.fooddeliveryservice.dtos.requests.AddToCartRequest;
//import ru.drobyazko.fooddeliveryservice.dtos.requests.SetQuantityCartItemRequest;
//import ru.drobyazko.fooddeliveryservice.entities.Cart;
//import ru.drobyazko.fooddeliveryservice.entities.CartItem;
//import ru.drobyazko.fooddeliveryservice.entities.MenuItem;
//import ru.drobyazko.fooddeliveryservice.repositories.CartItemRepository;
//import ru.drobyazko.fooddeliveryservice.repositories.CartRepository;
//import ru.drobyazko.fooddeliveryservice.repositories.MenuItemRepository;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class CartService {
//    private final CartRepository cartRepository;
//    private final CartItemRepository cartItemRepository;
//    private final MenuItemRepository menuItemRepository;
//
//    @Autowired
//    public CartService(CartRepository cartRepository,
//                       CartItemRepository cartItemRepository,
//                       MenuItemRepository menuItemRepository) {
//        this.cartRepository = cartRepository;
//        this.cartItemRepository = cartItemRepository;
//        this.menuItemRepository = menuItemRepository;
//    }
//
//    public CartDto createCart(Long userId) {
//        Cart cart = new Cart(userId);
//        cart = cartRepository.save(cart);
//        return new CartDto(cart.getId(), userId, Collections.emptyList());
//    }
//
//    public CartDto getOrCreateCart(Long userId) {
//        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
//        Cart cart;
//        List<CartItem> cartItems;
//        if (cartOptional.isPresent()) {
//            cart = cartOptional.get();
//            // TODO: maybe we should just use a bidirectional relationship in cart entity to cartItems
//            cartItems = cartItemRepository.findByCart_Id(cart.getId());
//        } else {
//            cart = new Cart(userId);
//            cart = cartRepository.save(cart);
//            cartItems = Collections.emptyList();
//        }
//
//        // in this mapping we do unnecessary work, do we really need to load Cart and MenuItemEntities to get their ids?
//        List<CartItemDto> cartItemDtos = cartItems
//                .stream()
//                .map(
//                        cartItem -> new CartItemDto(
//                                cartItem.getId(),
//                                cartItem.getCart().getId(),
//                                cartItem.getMenuItem().getId(),
//                                cartItem.getQuantity()))
//                .toList();
//        return new CartDto(cart.getId(), cart.getUserId(), cartItemDtos);
//    }
//
//    public CartItemDto addToCart(AddToCartRequest addToCartRequest) {
//        Optional<CartItem> cartItemOptional =
//                cartItemRepository.findByCart_IdAndMenuItem_Id(
//                        addToCartRequest.getCartId(),
//                        addToCartRequest.getMenuItemId());
//        CartItem cartItem;
//
//        if (cartItemOptional.isPresent()) {
//            cartItem = cartItemOptional.get();
//        } else {
//            Cart cart = cartRepository.getReferenceById(addToCartRequest.getCartId());
//            // this line of code should probably be up a level (in cartController), otherwise it introduces unnecessary coupling
//            MenuItem menuItem = menuItemRepository.findById(addToCartRequest.getMenuItemId()).orElseThrow();
//            cartItem = new CartItem(cart, menuItem, addToCartRequest.getQuantity());
//            cartItem = cartItemRepository.save(cartItem);
//        }
//
//        return new CartItemDto(
//                cartItem.getId(),
//                addToCartRequest.getCartId(),
//                addToCartRequest.getMenuItemId(),
//                cartItem.getQuantity());
//    }
//
//    //TODO: maybe return CartItemDto?
//    public CartItemDto setQuantity(SetQuantityCartItemRequest setQuantityCartItemRequest) {
//        cartItemRepository.updateQuantity(setQuantityCartItemRequest.getQuantity());
//        CartItem cartItem = cartItemRepository.findById(setQuantityCartItemRequest.getCartItemId()).orElseThrow();
//        return new CartItemDto(
//                cartItem.getId(),
//                cartItem.getCart().getId(),
//                cartItem.getMenuItem().getId(),
//                cartItem.getQuantity());
//    }
//}
