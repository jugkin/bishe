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
}