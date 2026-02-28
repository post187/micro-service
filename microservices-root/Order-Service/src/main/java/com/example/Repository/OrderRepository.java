package com.example.Repository;

import com.example.Model.Entity.Cart;
import com.example.Model.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Modifying
    @Query("DELETE FROM Order o WHERE o.cart = :cart")
    void deleteAllByCart(Cart cart);
}
