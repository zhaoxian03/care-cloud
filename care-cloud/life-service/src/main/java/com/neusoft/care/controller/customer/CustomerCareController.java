package com.neusoft.care.controller.customer;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.entity.CareRecord;
import com.neusoft.care.service.CareRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * C端护理记录控制器 —— 客户/长者App查看自己的护理记录
 *
 * 核心逻辑：
 * 1. 根据当前登录客户ID，分页查询该客户的护理记录
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/app")
public class CustomerCareController {

    @Autowired
    private CareRecordService careRecordService;

    /**
     * 查询当前客户的护理记录列表
     *
     * @param page 页码，默认1
     * @param size 每页条数，默认10
     * @return 分页结果
     */
    @GetMapping("/care/list")
    public Result<PageResult<CareRecord>> list(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        Long customerId = StpUtil.getLoginIdAsLong();
        IPage<CareRecord> recordPage = careRecordService.pageCareRecords(
                new Page<>(page, size), customerId, null, null);
        PageResult<CareRecord> pageResult = new PageResult<>();
        pageResult.setRecords(recordPage.getRecords());
        pageResult.setTotal(recordPage.getTotal());
        return Result.success(pageResult);
    }
}
