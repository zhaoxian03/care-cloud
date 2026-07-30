package com.neusoft.care.service.impl;

import com.neusoft.care.entity.CareItem;
import com.neusoft.care.mapper.CareItemMapper;
import com.neusoft.care.service.CareItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 护理项目表 服务实现
 * 
 * 功能说明：实现护理项目管理的所有业务逻辑
 * 
 * @author CareCenter Team
 */
@Service
public class CareItemServiceImpl extends ServiceImpl<CareItemMapper, CareItem> implements CareItemService {

}
