package com.neusoft.care.ai.config;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的ChatMemoryStore自定义实现，为LangChain4j提供持久化的对话记忆能力
 *
 * 核心逻辑：
 * 1. 将ChatMessage列表序列化为JSON存储到Redis
 * 2. 每个会话按sessionId隔离，Key格式：chat:memory:{sessionId}
 * 3. 设置24小时TTL，过期自动清理，防止内存无限膨胀
 * 4. 反序列化异常时返回空列表，保证服务的容错性
 *
 * 注意事项：
 * - TTL仅24小时，超时会话将丢失对话上下文
 * - 序列化/反序列化使用LangChain4j内置的ChatMessageSerializer/Deserializer
 *
 * ChatMemoryStore 是一个存储抽象接口，定义了对话历史如何保存、读取和删除,
 * 它的默认实现通常是内存存储（InMemoryChatMemoryStore），但重启服务后历史会丢失
 *
 * @author CareCenter Team
 */
@Component
public class RedisChatMemoryStore implements ChatMemoryStore {

    private static final long TTL_HOURS = 24;
    private final StringRedisTemplate redisTemplate;

    /**
     * 构造函数，注入StringRedisTemplate
     *
     * @param redisTemplate Spring Data Redis的字符串模板
     */
    public RedisChatMemoryStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 从Redis获取指定会话的聊天消息列表
     *
     * @param memoryId 会话标识（通常是sessionId）
     * @return 反序列化后的ChatMessage列表，不存在或异常时返回空列表
     */
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String json = redisTemplate.opsForValue().get("chat:memory:" + memoryId);
        if (json == null || json.isEmpty()) return new ArrayList<>();
        try {
            return ChatMessageDeserializer.messagesFromJson(json);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 更新指定会话的聊天消息列表并刷新TTL
     *
     * @param memoryId 会话标识
     * @param messages 最新的ChatMessage列表，为空时不操作
     */
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) return;
        String json = ChatMessageSerializer.messagesToJson(messages);
        redisTemplate.opsForValue().set("chat:memory:" + memoryId, json, TTL_HOURS, TimeUnit.HOURS);
    }

    /**
     * 删除指定会话的全部聊天记忆
     *
     * @param memoryId 会话标识
     */
    @Override
    public void deleteMessages(Object memoryId) {
        redisTemplate.delete("chat:memory:" + memoryId);
    }
}
