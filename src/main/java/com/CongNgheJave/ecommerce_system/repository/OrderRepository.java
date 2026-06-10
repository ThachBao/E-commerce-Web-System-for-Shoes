package com.CongNgheJave.ecommerce_system.repository;

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

    Optional<Order> findByOrderCode(String orderCode);

    long countByOrderStatus(String orderStatus);

    // Customer xem lịch sử đơn hàng.
    @EntityGraph(attributePaths = {"user"})
    List<Order> findByUser_IdOrderByPlacedAtDesc(Integer userId);

    @EntityGraph(attributePaths = {"user"})
    List<Order> findTop5ByUser_IdOrderByPlacedAtDesc(Integer userId);

    @EntityGraph(attributePaths = {"user", "items"})
    Optional<Order> findByOrderCodeAndUser_Id(String orderCode, Integer userId);

    // Admin xem danh sách và lọc theo trạng thái.
    @EntityGraph(attributePaths = {"user"})
    @Query("""
            select o from OrderEntity o
            where (:status is null or :status = '' or o.orderStatus = :status)
            order by o.placedAt desc
            """)
    Page<Order> findAdminOrders(@Param("status") String status, Pageable pageable);

    // Xem chi tiết đơn, items, customer, images, color, size.
    @EntityGraph(attributePaths = {"user", "items", "items.variant", "items.variant.product", "items.variant.product.images", "items.variant.color", "items.variant.size"})
    @Query("""
            select o from OrderEntity o
            where o.id = :id
            """)
    Optional<Order> findDetailById(@Param("id") Integer id);

    // ==================== Revenue Statistics ====================

    // Lấy đơn hàng đã hoàn thành trong khoảng thời gian (cho biểu đồ doanh thu)
    @Query("""
            select o from OrderEntity o
            where o.orderStatus = 'COMPLETED'
            and o.placedAt >= :from and o.placedAt < :to
            order by o.placedAt asc
            """)
    List<Order> findCompletedOrdersBetween(@Param("from") java.time.LocalDateTime from,
                                           @Param("to") java.time.LocalDateTime to);

    // Lấy tất cả đơn hàng trong khoảng thời gian (cho thống kê tổng quan)
    @Query("""
            select o from OrderEntity o
            where o.placedAt >= :from and o.placedAt < :to
            order by o.placedAt asc
            """)
    List<Order> findAllOrdersBetween(@Param("from") java.time.LocalDateTime from,
                                     @Param("to") java.time.LocalDateTime to);

    // Đếm số đơn hàng theo trạng thái trong khoảng thời gian
    @Query("""
            select count(o) from OrderEntity o
            where o.orderStatus = :status
            and o.placedAt >= :from and o.placedAt < :to
            """)
    long countByStatusBetween(@Param("status") String status,
                              @Param("from") java.time.LocalDateTime from,
                              @Param("to") java.time.LocalDateTime to);

    // Tổng doanh thu đơn hoàn thành trong khoảng thời gian
    @Query("""
            select coalesce(sum(o.totalAmount), 0) from OrderEntity o
            where o.orderStatus = 'COMPLETED'
            and o.placedAt >= :from and o.placedAt < :to
            """)
    java.math.BigDecimal sumRevenueBetween(@Param("from") java.time.LocalDateTime from,
                                           @Param("to") java.time.LocalDateTime to);

    // Đếm distinct customers trong khoảng thời gian
    @Query("""
            select count(distinct o.user.id) from OrderEntity o
            where o.placedAt >= :from and o.placedAt < :to
            """)
    long countDistinctCustomersBetween(@Param("from") java.time.LocalDateTime from,
                                       @Param("to") java.time.LocalDateTime to);
}
