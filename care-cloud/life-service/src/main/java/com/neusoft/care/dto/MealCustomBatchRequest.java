package com.neusoft.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 批量创建膳食请求DTO —— 一次性安排多个餐次的膳食
 *
 * @author CareCenter Team
 */
@Data
@Schema(description = "批量创建膳食请求")
public class MealCustomBatchRequest {

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "膳食日期")
    private LocalDate mealDate;

    @Schema(description = "餐次列表（1-早餐，2-午餐，3-晚餐）,如[1,2,3]")
    private List<Integer> mealTypes;

    @Schema(description = "各餐次对应的菜品ID映射，key=餐次(1/2/3)，value=菜品ID列表")
    private Map<Integer, List<Integer>> mealTypeDishMap;
}
