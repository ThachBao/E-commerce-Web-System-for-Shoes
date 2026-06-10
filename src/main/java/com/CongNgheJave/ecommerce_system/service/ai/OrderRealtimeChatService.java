package com.CongNgheJave.ecommerce_system.service.ai;
 
import com.CongNgheJave.ecommerce_system.entity.Order;
import com.CongNgheJave.ecommerce_system.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
@Service
@RequiredArgsConstructor
public class OrderRealtimeChatService {
 
    private final OrderRepository orderRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
 
    public String getOrderList(Integer userId) {
        if (userId == null) {
            return "Bạn vui lòng đăng nhập để mình kiểm tra thông tin đơn hàng của bạn.";
        }
 
        List<Order> userOrders = orderRepository.findTop5ByUser_IdOrderByPlacedAtDesc(userId);
        if (userOrders.isEmpty()) {
            return "Dạ, hiện tại bạn chưa có đơn hàng nào trên hệ thống của ShoeStore.";
        }
 
        StringBuilder response = new StringBuilder("Dưới đây là danh sách các đơn hàng gần nhất của bạn:\n\n");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
 
        for (int i = 0; i < userOrders.size(); i++) {
            Order o = userOrders.get(i);
            String dateStr = o.getPlacedAt() != null ? o.getPlacedAt().format(DATE_FORMATTER) : "N/A";
            String totalStr = currencyFormat.format(o.getTotalAmount());
            response.append(String.format("%d. Mã đơn: **%s**\n" +
                    "   - Ngày đặt: %s\n" +
                    "   - Tổng tiền: %s\n" +
                    "   - Trạng thái đơn: **%s**\n" +
                    "   - Thanh toán: %s\n\n",
                    i + 1,
                    o.getOrderCode(),
                    dateStr,
                    totalStr,
                    translateStatus(o.getOrderStatus()),
                    translateStatus(o.getPaymentStatus())
            ));
        }
 
        return response.toString().trim();
    }
 
    public String getLatestOrder(Integer userId) {
        if (userId == null) {
            return "Bạn vui lòng đăng nhập để mình kiểm tra thông tin đơn hàng của bạn.";
        }
 
        List<Order> userOrders = orderRepository.findTop5ByUser_IdOrderByPlacedAtDesc(userId);
        if (userOrders.isEmpty()) {
            return "Dạ, hiện tại bạn chưa có đơn hàng nào trên hệ thống của ShoeStore.";
        }
 
        Order latestOrder = userOrders.get(0);
        return formatOrderDetails(latestOrder);
    }
 
    public String getOrderByCode(String message, Integer userId) {
        if (userId == null) {
            return "Bạn vui lòng đăng nhập để mình kiểm tra thông tin đơn hàng của bạn.";
        }
 
        String foundOrderCode = extractOrderCode(message);
        if (foundOrderCode == null) {
            // Check if any word starts with ORD- or similar
            String[] words = message.replaceAll("[^a-zA-Z0-9\\-]", " ").split("\\s+");
            for (String word : words) {
                if (word.toUpperCase().startsWith("ORD")) {
                    foundOrderCode = word.toUpperCase().trim();
                    break;
                }
            }
        }
 
        if (foundOrderCode == null) {
            return "Dạ, bạn vui lòng cung cấp mã đơn hàng đúng định dạng (ví dụ: ORD-123456) để mình tra cứu nhé.";
        }
 
        Optional<Order> orderOpt = orderRepository.findByOrderCodeAndUser_Id(foundOrderCode, userId);
        if (orderOpt.isPresent()) {
            return formatOrderDetails(orderOpt.get());
        } else {
            return "Mình không tìm thấy đơn hàng \"" + foundOrderCode + "\" trong tài khoản của bạn. Bạn vui lòng kiểm tra lại mã đơn hàng nhé.";
        }
    }
 
    private String extractOrderCode(String message) {
        Pattern pattern = Pattern.compile("(?i)\\b(ORD-\\w+)\\b");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1).toUpperCase();
        }
        Pattern patternSimple = Pattern.compile("(?i)\\b(ORD\\d+)\\b");
        Matcher matcherSimple = patternSimple.matcher(message);
        if (matcherSimple.find()) {
            return matcherSimple.group(1).toUpperCase();
        }
        return null;
    }
 
    private String formatOrderDetails(Order order) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String dateStr = order.getPlacedAt() != null ? order.getPlacedAt().format(DATE_FORMATTER) : "N/A";
        String totalStr = currencyFormat.format(order.getTotalAmount());
 
        // Mask recipient information for safety
        String maskedPhone = maskPhone(order.getShippingPhone());
        String maskedAddress = maskAddress(order.getShippingAddress());
        String maskedName = maskName(order.getShippingFullName());
 
        return String.format("Thông tin chi tiết về đơn hàng **%s** của bạn:\n\n" +
                "* **Ngày đặt hàng:** %s\n" +
                "* **Tổng số tiền:** %s\n" +
                "* **Phương thức thanh toán:** %s\n" +
                "* **Trạng thái thanh toán:** %s\n" +
                "* **Trạng thái vận chuyển:** **%s**\n" +
                "* **Người nhận:** %s - %s\n" +
                "* **Địa chỉ giao hàng:** %s\n" +
                "* **Ghi chú:** %s",
                order.getOrderCode(),
                dateStr,
                totalStr,
                order.getPaymentMethod(),
                translateStatus(order.getPaymentStatus()),
                translateStatus(order.getOrderStatus()),
                maskedName,
                maskedPhone,
                maskedAddress,
                (order.getNote() != null && !order.getNote().isEmpty()) ? order.getNote() : "Không có"
        );
    }
 
    private String maskName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "N/A";
        String name = fullName.trim();
        int lastSpace = name.lastIndexOf(' ');
        if (lastSpace == -1 || lastSpace == name.length() - 1) {
            if (name.length() <= 3) return name;
            return name.substring(0, 1) + "**";
        }
        return name.substring(0, lastSpace + 2) + "**";
    }
 
    private String maskPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return "N/A";
        String clean = phone.trim();
        if (clean.length() < 6) return "***";
        return clean.substring(0, 3) + "****" + clean.substring(clean.length() - 3);
    }
 
    private String maskAddress(String address) {
        if (address == null || address.trim().isEmpty()) return "N/A";
        String clean = address.trim();
        if (clean.length() < 12) return "***";
        return clean.substring(0, 10) + "... (đã ẩn địa chỉ chi tiết vì lý do bảo mật)";
    }
 
    private String translateStatus(String status) {
        if (status == null) return "Chưa rõ";
        switch (status.toUpperCase()) {
            case "PENDING": return "Đang chờ xử lý (Chờ thanh toán/xác nhận)";
            case "CONFIRMED": return "Đã xác nhận";
            case "SHIPPING": return "Đang giao hàng";
            case "COMPLETED": return "Đã hoàn thành";
            case "CANCELLED": return "Đã hủy đơn";
            case "PAID": return "Đã thanh toán";
            case "UNPAID": return "Chưa thanh toán";
            default: return status;
        }
    }
}
