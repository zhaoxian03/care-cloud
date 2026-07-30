package com.neusoft.care.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 文件记录 —— 记录已上传文件的 MD5、大小、类型、存储路径等信息。
 * 上传前先通过 MD5+size+type 查重，相同文件不再重复上传，直接返回已有路径。
 */
@Data
@TableName("file_record")
public class FileRecord {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文件 MD5 */
    private String md5;

    /** 文件大小（字节） */
    private Long size;

    /** 文件类型（Content-Type） */
    private String contentType;

    /** MinIO 对象名（如 uuid.png） */
    private String objectName;

    /** 所属 bucket */
    private String bucket;

    /** 创建日期（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDate createDate;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalTime createTime;
}
