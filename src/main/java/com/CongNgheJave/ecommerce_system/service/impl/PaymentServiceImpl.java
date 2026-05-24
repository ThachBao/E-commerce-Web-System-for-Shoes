package com.CongNgheJave.ecommerce_system.service.impl;

import com.CongNgheJave.ecommerce_system.entity.Order;
import com.CongNgheJave.ecommerce_system.entity.Payment;
import com.CongNgheJave.ecommerce_system.exception.ResourceNotFoundException;
import com.CongNgheJave.ecommerce_system.repository.PaymentRepository;
import com.CongNgheJave.ecommerce_system.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Payment createPayment(Order order) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(order.getPaymentMethod());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentStatus("PENDING");

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void markAsPaid(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy payment id = " + paymentId));

        payment.setPaymentStatus("PAID");
        payment.setPaidAt(LocalDateTime.now());
        
        Order order = payment.getOrder();
        if (order != null) {
            order.setPaymentStatus("PAID");
        }
        
        paymentRepository.save(payment);
    }
}
