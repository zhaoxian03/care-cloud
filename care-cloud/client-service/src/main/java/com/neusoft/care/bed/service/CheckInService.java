package com.neusoft.care.bed.service;

import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.bed.dto.CheckInDTO;
import com.neusoft.care.bed.vo.CheckInVO;

/**
 * 入住/退住服务接口 - 定义入住和退住的业务方法
 * 
 * 功能说明：定义客户入住登记、退住登记和入住记录查询等业务接口
 * 
 * @author CareCenter Team
 */
public interface CheckInService {
    
    /** 入住登记 */
    void checkIn(CheckInDTO dto);

    /** 退住登记 */
    void checkOut(Long checkInId);

    /** 分页查询入住记录 */
    PageResult<CheckInVO> pageCheckIn(Integer page, Integer size, Long customerId, Integer status, String keyword);
}