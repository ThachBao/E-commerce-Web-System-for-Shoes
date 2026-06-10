package com.CongNgheJave.ecommerce_system.service.ai;
 
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
 
import java.util.*;
import lombok.extern.slf4j.Slf4j;
 
@Service
@Slf4j
public class CloudflareAiSearchService {
 
    @Value("${cloudflare.account-id}")
    private String accountId;
 
    @Value("${cloudflare.ai-search-instance}")
    private String instanceName;
 
    @Value("${cloudflare.api-token}")
    private String apiToken;
 
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();
 
    public String searchAndAnswer(String userMessage) {
        String url = String.format("https://api.cloudflare.com/client/v4/accounts/%s/ai-search/instances/%s/chat/completions",
                accountId, instanceName);
 
        String systemPrompt = "Bạn là nhân viên tư vấn của ShoeStore, hỗ trợ khách hàng chọn size, tìm sản phẩm và giải đáp thắc mắc.\n\n" +
                "Quy tắc trả lời bắt buộc:\n" +
                "1. Trả lời ngắn gọn, rõ ràng, tự nhiên như nhân viên shop.\n" +
                "2. Không bao giờ nói các câu như: \"theo tài liệu\", \"trong PDF\", \"theo nguồn\", \"trong dữ liệu RAG\".\n" +
                "3. Nếu câu hỏi có thông tin cố định trong kho tri thức thì phải trả lời trực tiếp bằng đúng thông tin đó, không trả lời chung chung.\n" +
                "4. Nếu khách hỏi thông tin liên hệ, liên lạc, số điện thoại, email, địa chỉ, hotline hoặc cách liên hệ shop thì phải trả lời đầy đủ:\n" +
                "   Địa chỉ: 140 Lê Trọng Tấn, Phường Tây Thạnh, TP.HCM.\n" +
                "   Số điện thoại hỗ trợ: 0373792910.\n" +
                "   Email hỗ trợ: thachbao2910@gmail.com.\n" +
                "5. Không được thay thế thông tin liên hệ cụ thể bằng câu chung chung như \"liên hệ nhân viên hỗ trợ\", \"liên hệ qua trang web\", \"shop luôn sẵn sàng hỗ trợ\".\n" +
                "6. Nếu khách hỏi đổi trả, tài khoản bị khóa, đơn đang giao muốn hủy, thanh toán lỗi hoặc cần kiểm tra đơn cụ thể thì hướng dẫn khách liên hệ qua số điện thoại 0373792910 hoặc email thachbao2910@gmail.com ở trên.\n" +
                "7. Nếu câu hỏi liên quan sản phẩm, giá, tồn kho, size còn hàng, màu còn hàng, mô tả sản phẩm hoặc trạng thái đơn hàng cụ thể thì phải dùng dữ liệu MySQL/backend realtime, không tự bịa từ RAG.\n" +
                "8. Nếu không có thông tin trong kho tri thức, hãy trả lời: “Hiện tại shop chưa có thông tin này. Bạn vui lòng liên hệ hotline: 0373792910 hoặc email: thachbao2910@gmail.com để được hỗ trợ nhé.”";
 
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, String>> messages = new ArrayList<>();
 
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);
 
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
 
        requestBody.put("messages", messages);
 
        try {
            String responseBody = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiToken)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
 
            if (responseBody != null) {
                return cleanOutput(parseAnswer(responseBody));
            }
        } catch (Exception e) {
            log.error("Error calling Cloudflare AI Search: ", e);
            return "Hiện tại chatbot chưa lấy được thông tin hỗ trợ, bạn vui lòng thử lại sau.";
        }
 
        return "Hiện tại shop chưa có thông tin này, bạn vui lòng liên hệ hotline: 0373792910 hoặc email: thachbao2910@gmail.com để được hỗ trợ.";
    }
 
    private String parseAnswer(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            // Try OpenAI format: choices[0].message.content
            if (root.has("choices") && root.get("choices").isArray() && root.get("choices").size() > 0) {
                JsonNode messageNode = root.get("choices").get(0).get("message");
                if (messageNode != null && messageNode.has("content")) {
                    return messageNode.get("content").asText();
                }
            }
 
            // Try Cloudflare alternative format: result.response or result.choices
            if (root.has("result")) {
                JsonNode result = root.get("result");
                if (result.has("response")) {
                    return result.get("response").asText();
                }
                if (result.has("choices") && result.get("choices").isArray() && result.get("choices").size() > 0) {
                    JsonNode messageNode = result.get("choices").get(0).get("message");
                    if (messageNode != null && messageNode.has("content")) {
                        return messageNode.get("content").asText();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing Cloudflare response: ", e);
        }
        return "Hiện tại chatbot chưa lấy được thông tin hỗ trợ, bạn vui lòng thử lại sau.";
    }
 
    private String cleanOutput(String text) {
        if (text == null) return "";
        
        // Clean phrases like "theo tài liệu", "theo file", "source", file extensions, chunk, etc. Case insensitive.
        String cleaned = text
                .replaceAll("(?i)theo tài liệu( cung cấp| này| tham khảo| của shop| học| đối chiếu)?", "")
                .replaceAll("(?i)theo file( pdf| txt)?", "")
                .replaceAll("(?i)source( code| file| data|\\s*\\d+)?", "")
                .replaceAll("(?i)chunk(s|\\s*\\d+)?", "")
                .replaceAll("(?i)\\b\\w+\\.(pdf|txt)\\b", "")
                .replaceAll("(?i)tài liệu cung cấp", "")
                .replaceAll("(?i)dựa trên tài liệu", "")
                .replaceAll("(?i)trong tài liệu", "")
                .replaceAll("(?i)kho tri thức", "");
 
        // Keep spaces clean but preserve newlines
        cleaned = cleaned.replaceAll("[ \\t\\x0B\\f]+", " ");
        cleaned = cleaned.replaceAll("(\\r?\\n){3,}", "\n\n").trim();
        
        if (cleaned.startsWith(",") || cleaned.startsWith(".")) {
            cleaned = cleaned.substring(1).trim();
        }

        return cleaned.isEmpty() ? "Hiện tại shop chưa có thông tin này. Bạn vui lòng liên hệ hotline: 0373792910 hoặc email: thachbao2910@gmail.com để được hỗ trợ nhé." : cleaned;
    }
}
