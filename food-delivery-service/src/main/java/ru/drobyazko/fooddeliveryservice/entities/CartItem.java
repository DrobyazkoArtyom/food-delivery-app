//package ru.drobyazko.fooddeliveryservice.entities;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.Id;
//import jakarta.persistence.ManyToOne;
//
//@Entity
//public class CartItem {
//    @Id
//    private Long id;
//    //TODO: need to create index on cart and menuitem fields (cart_id, menuitem_id) idx;
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Cart cart;
//    @ManyToOne(fetch = FetchType.LAZY)
//    private MenuItem menuItem;
//    private Integer quantity;
//
//    protected CartItem() {
//    }
//
//    public CartItem(Cart cart, MenuItem menuItem, Integer quantity) {
//        this.cart = cart;
//        this.menuItem = menuItem;
//        this.quantity = quantity;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public Cart getCart() {
//        return cart;
//    }
//
//    public MenuItem getMenuItem() {
//        return menuItem;
//    }
//
//    public Integer getQuantity() {
//        return quantity;
//    }
//}
