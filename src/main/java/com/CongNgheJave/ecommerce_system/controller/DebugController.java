package com.CongNgheJave.ecommerce_system.controller;

import com.CongNgheJave.ecommerce_system.entity.Order;
import com.CongNgheJave.ecommerce_system.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class DebugController {

    private final OrderRepository orderRepository;

    public DebugController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/debug-orders")
    public String debugOrders(Model model) {
        // Lấy TẤT CẢ đơn hàng từ database
        List<Order> allOrders = orderRepository.findAll();
        
        model.addAttribute("orders", allOrders);
        
        return "debug-orders";
    }
    
    @GetMapping("/debug-db-structure")
    @ResponseBody
    public String debugDatabaseStructure() {
        try {
            // Query để xem cấu trúc bảng Orders
            String result = "Checking Orders table structure...\n\n";
            
            // Thử query một order để xem có field nào
            List<Order> orders = orderRepository.findAll();
            if (!orders.isEmpty()) {
                Order order = orders.get(0);
                result += "Sample Order found:\n";
                result += "ID: " + order.getId() + "\n";
                result += "OrderCode: " + order.getOrderCode() + "\n";
                result += "ShippingFullName: " + order.getShippingFullName() + "\n";
                result += "ShippingPhone: " + order.getShippingPhone() + "\n";
                result += "ShippingAddressLine: " + order.getShippingAddressLine() + "\n";
                result += "ShippingWard: " + order.getShippingWard() + "\n";
                result += "ShippingDistrict: " + order.getShippingDistrict() + "\n";
                result += "ShippingCity: " + order.getShippingCity() + "\n";
            } else {
                result += "No orders found in database\n";
            }
            
            return result;
        } catch (Exception e) {
            return "Error: " + e.getMessage() + "\n\nStack trace:\n" + e.toString();
        }
    }
}
