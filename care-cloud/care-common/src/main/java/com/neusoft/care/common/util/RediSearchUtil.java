package com.neusoft.care.common.util;

import io.redisearch.Schema;
import io.redisearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RediSearch 工具类
 *
 * 核心逻辑：
 * 1. 提供静态方法快速构建 RediSearch Schema（索引结构）
 * 2. 支持添加文本字段、数字字段、标签字段，以及对应的可排序版本
 *
 * 使用方式：
 * RediSearchService 负责索引和查询操作，RediSearchUtil 负责构建 Schema 结构
 *
 * 注意事项：所有方法为静态方法，可直接通过类名调用，无需注入实例
 *
 * @author CareCenter Team
 */
@Component
public class RediSearchUtil {

    @Autowired
    private Client rediSearchClient;

    /**
     * 创建 Schema 对象
     * @return Schema
     */
    public static Schema createSchema() {
        return new Schema();
    }

    /**
     * 添加文本字段到 Schema
     * @param schema Schema 对象
     * @param fieldName 字段名
     * @param weight 权重
     * @return Schema
     */
    public static Schema addTextField(Schema schema, String fieldName, double weight) {
        return schema.addTextField(fieldName, weight);
    }

    /**
     * 添加可排序文本字段到 Schema
     * @param schema Schema 对象
     * @param fieldName 字段名
     * @param weight 权重
     * @return Schema
     */
    public static Schema addSortableTextField(Schema schema, String fieldName, double weight) {
        return schema.addSortableTextField(fieldName, weight);
    }

    /**
     * 添加数字字段到 Schema
     * @param schema Schema 对象
     * @param fieldName 字段名
     * @return Schema
     */
    public static Schema addNumericField(Schema schema, String fieldName) {
        return schema.addNumericField(fieldName);
    }

    /**
     * 添加可排序数字字段到 Schema
     * @param schema Schema 对象
     * @param fieldName 字段名
     * @return Schema
     */
    public static Schema addSortableNumericField(Schema schema, String fieldName) {
        return schema.addSortableNumericField(fieldName);
    }

    /**
     * 添加标签字段到 Schema
     * @param schema Schema 对象
     * @param fieldName 字段名
     * @param separator 分隔符
     * @return Schema
     */
    public static Schema addTagField(Schema schema, String fieldName, String separator) {
        return schema.addTagField(fieldName, separator);
    }
}