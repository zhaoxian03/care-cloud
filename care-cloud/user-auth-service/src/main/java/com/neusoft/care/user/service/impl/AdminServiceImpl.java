package com.neusoft.care.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.dto.AdminLoginDTO;
import com.neusoft.care.user.dto.CreateAdminDTO;
import com.neusoft.care.user.entity.Admin;
import com.neusoft.care.user.entity.AdminRole;
import com.neusoft.care.user.entity.Permission;
import com.neusoft.care.user.entity.Role;
import com.neusoft.care.user.mapper.AdminMapper;
import com.neusoft.care.user.mapper.AdminRoleMapper;
import com.neusoft.care.user.mapper.RoleMapper;
import com.neusoft.care.user.service.PermissionService;
import com.neusoft.care.user.util.Mask;
import com.neusoft.care.user.vo.AdminLoginVO;
import com.neusoft.care.user.vo.AdminVO;
import com.neusoft.care.user.vo.MenuNode;
import com.neusoft.care.user.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 管理员服务实现类 - 实现管理员相关的所有业务逻辑
 *
 * 核心逻辑：
 * 1. 管理员登录：Redis登录失败次数限制（5次/5min）→ 账号/手机号查询 → BCrypt密码验证 → 状态校验 → SaToken登录 → 加载RBAC权限并缓存到Redis → 返回Token+菜单树
 * 2. 创建管理员：账号唯一校验 → BCrypt编码密码 → 插入Admin记录（不可创建超级管理员）
 * 3. 创建健康管家：与创建管理员类似，roleLevel固定为"caregiver"
 * 4. 分页查询：关键词搜索 + 批量查询角色名称
 * 5. 状态更新：禁用时写Redis禁用标记，@CacheEvict清除缓存
 * 6. 删除：不能删除自己/超级管理员，@CacheEvict清除缓存
 *
 * 注意事项：
 * - 登录使用SaToken，权限码缓存到Redis供StpInterface鉴权时读取（同Token过期时间7200秒）
 * - 管理员信息缓存使用Spring Cache（@Cacheable/@CacheEvict），TTL 5分钟
 * - Redis不可用时所有功能降级可用（try-catch保护）
 *
 * @author CareCenter Team
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private PermissionService permissionService;

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AdminRoleMapper adminRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 管理员登录
     *
     * 核心逻辑：
     * 1. Redis检查登录失败次数（key=Login:fail:{username}，达5次则限流5分钟）
     * 2. 支持用户名或手机号登录
     * 3. BCrypt密码验证，失败时Redis失败计数+1
     * 4. 状态校验（status=1为启用）
     * 5. 登录成功后清除Redis失败计数
     * 6. SaToken登录 → 加载RBAC权限列表 → 缓存权限码和角色码到Redis → 构建菜单树 → 返回Token+菜单
     *
     * @param dto 登录DTO（用户名/手机号 + 密码）
     * @return 管理员登录响应VO（含Token、菜单树）
     * @throws RuntimeException 登录失败次数过多/账号不存在/密码错误/账号被禁用时抛出
     */
    @Override
    public AdminLoginVO login(AdminLoginDTO dto) {

        if(redisTemplate != null){
            String failKey = "Login:fail:"+dto.getUsername();
            String failCount = null;
            try {
                 failCount = redisTemplate.opsForValue().get(failKey);
            } catch (Exception e) {
            }

            if (failCount != null && Integer.parseInt(failCount) >= 5) {
                throw new RuntimeException("登录失败次数过多，请稍后再试");
            }

        }
        
        String identifier = dto.getUsername();

        //先按账号查询
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, identifier);
        Admin admin = adminMapper.selectOne(wrapper);

        //如果按账号查询不到，再按手机号查询
        if (admin == null) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Admin::getPhone, identifier);
            admin = adminMapper.selectOne(wrapper);
        }

        if (admin == null) {
            throw new RuntimeException("账号或密码错误");
        }

        //密码验证（BCrypt）
        if(!passwordEncoder.matches(dto.getPassword(), admin.getPassword())){
            if(redisTemplate != null ){
                try{
                    String failKey = "Login:fail:"+dto.getUsername();
                    redisTemplate.opsForValue().increment(failKey);
                    redisTemplate.expire(failKey, 5, TimeUnit.MINUTES);
                } catch (Exception e){
                }
            }
            throw new RuntimeException("账号或密码错误");
        }

        if (admin.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        if(redisTemplate != null){
            try {
                String failKey = "Login:fail:"+dto.getUsername();
                redisTemplate.delete(failKey);
            } catch (Exception e){
            }
        }

        StpUtil.login(admin.getId());

        //根据当前登录的管理员 ID，动态构建出专属的“后台菜单树”，最终返回给前端渲染左侧导航栏
        List<Permission> permissions = permissionService.getPermissionsByAdminId(admin.getId());
        List<Permission> menuPermissions = permissionService.buildMenuTree(permissions);
        List<MenuNode> menuNodes = convertToMenuNodes(menuPermissions);



        if (redisTemplate != null) {
            //权限码缓存--管理员权限码和角色码写入 Redis  先删后加
            try {
                Set<String> codes = permissions.stream()
                        .map(Permission::getCode).collect(Collectors.toSet());
                String codeKey = "admin:codes:" + admin.getId();
                redisTemplate.delete(codeKey);
                redisTemplate.opsForSet().add(codeKey, codes.toArray(new String[0]));
            } catch (Exception e) {
            }
            //角色码缓存--管理员角色码写入 Redis  先删后加
            try {
                List<Long> roleIds = adminRoleMapper.selectList(
                        new LambdaQueryWrapper<AdminRole>().eq(AdminRole::getAdminId, admin.getId())
                ).stream().map(AdminRole::getRoleId).collect(Collectors.toList());
                if (!roleIds.isEmpty()) {
                    List<Role> roles = roleMapper.selectBatchIds(roleIds);
                    Set<String> roleCodes = roles.stream()
                            .map(Role::getCode).collect(Collectors.toSet());
                    String roleKey = "admin:roles:" + admin.getId();
                    redisTemplate.delete(roleKey);
                    redisTemplate.opsForSet().add(roleKey, roleCodes.toArray(new String[0]));
                }
            } catch (Exception e) {
            }
        }

        String token = StpUtil.getTokenValue();

        AdminLoginVO vo = new AdminLoginVO();
        vo.setId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setRealName(admin.getRealName());
        vo.setRoleLevel(admin.getRoleLevel());
        vo.setToken(token);
        vo.setMenu(menuNodes);
        return vo;
    }

    /**
     * 管理员退出登录
     *
     * 核心逻辑：调用SaToken退出，清除当前会话
     */
    @Override
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 获取当前登录管理员信息
     *
     * 核心逻辑：
     * 1. 从SaToken获取当前登录管理员ID
     * 2. 查询Admin实体 + 管理员角色关联 + 角色名称
     * 3. 手机号脱敏后返回
     *
     * 缓存策略：使用Spring Cache（@Cacheable），key=管理员ID，缓存名=admin:info:v2，TTL 5分钟
     * 缓存清除：状态变更/删除时通过@CacheEvict清除
     *
     * @return 管理员信息VO（手机号已脱敏）
     * @throws RuntimeException 管理员不存在时抛出
     */
    @Override
    @Cacheable(value = "admin:info:v2", key = "T(cn.dev33.satoken.stp.StpUtil).getLoginIdAsLong()", unless = "#result == null")
    public AdminVO getCurrentAdmin() {
        Long adminId = StpUtil.getLoginIdAsLong();
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }

        AdminVO vo = new AdminVO();
        vo.setId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setRealName(admin.getRealName());
        vo.setPhone(Mask.maskPhone(admin.getPhone()));
        vo.setRoleLevel(admin.getRoleLevel());
        vo.setStatus(admin.getStatus());
        vo.setCreatorId(admin.getCreatorId());

        List<AdminRole> adminRoles = adminRoleMapper.selectList(
                new LambdaQueryWrapper<AdminRole>().eq(AdminRole::getAdminId, adminId));
        if (!adminRoles.isEmpty()) {
            List<Long> roleIds = adminRoles.stream().map(AdminRole::getRoleId).collect(Collectors.toList());
            List<Role> roles = roleMapper.selectBatchIds(roleIds);
            vo.setRoleNames(roles.stream().map(Role::getName).collect(Collectors.toList()));
        }

        return vo;
    }

    /**
     * 刷新Token
     *
     * 核心逻辑：调用SaToken续期Token有效期至7200秒
     *
     * @return 新的Token值
     */
    @Override
    public String refreshToken() {
        StpUtil.renewTimeout(7200);
        return StpUtil.getTokenValue();
    }

    /**
     * 创建管理员
     *
     * 核心逻辑：
     * 1. 校验账号（username）唯一性
     * 2. 禁止创建super_admin级别的管理员
     * 3. BCrypt编码密码，设置默认状态为启用（status=1）
     * 4. 记录创建者ID、创建时间
     *
     * 权限控制：仅超级管理员可调用（由Controller层@SaCheckPermission控制）
     *
     * @param dto 创建管理员请求DTO（含username、password、realName、phone）
     * @param creatorId 创建者ID（当前登录管理员）
     * @throws RuntimeException 账号已存在或试图创建超级管理员时抛出
     */
    @Override
    @Transactional
    public void createAdmin(CreateAdminDTO dto, Long creatorId) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, dto.getUsername());
        Long count = adminMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("账号已存在");
        }

        String roleLevel = "admin";
        if ("super_admin".equals(dto.getRoleLevel())) {
            throw new RuntimeException("不能创建超级管理员");
        }

        Admin admin = new Admin();
        admin.setUsername(dto.getUsername());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setRealName(dto.getRealName());
        admin.setPhone(dto.getPhone());
        admin.setRoleLevel(roleLevel);
        admin.setStatus(1);
        admin.setCreatorId(creatorId);
        admin.setCreateDate(LocalDate.now());
        admin.setCreateTime(LocalTime.now());
        admin.setUpdateDate(LocalDate.now());
        admin.setUpdateTime(LocalTime.now());
        adminMapper.insert(admin);
    }

    /**
     * 创建健康管家
     *
     * 核心逻辑：
     * 1. 校验账号唯一性
     * 2. 自动设置roleLevel为"caregiver"（不可选择级别）
     * 3. BCrypt编码密码，设置默认状态为启用（status=1）
     *
     * 权限控制：管理员及以上均可操作（由Controller层@SaCheckPermission("caregiver:create")控制）
     *
     * @param dto 创建请求DTO（username、password、realName、phone）
     * @param creatorId 创建者ID
     * @throws RuntimeException 账号已存在时抛出
     */
    @Override
    @Transactional
    public void createCaregiver(CreateAdminDTO dto, Long creatorId) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, dto.getUsername());
        Long count = adminMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("账号已存在");
        }

        Admin admin = new Admin();
        admin.setUsername(dto.getUsername());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setRealName(dto.getRealName());
        admin.setPhone(dto.getPhone());
        admin.setRoleLevel("caregiver");
        admin.setStatus(1);
        admin.setCreatorId(creatorId);
        admin.setCreateDate(LocalDate.now());
        admin.setCreateTime(LocalTime.now());
        admin.setUpdateDate(LocalDate.now());
        admin.setUpdateTime(LocalTime.now());
        adminMapper.insert(admin);
    }

    /**
     * 分页查询管理员列表
     *
     * 核心逻辑：
     * 1. 支持按姓名或用户名模糊搜索
     * 2. 按创建时间倒序排列
     * 3. 批量查询角色名称填充到VO
     *
     * @param page 页码
     * @param size 每页条数
     * @param keyword 搜索关键词（匹配姓名或用户名，可为空）
     * @return 分页结果（含角色名称列表）
     */
    @Override
    public PageResult<AdminVO> pageAdmins(Integer page, Integer size, String keyword) {
        Page<Admin> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Admin::getRealName, keyword).or().like(Admin::getUsername, keyword));
        }
        wrapper.orderByDesc(Admin::getCreateTime);

        IPage<Admin> adminPage = adminMapper.selectPage(pageParam, wrapper);

        List<AdminVO> records = adminPage.getRecords().stream().map(admin -> {
            AdminVO vo = new AdminVO();
            vo.setId(admin.getId());
            vo.setUsername(admin.getUsername());
            vo.setRealName(admin.getRealName());
            vo.setPhone(Mask.maskPhone(admin.getPhone()));
            vo.setRoleLevel(admin.getRoleLevel());
            vo.setStatus(admin.getStatus());
            vo.setCreatorId(admin.getCreatorId());
            return vo;
        }).collect(Collectors.toList());

        if (!records.isEmpty()) {
            List<Long> adminIds = records.stream().map(AdminVO::getId).collect(Collectors.toList());
            LambdaQueryWrapper<AdminRole> arWrapper = new LambdaQueryWrapper<>();
            arWrapper.in(AdminRole::getAdminId, adminIds);
            List<AdminRole> adminRoles = adminRoleMapper.selectList(arWrapper);

            if (!adminRoles.isEmpty()) {
                Set<Long> roleIds = adminRoles.stream().map(AdminRole::getRoleId).collect(Collectors.toSet());
                LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
                roleWrapper.in(Role::getId, roleIds);
                Map<Long, String> roleNameMap = roleMapper.selectList(roleWrapper).stream()
                        .collect(Collectors.toMap(Role::getId, Role::getName));

                Map<Long, List<String>> adminRoleNamesMap = new HashMap<>();
                for (AdminRole ar : adminRoles) {
                    String roleName = roleNameMap.get(ar.getRoleId());
                    if (roleName != null) {
                        adminRoleNamesMap.computeIfAbsent(ar.getAdminId(), k -> new ArrayList<>()).add(roleName);
                    }
                }

                for (AdminVO vo : records) {
                    vo.setRoleNames(adminRoleNamesMap.getOrDefault(vo.getId(), Collections.emptyList()));
                }
            }
        }

        PageResult<AdminVO> result = new PageResult<>();
        result.setTotal(adminPage.getTotal());
        result.setRecords(records);
        return result;
    }

    /**
     * 更新管理员状态
     *
     * 核心逻辑：
     * 1. 校验管理员是否存在
     * 2. 更新状态
     * 3. 若为禁用操作（status=0），向Redis写入禁用标记（key=admin:disable:{adminId}）
     * 4. 通过@CacheEvict清除该管理员的信息缓存
     *
     * 缓存策略：更新后清除admin:info:v2缓存，确保下次获取最新状态
     *
     * @param adminId 管理员ID
     * @param status 目标状态（1-启用，0-禁用）
     * @throws RuntimeException 管理员不存在时抛出
     */
    @Override
    @Transactional
    @CacheEvict(value = "admin:info:v2", key = "#adminId")
    public void updateAdminStatus(Long adminId, Integer status) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }

        admin.setStatus(status);
        adminMapper.updateById(admin);

        if(status == 0 && redisTemplate != null){
            try{
                String disableKey = "admin:disable:" + adminId;
                redisTemplate.opsForValue().set(
                        disableKey, "1",7200,
                        TimeUnit.SECONDS
                );
            } catch (Exception e){
            }
        }
    }

    /**
     * 删除管理员
     *
     * 核心逻辑：
     * 1. 不能删除自己
     * 2. 不能删除超级管理员
     * 3. 物理删除
     *
     * 缓存策略：删除后清除admin:info:v2缓存
     *
     * @param adminId 要删除的管理员ID
     * @param currentAdminId 当前操作者ID
     * @throws RuntimeException 尝试删除自己/管理员不存在/尝试删除超级管理员时抛出
     */
    @Override
    @Transactional
    @CacheEvict(value = "admin:info:v2", key = "#adminId")
    public void deleteAdmin(Long adminId, Long currentAdminId) {
        if (adminId.equals(currentAdminId)) {
            throw new RuntimeException("不能删除自己");
        }

        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }

        if ("super_admin".equals(admin.getRoleLevel())) {
            throw new RuntimeException("不能删除超级管理员");
        }

        adminMapper.deleteById(adminId);
    }

    /**
     * 将权限树结构转换为菜单节点树
     *
     * 核心逻辑：递归遍历Permission树，映射为MenuNode结构
     *
     * @param permissions 树形权限列表（含children）
     * @return 菜单节点树列表
     */
    private List<MenuNode> convertToMenuNodes(List<Permission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptyList();
        }
        List<MenuNode> nodes = new ArrayList<>();
        for (Permission p : permissions) {
            MenuNode node = new MenuNode();
            node.setId(p.getId());
            node.setName(p.getName());
            node.setCode(p.getCode());
            node.setType(p.getType());
            node.setPath(p.getPath());
            node.setIcon(p.getIcon());
            if (p.getChildren() != null && !p.getChildren().isEmpty()) {
                node.setChildren(convertToMenuNodes(p.getChildren()));
            }
            nodes.add(node);
        }
        return nodes;
    }

}
