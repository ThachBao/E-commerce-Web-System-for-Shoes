package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    //  Hiển thị thông tin thanh toán theo orderId.
    Optional<Payment> findByOrder_Id(Integer orderId);
}