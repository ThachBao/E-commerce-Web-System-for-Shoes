package com.CongNgheJave.ecommerce_system.controller;
 
import com.CongNgheJave.ecommerce_system.dto.request.ChatbotRequest;
import com.CongNgheJave.ecommerce_system.dto.response.ChatConversationResponse;
import com.CongNgheJave.ecommerce_system.dto.response.ChatMessageResponse;
import com.CongNgheJave.ecommerce_system.dto.response.ChatbotResponse;
import com.CongNgheJave.ecommerce_system.entity.AppUser;
import com.CongNgheJave.ecommerce_system.entity.ChatConversation;
import com.CongNgheJave.ecommerce_system.repository.ChatConversationRepository;
import com.CongNgheJave.ecommerce_system.repository.ChatMessageRepository;
import com.CongNgheJave.ecommerce_system.service.ai.AiChatService;
import com.CongNgheJave.ecommerce_system.service.ai.ChatAuthContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
 
@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
 
    private final AiChatService aiChatService;
    private final ChatAuthContextService authContextService;
    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
 
    @PostMapping
    public ResponseEntity<ChatbotResponse> chat(@RequestBody ChatbotRequest request) {
        ChatbotResponse response = aiChatService.processChatMessage(request);
        return ResponseEntity.ok(response);
    }
 
    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations() {
        Optional<AppUser> userOpt = authContextService.getCurrentUser();
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để xem lịch sử.");
        }
 
        List<ChatConversation> conversations = conversationRepository.findByUserOrderByUpdatedAtDesc(userOpt.get());
        List<ChatConversationResponse> response = conversations.stream()
                .map(c -> ChatConversationResponse.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .updatedAt(c.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
 
        return ResponseEntity.ok(response);
    }
 
    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Integer id) {
        Optional<AppUser> userOpt = authContextService.getCurrentUser();
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để xem tin nhắn.");
        }
 
        Optional<ChatConversation> convOpt = conversationRepository.findByIdAndUser(id, userOpt.get());
        if (convOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không tìm thấy hội thoại hoặc hội thoại không thuộc quyền sở hữu của bạn.");
        }
 
        List<ChatMessageResponse> response = messageRepository.findByConversationOrderByCreatedAtAsc(convOpt.get()).stream()
                .map(m -> ChatMessageResponse.builder()
                        .role(m.getRole())
                        .content(m.getContent())
                        .createdAt(m.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
 
        return ResponseEntity.ok(response);
    }
 
    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<?> deleteConversation(@PathVariable Integer id) {
        Optional<AppUser> userOpt = authContextService.getCurrentUser();
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để thực hiện thao tác.");
        }
 
        Optional<ChatConversation> convOpt = conversationRepository.findByIdAndUser(id, userOpt.get());
        if (convOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không tìm thấy hội thoại hoặc hội thoại không thuộc quyền sở hữu của bạn.");
        }
 
        conversationRepository.delete(convOpt.get());
        return ResponseEntity.ok("Xóa cuộc hội thoại thành công.");
    }
}
