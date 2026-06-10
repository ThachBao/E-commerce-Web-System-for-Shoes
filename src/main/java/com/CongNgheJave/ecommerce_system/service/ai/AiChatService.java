package com.CongNgheJave.ecommerce_system.service.ai;
 
import com.CongNgheJave.ecommerce_system.dto.ai.ChatContext;
import com.CongNgheJave.ecommerce_system.dto.request.ChatbotRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ChatbotResponse;
import com.CongNgheJave.ecommerce_system.entity.AppUser;
import com.CongNgheJave.ecommerce_system.entity.ChatConversation;
import com.CongNgheJave.ecommerce_system.entity.ChatMessage;
import com.CongNgheJave.ecommerce_system.entity.ChatIntent;
import com.CongNgheJave.ecommerce_system.repository.ChatConversationRepository;
import com.CongNgheJave.ecommerce_system.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.Optional;
 
@Service
@RequiredArgsConstructor
public class AiChatService {
 
    private final ChatAuthContextService authContextService;
    private final ChatIntentService intentService;
    private final CloudflareAiSearchService cloudflareAiSearchService;
    private final ProductRealtimeChatService productRealtimeChatService;
    private final OrderRealtimeChatService orderRealtimeChatService;
    private final ChatContextService chatContextService;
    
    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
 
