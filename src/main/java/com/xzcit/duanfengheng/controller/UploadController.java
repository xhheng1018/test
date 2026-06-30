package com.xzcit.duanfengheng.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
public class UploadController {

    // 读取yml配置的图片存储路径
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 图片上传接口
     * @param file 上传的图片文件
     * @return 生成的图片文件名（前端保存到数据库）
     */
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        // 1. 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 2. 截取后缀 .jpg/.png
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 3. UUID防止重名
        String fileName = UUID.randomUUID() + suffix;
        // 4. 拼接完整保存路径
        File saveFile = new File(basePath + fileName);
        // 5. 写入磁盘
        file.transferTo(saveFile);
        // 返回文件名给前端
        return fileName;
    }
}