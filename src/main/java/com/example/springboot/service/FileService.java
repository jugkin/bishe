package com.example.springboot.service;


import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    // 获取项目根目录下的 uploads 文件夹路径
    private final String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

    /**
     * 按关键词搜索图片
     * @param keyword 搜索关键词（文件名）
     * @return 图片文件名列表
     */
    public List<String> searchImages(String keyword) {
        File dir = new File(uploadDir);
        if (!dir.exists() || !dir.isDirectory()) {
            return List.of(); // 目录不存在，返回空列表
        }

        // 列出所有文件，并过滤出包含关键词的图片
        return Arrays.stream(dir.listFiles())
                .filter(file -> file.isFile())
                .map(File::getName)
                .filter(fileName -> StringUtils.hasText(keyword) ?
                        fileName.toLowerCase().contains(keyword.toLowerCase()) : true)
                .collect(Collectors.toList());
    }

    /**
     * 删除指定图片
     * @param filename 要删除的文件名
     * @return 是否删除成功
     */
    public boolean deleteImage(String filename) {
        if (!StringUtils.hasText(filename)) {
            return false;
        }

        // 防止路径遍历攻击 (例如: ../../etc/passwd)
        String safeFilename = Paths.get(filename).normalize().getFileName().toString();
        Path filePath = Paths.get(uploadDir, safeFilename);

        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}