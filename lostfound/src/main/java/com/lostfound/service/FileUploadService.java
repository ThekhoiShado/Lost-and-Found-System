package com.lostfound.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件上传服务
 */
@Service
public class FileUploadService {

    @Value("${upload.local-path:./uploads}")
    private String uploadPath;

    @Value("${upload.url-prefix:/uploads}")
    private String urlPrefix;

    /** 允许的图片类型 */
    private static final String[] ALLOWED_IMAGE_TYPES = {
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"
    };

    /** 最大文件大小 10MB */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 上传图片文件
     *
     * @param file 上传的文件
     * @return 文件的访问URL
     */
    public String uploadImage(MultipartFile file) throws IOException {
        // 校验文件类型
        String contentType = file.getContentType();
        boolean allowed = false;
        for (String type : ALLOWED_IMAGE_TYPES) {
            if (type.equals(contentType)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new IllegalArgumentException("不支持的图片格式，仅支持 JPG、PNG、GIF、WebP、BMP");
        }

        // 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("图片大小不能超过10MB");
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;

        // 按日期分目录存储
        String dateDir = java.time.LocalDate.now().toString().replace("-", "/");
        Path dirPath = Paths.get(uploadPath, dateDir);
        Files.createDirectories(dirPath);

        // 保存文件
        Path filePath = dirPath.resolve(filename);
        file.transferTo(filePath.toFile());

        // 返回访问URL
        String relativePath = dateDir + "/" + filename;
        return urlPrefix + "/" + relativePath;
    }

    /**
     * 删除上传的文件
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(urlPrefix)) {
            return;
        }
        String relativePath = fileUrl.substring(urlPrefix.length() + 1);
        Path filePath = Paths.get(uploadPath, relativePath);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 删除失败仅记录日志
        }
    }
}
