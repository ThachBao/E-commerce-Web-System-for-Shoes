package com.CongNgheJave.ecommerce_system.service.ai;
 
import com.CongNgheJave.ecommerce_system.dto.ai.ChatContext;
import com.CongNgheJave.ecommerce_system.dto.ai.SuggestedProduct;
import com.CongNgheJave.ecommerce_system.entity.ChatIntent;
import com.CongNgheJave.ecommerce_system.entity.ProductVariant;
import com.CongNgheJave.ecommerce_system.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
 
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor
public class ProductRealtimeChatService {
 
    private final ProductVariantRepository productVariantRepository;
 
    @lombok.Getter
    @lombok.Setter
    @lombok.ToString
    public static class ProductSearchCriteria {
        private String keyword;
        private String brand;
        private String category;
        private String gender;
        private String size;
        private String color;
        private boolean askPrice;
        private boolean askStock;
        private boolean askDescription;
    }
 
    public String handleProductChat(ChatIntent intent, String userMessage, ChatContext context) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Mình chưa tìm thấy sản phẩm phù hợp với thông tin này. Bạn muốn tìm theo hãng Nike, Adidas, Converse hay theo loại Sneaker/Running ạ?";
        }
 
        // 1. Detect selected index (e.g. "số 5", "mẫu số 5")
        Integer selectedIndex = extractSelectedIndex(userMessage);
        SuggestedProduct targetProduct = null;
        if (selectedIndex != null) {
            List<SuggestedProduct> lastSuggested = context.getLastSuggestedProducts();
            if (lastSuggested == null || lastSuggested.isEmpty()) {
                return "Dạ, hiện tại trong cuộc hội thoại chưa có danh sách sản phẩm nào được gợi ý để bạn chọn ạ.";
            }
            if (selectedIndex < 1 || selectedIndex > lastSuggested.size()) {
                return String.format("Mình chỉ tìm thấy %d sản phẩm trong danh sách vừa rồi. Bạn vui lòng chọn số từ 1 đến %d nhé.",
                        lastSuggested.size(), lastSuggested.size());
            }
            SuggestedProduct selected = lastSuggested.get(selectedIndex - 1);
            context.setCurrentProduct(selected);
            targetProduct = selected;
 
            // If the user input was just the number/selection itself, default the intent to DETAIL
            if (isMessageJustNumberOrSelection(userMessage)) {
                intent = ChatIntent.PRODUCT_DETAIL;
            }
        }
 
        // 2. Identify target product for specific attributes
        if (targetProduct == null && refersToCurrentProduct(userMessage)) {
            targetProduct = context.getCurrentProduct();
            if (targetProduct == null) {
                return "Bạn đang muốn hỏi về sản phẩm nào ạ? Bạn có thể gửi tên giày hoặc chọn sản phẩm trong danh sách.";
            }
        }
 
        // 3. If no relative reference and no index, check if message has a specific product keyword
        if (targetProduct == null && intent != ChatIntent.PRODUCT_STOCK_RANKING && 
            intent != ChatIntent.PRODUCT_SEARCH && intent != ChatIntent.PRODUCT_RECOMMENDATION) {
            ProductSearchCriteria tempCriteria = new ProductSearchCriteria();
            tempCriteria.setColor(extractColor(userMessage));
            String keyword = extractKeyword(userMessage, tempCriteria);
            if (!keyword.isEmpty()) {
                List<ProductVariant> matchedVariants = productVariantRepository.findProductDetailByKeyword(keyword);
                if (!matchedVariants.isEmpty()) {
                    ProductVariant pv = matchedVariants.get(0);
                    targetProduct = SuggestedProduct.builder()
                            .productId(pv.getProduct().getId())
                            .name(pv.getProduct().getName())
                            .slug(pv.getProduct().getSlug())
                            .code(pv.getProduct().getCode())
                            .build();
                    context.setCurrentProduct(targetProduct); // promote to current active context
                }
            }
        }
 
        // 4. Route to specific logic
        switch (intent) {
            case PRODUCT_STOCK_RANKING:
                return handleStockRanking(context);
            case PRODUCT_DETAIL:
                return handleProductDetail(userMessage, targetProduct, context);
            case PRODUCT_SIZE:
                return handleProductSize(userMessage, targetProduct, context);
            case PRODUCT_PRICE:
                return handleProductPrice(userMessage, targetProduct, context);
            case PRODUCT_STOCK:
                return handleProductStock(userMessage, targetProduct, context);
            case PRODUCT_RECOMMENDATION:
            case PRODUCT_SEARCH:
            default:
                return handleProductSearchOrRecommend(intent, userMessage, context);
        }
    }
 
    private String handleStockRanking(ChatContext context) {
        List<ProductVariant> variants = productVariantRepository.findTopStockVariants(PageRequest.of(0, 5));
        if (variants.isEmpty()) {
            return "Hiện tại kho hàng của shop đã hết hàng hoặc chưa cập nhật số lượng tồn kho.";
        }
 
        context.setLastSuggestedProducts(convertToSuggestedProducts(variants));
 
        StringBuilder response = new StringBuilder("Một số sản phẩm đang còn tồn kho nhiều nhất hiện tại:\n\n");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
 
        for (int i = 0; i < variants.size(); i++) {
            ProductVariant pv = variants.get(i);
            String brandName = pv.getProduct().getBrand() != null ? pv.getProduct().getBrand().getName() : "Khác";
            String sizeName = pv.getSize() != null ? pv.getSize().getName() : "N/A";
            String colorName = pv.getColor() != null ? pv.getColor().getName() : "N/A";
            java.math.BigDecimal finalPrice = pv.getSalePrice() != null ? pv.getSalePrice() : pv.getPrice();
            String priceStr = currencyFormat.format(finalPrice);
 
            response.append(String.format("%d. %s - Size: %s - Màu: %s - Hãng: %s - Giá: %s - Tồn kho: %d đôi",
                    i + 1,
                    pv.getProduct().getName(),
                    sizeName,
                    colorName,
                    brandName,
                    priceStr,
                    pv.getStockQuantity()
            ));
            if (pv.getProduct().getSlug() != null && !pv.getProduct().getSlug().isEmpty()) {
                response.append(" | [Xem chi tiết](/products/").append(pv.getProduct().getSlug()).append(")");
            }
            response.append("\n");
        }
 
        response.append("\nBạn muốn mình lọc theo Nike, Adidas, Converse hay theo size không?");
        return response.toString().trim();
    }
 
    private String handleProductDetail(String userMessage, SuggestedProduct targetProduct, ChatContext context) {
        List<ProductVariant> variants;
        if (targetProduct != null) {
            variants = productVariantRepository.findByProductIdAndIsActiveTrue(targetProduct.getProductId());
        } else {
            ProductSearchCriteria criteria = new ProductSearchCriteria();
            criteria.setColor(extractColor(userMessage));
            String keyword = extractKeyword(userMessage, criteria);
            if (keyword.isEmpty()) {
                String brand = extractBrand(userMessage);
                if (brand != null) {
                    keyword = brand;
                } else {
                    return "Bạn vui lòng cung cấp tên sản phẩm cụ thể (ví dụ: Adidas Alphabounce 1) để mình tìm thông tin chi tiết giúp nhé.";
                }
            }
            variants = productVariantRepository.findProductDetailByKeyword(keyword);
        }
 
        if (variants.isEmpty()) {
            return "Mình chưa tìm thấy sản phẩm nào khớp với thông tin bạn yêu cầu. Bạn vui lòng kiểm tra lại tên giày nhé.";
        }
 
        ProductVariant pv = variants.get(0);
        SuggestedProduct suggested = SuggestedProduct.builder()
                .productId(pv.getProduct().getId())
                .name(pv.getProduct().getName())
                .slug(pv.getProduct().getSlug())
                .code(pv.getProduct().getCode())
                .build();
        context.setCurrentProduct(suggested);
 
        String name = pv.getProduct().getName();
        String brandName = pv.getProduct().getBrand() != null ? pv.getProduct().getBrand().getName() : "Khác";
        String catName = pv.getProduct().getCategory() != null ? pv.getProduct().getCategory().getName() : "Khác";
        String description = pv.getProduct().getDescription();
        if (description == null || description.trim().isEmpty()) {
            description = "Sản phẩm hiện chưa có mô tả chi tiết từ hãng, nhưng cực kỳ êm ái và thời trang.";
        }
 
        List<String> sizes = variants.stream()
                .map(v -> v.getSize() != null ? v.getSize().getName() : null)
                .filter(s -> s != null)
                .distinct()
                .collect(Collectors.toList());
 
        List<String> colors = variants.stream()
                .map(v -> v.getColor() != null ? v.getColor().getName() : null)
                .filter(c -> c != null)
                .distinct()
                .collect(Collectors.toList());
 
        int totalStock = variants.stream().mapToInt(ProductVariant::getStockQuantity).sum();
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        java.math.BigDecimal finalPrice = pv.getSalePrice() != null ? pv.getSalePrice() : pv.getPrice();
        String priceStr = currencyFormat.format(finalPrice);
 
        StringBuilder response = new StringBuilder();
        response.append(String.format("**%s** là mẫu giày thuộc nhóm %s của %s, %s.\n\n",
                name,
                catName,
                brandName,
                catName.equalsIgnoreCase("Running") ? "phù hợp đi bộ, chạy bộ nhẹ, tập luyện và mang hằng ngày" : "mang thiết kế thời trang, dễ phối đồ và cực kỳ êm chân"
        ));
        response.append(String.format("* **Size hiện có:** %s\n", sizes.isEmpty() ? "N/A" : String.join(", ", sizes)));
        response.append(String.format("* **Màu sắc:** %s\n", colors.isEmpty() ? "N/A" : String.join(", ", colors)));
        response.append(String.format("* **Giá bán:** %s\n", priceStr));
        response.append(String.format("* **Tồn kho:** %d đôi\n", totalStock));
        response.append(String.format("* **Mô tả:** %s", description));
 
        if (pv.getProduct().getSlug() != null && !pv.getProduct().getSlug().isEmpty()) {
            response.append("\n\n👉 [Xem chi tiết sản phẩm và đặt mua tại đây](/products/").append(pv.getProduct().getSlug()).append(")");
        }
 
        return response.toString().trim();
    }
 
    private String handleProductSize(String userMessage, SuggestedProduct targetProduct, ChatContext context) {
        List<ProductVariant> variants;
        if (targetProduct != null) {
            variants = productVariantRepository.findByProductIdAndIsActiveTrue(targetProduct.getProductId());
        } else {
            ProductSearchCriteria criteria = new ProductSearchCriteria();
            criteria.setColor(extractColor(userMessage));
            String keyword = extractKeyword(userMessage, criteria);
            if (keyword.isEmpty()) {
                return "Bạn vui lòng cung cấp tên sản phẩm cụ thể để mình kiểm tra size còn hàng giúp nhé.";
            }
            variants = productVariantRepository.findProductDetailByKeyword(keyword);
        }
 
        if (variants.isEmpty()) {
            return "Mình chưa tìm thấy sản phẩm nào khớp với thông tin bạn yêu cầu. Bạn vui lòng kiểm tra lại tên giày nhé.";
        }
 
        ProductVariant first = variants.get(0);
        SuggestedProduct suggested = SuggestedProduct.builder()
                .productId(first.getProduct().getId())
                .name(first.getProduct().getName())
                .slug(first.getProduct().getSlug())
                .code(first.getProduct().getCode())
                .build();
        context.setCurrentProduct(suggested);
 
        StringBuilder response = new StringBuilder(String.format("%s hiện còn các size sau:\n\n", first.getProduct().getName()));
        
        List<ProductVariant> activeVariants = variants.stream()
                .filter(v -> v.getStockQuantity() > 0)
                .collect(Collectors.toList());
 
        if (activeVariants.isEmpty()) {
            return String.format("Mẫu %s hiện tại đã hết hàng trên hệ thống rồi ạ.", first.getProduct().getName());
        }
 
        for (ProductVariant v : activeVariants) {
            String sizeName = v.getSize() != null ? v.getSize().getName() : "N/A";
            String colorName = v.getColor() != null ? v.getColor().getName() : "N/A";
            response.append(String.format("* Size %s - Màu %s - Còn %d đôi\n", sizeName, colorName, v.getStockQuantity()));
        }
 
        response.append("\nBạn muốn xem thêm mẫu khác không ạ?");
        return response.toString().trim();
    }
 
    private String handleProductPrice(String userMessage, SuggestedProduct targetProduct, ChatContext context) {
        List<ProductVariant> variants;
        if (targetProduct != null) {
            variants = productVariantRepository.findByProductIdAndIsActiveTrue(targetProduct.getProductId());
        } else {
            ProductSearchCriteria criteria = new ProductSearchCriteria();
            criteria.setColor(extractColor(userMessage));
            String keyword = extractKeyword(userMessage, criteria);
            if (keyword.isEmpty()) {
                return "Bạn vui lòng cung cấp tên sản phẩm cụ thể để mình báo giá giúp nhé.";
            }
            variants = productVariantRepository.findProductDetailByKeyword(keyword);
        }
 
        if (variants.isEmpty()) {
            return "Mình chưa tìm thấy sản phẩm nào khớp với thông tin bạn yêu cầu để báo giá.";
        }
 
        ProductVariant first = variants.get(0);
        SuggestedProduct suggested = SuggestedProduct.builder()
                .productId(first.getProduct().getId())
                .name(first.getProduct().getName())
                .slug(first.getProduct().getSlug())
                .code(first.getProduct().getCode())
                .build();
        context.setCurrentProduct(suggested);
 
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        java.math.BigDecimal finalPrice = first.getSalePrice() != null ? first.getSalePrice() : first.getPrice();
        String priceStr = currencyFormat.format(finalPrice);
 
        return String.format("Mẫu **%s** hiện đang có giá bán là **%s** ạ. Bạn có muốn kiểm tra các size còn hàng không?",
                first.getProduct().getName(), priceStr);
    }
 
    private String handleProductStock(String userMessage, SuggestedProduct targetProduct, ChatContext context) {
        List<ProductVariant> variants;
        if (targetProduct != null) {
            variants = productVariantRepository.findByProductIdAndIsActiveTrue(targetProduct.getProductId());
        } else {
            ProductSearchCriteria criteria = new ProductSearchCriteria();
            criteria.setColor(extractColor(userMessage));
            String keyword = extractKeyword(userMessage, criteria);
            if (keyword.isEmpty()) {
                return "Bạn vui lòng cung cấp tên sản phẩm cụ thể để mình kiểm tra tồn kho giúp nhé.";
            }
            variants = productVariantRepository.findProductDetailByKeyword(keyword);
        }
 
        if (variants.isEmpty()) {
            return "Mình chưa tìm thấy sản phẩm nào khớp với thông tin bạn yêu cầu để kiểm tra kho.";
        }
 
        ProductVariant first = variants.get(0);
        SuggestedProduct suggested = SuggestedProduct.builder()
                .productId(first.getProduct().getId())
                .name(first.getProduct().getName())
                .slug(first.getProduct().getSlug())
                .code(first.getProduct().getCode())
                .build();
        context.setCurrentProduct(suggested);
 
        int totalStock = variants.stream().mapToInt(ProductVariant::getStockQuantity).sum();
 
        return String.format("Mẫu **%s** hiện còn tổng cộng **%d** đôi trong kho ạ. Bạn có cần hỗ trợ xem chi tiết size còn hàng không?",
                first.getProduct().getName(), totalStock);
    }
 
    private String handleProductSearchOrRecommend(ChatIntent intent, String userMessage, ChatContext context) {
        String msgLower = userMessage.toLowerCase().trim();
 
        if (msgLower.contains("nổi bật") || msgLower.contains("noi bat") || msgLower.contains("featured")) {
            return handleFeaturedProducts(context);
        }
 
        if (msgLower.contains("hot") || msgLower.contains("bán chạy") || msgLower.contains("ban chay") || msgLower.contains("mua nhiều") || msgLower.contains("mua nhieu")) {
            return handleBestSellingProducts(context);
        }
 
        ProductSearchCriteria criteria = new ProductSearchCriteria();
        criteria.setBrand(extractBrand(msgLower));
        criteria.setCategory(extractCategory(msgLower));
        criteria.setGender(extractGender(msgLower));
        criteria.setSize(extractSize(msgLower));
        criteria.setColor(extractColor(msgLower));
        criteria.setKeyword(extractKeyword(msgLower, criteria));
 
        String searchKeyword = criteria.getKeyword();
        if (searchKeyword.isEmpty()) {
            searchKeyword = null;
        }
 
        List<ProductVariant> variants = productVariantRepository.searchByCriteria(
                criteria.getBrand(),
                criteria.getCategory(),
                criteria.getGender(),
                criteria.getSize(),
                criteria.getColor(),
                searchKeyword,
                PageRequest.of(0, 5)
        );
 
        if (variants.isEmpty()) {
            return "Mình chưa tìm thấy sản phẩm phù hợp với thông tin này. Bạn muốn tìm theo hãng Nike, Adidas, Converse hay theo loại Sneaker/Running ạ?";
        }
 
        context.setLastSuggestedProducts(convertToSuggestedProducts(variants));
 
        StringBuilder response = new StringBuilder();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
 
        String categoryStr = criteria.getCategory() != null ? criteria.getCategory() : "";
        String brandStr = criteria.getBrand() != null ? criteria.getBrand() : "";
        String genderStr = criteria.getGender() != null ? (criteria.getGender().equals("WOMEN") ? "dành cho nữ" : (criteria.getGender().equals("MEN") ? "dành cho nam" : "unisex")) : "";
        
        String filterHeader = String.join(" ", brandStr, categoryStr, genderStr).replaceAll("\\s+", " ").trim();
        if (filterHeader.isEmpty()) {
            filterHeader = "sản phẩm";
        }
 
        if (intent == ChatIntent.PRODUCT_RECOMMENDATION) {
            response.append(String.format("Mình gợi ý cho bạn một số mẫu %s phù hợp:\n\n", filterHeader));
        } else {
            response.append(String.format("Mình tìm thấy một số mẫu %s hiện có trên hệ thống:\n\n", filterHeader));
        }
 
        for (int i = 0; i < variants.size(); i++) {
            ProductVariant pv = variants.get(i);
            String brandName = pv.getProduct().getBrand() != null ? pv.getProduct().getBrand().getName() : "Khác";
            String sizeName = pv.getSize() != null ? pv.getSize().getName() : "N/A";
            String colorName = pv.getColor() != null ? pv.getColor().getName() : "N/A";
            java.math.BigDecimal finalPrice = pv.getSalePrice() != null ? pv.getSalePrice() : pv.getPrice();
            String priceStr = currencyFormat.format(finalPrice);
            String stockStatus = pv.getStockQuantity() > 0 ? "Còn " + pv.getStockQuantity() + " đôi" : "Hết hàng";
 
            response.append(String.format("%d. %s - Size: %s - Màu: %s - Giá: %s - %s",
                    i + 1,
                    pv.getProduct().getName(),
                    sizeName,
                    colorName,
                    priceStr,
                    stockStatus
            ));
 
            if (pv.getProduct().getSlug() != null && !pv.getProduct().getSlug().isEmpty()) {
                response.append(" | [Xem chi tiết](/products/").append(pv.getProduct().getSlug()).append(")");
            }
            response.append("\n");
        }
 
        response.append("\n");
        if (intent == ChatIntent.PRODUCT_RECOMMENDATION) {
            response.append("Các mẫu này phù hợp đi bộ, chạy bộ nhẹ hoặc mang hằng ngày. Bạn đang đi size bao nhiêu để mình lọc chính xác hơn?");
        } else {
            response.append("Bạn muốn xem chi tiết mẫu số mấy ạ?");
        }
 
        return response.toString().trim();
    }

    private List<ProductVariant> filterToDistinctProducts(List<ProductVariant> variants, int limit) {
        java.util.Map<Integer, ProductVariant> productToVariantMap = new java.util.LinkedHashMap<>();
        for (ProductVariant v : variants) {
            if (v.getProduct() != null && v.getProduct().getIsActive()) {
                productToVariantMap.merge(v.getProduct().getId(), v, (oldVal, newVal) -> {
                    if (oldVal.getStockQuantity() <= 0 && newVal.getStockQuantity() > 0) {
                        return newVal;
                    }
                    return oldVal;
                });
            }
        }
        return productToVariantMap.values().stream().limit(limit).collect(Collectors.toList());
    }

    private String handleFeaturedProducts(ChatContext context) {
        List<ProductVariant> allFeatured = productVariantRepository.findFeaturedVariants();
        List<ProductVariant> representativeVariants = filterToDistinctProducts(allFeatured, 5);

        if (representativeVariants.isEmpty()) {
            return "Dạ, hiện tại shop chưa cập nhật sản phẩm nổi bật nào ạ. Bạn có muốn xem các mẫu bán chạy nhất không?";
        }

        context.setLastSuggestedProducts(convertToSuggestedProducts(representativeVariants));

        StringBuilder response = new StringBuilder("Dạ, đây là các sản phẩm nổi bật đang được quan tâm nhất tại shop:\n\n");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (int i = 0; i < representativeVariants.size(); i++) {
            ProductVariant pv = representativeVariants.get(i);
            String brandName = pv.getProduct().getBrand() != null ? pv.getProduct().getBrand().getName() : "Khác";
            java.math.BigDecimal finalPrice = pv.getSalePrice() != null ? pv.getSalePrice() : pv.getPrice();
            String priceStr = currencyFormat.format(finalPrice);

            response.append(String.format("%d. **%s** - Hãng: %s - Giá: %s",
                    i + 1,
                    pv.getProduct().getName(),
                    brandName,
                    priceStr
            ));

            if (pv.getProduct().getSlug() != null && !pv.getProduct().getSlug().isEmpty()) {
                response.append(" | [Xem chi tiết](/products/").append(pv.getProduct().getSlug()).append(")");
            }
            response.append("\n");
        }

        response.append("\nBạn có thể chọn xem chi tiết bằng cách gõ số (ví dụ: `1`, `2`) nhé!");
        return response.toString().trim();
    }

    private String handleBestSellingProducts(ChatContext context) {
        List<ProductVariant> allBestSelling = productVariantRepository.findBestSellingVariants();
        List<ProductVariant> representativeVariants = filterToDistinctProducts(allBestSelling, 5);

        if (representativeVariants.isEmpty()) {
            return "Dạ, hiện tại shop chưa có dữ liệu bán chạy. Bạn có muốn tham khảo các sản phẩm nổi bật không?";
        }

        context.setLastSuggestedProducts(convertToSuggestedProducts(representativeVariants));

        StringBuilder response = new StringBuilder("Dạ, đây là danh sách các sản phẩm bán chạy nhất (hot nhất) của shop:\n\n");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (int i = 0; i < representativeVariants.size(); i++) {
            ProductVariant pv = representativeVariants.get(i);
            String brandName = pv.getProduct().getBrand() != null ? pv.getProduct().getBrand().getName() : "Khác";
            java.math.BigDecimal finalPrice = pv.getSalePrice() != null ? pv.getSalePrice() : pv.getPrice();
            String priceStr = currencyFormat.format(finalPrice);

            response.append(String.format("%d. **%s** - Hãng: %s - Giá: %s",
                    i + 1,
                    pv.getProduct().getName(),
                    brandName,
                    priceStr
            ));

            if (pv.getProduct().getSlug() != null && !pv.getProduct().getSlug().isEmpty()) {
                response.append(" | [Xem chi tiết](/products/").append(pv.getProduct().getSlug()).append(")");
            }
            response.append("\n");
        }

        response.append("\nBạn muốn xem chi tiết hoặc tư vấn size cho sản phẩm số mấy ạ?");
        return response.toString().trim();
    }

    private List<SuggestedProduct> convertToSuggestedProducts(List<ProductVariant> variants) {
        java.util.Map<Integer, SuggestedProduct> map = new java.util.LinkedHashMap<>();
        for (ProductVariant pv : variants) {
            if (pv.getProduct() != null) {
                map.putIfAbsent(pv.getProduct().getId(), SuggestedProduct.builder()
                        .productId(pv.getProduct().getId())
                        .name(pv.getProduct().getName())
                        .slug(pv.getProduct().getSlug())
                        .code(pv.getProduct().getCode())
                        .build());
            }
        }
        return new java.util.ArrayList<>(map.values());
    }
 
    private Integer extractSelectedIndex(String msg) {
        Pattern pattern = Pattern.compile("(?i)\\b(giày số|mẫu số|sản phẩm số|số|thứ|sản phẩm thứ)\\s*([1-9])\\b");
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(2));
        }
        Pattern digitPattern = Pattern.compile("^\\s*([1-9])\\s*$");
        Matcher digitMatcher = digitPattern.matcher(msg);
        if (digitMatcher.find()) {
            return Integer.parseInt(digitMatcher.group(1));
        }
        return null;
    }
 
    private boolean isMessageJustNumberOrSelection(String msg) {
        String clean = msg.toLowerCase().trim();
        return clean.matches("^(mẫu|số|giày|sản phẩm|thứ)?\\s*([1-9])$") ||
               clean.matches("^([1-9])$");
    }
 
    private boolean refersToCurrentProduct(String msg) {
        String lower = msg.toLowerCase();
        return lower.contains("giày đó") || lower.contains("đôi đó") || 
               lower.contains("mẫu đó") || lower.contains("sản phẩm đó") || 
               lower.contains("nó") || lower.contains("mẫu vừa gửi") || 
               lower.contains("giày này") || lower.contains("đôi này") ||
               lower.contains("mẫu này") || lower.contains("sản phẩm này");
    }
 
    private String extractSize(String msg) {
        Pattern sizePattern = Pattern.compile("(?i)\\b(size|sz|số)\\s*[:\\-]?\\s*([3-4]\\d)\\b");
        Matcher matcher = sizePattern.matcher(msg);
        if (matcher.find()) {
            return matcher.group(2);
        }
        if (msg.contains("size") || msg.contains("sz") || msg.contains("giày") || msg.contains("tìm")) {
            Pattern numPattern = Pattern.compile("\\b(3[6-9]|4[0-5])\\b");
            Matcher numMatcher = numPattern.matcher(msg);
            if (numMatcher.find()) {
                return numMatcher.group(1);
            }
        }
        return null;
    }
 
    private String extractBrand(String msg) {
        if (msg.contains("nike")) return "Nike";
        if (msg.contains("adidas")) return "Adidas";
        if (msg.contains("converse")) return "Converse";
        return null;
    }
 
    private String extractCategory(String msg) {
        if (msg.contains("sneaker")) return "Sneaker";
        if (msg.contains("running") || msg.contains("chạy bộ") || msg.contains("chạy")) return "Running";
        if (msg.contains("boots") || msg.contains("boot")) return "Boots";
        if (msg.contains("sandal") || msg.contains("sandals")) return "Sandal";
        return null;
    }
 
    private String extractGender(String msg) {
        if (msg.contains("unisex")) return "UNISEX";
        if (msg.contains("nữ") || msg.contains("women") || msg.contains("female")) return "WOMEN";
        if (msg.contains("nam") || msg.contains("men") || msg.contains("male")) return "MEN";
        return null;
    }
 
    private String extractColor(String msg) {
        String[] colors = {"đen", "trắng", "đỏ", "xanh", "vàng", "hồng", "xám", "grey", "black", "white", "red", "blue"};
        for (String c : colors) {
            if (msg.contains("màu " + c) || msg.contains(" " + c + " ")) {
                return c;
            }
        }
        return null;
    }
 
    private String extractKeyword(String msg, ProductSearchCriteria criteria) {
        String cleaned = msg.toLowerCase();
 
        cleaned = cleaned.replaceAll("(?i)\\b(size|sz|số)\\s*[:\\-]?\\s*([3-4]\\d)\\b", "");
        cleaned = cleaned.replaceAll("\\b(3[6-9]|4[0-5])\\b", "");
 
        cleaned = cleaned.replace("nike", "");
        cleaned = cleaned.replace("adidas", "");
        cleaned = cleaned.replace("converse", "");
 
        cleaned = cleaned.replace("sneaker", "");
        cleaned = cleaned.replace("running", "");
        cleaned = cleaned.replace("chạy bộ", "");
        cleaned = cleaned.replace("chạy", "");
        cleaned = cleaned.replace("boots", "");
        cleaned = cleaned.replace("boot", "");
        cleaned = cleaned.replace("sandal", "");
        cleaned = cleaned.replace("sandals", "");
 
        cleaned = cleaned.replace("nam", "");
        cleaned = cleaned.replace("nữ", "");
        cleaned = cleaned.replace("women", "");
        cleaned = cleaned.replace("men", "");
        cleaned = cleaned.replace("unisex", "");
 
        if (criteria.getColor() != null) {
            cleaned = cleaned.replace("màu " + criteria.getColor(), "");
            cleaned = cleaned.replace(criteria.getColor(), "");
        }
 
        String[] conversationalNoise = {
                "tới", "tôi muốn", "cho tôi", "gợi ý", "tư vấn", "tìm kiếm", "tìm", "mô tả", "chi tiết", "thông tin",
                "những", "các loại", "các", "loại", "giày", "đôi", "cần", "muốn", "lựa", "chọn", "xem", "hỏi",
                "shop", "bạn", "nhé", "nha", "ạ", "với", "hãy", "giùm", "được", "không", "này", "kia", "đó",
                "mẫu", "hiện có", "có", "giá", "bao nhiêu", "tiền", "mấy", "còn hàng", "còn", "con", "tồn kho",
                "size", "sz", "số", "nào", "đâu", "ở", "thứ", "theo", "cho", "của", "về", "để", "trong", "tại",
                "từ", "đến", "bởi", "giúp", "hộ", "mình", "anh", "chị", "em", "nhất", "hơn", "là", "thì",
                "dành", "hãng", "hiệu", "thương hiệu"
        };
        for (String word : conversationalNoise) {
            cleaned = cleaned.replaceAll("(?i)\\b" + Pattern.quote(word) + "\\b", " ");
            cleaned = cleaned.replace(word, " ");
        }
 
        cleaned = cleaned.replaceAll("[\\?\\.,;:\\!\\-\"']", " ");
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        return cleaned;
    }
}
