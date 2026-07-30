package com.neusoft.care.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.care.common.entity.Customer;
import com.neusoft.care.common.exception.BusinessException;
import com.neusoft.care.common.mapper.CustomerMapper;
import com.neusoft.care.dto.CreateSubscriptionDTO;
import com.neusoft.care.dto.RenewSubscriptionDTO;
import com.neusoft.care.entity.CustomerSubscription;
import com.neusoft.care.entity.ServiceCatalog;
import com.neusoft.care.entity.ServiceCategory;
import com.neusoft.care.mapper.CustomerSubscriptionMapper;
import com.neusoft.care.mapper.ServiceCatalogMapper;
import com.neusoft.care.mapper.ServiceCategoryMapper;
import com.neusoft.care.service.CustomerSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户订阅业务实现
 */
@Service
public class CustomerSubscriptionServiceImpl extends ServiceImpl<CustomerSubscriptionMapper, CustomerSubscription> implements CustomerSubscriptionService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ServiceCatalogMapper catalogMapper;

    @Autowired
    private ServiceCategoryMapper categoryMapper;

    /**
     * 分页查询订阅记录，支持筛选和客户姓名模糊搜索
     * @param customerId 客户ID（可选）
     * @param catalogId  服务产品ID（可选）
     * @param status     订阅状态（可选）
     * @param keyword    客户姓名关键字（可选，先查客户ID再拼入查询条件）
     */
    @Override
    public IPage<CustomerSubscription> pageSubscriptions(IPage<CustomerSubscription> page, Long customerId, Long catalogId, String status, String keyword) {
        // 1. 构建查询条件（customerId、catalogId、status、keyword）
        // 2. 如果有关键词，先查匹配的客户ID，再用 IN 条件
        // 3. 执行分页查询
        LambdaQueryWrapper<CustomerSubscription> wrapper = new LambdaQueryWrapper<>();

        //空值判断
        if (customerId != null) {
            wrapper.eq(CustomerSubscription::getCustomerId, customerId);
        }
        if (catalogId != null) {
            wrapper.eq(CustomerSubscription::getCatalogId, catalogId);
        }
        if (status != null) {
            wrapper.eq(CustomerSubscription::getStatus, status);
        }
        // 关键字在 DB 层过滤：先查匹配的客户ID，再用 IN 条件
        if (keyword != null && !keyword.isEmpty()) {
            LambdaQueryWrapper<Customer> customerWrapper = new LambdaQueryWrapper<>();
            customerWrapper.like(Customer::getRealName, keyword);
            List<Long> matchedIds = customerMapper.selectList(customerWrapper).stream()
                    .map(Customer::getId).collect(Collectors.toList());
            if (matchedIds.isEmpty()) {
                return page;
            }
            wrapper.in(CustomerSubscription::getCustomerId, matchedIds);
        }
        wrapper.orderByDesc(CustomerSubscription::getCreateDate, CustomerSubscription::getCreateTime);

        IPage<CustomerSubscription> result = page(page, wrapper);

        // 只加载当前页涉及的关联数据，避免全表扫描

        // 提取当前页所有不重复的客户 ID 即一次性批量查询客户表，放到 Map<ID, Name>
        List<Long> customerIdsInPage = result.getRecords().stream()
                .map(CustomerSubscription::getCustomerId).distinct().collect(Collectors.toList());

        Map<Long, String> customerNameMap;
        if (customerIdsInPage.isEmpty()) {
            customerNameMap = Map.of();
        } else {
            customerNameMap = customerMapper.selectBatchIds(customerIdsInPage).stream()
                    .collect(Collectors.toMap(Customer::getId, Customer::getRealName));
        }

        // 4. 批量加载关联数据：客户姓名、产品名称、分类名称、单位、价格
        //    只加载当前页涉及的 ID，避免全表扫描
        List<Long> catalogIdsInPage = result.getRecords().stream()
                .map(CustomerSubscription::getCatalogId).distinct().collect(Collectors.toList());
        Map<Long, String> catalogNameMap;
        Map<Long, Long> catalogCategoryMap;
        Map<Long, String> catalogUnitMap;
        Map<Long, BigDecimal> catalogPriceMap;
        if (catalogIdsInPage.isEmpty()) {
            catalogNameMap = Map.of();
            catalogCategoryMap = Map.of();
            catalogUnitMap = Map.of();
            catalogPriceMap = Map.of();
        } else {
            List<ServiceCatalog> catalogs = catalogMapper.selectBatchIds(catalogIdsInPage);
            catalogNameMap = catalogs.stream()
                    .collect(Collectors.toMap(ServiceCatalog::getId, ServiceCatalog::getName));
            catalogCategoryMap = catalogs.stream()
                    .collect(Collectors.toMap(ServiceCatalog::getId, ServiceCatalog::getCategoryId));
            catalogUnitMap = catalogs.stream()
                    .collect(Collectors.toMap(ServiceCatalog::getId, c -> c.getUnit() != null ? c.getUnit() : ""));
            catalogPriceMap = catalogs.stream()
                    .collect(Collectors.toMap(ServiceCatalog::getId, c -> c.getPrice() != null ? c.getPrice() : BigDecimal.ZERO));
        }

        List<Long> categoryIdsInPage = catalogCategoryMap.values().stream().distinct().collect(Collectors.toList());
        Map<Long, String> categoryNameMap;
        if (categoryIdsInPage.isEmpty()) {
            categoryNameMap = Map.of();
        } else {
            categoryNameMap = categoryMapper.selectBatchIds(categoryIdsInPage).stream()
                    .collect(Collectors.toMap(ServiceCategory::getId, ServiceCategory::getName));
        }

        // 5. 填充到订阅记录中
        for (CustomerSubscription sub : result.getRecords()) {
            sub.setCustomerName(customerNameMap.get(sub.getCustomerId()));
            sub.setCatalogName(catalogNameMap.get(sub.getCatalogId()));
            sub.setCatalogUnit(catalogUnitMap.get(sub.getCatalogId()));
            sub.setCatalogPrice(catalogPriceMap.get(sub.getCatalogId()));
            Long catId = catalogCategoryMap.get(sub.getCatalogId());
            if (catId != null) {
                sub.setCategoryName(categoryNameMap.get(catId));
            }
        }
        return result;
    }
    /**
     * 创建订阅：校验产品状态、检查是否已有生效中的订阅、计算到期日期和价格、保存记录
     * 使用 @Transactional 保证数据一致性
     *
     * @param dto 订阅创建请求
     * @return 创建后的订阅记录
     * @throws BusinessException 产品不存在/已下架/已有生效中的订阅/日期校验失败
     */
    @Override
    @Transactional
    public CustomerSubscription create(CreateSubscriptionDTO dto) {
        // 1. 校验产品存在且启用
        ServiceCatalog catalog = catalogMapper.selectById(dto.getCatalogId());
        if (catalog == null || catalog.getIsDeleted() == 1) {
            throw new BusinessException("服务产品不存在");
        }
        if (catalog.getIsActive() == null || catalog.getIsActive() != 1) {
            throw new BusinessException("该服务产品已下架");
        }

        // 检查同一客户+同一服务是否已有生效中的订阅
        LambdaQueryWrapper<CustomerSubscription> dup = new LambdaQueryWrapper<>();
        dup.eq(CustomerSubscription::getCustomerId, dto.getCustomerId())
           .eq(CustomerSubscription::getCatalogId, dto.getCatalogId())
           .eq(CustomerSubscription::getStatus, "ACTIVE");
        // 2. 检查是否已有生效中的订阅（防止重复订阅同一产品）
        if (count(dup) > 0) {
            throw new BusinessException("该服务已订阅且仍在生效中，不可重复订阅");
        }

        // 3. 计算到期日期（根据计价单位 + 时长）
        //    - day/week/month/year：按单位累加
        //    - once：endDate = null（单次服务无到期日）
        //    - long：endDate = null（长期服务无到期日）
        //    - 默认按月计算
        LocalDate endDate = dto.getEndDate();
        if (endDate == null && dto.getDuration() != null && dto.getDuration() > 0) {
            String unit = catalog.getUnit();
            LocalDate start = dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now();
            switch (unit != null ? unit : "") {
                case "day": endDate = start.plusDays(dto.getDuration()); break;
                case "week": endDate = start.plusWeeks(dto.getDuration()); break;
                case "month": endDate = start.plusMonths(dto.getDuration()); break;
                case "year": endDate = start.plusYears(dto.getDuration()); break;
                case "once": endDate = null; break;
                case "long": endDate = null; break;
                default: endDate = start.plusMonths(dto.getDuration());
            }
        }
        if (endDate != null && dto.getStartDate() != null && endDate.isBefore(dto.getStartDate())) {
            throw new BusinessException("到期日期不能早于开始日期");
        }

        // 4. 计算价格（单价 × 时长）
        BigDecimal price = catalog.getPrice() != null ? catalog.getPrice() : BigDecimal.ZERO;
        String unit = catalog.getUnit();
        if (dto.getDuration() != null && dto.getDuration() > 1
                && !"once".equals(unit) && !"long".equals(unit)) {
            price = price.multiply(new BigDecimal(dto.getDuration()));
        }

        // 5. 保存订阅记录
        CustomerSubscription sub = new CustomerSubscription();
        sub.setCustomerId(dto.getCustomerId());
        sub.setCatalogId(dto.getCatalogId());
        sub.setStartDate(dto.getStartDate());
        sub.setEndDate(endDate);
        sub.setPrice(price);
        sub.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        save(sub);
        return sub;
    }

    /**
     * 取消订阅：将状态改为 CANCELLED（保留记录用于历史查看）
     * @param id 订阅ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        // 取消订阅：仅将状态改为 CANCELLED，保留记录用于历史查看
        CustomerSubscription sub = getById(id);
        if (sub == null || sub.getIsDeleted() == 1) {
            throw new BusinessException("订阅记录不存在");
        }
        sub.setStatus("CANCELLED");
        updateById(sub);
    }

    /**
     * 续期：更新到期日期（校验新日期不能早于当前到期日）
     * @param id  订阅ID
     * @param dto 包含新的到期日期
     */
    @Override
    @Transactional
    public void renew(Long id, RenewSubscriptionDTO dto) {
        CustomerSubscription sub = getById(id);
        if (sub == null || sub.getIsDeleted() == 1) {
            throw new BusinessException("订阅记录不存在");
        }
        if (!"ACTIVE".equals(sub.getStatus())) {
            throw new BusinessException("仅活跃状态的订阅可以续期");
        }

        LocalDate currentEnd = sub.getEndDate() != null ? sub.getEndDate() : LocalDate.now();
        LocalDate newEndDate;

        if (dto.getDuration() != null && dto.getDuration() > 0) {
            ServiceCatalog catalog = catalogMapper.selectById(sub.getCatalogId());
            if (catalog == null) {
                throw new BusinessException("服务产品不存在");
            }
            String unit = catalog.getUnit();
            if (unit == null) {
                throw new BusinessException("服务未设置计价单位");
            }
            switch (unit) {
                case "day":
                    newEndDate = currentEnd.plusDays(dto.getDuration());
                    break;
                case "week":
                    newEndDate = currentEnd.plusWeeks(dto.getDuration());
                    break;
                case "month":
                    newEndDate = currentEnd.plusMonths(dto.getDuration());
                    break;
                case "year":
                    newEndDate = currentEnd.plusYears(dto.getDuration());
                    break;
                case "once":
                    throw new BusinessException("单次服务不支持续期");
                case "long":
                    throw new BusinessException("长期服务无需续期");
                default:
                    newEndDate = currentEnd.plusMonths(dto.getDuration());
            }
            BigDecimal unitPrice = catalog.getPrice() != null ? catalog.getPrice() : BigDecimal.ZERO;
            sub.setPrice(unitPrice.multiply(new BigDecimal(dto.getDuration())));
        } else if (dto.getNewEndDate() != null) {
            newEndDate = dto.getNewEndDate();
            if (currentEnd != null && newEndDate.isBefore(currentEnd)) {
                throw new BusinessException("新到期日期不能早于当前到期日期");
            }
        } else {
            throw new BusinessException("请提供续约时长或新到期日期");
        }

        sub.setEndDate(newEndDate);
        updateById(sub);
    }

    /**
     * 获取即将到期的订阅列表（供仪表盘/通知使用）
     * @param days 提前天数，如 7 表示未来 7 天内到期
     */
    @Override
    public List<CustomerSubscription> getExpiringSoon(int days) {
        // 查询指定天数内即将到期的活跃订阅，用于到期提醒
        LocalDate threshold = LocalDate.now().plusDays(days);
        LambdaQueryWrapper<CustomerSubscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerSubscription::getStatus, "ACTIVE")
                .le(CustomerSubscription::getEndDate, threshold)
                .orderByAsc(CustomerSubscription::getEndDate);
        return list(wrapper);
    }
}
