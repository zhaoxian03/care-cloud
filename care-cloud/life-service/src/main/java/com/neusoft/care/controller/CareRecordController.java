package com.neusoft.care.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.entity.CareRecord;
import com.neusoft.care.service.CareRecordService;
import com.neusoft.care.vo.CareStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 护理记录表 控制层
 * 
 * 功能说明：处理护理记录管理相关的所有HTTP请求
 * 
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/care/record")
public class CareRecordController {

    @Autowired
    private CareRecordService careRecordService;

    /**
     * 添加护理记录
     * URL: POST /api/care/record
     * 权限: 需要管理员权限（护理人员）
     * 
     * @param careRecord 护理记录信息
     * @return 是否成功
     */
    @SaCheckPermission("care:record:create")
    @PostMapping
    public Result<Void> save(@RequestBody CareRecord careRecord) {
        // 自动设置护理员ID为当前登录管理员
        if (careRecord.getAdminId() == null) {
            careRecord.setAdminId(StpUtil.getLoginIdAsLong());
        }
        careRecordService.save(careRecord);
        return Result.success();
    }

    /**
     * 分页查询全部护理记录
     * URL: GET /api/care/record/page
     * 权限: 需要认证
     * 
     * @param page   页码
     * @param size   每页条数
     * @param status 状态筛选（可选，0-待执行，1-执行中，2-已完成）
     * @return 分页结果
     */
    @SaCheckPermission("care:record")
    @GetMapping("/page")
    public Result<IPage<CareRecord>> pageAllCareRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        IPage<CareRecord> pageResult = careRecordService.pageAllCareRecords(
                new Page<>(page, size), status, keyword);
        return Result.success(pageResult);
    }

    /**
     * 分页查询指定客户的护理记录
     * URL: GET /api/care/record/page/{customerId}
     * 权限: 需要认证
     * 
     * @param customerId 客户ID
     * @param page       页码
     * @param size       每页条数
     * @param status     状态筛选（可选，0-待执行，1-执行中，2-已完成）
     * @return 分页结果
     */
    @SaCheckPermission("care:record")
    @GetMapping("/page/{customerId}")
    public Result<IPage<CareRecord>> pageCareRecords(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        IPage<CareRecord> pageResult = careRecordService.pageCareRecords(
                new Page<>(page, size), customerId, status, keyword);
        return Result.success(pageResult);
    }

    /**
     * 删除护理记录
     * URL: DELETE /api/care/record/{id}
     * 权限: 需要管理员权限
     * 
     * @param id 记录ID
     * @return 是否成功
     */
    @SaCheckPermission("care:record")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        careRecordService.removeById(id);
        return Result.success();
    }

    /**
     * 更新护理记录状态
     * URL: PUT /api/care/record/{id}/status
     * 权限: 需要管理员权限
     * 
     * @param id     记录ID
     * @param status 状态（0-待执行，1-执行中，2-已完成）
     * @return 是否成功
     */
    @SaCheckPermission("care:record")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        CareRecord careRecord = careRecordService.getById(id);

        //判断status状态是否正确
        if( status == null || status <0 || status >2){
            return Result.error("无效的状态值");
        }


        if (careRecord == null) {
            return Result.error("护理记录不存在");
        }
        careRecord.setStatus(status);
        careRecordService.updateById(careRecord);
        return Result.success();
    }

    /**
     * 更新护理记录
     * URL: PUT /api/care/record
     * 权限: 需要管理员权限
     * 
     * @param careRecord 护理记录信息
     * @return 是否成功
     */
    @SaCheckPermission("care:record")
    @PutMapping
    public Result<Void> update(@RequestBody CareRecord careRecord) {
        careRecordService.updateById(careRecord);
        return Result.success();
    }

    /**
     * 批量删除护理记录
     * URL: DELETE /api/care/record/batch
     * 权限: 需要管理员权限
     * 
     * @param ids 记录ID列表
     * @return 是否成功
     */
    @SaCheckPermission("care:record")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        careRecordService.removeByIds(ids);
        return Result.success();
    }

    /**
     * 护理统计
     * URL: GET /api/care/record/stats
     * 权限: 需要认证
     * 
     * @return 统计数据
     */
    @SaCheckPermission("care:record")
    @GetMapping("/stats")
    public Result<CareStatsVO> getStats() {
        CareStatsVO stats = careRecordService.getStats();
        return Result.success(stats);
    }
}
