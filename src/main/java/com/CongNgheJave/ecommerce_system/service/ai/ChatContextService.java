package com.CongNgheJave.ecommerce_system.service.ai;
 
import com.CongNgheJave.ecommerce_system.dto.ai.ChatContext;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
 
@Service
public class ChatContextService {
    private final ConcurrentHashMap<String, ChatContext> contexts = new ConcurrentHashMap<>();
 
    public ChatContext getContext(String sessionId) {
        if (sessionId == null) {
            return new ChatContext();
        }
        return contexts.computeIfAbsent(sessionId, k -> new ChatContext());
    }
 
    public void saveContext(String sessionId, ChatContext context) {
        if (sessionId != null && context != null) {
            contexts.put(sessionId, context);
        }
    }
 
    public void clearContext(String sessionId) {
        if (sessionId != null) {
            contexts.remove(sessionId);
        }
    }
}
