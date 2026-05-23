package com.CongNgheJave.ecommerce_system.repository;

<<<<<<< HEAD
public class OrderRepository {
=======
import com.CongNgheJave.ecommerce_system.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
<<<<<<< HEAD
    Optional<Order> findByOrderCode(String orderCode);
>>>>>>> origin/member3_cart_payment
}
=======

    // Customer xem lịch sử đơn hàng.
    @EntityGraph(attributePaths = {"user"})
    List<Order> findByUser_IdOrderByPlacedAtDesc(Integer userId);

    // Admin xem danh sách và lọc theo trạng thái.
    @EntityGraph(attributePaths = {"user"})
    @Query("""
            select o from OrderEntity o
            where (:status is null or :status = '' or o.orderStatus = :status)
            order by o.placedAt desc
            """)
    Page<Order> findAdminOrders(@Param("status") String status, Pageable pageable);

    // Xem chi tiết đơn, items, customer.
    @EntityGraph(attributePaths = {"user", "items"})
    @Query("""
            select o from OrderEntity o
            where o.id = :id
            """)
    Optional<Order> findDetailById(@Param("id") Integer id);
}
>>>>>>> origin/member2-product-catalog
