package com.neusoft.care.ai.config;

import com.neusoft.care.ai.service.AiChatService;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.time.Duration;

/**
 * AI核心配置类，负责初始化LangChain4j各组件的Spring Bean
 *
 * 核心逻辑：
 * 1. 初始化ChatLanguageModel和StreamingChatLanguageModel，用于常规对话和流式对话
 * 2. 初始化EmbeddingModel（DASHSCOPE text-embedding-v3），用于文本向量化
 * 3. 构建AiChatService，配置ChatMemory实现对话上下文管理：
 *    - 基于Redis存储，通过RedisChatMemoryStore持久化
 *    - 按sessionId隔离会话，不同用户/会话互不干扰
 *    - MessageWindow模式，最多保留最近10条消息，24小时TTL自动过期
 * 4. 配置PgVectorEmbeddingStore作为RAG向量检索的存储后端（1024维向量）
 *
 * @author CareCenter Team
 */
@Configuration
public class AiConfig {

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String baseUrl;

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Value("${langchain4j.open-ai.embedding-model.model-name:text-embedding-v3}")
    private String embeddingModelName;

    /**
     * 创建非流式ChatLanguageModel Bean，用于普通同步对话调用
     *
     * @return 配置了apiKey、baseUrl、modelName和300秒超时的ChatLanguageModel实例
     */
    @Bean
    public ChatLanguageModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey).baseUrl(baseUrl).modelName(modelName)
                .timeout(Duration.ofSeconds(300)).build();
    }

    /**
     * 创建流式StreamingChatLanguageModel Bean，用于SSE流式对话
     *
     * @return 配置了apiKey、baseUrl、modelName和300秒超时的StreamingChatLanguageModel实例
     */
    @Bean
    public StreamingChatLanguageModel openAiStreamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey).baseUrl(baseUrl).modelName(modelName)
                .timeout(Duration.ofSeconds(300)).build();
    }

    /**
     * 创建EmbeddingModel Bean，用于文本向量化（DASHSCOPE text-embedding-v3，1024维）
     *
     * @return 配置了apiKey、baseUrl、embeddingModelName和120秒超时的EmbeddingModel实例
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey).baseUrl(baseUrl).modelName(embeddingModelName)
                .timeout(Duration.ofSeconds(120)).build();
    }

    /**
     * 构建AiChatService Bean，组合对话模型和聊天记忆
     *
     * @param openAiChatModel 非流式对话模型
     * @param openAiStreamingChatModel 流式对话模型
     * @param chatMemoryStore Redis聊天记忆存储器
     * @return 配置了MessageWindow（最多10条消息）和Redis持久化的AiChatService代理实例
     */
    @Bean
    public AiChatService aiChatService(ChatLanguageModel openAiChatModel,
                                        StreamingChatLanguageModel openAiStreamingChatModel,
                                        ChatMemoryStore chatMemoryStore) {
        return AiServices.builder(AiChatService.class)
                .chatLanguageModel(openAiChatModel)
                .streamingChatLanguageModel(openAiStreamingChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(10)
                        .chatMemoryStore(chatMemoryStore)
                        .build())
                .build();
    }

    /**
     * 创建PgVector向量存储Bean，用于RAG检索
     *
     * @param dataSource PostgreSQL数据源
     * @return 配置了care_embedding表和1024维向量的PgVectorEmbeddingStore实例
     */
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(DataSource dataSource) {
        return PgVectorEmbeddingStore.datasourceBuilder()
                .datasource(dataSource).table("care_embedding").dimension(1024).build();
    }
}
