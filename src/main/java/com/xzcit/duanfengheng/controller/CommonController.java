package com.xzcit.duanfengheng.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 新增缺失导入
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 菜品图片上传
     */
    @PostMapping("/upload")
    public String upload(MultipartFile file, Model model) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileName = "";
        // 修复substring空指针警告：判断文件名不为空
        if (originalFileName != null && originalFileName.length() > 0) {
            String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            fileName = UUID.randomUUID() + suffix;
        }

        File dir = new File(basePath);
        // 接收mkdirs返回值消除警告
        boolean isCreated = dir.exists() || dir.mkdirs();
        file.transferTo(new File(basePath + fileName));
        model.addAttribute("imgFileName", fileName);
        return "redirect:/dish/toAdd";
    }

    /**
     * 图片回显下载
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        FileInputStream fis = new FileInputStream(basePath + name);
        ServletOutputStream os = response.getOutputStream();
        response.setContentType("image/jpeg");
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        fis.close();
        os.flush();
        os.close();
    }
}