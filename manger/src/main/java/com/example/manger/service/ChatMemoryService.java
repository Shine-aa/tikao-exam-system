package com.example.manger.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
public class ChatMemoryService {

    private static final Logger log = LoggerFactory.getLogger(ChatMemoryService.class);

    private final ChatClient chatClient;
    private final MessageWindowChatMemory messageWindowChatMemory;
    private final ChatMemoryRepository chatMemoryRepository;

    public ChatMemoryService(ChatClient.Builder builder, MessageWindowChatMemory messageWindowChatMemory, ChatMemoryRepository chatMemoryRepository) {
        this.messageWindowChatMemory = messageWindowChatMemory;
        this.chatMemoryRepository = chatMemoryRepository;

        this.chatClient = builder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
                                .build()
                )
                .build();
    }

    /**
     * Send a message for a given userId and return AI reply. Each userId is used as conversation id to isolate memory.
     */
    public String chat(String userId, String message) {
        long start = System.currentTimeMillis();
        try {
            String content = chatClient.prompt(message)
                    .advisors(a -> a.param(CONVERSATION_ID, userId))
                    .call()
                    .content();
            long cost = System.currentTimeMillis() - start;
            log.info("AI chat success, userId={}, cost={}ms", userId, cost);
            return content;
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.error("AI chat failed, userId={}, cost={}ms, error={}", userId, cost, e.getMessage(), e);
            throw e; // 交给全局异常处理器
        }
    }

    /**
     * Get chat history for a given userId.
     * @param userId The user id, which is the conversation id.
     * @return A list of messages.
     */
    public List<org.springframework.ai.chat.messages.Message> getMessages(String userId) {
        return messageWindowChatMemory.get(userId);
    }

    /**
     * Clear chat history for a given userId.
     * @param userId The user id, which is the conversation id.
     */
    public void clear(String userId) {
        messageWindowChatMemory.clear(userId);
    }

    /**
     * Get all conversation IDs from the memory store.
     * Note: This might not be supported by all ChatMemoryRepository implementations.
     * @return A set of conversation IDs.
     */
    @SuppressWarnings("unchecked")
    public Set<String> getConversationIds() {
        try {
            java.lang.reflect.Method method = this.chatMemoryRepository.getClass().getMethod("getConversationIds");
            return (Set<String>) method.invoke(this.chatMemoryRepository);
        } catch (Exception e) {
            log.warn("getConversationIds() not supported by {}: {}", this.chatMemoryRepository.getClass().getSimpleName(), e.getMessage());
            return Collections.emptySet();
        }
    }
}
