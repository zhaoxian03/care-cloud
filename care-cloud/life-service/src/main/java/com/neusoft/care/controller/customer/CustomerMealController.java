package com.neusoft.care.controller.customer;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.common.exception.BusinessException;
import com.neusoft.care.entity.Dish;
import com.neusoft.care.entity.MealCustom;
import com.neusoft.care.service.DishService;
import com.neusoft.care.service.MealCustomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

/**
 * C端膳食控制器 —— 客户/长者App查看和自定义膳食
 *
 * 核心逻辑：
 * 1. 查看本周膳食日历（周一至周日）
 * 2. 查看菜品列表供选择
 * 3. 自定义膳食记录（新增或修改指定日期的餐次菜品）
 *
 * 注意事项：不允许修改过往日期的膳食记录
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/app")
public class CustomerMealController {

    @Autowired
    private MealCustomService mealCustomService;

    @Autowired
    private DishService dishService;

    /**
     * 查看本周膳食日历（周一至周日）
     *
     * @return 本周膳食列表（包含菜品名称）
     */
    @GetMapping("/meal/calendar")
    public Result<List<MealCustom>> calendar() {
        Long customerId = StpUtil.getLoginIdAsLong();
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        List<MealCustom> meals = mealCustomService.getMealCalendar(customerId, monday, sunday);
        return Result.success(meals);
    }

    /**
     * 查看所有菜品列表
     *
     * @return 菜品列表
     */
    @GetMapping("/dish/list")
    public Result<List<Dish>> dishList() {
        List<Dish> dishes = dishService.list();
        return Result.success(dishes);
    }

    /**
     * 新增或修改自定义膳食记录
     * 对每个餐次判断是新增还是更新（按客户+日期+餐次查重），不允许修改过往日期
     *
     * @param body 请求参数：mealDate（日期）、mealTypes（餐次列表）、dishIds（菜品ID列表）
     * @return 操作结果
     */
    @PostMapping("/meal/custom")
    public Result<Void> saveCustom(@RequestBody Map<String, Object> body) {
        Long customerId = StpUtil.getLoginIdAsLong();
        String mealDate = (String) body.get("mealDate");
        LocalDate date = mealDate != null ? LocalDate.parse(mealDate) : LocalDate.now();

        if (date.isBefore(LocalDate.now())) {
            throw new BusinessException("不能修改过往日期的膳食记录");
        }

        @SuppressWarnings("unchecked")
        List<Integer> mealTypes = (List<Integer>) body.get("mealTypes");

        @SuppressWarnings("unchecked")
        List<Integer> dishIds = (List<Integer>) body.get("dishIds");

        // 默认处理三餐
        if (mealTypes == null || mealTypes.isEmpty()) {
            mealTypes = List.of(1, 2, 3);
        }

        for (Integer mealType : mealTypes) {
            LambdaQueryWrapper<MealCustom> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MealCustom::getCustomerId, customerId)
                   .eq(MealCustom::getMealDate, date)
                   .eq(MealCustom::getMealType, mealType);
            // 查是否存在该日期的该餐次
            MealCustom existing = mealCustomService.getOne(wrapper);

            if (existing != null) {
                // 更新：修改关联菜品
                existing.setDishIds(dishIds);
                mealCustomService.updateWithDishes(existing, dishIds);
            } else {
                // 新增：创建膳食记录
                MealCustom meal = new MealCustom();
                meal.setCustomerId(customerId);
                meal.setMealDate(date);
                meal.setMealType(mealType);
                mealCustomService.saveWithDishes(meal, dishIds);
            }
        }

        return Result.success();
    }
}
