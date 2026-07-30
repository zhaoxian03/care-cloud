package com.neusoft.care.user.controller;

import com.neusoft.care.common.common.Result;
import com.neusoft.care.common.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件控制器 - 处理文件上传相关的HTTP请求
 *
 * 核心逻辑：
 * 1. 接收前端上传的MultipartFile文件
 * 2. 委托MinioService上传至MinIO对象存储
 * 3. 返回文件的访问URL
 *
 * 注意事项：文件上传为公开接口（无需权限认证），大文件需配置multipart限制
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/storage")
public class FileController {

    @Autowired
    private MinioService minioService;

    /**
     * 文件上传接口
     *
     * URL: POST /api/storage/upload
     * 权限: 无需认证（公开接口）
     *
     * 核心逻辑：
     * 1. 校验文件非空
     * 2. 调用MinioService.upload上传至MinIO
     * 3. 返回文件在MinIO中的访问路径
     *
     * @param file 上传的文件（multipart/form-data）
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        try {
            String path = minioService.upload(file);
            return Result.success(path);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
