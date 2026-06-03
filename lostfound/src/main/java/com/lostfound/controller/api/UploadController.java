package com.lostfound.controller.api;

import com.lostfound.common.Result;
import com.lostfound.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传 REST API 控制器
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 上传图片（富文本编辑器使用）
     */
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadService.uploadImage(file);
            return Result.success("上传成功", url);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (IOException e) {
            return Result.serverError("文件上传失败");
        }
    }
}
