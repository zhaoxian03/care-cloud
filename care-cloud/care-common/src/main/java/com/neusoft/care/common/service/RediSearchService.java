package com.neusoft.care.common.service;

import io.redisearch.Document;
import io.redisearch.Query;
import io.redisearch.SearchResult;
import io.redisearch.Schema;
import io.redisearch.client.Client;
import io.redisearch.AggregationResult;
import io.redisearch.aggregation.AggregationBuilder;
import io.redisearch.aggregation.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RediSearch 搜索服务
 *
 * 核心逻辑：
 * 1. 封装 RediSearch 客户端的索引创建、文档增删、全文搜索、数字范围查询、聚合统计等操作
 * 2. createIndex() —— 创建索引（索引已存在时忽略错误）
 * 3. addDocument() —— 向索引中添加文档
 * 4. fullTextSearch() —— 全文模糊搜索
 * 5. numericRangeSearch() —— 数字字段范围查询
 * 6. aggregateCount() / aggregateSum() —— 聚合分组统计
 * 7. deleteDocument() —— 根据 ID 删除文档
 *
 * 注意事项：RediSearch 依赖 RediSearch 模块，需确保 Redis 已安装该模块
 *
 * @author CareCenter Team
 */
@Service
public class RediSearchService {

    @Autowired
    private Client rediSearchClient;

    /**
     * 创建索引
     * @param schema 索引结构
     */
    public void createIndex(Schema schema) {
        try {
            rediSearchClient.createIndex(schema, Client.IndexOptions.defaultOptions());
        } catch (Exception e) {
            // 索引可能已存在，忽略错误
            System.out.println("索引创建失败或已存在: " + e.getMessage());
        }
    }

    /**
     * 添加文档
     * @param docId 文档ID
     * @param fields 文档字段（键值对）
     */
    public void addDocument(String docId, Map<String, Object> fields) {
        rediSearchClient.addDocument(docId, fields);
    }

    /**
     * 全文搜索
     * @param queryText 搜索关键词
     * @param limit 返回数量限制
     * @return 搜索结果
     */
    public SearchResult fullTextSearch(String queryText, int limit) {
        Query query = new Query(queryText)
                .limit(0, limit);
        return rediSearchClient.search(query);
    }

    /**
     * 数字范围查询
     * @param fieldName 字段名
     * @param min 最小值
     * @param max 最大值
     * @param limit 返回数量限制
     * @return 搜索结果
     */
    public SearchResult numericRangeSearch(String fieldName, double min, double max, int limit) {
        Query query = new Query("@" + fieldName + ":[" + min + " " + max + "]")
                .limit(0, limit);
        return rediSearchClient.search(query);
    }

    /**
     * 聚合统计 - 分组计数
     * @param groupField 分组字段
     * @return 聚合结果
     */
    public AggregationResult aggregateCount(String groupField) {
        AggregationBuilder builder = new AggregationBuilder("*")
                .groupBy("@" + groupField,
                        io.redisearch.aggregation.reducers.Reducers.count());
        return rediSearchClient.aggregate(builder);
    }

    /**
     * 聚合统计 - 求和
     * @param groupField 分组字段
     * @param sumField 求和字段
     * @return 聚合结果
     */
    public AggregationResult aggregateSum(String groupField, String sumField) {
        AggregationBuilder builder = new AggregationBuilder("*")
                .groupBy("@" + groupField,
                        io.redisearch.aggregation.reducers.Reducers.sum("@" + sumField));
        return rediSearchClient.aggregate(builder);
    }

    /**
     * 删除文档
     * @param docId 文档ID
     */
    public void deleteDocument(String docId) {
        rediSearchClient.deleteDocument(docId);
    }
}