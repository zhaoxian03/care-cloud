package com.neusoft.care.mapper;

import com.neusoft.care.entity.CareItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 护理项目表 Mapper 接口
 * 
 * 功能说明：继承MyBatis-Plus的BaseMapper，提供基本的CRUD操作
 * 
 * @author CareCenter Team
 */
@Mapper
public interface CareItemMapper extends BaseMapper<CareItem> {

}
