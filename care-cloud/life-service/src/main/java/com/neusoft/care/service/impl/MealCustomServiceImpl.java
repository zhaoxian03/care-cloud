package com.neusoft.care.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.entity.MealCustom;
import com.neusoft.care.entity.MealCustomDish;
import com.neusoft.care.mapper.MealCustomDishMapper;
import com.neusoft.care.mapper.MealCustomMapper;
import com.neusoft.care.service.MealCustomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.care.vo.MealGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 膳食定制表 服务实现
 * 
 * 功能说明：实现膳食管理的所有业务逻辑
 * 
 * @author CareCenter Team
 */
@Service
public class MealCustomServiceImpl extends ServiceImpl<MealCustomMapper, MealCustom> implements MealCustomService {

    @Autowired
    private MealCustomMapper mealCustomMapper;

    @Autowired
    private MealCustomDishMapper mealCustomDishMapper;

    /**
     * 查询客户膳食日历
     */
    @Override
    public List<MealCustom> getMealCalendar(Long customerId, LocalDate startDate, LocalDate endDate) {
        List<MealCustom> meals = mealCustomMapper.selectMealCalendar(customerId, startDate, endDate);

        if (meals.isEmpty()) {
            return meals;
        }

        // 批量查询关联的菜品
        List<Long> mealIds = meals.stream().map(MealCustom::getId).collect(Collectors.toList());
        List<MealCustomDish> dishes = mealCustomDishMapper.selectByMealCustomIds(mealIds);

        // 按膳食记录ID分组
        Map<Long, List<MealCustomDish>> dishMap = dishes.stream()
                .collect(Collectors.groupingBy(MealCustomDish::getMealCustomId));

        // 设置菜品名称列表
        for (MealCustom meal : meals) {
            List<MealCustomDish> mealDishes = dishMap.getOrDefault(meal.getId(), new ArrayList<>());
            meal.setDishNames(mealDishes.stream().map(MealCustomDish::getDishName).collect(Collectors.toList()));
            meal.setDishIds(mealDishes.stream().map(MealCustomDish::getDishId).collect(Collectors.toList()));
        }

        return meals;
    }

    /**
     * 保存膳食记录
     * 自动设置创建日期和时间
     */
    @Override
    @Transactional
    public boolean save(MealCustom mealCustom) {
        // 自动设置创建日期和时间
        if (mealCustom.getCreateDate() == null) {
            mealCustom.setCreateDate(LocalDate.now());
        }
        if (mealCustom.getCreateTime() == null) {
            mealCustom.setCreateTime(LocalTime.now());
        }
        // 默认启用
        if (mealCustom.getStatus() == null) {
            mealCustom.setStatus(0);
        }
        return super.save(mealCustom);
    }

    /**
     * 保存膳食记录及其关联菜品
     */
    @Transactional
    public boolean saveWithDishes(MealCustom mealCustom, List<Integer> dishIds) {
        // 保存膳食记录
        save(mealCustom);

        // 保存关联菜品
        if (dishIds != null && !dishIds.isEmpty()) {
            for (Integer dishId : dishIds) {
                MealCustomDish mcd = new MealCustomDish();
                mcd.setMealCustomId(mealCustom.getId());
                mcd.setDishId(dishId);
                mealCustomDishMapper.insert(mcd);
            }
        }

        return true;
    }

    /**
     * 更新膳食记录及其关联菜品
     */
    @Transactional
    public boolean updateWithDishes(MealCustom mealCustom, List<Integer> dishIds) {
        // 更新膳食记录
        updateById(mealCustom);

        // 删除旧的关联菜品
        LambdaQueryWrapper<MealCustomDish> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(MealCustomDish::getMealCustomId, mealCustom.getId());
        mealCustomDishMapper.delete(deleteWrapper);

        // 保存新的关联菜品
        if (dishIds != null && !dishIds.isEmpty()) {
            for (Integer dishId : dishIds) {
                MealCustomDish mcd = new MealCustomDish();
                mcd.setMealCustomId(mealCustom.getId());
                mcd.setDishId(dishId);
                mealCustomDishMapper.insert(mcd);
            }
        }

        return true;
    }

    /**
     * 批量创建膳食记录（每个餐次独立分配菜品）
     */
    @Transactional
    public void saveBatchWithDishes(Long customerId, LocalDate mealDate, List<Integer> mealTypes, Map<Integer, List<Integer>> mealTypeDishMap) {
        for (Integer mealType : mealTypes) {
            MealCustom mealCustom = new MealCustom();
            mealCustom.setCustomerId(customerId);
            mealCustom.setMealDate(mealDate);
            mealCustom.setMealType(mealType);
            List<Integer> typeDishIds = (mealTypeDishMap != null) ? mealTypeDishMap.get(mealType) : null;
            saveWithDishes(mealCustom, typeDishIds);
        }
    }

