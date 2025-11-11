//package ru.drobyazko.fooddeliveryservice.repositories;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import ru.drobyazko.fooddeliveryservice.entities.CartItem;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface CartItemRepository extends JpaRepository<CartItem, Long> {
//    //TODO: see what sql this actually generates
//    Optional<CartItem> findByCart_IdAndMenuItem_Id(Long cartId, Long menuItemId);
//
//    List<CartItem> findByCart_Id(Long cartId);
//
//    @Modifying
//    @Query("UPDATE CartItem cart_item set cart_item.quantity = ?1")
//    void updateQuantity(int quantity);
//}
