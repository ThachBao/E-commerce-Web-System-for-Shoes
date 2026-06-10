package com.CongNgheJave.ecommerce_system.service.ai;
 
import com.CongNgheJave.ecommerce_system.entity.ChatIntent;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;
 
@Service
public class ChatIntentService {
 
    public ChatIntent determineIntent(String message, boolean isAuthenticated) {
        if (message == null || message.trim().isEmpty()) {
            return ChatIntent.UNKNOWN;
        }
 
        String msgLower = message.toLowerCase().trim();
 
        // 0. Index selection priority
        Pattern indexPattern = Pattern.compile("(?i)\\b(giày số|mẫu số|sản phẩm số|mẫu|số|thứ|sản phẩm thứ)\\s*([1-9])\\b");
        if (indexPattern.matcher(msgLower).find()) {
            return ChatIntent.PRODUCT_DETAIL;
        }
        if (msgLower.matches("^\\s*[1-9]\\s*$")) {
            return ChatIntent.PRODUCT_DETAIL;
        }
 
        // 1. ORDER priority
        boolean hasOrderKeywords = false;
        String[] orderKeywords = {
                "đơn hàng của tôi", "đơn của tôi", "lịch sử đơn", "lịch sử mua", 
                "đơn gần nhất", "đơn mới nhất", "đơn vừa đặt", "trạng thái đơn"
        };
        for (String kw : orderKeywords) {
            if (msgLower.contains(kw)) {
                hasOrderKeywords = true;
                break;
            }
        }
        if (!hasOrderKeywords) {
            // Check for order code patterns like ORD-123456 or ORD123
            Pattern pattern = Pattern.compile("(?i)\\b(ord-\\w+)\\b|(?i)\\b(ord\\d+)\\b");
            if (pattern.matcher(msgLower).find()) {
                hasOrderKeywords = true;
            }
        }
 
        if (hasOrderKeywords) {
            if (!isAuthenticated) {
                return ChatIntent.AUTH_REQUIRED;
            }
            if (msgLower.contains("gần nhất") || msgLower.contains("mới nhất") || msgLower.contains("vừa đặt")) {
                return ChatIntent.ORDER_LATEST;
            }
            Pattern codePattern = Pattern.compile("(?i)\\b(ord-\\w+)\\b|(?i)\\b(ord\\d+)\\b");
            if (codePattern.matcher(msgLower).find()) {
                return ChatIntent.ORDER_BY_CODE;
            }
            return ChatIntent.ORDER_LIST;
        }
 
        // 2. SIZE GUIDE RAG priority
        String[] sizeGuideKeywords = {
                "hướng dẫn chọn size", "cách chọn size", "chọn size như thế nào", "tư vấn size", "nên chọn size"
        };
        for (String kw : sizeGuideKeywords) {
            if (msgLower.contains(kw)) {
                return ChatIntent.SIZE_GUIDE_RAG;
            }
        }
 
        // 3. POLICY RAG priority
        String[] policyKeywords = {
                "đổi trả", "đổi size", "trả hàng", "bảo hành", "giao hàng", "thanh toán", "cod", 
                "chuyển khoản", "vietqr", "quên mật khẩu", "đăng nhập", "đăng ký", "hủy đơn"
        };
        for (String kw : policyKeywords) {
            if (msgLower.contains(kw)) {
                return ChatIntent.POLICY_RAG;
            }
        }
 
        // 4. PRODUCT STOCK RANKING priority
        String[] stockRankingKeywords = {
                "tồn kho nhiều nhất", "còn nhiều nhất", "sản phẩm còn nhiều", "giày còn nhiều"
        };
        for (String kw : stockRankingKeywords) {
            if (msgLower.contains(kw)) {
                return ChatIntent.PRODUCT_STOCK_RANKING;
            }
        }
 
        // 5. PRODUCT_SIZE priority
        String[] sizeKeywords = {
                "size nào", "có size", "còn size", "size của", "giày đó có những size nào", "size bao nhiêu"
        };
        for (String kw : sizeKeywords) {
            if (msgLower.contains(kw)) {
                return ChatIntent.PRODUCT_SIZE;
            }
        }
        // Match numbers 36-45 when combined with size terms
        if (msgLower.contains("size") || msgLower.contains("sz") || msgLower.contains("số")) {
            Pattern numPattern = Pattern.compile("\\b(3[6-9]|4[0-5])\\b");
            if (numPattern.matcher(msgLower).find()) {
                return ChatIntent.PRODUCT_SIZE;
            }
        }
 
        // 6. PRODUCT_PRICE priority
        String[] priceKeywords = {
                "giá bao nhiêu", "giá", "giá bán", "bao nhiêu tiền", "nhiêu tiền", "mấy tiền"
        };
        for (String kw : priceKeywords) {
            if (msgLower.contains(kw)) {
                return ChatIntent.PRODUCT_PRICE;
            }
        }
 
        // 7. PRODUCT_STOCK priority
        String[] stockKeywords = {
                "tồn kho", "còn hàng", "hết hàng", "còn không", "còn đôi nào"
        };
        for (String kw : stockKeywords) {
            if (msgLower.contains(kw)) {
                return ChatIntent.PRODUCT_STOCK;
            }
        }
 
        // 8. PRODUCT RECOMMENDATION priority
        String[] recommendationKeywords = {
                "gợi ý", "tư vấn", "nên mua", "nên chọn", "muốn lựa", "phù hợp",
                "nổi bật", "noi bat", "hot", "bán chạy", "ban chay", "mua nhiều", "mua nhieu", "ưa thích", "bán chạy nhất"
        };
        for (String kw : recommendationKeywords) {
            if (msgLower.contains(kw)) {
                return ChatIntent.PRODUCT_RECOMMENDATION;
            }
        }
 
        // 9. PRODUCT DETAIL priority
        String[] detailKeywords = {
                "mô tả", "chi tiết", "thông tin giày", "giày này", "sản phẩm này"
        };
        for (String kw : detailKeywords) {
            if (msgLower.contains(kw)) {
                return ChatIntent.PRODUCT_DETAIL;
            }
        }
 
        // 10. PRODUCT SEARCH priority
        String[] searchKeywords = {
                "nike", "adidas", "converse", "sneaker", "running", "boots", "sandal", 
                "giày nam", "giày nữ", "giày unisex", "tìm giày", "các loại giày", 
                "shop có giày"
        };
        for (String kw : searchKeywords) {
            if (msgLower.contains(kw)) {
                return ChatIntent.PRODUCT_SEARCH;
            }
        }
 
        // 11. SMALL TALK priority
        String[] smallTalkKeywords = { "xin chào", "hello", "shop ơi", "hi" };
        for (String kw : smallTalkKeywords) {
            if (msgLower.contains(kw)) {
                return ChatIntent.SMALL_TALK;
            }
        }
 
        return ChatIntent.UNKNOWN;
    }
}
