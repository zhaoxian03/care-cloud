package com.neusoft.care.bed.service;

import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.bed.dto.BedDTO;
import com.neusoft.care.bed.entity.Bed;
import com.neusoft.care.bed.vo.BedVO;

import java.util.List;

/**
 * 床位服务接口 - 定义床位管理的业务方法
 * 
 * 功能说明：定义床位增删改查和空闲床位查询等业务接口
 * 
 * @author CareCenter Team
 */
public interface BedService {
    
    /** 获取所有空闲床位列表 */
    List<Bed> getFreeBeds();

    /** 分页查询床位列表 */
    PageResult<BedVO> pageBeds(Integer page, Integer size, String roomNumber, Integer status);

    /** 新增床位 */
    void addBed(BedDTO dto);

    /** 修改床位信息 */
    void updateBed(Long id, BedDTO dto);

    /** 删除床位 */
    void deleteBed(Long id);
}