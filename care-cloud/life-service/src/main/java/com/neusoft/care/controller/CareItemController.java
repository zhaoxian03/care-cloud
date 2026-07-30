package com.neusoft.care.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.entity.CareItem;
import com.neusoft.care.entity.CareLevelItem;
import com.neusoft.care.entity.CareRecord;
import com.neusoft.care.service.CareItemService;
import com.neusoft.care.service.CareLevelItemService;
import com.neusoft.care.service.CareRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 护理项目表 控制层
 * 
 * 功能说明：处理护理项目管理相关的所有HTTP请求
 * 
 * @author CareCenter Team
 *
 * 没加参数校验和异常处理，后面记得加上
 */
@RestController
@RequestMapping("/api/care/item")
public class CareItemController {

    @Autowired
    private CareItemService careItemService;

    @Autowired
    private CareRecordService careRecordService;

    @Autowired
    private CareLevelItemService careLevelItemService;

    /**
     * 查询所有护理项目列表
     * URL: GET /api/care/item/list
     * 权限: 需要认证
     * 返回: 护理项目列表（只返回启用的）
     */
    @SaCheckPermission("care:item")
    @GetMapping("/list")
    public Result<List<CareItem>> list() {
        LambdaQueryWrapper<CareItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CareItem::getIsActive, 1);
        List<CareItem> list = careItemService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 查询所有护理项目列表（包含停用的）
     * URL: GET /api/care/item/list-all
     * 权限: 需要认证
     */
    @SaCheckPermission("care:item")
    @GetMapping("/list-all")
    public Result<List<CareItem>> listAll() {
        List<CareItem> list = careItemService.list();
        return Result.success(list);
    }

    /**
     * 分页查询护理项目
     * URL: GET /api/care/item/page
     * 权限: 需要认证
     * 
     * @param page 页码
     * @param size 每页条数
     * @return 分页结果
     */
    @SaCheckPermission("care:item")
    @GetMapping("/page")
    public Result<IPage<CareItem>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<CareItem> pageParam = new Page<>(page, size);
        IPage<CareItem> pageResult = careItemService.page(pageParam);
        return Result.success(pageResult);
    }

    /**
     * 新增护理项目
     * URL: POST /api/care/item
     * 权限: 需要管理员权限
     * 
     * @param careItem 护理项目信息
     * @return 是否成功
     */
    @SaCheckPermission("care:item:create")
    @PostMapping
    public Result<Void> save(@RequestBody CareItem careItem) {
        // 默认启用
        if (careItem.getIsActive() == null) {
            careItem.setIsActive(1);
        }
        careItemService.save(careItem);
        return Result.success();
    }

    /**
     * 修改护理项目
     * URL: PUT /api/care/item
     * 权限: 需要管理员权限
     * 
     * @param careItem 护理项目信息
     * @return 是否成功
     */
    @SaCheckPermission("care:item:edit")
    @PutMapping
    public Result<Void> update(@RequestBody CareItem careItem) {
        careItemService.updateById(careItem);
        return Result.success();
    }

    /**
     * 删除护理项目
     * URL: DELETE /api/care/item/{id}
     * 权限: 需要管理员权限
     * 
     * @param id 主键ID
     * @return 是否成功
     */
    @SaCheckPermission("care:item:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 检查是否有关联的护理记录
        LambdaQueryWrapper<CareRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(CareRecord::getCareItemId, id);
        long recordCount = careRecordService.count(recordWrapper);
        if (recordCount > 0) {
            return Result.error("该护理项目已被护理记录引用，无法删除");
        }

        // 检查是否有关联的护理等级配置
        LambdaQueryWrapper<CareLevelItem> levelItemWrapper = new LambdaQueryWrapper<>();
        levelItemWrapper.eq(CareLevelItem::getCareItemId, id);
        long levelItemCount = careLevelItemService.count(levelItemWrapper);
        if (levelItemCount > 0) {
            return Result.error("该护理项目已被护理等级配置引用，无法删除");
        }

        careItemService.removeById(id);
        return Result.success();
    }
}
