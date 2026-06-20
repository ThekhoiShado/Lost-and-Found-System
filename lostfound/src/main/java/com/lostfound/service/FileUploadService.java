package com.lostfound.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 文件上传服务
 */
@Slf4j
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
        // 校验文件类型（兼容 contentType 为 null 的情况）
        String contentType = file.getContentType();
        boolean allowed = false;
        if (contentType != null) {
            for (String type : ALLOWED_IMAGE_TYPES) {
                if (type.equals(contentType)) {
                    allowed = true;
                    break;
                }
            }
        }
        // contentType 为 null 时，通过文件扩展名兜底校验
        if (!allowed) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                String lower = originalFilename.toLowerCase();
                if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                    allowed = true;
                } else if (lower.endsWith(".png")) {
                    contentType = "image/png";
                    allowed = true;
                } else if (lower.endsWith(".gif")) {
                    contentType = "image/gif";
                    allowed = true;
                } else if (lower.endsWith(".webp")) {
                    contentType = "image/webp";
                    allowed = true;
                } else if (lower.endsWith(".bmp")) {
                    contentType = "image/bmp";
                    allowed = true;
                }
            }
        }
        if (!allowed) {
            throw new IllegalArgumentException("不支持的图片格式，仅支持 JPG、PNG、GIF、WebP、BMP");
        }

        // 校验文件是否为空
        if (file.isEmpty() || file.getSize() <= 0) {
            throw new IllegalArgumentException("文件不能为空");
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

        // 保存文件（使用 Files.copy 替代 transferTo，Spring 7.0 下后者可能异常）
        Path filePath = dirPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        log.info("文件已保存: {} ({} bytes)", filePath, file.getSize());

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