    /**
     * 分页查询膳食记录（按客户+日期分组后分页）
     */
    @Override
    public PageResult<MealGroupVO> pageMeals(Integer page, Integer size, Long customerId, String keyword) {
        //阶段一：全量查询主表
        List<MealCustom> allMeals = mealCustomMapper.selectAllMeals(customerId, keyword);

        //阶段二：批量加载菜品信息
        if (!allMeals.isEmpty()) {
            List<Long> mealIds = allMeals.stream().map(MealCustom::getId).collect(Collectors.toList());
            List<MealCustomDish> dishes = mealCustomDishMapper.selectByMealCustomIds(mealIds);
            Map<Long, List<MealCustomDish>> dishMap = dishes.stream()
                    .collect(Collectors.groupingBy(MealCustomDish::getMealCustomId));

            for (MealCustom meal : allMeals) {
                List<MealCustomDish> mealDishes = dishMap.getOrDefault(meal.getId(), new ArrayList<>());
                meal.setDishNames(mealDishes.stream().map(MealCustomDish::getDishName).collect(Collectors.toList()));
                meal.setDishIds(mealDishes.stream().map(MealCustomDish::getDishId).collect(Collectors.toList()));
            }
        }

        //阶段三：内存分组（客户 + 日期）
        Map<String, MealGroupVO> groupMap = new LinkedHashMap<>();
        for (MealCustom meal : allMeals) {
            String key = meal.getCustomerId() + "_" + meal.getMealDate();
            groupMap.computeIfAbsent(key, k -> {
                MealGroupVO vo = new MealGroupVO();
                vo.setCustomerId(meal.getCustomerId());
                vo.setCustomerName(meal.getCustomerName());
                vo.setMealDate(meal.getMealDate());
                vo.setMeals(new ArrayList<>());
                return vo;
            });
            groupMap.get(key).getMeals().add(meal);
        }

        //阶段四：内存分页
        List<MealGroupVO> groups = new ArrayList<>(groupMap.values());
        int total = groups.size();
        int fromIndex = (page - 1) * size;
        if (fromIndex >= total) {
            PageResult<MealGroupVO> empty = new PageResult<>();
            empty.setTotal((long) total);
            empty.setRecords(new ArrayList<>());
            return empty;
        }
        int toIndex = Math.min(fromIndex + size, total);
        List<MealGroupVO> pageRecords = groups.subList(fromIndex, toIndex);

        PageResult<MealGroupVO> result = new PageResult<>();
        result.setTotal((long) total);
        result.setRecords(pageRecords);
        return result;
    }

    /**
     * 获取菜品名称列表
     */
    private List<String> getDishNames(List<Integer> dishIds) {
        List<String> names = new ArrayList<>();
        for (Integer dishId : dishIds) {
            // 简单实现，实际应该批量查询
            names.add("菜品" + dishId);
        }
        return names;
    }

    /**
     * 复制本周膳食到下周
     */
    @Override
    @Transactional
    public int copyToNextWeek() {
        //步骤 1：计算本周日期范围
        LocalDate today = LocalDate.now();
        LocalDate thisMonday = today.with(DayOfWeek.MONDAY);
        LocalDate thisSunday = today.with(DayOfWeek.SUNDAY);

        //步骤 2：查询本周所有膳食记录
        List<MealCustom> thisWeekMeals = list(
                new LambdaQueryWrapper<MealCustom>()
                        .ge(MealCustom::getMealDate, thisMonday)
                        .le(MealCustom::getMealDate, thisSunday)
        );

        if (thisWeekMeals.isEmpty()) return 0;

        //步骤 3：批量加载关联菜品
        List<Long> mealIds = thisWeekMeals.stream()
                .map(MealCustom::getId).collect(Collectors.toList());
        List<MealCustomDish> allDishes = mealCustomDishMapper.selectByMealCustomIds(mealIds);
        Map<Long, List<MealCustomDish>> dishMap = allDishes.stream()
                .collect(Collectors.groupingBy(MealCustomDish::getMealCustomId));

        //步骤 4：遍历复制
        LocalDate now = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        int count = 0;

        for (MealCustom meal : thisWeekMeals) {
            MealCustom newMeal = new MealCustom();
            newMeal.setCustomerId(meal.getCustomerId());
            newMeal.setMealDate(meal.getMealDate().plusWeeks(1));
            newMeal.setMealType(meal.getMealType());
            newMeal.setStatus(0);
            newMeal.setCreateDate(now);
            newMeal.setCreateTime(nowTime);

            try {
                save(newMeal);  // 唯一键冲突时会抛 DuplicateKeyException
            } catch (DuplicateKeyException e) {
                continue;  // 目标餐次已存在，跳过
            }

            List<MealCustomDish> dishes = dishMap.getOrDefault(meal.getId(), new ArrayList<>());
            for (MealCustomDish dish : dishes) {
                MealCustomDish newDish = new MealCustomDish();
                newDish.setMealCustomId(newMeal.getId());
                newDish.setDishId(dish.getDishId());
                mealCustomDishMapper.insert(newDish);
            }
            count++;
        }

        return count;
    }
}
