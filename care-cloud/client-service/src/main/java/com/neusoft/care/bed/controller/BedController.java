package com.neusoft.care.bed.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neusoft.care.bed.service.BedService;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.bed.dto.BedDTO;
import com.neusoft.care.bed.entity.Bed;
import com.neusoft.care.bed.vo.BedVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 床位控制器 - 处理床位管理相关的所有HTTP请求
 * 
 * 功能说明：
 * 1. 获取空闲床位列表：用于入住登记时选择床位
 * 2. 床位分页查询：支持按房间号和状态筛选
 * 3. 新增床位：添加新的床位信息
 * 4. 修改床位：更新床位信息
 * 5. 删除床位：删除指定床位
 * 
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/bed")
public class BedController {

    @Autowired
    private BedService bedService;

    /**
     * 获取空闲床位列表接口
     * URL: GET /api/bed/free
     * 权限: 需要认证
     * 返回: 空闲床位列表（status=0）
     */
    @SaCheckPermission("bed:view")
    @GetMapping("/free")
    public Result<List<Bed>> getFreeBeds() {
        return Result.success(bedService.getFreeBeds());
    }

    /**
     * 床位分页查询接口
     * URL: GET /api/bed/page
     * 权限: 需要认证
     * 返回: 分页床位列表
     */
    @SaCheckPermission("bed:view")
    @GetMapping("/page")
    public Result<PageResult<BedVO>> pageBeds(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Integer status) {
        return Result.success(bedService.pageBeds(page, size, roomNumber, status));
    }

    /**
     * 新增床位接口
     * URL: POST /api/bed
     * 权限: 仅管理员
     */
    @SaCheckPermission("bed:create")
    @PostMapping
    public Result<Void> addBed(@Valid @RequestBody BedDTO dto) {
        bedService.addBed(dto);
        return Result.success();
    }

    /**
     * 修改床位接口
     * URL: PUT /api/bed/{id}
     * 权限: 仅管理员
     */
    @SaCheckPermission("bed:edit")
    @PutMapping("/{id}")
    public Result<Void> updateBed(@PathVariable Long id, @Valid @RequestBody BedDTO dto) {
        bedService.updateBed(id, dto);
        return Result.success();
    }

    /**
     * 删除床位接口
     * URL: DELETE /api/bed/{id}
     * 权限: 仅管理员
     */
    @SaCheckPermission("bed:delete")
    @DeleteMapping("/{id}")
    public Result<Void> deleteBed(@PathVariable Long id) {
        bedService.deleteBed(id);
        return Result.success();
    }
}
