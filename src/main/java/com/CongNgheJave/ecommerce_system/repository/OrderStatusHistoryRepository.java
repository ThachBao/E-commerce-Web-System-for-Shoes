package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Integer> {

    // Lấy lịch sử đổi trạng thái của đơn hàng.
    @EntityGraph(attributePaths = {"changedBy"})
    List<OrderStatusHistory> findByOrder_IdOrderByChangedAtDesc(Integer orderId);
}