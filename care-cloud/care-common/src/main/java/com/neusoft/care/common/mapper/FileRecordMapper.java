package com.neusoft.care.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.common.entity.FileRecord;

/**
 * 文件记录 Mapper —— 提供 file_record 表的基础 CRUD 操作。
 * 配合 FileRecordService 使用，上传前查重 + 上传后落库。
 */
public interface FileRecordMapper extends BaseMapper<FileRecord> {
}
