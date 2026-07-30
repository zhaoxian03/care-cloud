package com.neusoft.care.bed.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neusoft.care.bed.entity.OutRecord;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.bed.service.OutRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 外出记录控制器 —— 处理外出登记、返回和强制返回等HTTP请求
 *
 * 核心逻辑：
 * 1. 外出登记：自动设置外出日期时间，初始状态为"外出中"
 * 2. 正常返回：更新实际返回日期时间，状态改为"已返回"
 * 3. 强制返回：管理员强制执行返回操作，处理逾期未归等情况
 * 4. 分页查询：支持按客户ID、状态和关键词筛选
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/outrecord")
public class OutRecordController {

    @Autowired
    private OutRecordService outRecordService;

    /**
     * 分页查询外出记录 —— 支持按客户ID、状态和关键词筛选
     *
     * @param page       页码
     * @param size       每页条数
     * @param customerId 客户ID（可选）
     * @param status     状态（可选）：0-外出中，1-已返回，2-超时
     * @param keyword    关键词搜索（可选，模糊匹配客户姓名）
     * @return 分页外出记录结果
     */
    @SaCheckPermission("outrecord:view")
    @GetMapping("/page")
    public Result<IPage<OutRecord>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        IPage<OutRecord> pageResult = outRecordService.page(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size),
                customerId,
                status,
                keyword
        );
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询外出记录
     *
     * @param id 外出记录ID
     * @return 外出记录实体
     */
    @SaCheckPermission("outrecord:view")
    @GetMapping("/{id}")
    public Result<OutRecord> getById(@PathVariable Long id) {
        OutRecord outRecord = outRecordService.getById(id);
        return Result.success(outRecord);
    }

    /**
     * 查询所有外出记录列表
     *
     * @return 外出记录列表
     */
    @SaCheckPermission("outrecord:view")
    @GetMapping("/list")
    public Result<List<OutRecord>> list() {
        List<OutRecord> list = outRecordService.list();
        return Result.success(list);
    }

    /**
     * 新增外出登记 —— 自动设置外出日期时间和初始状态
     *
     * @param outRecord 外出记录实体
     */
    @SaCheckPermission("outrecord:create")
    @PostMapping
    public Result<Void> save(@RequestBody OutRecord outRecord) {
        // 自动设置外出日期和时间为当前
        if (outRecord.getOutDate() == null) {
            outRecord.setOutDate(LocalDate.now());
        }
        if (outRecord.getOutTime() == null) {
            outRecord.setOutTime(LocalTime.now());
        }
        // 设置初始状态为外出中
        if (outRecord.getStatus() == null) {
            outRecord.setStatus(0);
        }
        outRecordService.save(outRecord);
        return Result.success();
    }

    /**
     * 外出返回登记 —— 更新实际返回日期和时间，将状态改为"已返回"
     *
     * @param id 外出记录ID
     */
    @SaCheckPermission("outrecord:view")
    @PutMapping("/back/{id}")
    public Result<Void> updateBYID(@PathVariable Long id) {
        outRecordService.returnOutRecord(id);
        return Result.success();
    }

    /**
     * 软删除外出记录 —— 将is_deleted标记为1
     *
     * @param id 外出记录ID
     */
    @SaCheckPermission("outrecord:view")
    @DeleteMapping("/{id}")
    public Result<Void> softDelete(@PathVariable Long id) {
        boolean success = outRecordService.softDeleteOutRecord(id);

        if (!success) {
            return Result.error("删除失败");
        } else {
            return Result.success();
        }
    }

    /**
     * 强制返回 —— 管理员强制执行外出返回操作，用于处理逾期未归等情况
     *
     * @param id 外出记录ID
     */
    @SaCheckPermission("outrecord:view")
    @PutMapping("/force-back/{id}")
    public Result<Void> forceBack(@PathVariable Long id) {
        outRecordService.forceBack(id);
        return Result.success();
    }
}
