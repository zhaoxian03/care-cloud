package com.neusoft.care.common.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果封装类
 * 
 * 功能说明：
 * 统一所有分页接口的返回格式
 * 
 * 使用方式：
 * 在Controller中返回分页数据时使用此类封装
 * 
 * @param <T> 记录类型
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {

    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页数据列表
     */
    private List<T> records;
}
