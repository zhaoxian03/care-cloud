package com.neusoft.care.ai.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * RAG检索增强服务，负责将用户查询转换为向量并检索相似文档
 *
 * 核心逻辑：
 * 1. 将用户消息通过EmbeddingModel（DASHSCOPE text-embedding-v3）转换为向量
 * 2. 在PgVector向量数据库中执行余弦相似度检索，返回Top5匹配结果
 * 3. 将检索到的TextSegment组装为结构化的上下文文本
 * 4. 检索失败或结果为空时返回空字符串，保证流程不中断
 *
 * 注意事项：
 * - 向量维度1024，与PgVectorEmbeddingStore的配置保持一致
 * - 检索结果最多返回5条，包含type元数据标注来源类型（service/care）
 *
 * @author CareCenter Team
 */
@Service
public class AiRagService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    /**
     * 构造函数，注入向量模型和向量存储
     *
     * @param embeddingModel 文本嵌入模型
     * @param embeddingStore PgVector向量存储
     */
    public AiRagService(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    /**
     * 向量检索并构建RAG上下文
     *
     * @param userMessage 用户输入消息
     * @return 格式化的Top5检索结果上下文，失败或无结果时返回空字符串
     *
     * <-> 运算符：计算欧氏距离（或余弦相似度，取决于索引类型）。
     */
    public String searchAndBuildContext(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) return "";
        try {
            // Step 1: 将用户问题转换为向量
            Embedding queryEmbedding = embeddingModel.embed(userMessage).content();
            //// Step 2: 构建检索请求（Top 5）
            EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmbedding)
                    .maxResults(5)
                    .build();

            // Step 3: 执行相似度检索
            List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(request).matches();
            if (matches.isEmpty()) return "";

            // Step 4: 格式化为上下文文本
            StringBuilder sb = new StringBuilder("以下是根据用户需求匹配到的服务和护理项目：\n");
            int i = 1;
            for (EmbeddingMatch<TextSegment> match : matches) {
                TextSegment seg = match.embedded();
                sb.append(i++).append(". ").append(seg.text())
                  .append(" [类型:").append(nvl(seg.metadata().getString("type"))).append("]\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 空值转换为横线，避免null拼接
     *
     * @param s 待处理的字符串
     * @return 非空返回原值，null返回"-"
     */
    private String nvl(String s) {
        return s != null ? s : "-";
    }
}
