package com.example.springboot.controller;

import com.example.springboot.entity.ParkingArea;
import com.example.springboot.service.ParkingAreaService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parking-areas")
public class ParkingAreaController {

    private final ParkingAreaService parkingAreaService;

    public ParkingAreaController(ParkingAreaService parkingAreaService) {
        this.parkingAreaService = parkingAreaService;
    }

    @GetMapping
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) { // <-- 新增这一行

        // <-- 修改这一行，将 keyword 传入 service
        List<ParkingArea> list = parkingAreaService.list(page, size, keyword);
        // <-- 修改这一行，将 keyword 传入 count
        int total = parkingAreaService.count(keyword);

        // 将 status 转为中文状态
        for (ParkingArea area : list) {
            area.setStatusDescFromStatus(); // 调用我们刚加的方法
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        result.put("total", total);
        return result;
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody ParkingArea parkingArea) {
        // 1. 调用 Service 执行更新
        boolean success = parkingAreaService.update(parkingArea);

        // 2. 构建响应（保持与 list 接口一致的风格）
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("msg", "更新成功");
        } else {
            result.put("msg", "更新失败");
        }
        return result;
    }

}