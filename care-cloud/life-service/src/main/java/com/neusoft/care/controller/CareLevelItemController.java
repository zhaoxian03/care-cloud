package com.neusoft.care.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.entity.CareLevelItem;
import com.neusoft.care.service.CareLevelItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 护理级别与项目关联表 控制层
 * 
 * 功能说明：处理护理级别与项目关联管理相关的所有HTTP请求
 * 
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/care/level-item")
public class CareLevelItemController {

    @Autowired
    private CareLevelItemService careLevelItemService;

    /**
     * 查询护理级别关联的护理项目列表
     * URL: GET /api/care/level-item/{careLevelId}
     * 权限: 需要认证
     * 
     * @param careLevelId 护理级别ID
     * @return 关联的护理项目列表
     */
    @SaCheckPermission("care:level")
    @GetMapping("/{careLevelId}")
    public Result<List<CareLevelItem>> getByCareLevelId(@PathVariable Integer careLevelId) {
        List<CareLevelItem> list = careLevelItemService.getByCareLevelId(careLevelId);
        return Result.success(list);
    }

    /**
     * 批量保存护理级别与项目的关联
     * URL: POST /api/care/level-item/{careLevelId}
     * 权限: 需要管理员权限
     * 
     * @param careLevelId 护理级别ID
     * @param careItemIds 护理项目ID列表
     * @return 是否成功
     */
    @SaCheckPermission("care:level:create")
    @PostMapping("/{careLevelId}")
    public Result<Void> saveBatch(@PathVariable Integer careLevelId, @RequestBody List<Integer> careItemIds) {
        careLevelItemService.saveBatch(careLevelId, careItemIds);
        return Result.success();
    }
}
