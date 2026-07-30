package com.neusoft.care.common.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.neusoft.care.common.config.MinioProperties;
import com.neusoft.care.common.entity.FileRecord;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MinIO 存储服务 —— 封装文件上传逻辑，支持 MD5 去重。
 * 上传前计算文件 MD5，在 file_record 表中查重；
 * 已存在则直接返回旧路径，不存在则上传到 MinIO 并记录。
 * 返回的路径为相对路径：{bucket}/{objectName}
 * 添加 @ConditionalOnBean，只有 MinioClient Bean 存在时才加载（即仅含 minio profile 的服务）。
 */
@Service
@ConditionalOnBean(MinioClient.class)
public class MinioService {

    private static final Logger log = LoggerFactory.getLogger(MinioService.class);

    private final MinioClient minioClient;
    private final MinioProperties props;
    private final FileRecordService fileRecordService;

    public MinioService(MinioClient minioClient, MinioProperties props, FileRecordService fileRecordService) {
        this.minioClient = minioClient;
        this.props = props;
        this.fileRecordService = fileRecordService;
    }

    /**
     * 上传文件到 MinIO。
     * 先通过 MD5 去重，相同文件直接返回已有路径，避免重复存储。
     * 文件名采用 "yyyyMMddHHmmssSSS + 6位随机数 + 后缀" 格式，防止冲突。
     *
     * @param file 上传的 multipart 文件
     * @return 相对路径，如 "care-center/20260629220000123456.png"
     */
    public String upload(MultipartFile file) throws Exception {
        // 计算 MD5（Hutool），try-with-resources 确保流关闭以免 Windows 删不掉临时文件
        String md5;
        try (InputStream is = file.getInputStream()) {
            md5 = SecureUtil.md5(is);
        }
        long size = file.getSize();
        String contentType = file.getContentType();

        // 查重：MD5 + 大小 + 类型 都匹配则直接返回已有路径
        FileRecord record = fileRecordService.findByMd5(md5, size, contentType);
        if (record != null) {
            log.info("文件已存在，直接返回已有路径: bucket={}, object={}", record.getBucket(), record.getObjectName());
            return record.getBucket() + "/" + record.getObjectName();
        }

        // 生成唯一文件名：时间戳 + 6 位随机数 + 原后缀
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String objectName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + RandomUtil.randomNumbers(6)
                + ext;

        // 上传到 MinIO
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(props.getBucket())
                    .object(objectName)
                    .stream(is, size, -1)
                    .contentType(contentType)
                    .build());
        }

        // 保存文件记录
        fileRecordService.saveRecord(md5, size, contentType, objectName, props.getBucket());

        log.info("MinIO 上传成功: bucket={}, object={}", props.getBucket(), objectName);
        return props.getBucket() + "/" + objectName;
    }

    /**
     * 获取 MinIO 中对象的输入流（用于后端代理下载）。
     *
     * @param bucket     bucket 名称
     * @param objectName 对象名
     * @return 文件输入流
     */
    public InputStream download(String bucket, String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build());
    }
}
