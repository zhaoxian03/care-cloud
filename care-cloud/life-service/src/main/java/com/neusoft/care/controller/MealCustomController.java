package com.neusoft.care.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.dto.MealCustomBatchRequest;
import com.neusoft.care.entity.MealCustom;
import com.neusoft.care.entity.MealCustomDish;
import com.neusoft.care.mapper.MealCustomDishMapper;
import com.neusoft.care.service.MealCustomService;
import com.neusoft.care.service.impl.MealCustomServiceImpl;
import com.neusoft.care.vo.MealGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 膳食定制表 控制层
 * 
 * 功能说明：处理膳食管理相关的所有HTTP请求
 * 
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/meal")
public class MealCustomController {

    @Autowired
    private MealCustomService mealCustomService;

    @Autowired
    private MealCustomServiceImpl mealCustomServiceImpl;

    @Autowired
    private MealCustomDishMapper mealCustomDishMapper;

    /**
     * 添加自定义膳食（支持菜品关联）
     * URL: POST /api/meal/custom
     * 权限: 需要管理员权限
     * 
     * @param mealCustom 膳食信息
     * @param dishIds 菜品ID列表（可选）
     * @return 是否成功
     */
    @SaCheckPermission("meal:view")
    @PostMapping("/custom")
    public Result<Void> addMealCustom(@RequestBody MealCustom mealCustom,
                                       @RequestParam(required = false) List<Integer> dishIds) {
        mealCustomServiceImpl.saveWithDishes(mealCustom, dishIds);
        return Result.success();
    }

    /**
     * 批量创建膳食记录（一次性安排早/午/晚餐）
     * URL: POST /api/meal/custom/batch
     * 权限: 需要管理员权限
     */
    @SaCheckPermission("meal:view")
    @PostMapping("/custom/batch")
    public Result<Void> addMealCustomBatch(@RequestBody MealCustomBatchRequest request) {
        mealCustomServiceImpl.saveBatchWithDishes(
                request.getCustomerId(),
                request.getMealDate(),
                request.getMealTypes(),
                request.getMealTypeDishMap());
        return Result.success();
    }

    /**
     * 查询客户膳食日历
     * URL: GET /api/meal/calendar/{userId}
     * 权限: 需要认证
     * 
     * @param userId    客户ID
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @return 膳食列表
     */
    @SaCheckPermission("meal:view")
    @GetMapping("/calendar/{userId}")
    public Result<List<MealCustom>> getMealCalendar(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<MealCustom> list = mealCustomService.getMealCalendar(userId, startDate, endDate);
        return Result.success(list);
    }

    /**
     * 删除膳食记录
     * URL: DELETE /api/meal/custom/{id}
     * 权限: 需要管理员权限
     * 
     * @param id 记录ID
     * @return 是否成功
     */
    @SaCheckPermission("meal:view")
    @DeleteMapping("/custom/{id}")
    public Result<Void> deleteMealCustom(@PathVariable Long id) {
        // 先删除关联的菜品
        mealCustomDishMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MealCustomDish>()
                        .eq(MealCustomDish::getMealCustomId, id)
        );
        mealCustomService.removeById(id);
        return Result.success();
    }

    /**
     * 更新膳食记录
     * URL: PUT /api/meal/custom
     * 权限: 需要管理员权限
     * 
     * @param mealCustom 膳食信息
     * @param dishIds 菜品ID列表（可选）
     * @return 是否成功
     */
    @SaCheckPermission("meal:view")
    @PutMapping("/custom")
    public Result<Void> updateMealCustom(@RequestBody MealCustom mealCustom,
                                          @RequestParam(required = false) List<Integer> dishIds) {
        mealCustomServiceImpl.updateWithDishes(mealCustom, dishIds);
        return Result.success();
    }

    /**
     * 更新膳食记录状态
     * URL: PUT /api/meal/custom/{id}/status
     * 权限: 需要管理员权限
     * 
     * @param id 记录ID
     * @param status 状态（0-启用，1-停用）
     * @return 是否成功
     */
    @SaCheckPermission("meal:view")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        MealCustom mealCustom = mealCustomService.getById(id);
        if (mealCustom == null) {
            return Result.error("膳食记录不存在");
        }
        mealCustom.setStatus(status);
        mealCustomService.updateById(mealCustom);
        return Result.success();
    }

    /**
     * 查询全部膳食记录（分页）
     * URL: GET /api/meal/page
     * 权限: 需要认证
     * 
     * @param page 页码
     * @param size 每页条数
     * @param customerId 客户ID（可选）
     * @return 分页结果
     */
    @SaCheckPermission("meal:view")
    @GetMapping("/page")
    public Result<PageResult<MealGroupVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String keyword) {
        return Result.success(mealCustomService.pageMeals(page, size, customerId, keyword));
    }

    /**
     * 复制本周膳食到下周
     * URL: POST /api/meal/copy-next-week
     * 权限: 需要管理员权限
     * 
     * @return 复制的记录数
     */
    @SaCheckPermission("meal:view")
    @PostMapping("/copy-next-week")
    public Result<Integer> copyNextWeek() {
        int count = mealCustomService.copyToNextWeek();
        return Result.success(count);
    }
}
