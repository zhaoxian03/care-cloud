package com.neusoft.care.service;

import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.entity.MealCustom;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.care.vo.MealGroupVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 膳食定制服务接口 —— 定义膳食管理的业务方法
 *
 * @author CareCenter Team
 */
public interface MealCustomService extends IService<MealCustom> {

    /**
     * 查询客户膳食日历
     *
     * @param customerId 客户ID
     * @param startDate  开始日期（可选）
     * @param endDate    结束日期（可选）
     * @return 膳食列表（含菜品名称）
     */
    List<MealCustom> getMealCalendar(Long customerId, LocalDate startDate, LocalDate endDate);

    /**
     * 分页查询膳食记录（按客户+日期分组后分页）
     *
     * @param page       页码
     * @param size       每页条数
     * @param customerId 客户ID（可选）
     * @param keyword    客户姓名关键词（可选）
     * @return 分页结果（MealGroupVO分组视图）
     */
    PageResult<MealGroupVO> pageMeals(Integer page, Integer size, Long customerId, String keyword);

    /**
     * 复制本周膳食到下周
     *
     * @return 复制的记录数
     */
    int copyToNextWeek();

    /**
     * 保存膳食记录及其关联菜品
     *
     * @param mealCustom 膳食记录
     * @param dishIds    菜品ID列表（可选）
     * @return 是否成功
     */
    boolean saveWithDishes(MealCustom mealCustom, List<Integer> dishIds);

    /**
     * 更新膳食记录及其关联菜品
     *
     * @param mealCustom 膳食记录
     * @param dishIds    菜品ID列表（可选）
     * @return 是否成功
     */
    boolean updateWithDishes(MealCustom mealCustom, List<Integer> dishIds);
}
