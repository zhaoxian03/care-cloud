package com.neusoft.care.vo;

import com.neusoft.care.entity.MealCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 膳食分组视图——按客户+日期分组展示膳食记录
 *
 * @author CareCenter Team
 */
@Data
@Schema(description = "膳食分组视图（按客户+日期分组）")
public class MealGroupVO {

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户姓名")
    private String customerName;

    @Schema(description = "膳食日期")
    private LocalDate mealDate;

    @Schema(description = "该日各餐次记录")
    private List<MealCustom> meals;
}
