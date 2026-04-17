package com.example.springboot.controller;


import com.example.springboot.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*") // 如果前端有跨域问题，保留此注解
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 搜索图片接口
     * URL: GET /api/images/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchImages(@RequestParam(required = false) String keyword) {
        List<String> results = fileService.searchImages(keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", results);
        response.put("total", results.size());
        return ResponseEntity.ok(response);
    }

    /**
     * 删除图片接口
     * URL: DELETE /api/images/{filename}
     */
    @DeleteMapping("/{filename:.+}") // .+ 允许文件名中包含点号 (如 image.jpg)
    public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable String filename) {
        boolean deleted = fileService.deleteImage(filename);
        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("success", true);
            response.put("message", "图片删除成功");
        } else {
            response.put("success", false);
            response.put("message", "图片删除失败，可能文件不存在或已被删除");
        }
        return ResponseEntity.ok(response);
    }
}