package com.neusoft.care.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.dto.BindCaregiverDTO;
import com.neusoft.care.service.CaregiverRelationService;
import com.neusoft.care.service.CaregiverService;
import com.neusoft.care.vo.CaregiverManageVO;
import com.neusoft.care.vo.CaregiverVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 健康管家关联控制器 —— 管理端管家绑定/解绑/查询接口
 *
 * 核心逻辑：
 * 1. 绑定客户与健康管家，校验管家身份和绑定量上限
 * 2. 按客户ID或管家ID查询绑定关系列表
 * 3. 管家管理：分页查询、状态启用/禁用、删除
 *
 * 注意事项：绑定操作在 CaregiverRelationServiceImpl 中标注了 @Transactional
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/caregiver")
public class CaregiverRelationController {

    @Autowired
    private CaregiverRelationService caregiverRelationService;

    @Autowired
    private CaregiverService caregiverService;

    /**
     * 绑定客户与健康管家
     *
     * @param dto 绑定请求（客户ID、管家ID）
     * @return 操作结果
     */
    @SaCheckPermission("caregiver:create")
    @PostMapping("/bind")
    public Result<Void> bind(@Valid @RequestBody BindCaregiverDTO dto) {
        caregiverRelationService.bind(dto);
        return Result.success();
    }

    /**
     * 解绑健康管家
     *
     * @param id 关联记录ID
     * @return 操作结果
     */
    @SaCheckPermission("caregiver:delete")
    @DeleteMapping("/{id}")
    public Result<Void> unbind(@PathVariable Long id) {
        caregiverRelationService.unbind(id);
        return Result.success();
    }

    /**
     * 按客户ID查询绑定的健康管家列表
     *
     * @param customerId 客户ID
     * @return 管家列表
     */
    @SaCheckPermission("caregiver:view")
    @GetMapping("/customer/{customerId}")
    public Result<List<CaregiverVO>> getByCustomerId(@PathVariable Long customerId) {
        List<CaregiverVO> list = caregiverRelationService.getByCustomerId(customerId);
        return Result.success(list);
    }

    /**
     * 按管家ID查询绑定的客户列表
     *
     * @param adminId 管家ID
     * @return 客户列表
     */
    @SaCheckPermission("caregiver:view")
    @GetMapping("/admin/{adminId}")
    public Result<List<CaregiverVO>> getByAdminId(@PathVariable Long adminId) {
        List<CaregiverVO> list = caregiverRelationService.getByAdminId(adminId);
        return Result.success(list);
    }

    /**
     * 分页查询健康管家列表（管理端）
     *
     * @param page    页码，默认1
     * @param size    每页条数，默认10
     * @param keyword 姓名/手机号关键词（可选）
     * @param status  状态筛选（可选，1-启用，0-禁用）
     * @return 分页结果
     */
    @SaCheckPermission("caregiver:view")
    @GetMapping("/page")
    public Result<PageResult<CaregiverManageVO>> pageCaregivers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<CaregiverManageVO> result = caregiverService.pageCaregivers(page, size, keyword, status);
        return Result.success(result);
    }

    /**
     * 获取所有可用的健康管家列表（已启用、可被绑定）
     *
     * @return 管家列表
     */
    @SaCheckPermission("caregiver:view")
    @GetMapping("/available")
    public Result<List<CaregiverManageVO>> getAvailableCaregivers() {
        List<CaregiverManageVO> list = caregiverService.getAvailableCaregivers();
        return Result.success(list);
    }

    /**
     * 根据ID查询健康管家详情
     *
     * @param id 管家ID
     * @return 管家详情
     */
    @SaCheckPermission("caregiver:view")
    @GetMapping("/{id}")
    public Result<CaregiverManageVO> getCaregiver(@PathVariable Long id) {
        CaregiverManageVO vo = caregiverService.getById(id);
        return Result.success(vo);
    }

    /**
     * 更新管家姓名和手机号
     *
     * @param id   管家ID
     * @param body 请求体（realName, phone）
     * @return 操作结果
     */
    @SaCheckPermission("caregiver:edit")
    @PutMapping("/{id}")
    public Result<Void> updateCaregiver(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String realName = body.get("realName");
        String phone = body.get("phone");
        caregiverService.update(id, realName, phone);
        return Result.success();
    }

    /**
     * 更新管家启用/禁用状态
     *
     * @param id     管家ID
     * @param status 状态（1-启用，0-禁用）
     * @return 操作结果
     */
    @SaCheckPermission("caregiver:status")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        caregiverService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 删除健康管家（将status置为0禁用）
     *
     * @param id 管家ID
     * @return 操作结果
     */
    @SaCheckPermission("caregiver:delete")
    @DeleteMapping("/account/{id}")
    public Result<Void> deleteCaregiver(@PathVariable Long id) {
        caregiverService.deleteCaregiver(id);
        return Result.success();
    }
}
