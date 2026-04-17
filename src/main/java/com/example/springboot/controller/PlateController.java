package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.PlateRecord;
import com.example.springboot.service.PlateService;
import com.example.springboot.service.PythonService; // 确保导入正确
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/plate")
@CrossOrigin
public class PlateController {

    @Resource
    private PlateService plateService;

    @Resource
    private PythonService pythonService;

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.error("文件名无效");
            }
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + suffix;

            // 使用项目根目录下的 uploads 文件夹
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dest = new File(uploadDir + newFileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);

            String absoluteImagePath = dest.getAbsolutePath();

            // ✅ 修正1: 调用正确的方法名 recognizePlate (不是 recognizep)
            PythonService.PlateRecognitionResult pyResult = pythonService.recognizePlate(absoluteImagePath);

            if (pyResult == null || pyResult.getPlateNumber() == null) {
                return Result.error("未能识别到有效车牌");
            }

            PlateRecord record = new PlateRecord();
            record.setPlateNumber(pyResult.getPlateNumber());
            record.setConfidence(pyResult.getConfidence());

            // ✅ 修正2: 正确处理裁剪图路径
            if (pyResult.getCroppedImageUrl() != null) {
                // croppedImageUrl 已经是相对路径，如 "/results/cropped_xxx.jpg"
                record.setCroppedImageUrl(pyResult.getCroppedImageUrl());
            }

            record.setImageUrl("/uploads/" + newFileName); // 原始图相对路径
            record.setCreateTime(LocalDateTime.now());

            plateService.saveRecord(record);

            return Result.success(record);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统内部错误：" + e.getMessage());
        }
    }

    // 👇【已修改】支持搜索和分页的 list 接口
    @GetMapping("/list")
    public Result list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String plateNumber) {

        List<PlateRecord> allRecords;
        if (plateNumber != null && !plateNumber.trim().isEmpty()) {
            allRecords = plateService.selectByPlateNumber(plateNumber);
        } else {
            allRecords = plateService.selectAll();
        }

        // 简单模拟分页（生产环境建议用 PageHelper 或 MyBatis-Plus）
        int total = allRecords.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        List<PlateRecord> pageData = (startIndex < total) ?
                allRecords.subList(startIndex, endIndex) : List.of();

        Map<String, Object> data = new HashMap<>();
        data.put("data", pageData);
        data.put("total", total);

        return Result.success(data);
    }

    // 👇【新增】删除接口
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Long id) {
        try {
            PlateRecord record = plateService.getById(id);
            if (record == null) {
                return Result.error("记录不存在");
            }

            // 删除原始图片
            deleteFileIfPossible(record.getImageUrl(), "uploads");

            // 删除裁剪图（如果存在）
            deleteFileIfPossible(record.getCroppedImageUrl(), "results");

            plateService.removeById(id);
            return Result.success("删除成功");

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    // 辅助方法：安全删除文件
    private void deleteFileIfPossible(String urlPath, String folderName) {
        if (urlPath != null && urlPath.startsWith("/" + folderName + "/")) {
            String filename = urlPath.substring(("/" + folderName + "/").length());
            String filePath = System.getProperty("user.dir") + "/" + folderName + "/" + filename;
            File file = new File(filePath);
            if (file.exists() && !file.delete()) {
                System.err.println("⚠️ 警告：物理文件删除失败: " + file.getAbsolutePath());
            }
        }
    }
}