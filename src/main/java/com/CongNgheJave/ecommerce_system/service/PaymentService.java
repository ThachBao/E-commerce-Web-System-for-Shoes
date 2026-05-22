package com.CongNgheJave.ecommerce_system.service;

import com.CongNgheJave.ecommerce_system.entity.Order;
import com.CongNgheJave.ecommerce_system.entity.Payment;

public interface PaymentService {

    /**
     * Create payment record for an order.
     */
    Payment createPayment(Order order);

    /**
     * Mark payment as paid (for bank transfer confirmation).
     */
    void markAsPaid(Integer paymentId);
}
