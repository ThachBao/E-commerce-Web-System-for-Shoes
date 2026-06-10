package com.CongNgheJave.ecommerce_system.dto.request;
 
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotRequest {
    private String message;
    private Integer conversationId;
    private String sessionId;
}
