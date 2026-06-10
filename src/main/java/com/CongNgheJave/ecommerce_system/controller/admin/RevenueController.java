package com.CongNgheJave.ecommerce_system.controller.admin;

import com.CongNgheJave.ecommerce_system.entity.Order;
import com.CongNgheJave.ecommerce_system.repository.AppUserRepository;
import com.CongNgheJave.ecommerce_system.repository.OrderRepository;
import com.CongNgheJave.ecommerce_system.repository.ProductRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/revenue")
public class RevenueController {

    private final OrderRepository orderRepository;
    private final AppUserRepository appUserRepository;
    private final ProductRepository productRepository;

    public RevenueController(OrderRepository orderRepository,
                             AppUserRepository appUserRepository,
                             ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.appUserRepository = appUserRepository;
        this.productRepository = productRepository;
    }

    /**
     * Hiển thị trang dashboard báo cáo doanh thu.
     */
    @GetMapping
    public String dashboard() {
        return "admin/revenue/dashboard";
    }

    /**
     * REST API trả dữ liệu thống kê cho biểu đồ và thẻ tổng hợp.
     * Params: from (yyyy-MM-dd), to (yyyy-MM-dd)
     */
    @GetMapping("/api/stats")
    @ResponseBody
    public Map<String, Object> getStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay(); // inclusive end date

        Map<String, Object> result = new LinkedHashMap<>();

        // === 1. Summary Cards ===
        List<Order> allOrders = orderRepository.findAllOrdersBetween(fromDateTime, toDateTime);
        List<Order> completedOrders = orderRepository.findCompletedOrdersBetween(fromDateTime, toDateTime);

        long totalOrders = allOrders.size();
        BigDecimal totalRevenue = orderRepository.sumRevenueBetween(fromDateTime, toDateTime);
        long totalCustomers = orderRepository.countDistinctCustomersBetween(fromDateTime, toDateTime);
        long totalProducts = productRepository.count();

        result.put("totalOrders", totalOrders);
        result.put("totalRevenue", totalRevenue);
        result.put("totalCustomers", totalCustomers);
        result.put("totalProducts", totalProducts);
        result.put("completedOrders", completedOrders.size());

        // === 2. Revenue Chart Data (by date) ===
        // Group completed orders by date
        Map<LocalDate, BigDecimal> dailyRevenue = new LinkedHashMap<>();

        // Initialize all dates in range with 0
        LocalDate current = from;
        while (!current.isAfter(to)) {
            dailyRevenue.put(current, BigDecimal.ZERO);
            current = current.plusDays(1);
        }

        // Fill with actual revenue data
        for (Order order : completedOrders) {
            LocalDate orderDate = order.getPlacedAt().toLocalDate();
            dailyRevenue.merge(orderDate, order.getTotalAmount(), BigDecimal::add);
        }

        List<String> chartLabels = new ArrayList<>();
        List<BigDecimal> chartData = new ArrayList<>();
        for (Map.Entry<LocalDate, BigDecimal> entry : dailyRevenue.entrySet()) {
            chartLabels.add(entry.getKey().toString());
            chartData.add(entry.getValue());
        }

        result.put("chartLabels", chartLabels);
        result.put("chartData", chartData);

        // === 3. Order count chart (by date) ===
        Map<LocalDate, Long> dailyOrderCount = new LinkedHashMap<>();
        current = from;
        while (!current.isAfter(to)) {
            dailyOrderCount.put(current, 0L);
            current = current.plusDays(1);
        }
        for (Order order : allOrders) {
            LocalDate orderDate = order.getPlacedAt().toLocalDate();
            dailyOrderCount.merge(orderDate, 1L, Long::sum);
        }

        List<Long> orderCountData = new ArrayList<>(dailyOrderCount.values());
        result.put("orderCountData", orderCountData);

        // === 4. Order Status Distribution (Pie chart) ===
        long pendingCount = orderRepository.countByStatusBetween("PENDING", fromDateTime, toDateTime);
        long confirmedCount = orderRepository.countByStatusBetween("CONFIRMED", fromDateTime, toDateTime);
        long processingCount = orderRepository.countByStatusBetween("PROCESSING", fromDateTime, toDateTime);
        long shippingCount = orderRepository.countByStatusBetween("SHIPPING", fromDateTime, toDateTime);
        long completedCount = orderRepository.countByStatusBetween("COMPLETED", fromDateTime, toDateTime);
        long cancelledCount = orderRepository.countByStatusBetween("CANCELLED", fromDateTime, toDateTime);

        Map<String, Long> statusDistribution = new LinkedHashMap<>();
        statusDistribution.put("Chờ duyệt", pendingCount);
        statusDistribution.put("Đã xác nhận", confirmedCount);
        statusDistribution.put("Đang xử lý", processingCount);
        statusDistribution.put("Đang giao", shippingCount);
        statusDistribution.put("Hoàn thành", completedCount);
        statusDistribution.put("Đã hủy", cancelledCount);

        result.put("statusLabels", new ArrayList<>(statusDistribution.keySet()));
        result.put("statusData", new ArrayList<>(statusDistribution.values()));

        // === 5. Top selling products (from completed orders in range) ===
        Map<String, Long> productSales = new HashMap<>();
        for (Order order : completedOrders) {
            if (order.getItems() != null) {
                for (var item : order.getItems()) {
                    String name = item.getProductName() != null ? item.getProductName() : "Unknown";
                    // Lấy tên sản phẩm gốc (bỏ phần color/size suffix nếu có)
                    productSales.merge(name, (long) item.getQuantity(), Long::sum);
                }
            }
        }

        List<Map.Entry<String, Long>> topProducts = productSales.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<String> topProductNames = topProducts.stream().map(Map.Entry::getKey).collect(Collectors.toList());
        List<Long> topProductQty = topProducts.stream().map(Map.Entry::getValue).collect(Collectors.toList());

        result.put("topProductNames", topProductNames);
        result.put("topProductQty", topProductQty);

        return result;
    }
}
