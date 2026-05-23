package com.CongNgheJave.ecommerce_system.repository;

<<<<<<< HEAD
public class OrderRepository {
=======
import com.CongNgheJave.ecommerce_system.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByOrderCode(String orderCode);
>>>>>>> origin/member3_cart_payment
}
