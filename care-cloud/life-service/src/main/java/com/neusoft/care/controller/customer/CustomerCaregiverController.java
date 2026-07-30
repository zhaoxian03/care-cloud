package com.neusoft.care.controller.customer;

import cn.dev33.satoken.stp.StpUtil;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.service.CaregiverRelationService;
import com.neusoft.care.vo.CaregiverVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * C端健康管家控制器 —— 客户/长者App查看绑定的健康管家
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/app")
public class CustomerCaregiverController {

    @Autowired
    private CaregiverRelationService caregiverRelationService;

    /**
     * 查询当前客户绑定的健康管家列表
     *
     * @return 管家列表
     */
    @GetMapping("/caregiver")
    public Result<List<CaregiverVO>> caregiver() {
        Long customerId = StpUtil.getLoginIdAsLong();
        List<CaregiverVO> caregivers = caregiverRelationService.getByCustomerId(customerId);
        return Result.success(caregivers);
    }
}
