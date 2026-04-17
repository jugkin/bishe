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
            @RequestParam(defaultValue = "10") int size) {

        List<ParkingArea> list = parkingAreaService.list(page, size);
        int total = parkingAreaService.count();

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