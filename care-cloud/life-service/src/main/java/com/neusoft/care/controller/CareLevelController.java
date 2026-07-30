package com.neusoft.care.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.entity.CareLevel;
import com.neusoft.care.entity.CareLevelItem;
import com.neusoft.care.service.CareLevelItemService;
import com.neusoft.care.service.CareLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 护理级别表 控制层
 *
 * @author neusoft
 */
@RestController
@RequestMapping("/api/carelevel")
public class CareLevelController {

    @Autowired
    private CareLevelService careLevelService;

    @Autowired
    private CareLevelItemService careLevelItemService;

    /**
     * 分页查询护理级别表
     *
     * @param page 页码
     * @param size 每页条数
     * @return 分页结果
     */
    @SaCheckPermission("care:level")
    @GetMapping("/page")
    public Result<IPage<CareLevel>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<CareLevel> pageResult = careLevelService.page(new Page<>(page, size));
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询护理级别表
     *
     * @param id 主键ID
     * @return 护理级别表详情
     */
    @SaCheckPermission("care:level")
    @GetMapping("/{id}")
    public Result<CareLevel> getById(@PathVariable Long id) {
        CareLevel careLevel = careLevelService.getById(id);
        return Result.success(careLevel);
    }

    /**
     * 查询所有护理级别表
     *
     * @return 护理级别表列表
     */
    @SaCheckPermission("care:level")
    @GetMapping("/list")
    public Result<List<CareLevel>> list() {
        List<CareLevel> list = careLevelService.list();
        return Result.success(list);
    }

    /**
     * 新增护理级别表
     *
     * @param careLevel 护理级别表信息
     * @return 是否成功
     */
    @SaCheckPermission("care:level:create")
    @PostMapping
    public Result<Void> save(@RequestBody CareLevel careLevel) {
        careLevelService.save(careLevel);
        return Result.success();
    }

    /**
     * 修改护理级别表
     *
     * @param careLevel 护理级别表信息
     * @return 是否成功
     */
    @SaCheckPermission("care:level:edit")
    @PutMapping
    public Result<Void> update(@RequestBody CareLevel careLevel) {
        careLevelService.updateById(careLevel);
        return Result.success();
    }

    /**
     * 删除护理级别表
     *
     * @param id 主键ID
     * @return 是否成功
     */
    @SaCheckPermission("care:level:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 检查是否有关联的护理等级配置
        LambdaQueryWrapper<CareLevelItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CareLevelItem::getCareLevelId, id);
        long count = careLevelItemService.count(wrapper);
        if (count > 0) {
            return Result.error("该护理等级已配置护理项目，请先清除配置后再删除");
        }

        careLevelService.removeById(id);
        return Result.success();
    }
}
