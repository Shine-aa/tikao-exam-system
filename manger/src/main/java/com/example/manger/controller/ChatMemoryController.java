package com.example.manger.controller;

import com.example.manger.service.ChatMemoryService;
import com.example.manger.common.ApiResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AI 对话控制器，提供带记忆的聊天功能以及会话管理。
 * 借鉴了 spring-ai-alibaba-chat-memory-example 的设计。
 */
@RestController
@RequestMapping("/api/ai/memory")
public class ChatMemoryController {

    private final ChatMemoryService chatMemoryService;

    public ChatMemoryController(ChatMemoryService chatMemoryService) {
        this.chatMemoryService = chatMemoryService;
    }

    /**
     * 发送消息进行对话。
     * AI 将根据 userId (作为 conversationId) 记住上下文。
     *
     * @param request 包含 userId 和 message 的请求体。
     * @return 统一响应，data 内包含 { reply: "..." }
     */
    @PostMapping("/chat")
    public ApiResponse<Map<String, String>> chat(@RequestBody ChatRequest request) {
        String reply = chatMemoryService.chat(request.getUserId(), request.getMessage());
        return ApiResponse.success(Map.of("reply", reply));
    }

    /**
     * 获取指定用户的完整对话历史记录。
     *
     * @param userId 用户ID (即 conversationId)。
     * @return 消息列表。
     */
    @GetMapping("/messages")
    public List<Message> getMessages(@RequestParam String userId) {
        return chatMemoryService.getMessages(userId);
    }

    /**
     * 清除指定用户的对话历史记录。
     *
     * @param userId 用户ID (即 conversationId)。
     * @return 成功响应。
     */
    @DeleteMapping("/messages")
    public ResponseEntity<Void> clearMessages(@RequestParam String userId) {
        chatMemoryService.clear(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取当前存储中所有的会话ID列表。
     * 注意：并非所有 ChatMemoryRepository 都支持此功能。
     *
     * @return 会话ID集合。
     */
    @GetMapping("/conversations")
    public Set<String> getConversationIds() {
        return chatMemoryService.getConversationIds();
    }


    /**
     * 内部 DTO 类，用于接收聊天请求。
     */
    public static class ChatRequest {
        private String userId;
        private String message;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
