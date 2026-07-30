package com.neusoft.care.ai.service;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;

/**
 * 向量数据同步服务，在应用启动时自动将MySQL业务数据同步到PgVector向量库
 *
 * 核心逻辑：
 * 1. 实现CommandLineRunner，在Spring Boot启动后自动执行
 * 2. 初始化PgVector扩展和care_embedding表（含幂等性检查）
 * 3. 检查是否已同步（通过向量表记录数判断），已同步则跳过
 * 4. 从MySQL读取service_catalog和care_item两张表的数据
 * 5. 将每条数据通过EmbeddingModel（DASHSCOPE text-embedding-v3）向量化后存入PgVector
 *
 * 注意事项：
 * - 仅在应用首次启动时执行全量同步，后续重启跳过
 * - 通过独立的MySQL连接（my.mysql.*配置）读取业务库数据
 * - 同步失败只记录日志，不影响应用正常启动
 *
 * @author CareCenter Team
 */
@Service
public class EmbeddingSyncService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingSyncService.class);

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final DataSource dataSource;

    @Value("${my.mysql.url}")
    private String mySqlUrl;

    @Value("${my.mysql.username}")
    private String mySqlUser;

    @Value("${my.mysql.password}")
    private String mySqlPassword;

    /**
     * 构造函数，注入向量模型、向量存储和数据源
     *
     * @param embeddingModel 文本嵌入模型
     * @param embeddingStore PgVector向量存储
     * @param dataSource PostgreSQL数据源
     */
    public EmbeddingSyncService(EmbeddingModel embeddingModel,
                                 EmbeddingStore<TextSegment> embeddingStore,
                                 DataSource dataSource) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
        this.dataSource = dataSource;
    }

    /**
     * 应用启动后自动执行向量同步流程
     *
     * @param args 命令行参数
     */
    @Override
    public void run(String... args) {
        try {
            initPgVectorStore();        // ① 初始化向量库表结构
            if (isAlreadySynced()) {    // ② 幂等性检查
                log.info("Embeddings already synced, skipping");
                return;
            }
            syncServiceCatalog();       // ③ 同步服务目录
            syncCareItems();            // ④ 同步护理项目
            log.info("Embedding sync completed");
        } catch (Exception e) {
            log.error("Embedding sync failed: {}", e.getMessage(), e);
        }
    }

    /**
     * 初始化PgVector扩展和向量存储表，已存在则跳过
     */
    private void initPgVectorStore() {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE EXTENSION IF NOT EXISTS vector");
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS care_embedding (
                    embedding_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                    text TEXT,
                    metadata JSON,
                    embedding vector(1024)
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Init pgvector store failed", e);
        }
    }

    /**
     * 检查向量表是否已有数据，有数据则认为已同步
     *
     * @return true表示已同步过，无需重复执行
     */
    private boolean isAlreadySynced() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT count(*) FROM care_embedding")) {
            if (rs.next() && rs.getInt(1) > 0) return true;
        } catch (SQLException ignored) {
        }
        return false;
    }

    /**
     * 从MySQL同步service_catalog表的服务目录数据到向量库
     */
    private void syncServiceCatalog() {
        try (Connection conn = DriverManager.getConnection(mySqlUrl, mySqlUser, mySqlPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, description FROM service_catalog WHERE is_deleted = 0")) {
            int count = 0;
            while (rs.next()) {
                // 数据读取
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String desc = rs.getString("description");
                //文本拼接，格式：服务名称：服务描述
                String text = name + (desc != null ? "：" + desc : "");

                /* 元数据封装
                * Metadata 是 LangChain4j 提供的键值对容器，用于存储与文本相关的附属信息。
                * */
                Metadata meta = new Metadata();
                meta.put("id", String.valueOf(id));
                meta.put("type", "service");
                meta.put("name", name != null ? name : "");
                //构建 TextSegment
                TextSegment segment = TextSegment.from(text, meta);
                //向量化
                Embedding emb = embeddingModel.embed(segment).content();
                //存储到 PgVector
                embeddingStore.add(emb, segment);
                //计数器递增
                count++;
            }
            log.info("Synced {} service catalogs", count);
        } catch (SQLException e) {
            log.error("Sync service catalog failed: {}", e.getMessage(), e);
        }
    }

    /**
     * 从MySQL同步care_item表的护理项目数据到向量库
     */
    private void syncCareItems() {
        try (Connection conn = DriverManager.getConnection(mySqlUrl, mySqlUser, mySqlPassword);
             Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery("SELECT id, item_name as name FROM care_item WHERE is_deleted = 0")) {
            int count = 0;
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                Metadata meta = new Metadata();
                meta.put("id", String.valueOf(id));
                meta.put("type", "care");
                meta.put("name", name != null ? name : "");
                TextSegment segment = TextSegment.from("护理项目：" + (name != null ? name : ""), meta);
                Embedding emb = embeddingModel.embed(segment).content();
                embeddingStore.add(emb, segment);
                count++;
            }
            log.info("Synced {} care items", count);
        } catch (SQLException e) {
            log.error("Sync care items failed: {}", e.getMessage(), e);
        }
    }
}
