package com.neusoft.care.common.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.care.common.entity.FileRecord;
import com.neusoft.care.common.mapper.FileRecordMapper;
import org.springframework.stereotype.Service;

/**
 * 文件记录业务 —— 提供按 MD5+文件大小+类型 查重、保存上传记录的能力。
 * MinioService 在上传前会调用 findByMd5() 检查是否已存在，避免重复上传。
 */
@Service
public class FileRecordService extends ServiceImpl<FileRecordMapper, FileRecord> {

    /**
     * 按 MD5 + 文件大小 + 内容类型 查找已有文件记录。
     * @param md5         文件 MD5
     * @param size        文件大小
     * @param contentType 文件类型
     * @return 匹配的记录，没有则返回 null
     */
    public FileRecord findByMd5(String md5, Long size, String contentType) {
        LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileRecord::getMd5, md5);
        wrapper.eq(FileRecord::getSize, size);
        wrapper.eq(FileRecord::getContentType, contentType);
        wrapper.last("LIMIT 1");
        return getOne(wrapper, false);
    }

    /**
     * 保存一条上传记录。
     * @param md5         文件 MD5
     * @param size        文件大小
     * @param contentType 文件类型
     * @param objectName  MinIO 对象名
     * @param bucket      bucket 名称
     */
    public void saveRecord(String md5, Long size, String contentType, String objectName, String bucket) {
        FileRecord record = new FileRecord();
        record.setMd5(md5);
        record.setSize(size);
        record.setContentType(contentType);
        record.setObjectName(objectName);
        record.setBucket(bucket);
        save(record);
    }
}