    @Transactional
    public ChatbotResponse processChatMessage(ChatbotRequest request) {
        String userMsg = request.getMessage();
        
        // 1. Validate empty message
        if (userMsg == null || userMsg.trim().isEmpty()) {
            return ChatbotResponse.builder()
                    .answer("Dạ, nội dung tin nhắn không được để trống ạ.")
                    .intent(ChatIntent.UNKNOWN.name())
                    .build();
        }
 
        // 2. Validate max 500 characters
        if (userMsg.length() > 500) {
            return ChatbotResponse.builder()
                    .answer("Dạ, tin nhắn của bạn quá dài (tối đa 500 ký tự). Bạn vui lòng rút ngắn câu hỏi để mình hỗ trợ tốt nhất nhé.")
                    .intent(ChatIntent.UNKNOWN.name())
                    .build();
        }
 
        // Get authenticated user (if any)
        Optional<AppUser> currentUserOpt = authContextService.getCurrentUser();
        boolean isAuthenticated = currentUserOpt.isPresent();
 
        // Classify intent
        ChatIntent intent = intentService.determineIntent(userMsg, isAuthenticated);
 
        // Get or create context by sessionId
        String sessId = request.getSessionId();
        if (sessId == null || sessId.trim().isEmpty()) {
            if (request.getConversationId() != null) {
                sessId = "conv_" + request.getConversationId();
            } else {
                sessId = "temp_default";
            }
        }
        ChatContext context = chatContextService.getContext(sessId);
 
        String answer;
        
        // Security check for configuration leaks
        if (isInternalInquiry(userMsg)) {
            answer = "Dạ, là một trợ lý ảo hỗ trợ bán hàng, mình chỉ có thể cung cấp các thông tin liên quan đến sản phẩm, chính sách bán hàng và thông tin đơn hàng cá nhân của bạn thôi ạ. Rất mong bạn thông cảm!";
            intent = ChatIntent.POLICY_RAG;
        } else if (intent == ChatIntent.SMALL_TALK) {
            answer = "Dạ, ShoeStore xin chào bạn! Mình có thể hỗ trợ gì cho bạn về sản phẩm, size giày, hoặc các chính sách đổi trả, giao nhận không ạ?";
        } else if (intent == ChatIntent.AUTH_REQUIRED) {
            answer = "Bạn vui lòng đăng nhập để mình kiểm tra thông tin đơn hàng của bạn.";
        } else if (intent == ChatIntent.ORDER_LIST) {
            AppUser currentUser = currentUserOpt.get();
            answer = orderRealtimeChatService.getOrderList(currentUser.getId());
        } else if (intent == ChatIntent.ORDER_LATEST) {
            AppUser currentUser = currentUserOpt.get();
            answer = orderRealtimeChatService.getLatestOrder(currentUser.getId());
        } else if (intent == ChatIntent.ORDER_BY_CODE) {
            AppUser currentUser = currentUserOpt.get();
            answer = orderRealtimeChatService.getOrderByCode(userMsg, currentUser.getId());
        } else if (intent == ChatIntent.PRODUCT_SEARCH || intent == ChatIntent.PRODUCT_RECOMMENDATION ||
                   intent == ChatIntent.PRODUCT_DETAIL || intent == ChatIntent.PRODUCT_STOCK_RANKING ||
                   intent == ChatIntent.PRODUCT_SIZE || intent == ChatIntent.PRODUCT_PRICE ||
                   intent == ChatIntent.PRODUCT_STOCK) {
            answer = productRealtimeChatService.handleProductChat(intent, userMsg, context);
        } else if (intent == ChatIntent.SIZE_GUIDE_RAG || intent == ChatIntent.POLICY_RAG) {
            answer = cloudflareAiSearchService.searchAndAnswer(userMsg);
        } else { // ChatIntent.UNKNOWN
            // Default to RAG, fallback if no answer found
            answer = cloudflareAiSearchService.searchAndAnswer(userMsg);
            if (answer == null || answer.contains("chưa có thông tin") || answer.contains("chưa lấy được thông tin") ||
                answer.contains("chưa lấy được thông tin hỗ trợ") || answer.contains("chưa hiểu rõ")) {
                answer = "Dạ, hiện tại mình chưa hiểu rõ câu hỏi của bạn. Bạn có thể hỏi mình về sản phẩm, size, tồn kho, đổi trả, thanh toán hoặc đơn hàng của bạn để shop kiểm tra hỗ trợ nhé.";
            }
        }
 
        // Save Context
        chatContextService.saveContext(sessId, context);
 
        Integer conversationId = null;
 
        // If user is authenticated, handle conversation and message saving
        if (isAuthenticated) {
            AppUser user = currentUserOpt.get();
            ChatConversation conversation = null;
 
            // Load existing conversation if conversationId is provided and valid for user
            if (request.getConversationId() != null) {
                Optional<ChatConversation> convOpt = conversationRepository.findByIdAndUser(request.getConversationId(), user);
                if (convOpt.isPresent()) {
                    conversation = convOpt.get();
                    conversation.setUpdatedAt(LocalDateTime.now());
                    conversation = conversationRepository.save(conversation);
                }
            }
 
            // Otherwise, create a new conversation
            if (conversation == null) {
                String title = userMsg.length() > 40 ? userMsg.substring(0, 37) + "..." : userMsg;
                conversation = ChatConversation.builder()
                        .user(user)
                        .title(title)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                conversation = conversationRepository.save(conversation);
            }
 
            conversationId = conversation.getId();
 
            // Save user message
            ChatMessage userMessageEntity = ChatMessage.builder()
                    .conversation(conversation)
                    .user(user)
                    .role("USER")
                    .content(userMsg)
                    .intent(intent.name())
                    .createdAt(LocalDateTime.now())
                    .build();
            messageRepository.save(userMessageEntity);
 
            // Save assistant message
            ChatMessage assistantMessageEntity = ChatMessage.builder()
                    .conversation(conversation)
                    .user(user)
                    .role("ASSISTANT")
                    .content(answer)
                    .intent(intent.name())
                    .createdAt(LocalDateTime.now())
                    .build();
            messageRepository.save(assistantMessageEntity);
        }
 
        return ChatbotResponse.builder()
                .answer(answer)
                .conversationId(conversationId)
                .intent(intent.name())
                .build();
    }
 
    private boolean isInternalInquiry(String message) {
        String lower = message.toLowerCase();
        return lower.contains("api-token") || lower.contains("api token") ||
                lower.contains("cloudflare.api-token") || lower.contains("mật khẩu") && lower.contains("admin") ||
                lower.contains("cấu trúc database") || lower.contains("database schema") ||
                lower.contains("code backend") || lower.contains("secret key") ||
                lower.contains("tài khoản admin") || lower.contains("admin account") ||
                lower.contains("credentials") || lower.contains("config");
    }
}
