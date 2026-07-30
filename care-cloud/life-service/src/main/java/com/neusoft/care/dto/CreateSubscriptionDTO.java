package com.neusoft.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 创建订阅请求 DTO
 */
@Data
@Schema(description = "创建订阅请求")
public class CreateSubscriptionDTO {

    /** 客户ID */
    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @Schema(description = "服务产品ID", example = "1")
    private Long catalogId;

    @Schema(description = "订阅开始日期", example = "2025-01-01")
    private LocalDate startDate;

    /** 订阅到期日期（可选，为空表示长期有效） */
    @Schema(description = "订阅到期日期", example = "2025-12-31")
    private LocalDate endDate;

    private Integer duration;

    private String status;
}
