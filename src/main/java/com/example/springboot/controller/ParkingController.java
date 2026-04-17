package com.example.springboot.controller;

import com.example.springboot.entity.ParkingRecord;
import com.example.springboot.service.ParkingRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 停车记录控制器
 * 提供两套接口：
 * 1. 基础 CRUD: 用于后台管理
 * 2. 业务接口 (/recognize/entry, /recognize/exit): 用于处理车牌识别事件
 */
@RestController
@RequestMapping("/api/parking-records")
public class ParkingController {

    @Autowired
    private ParkingRecordService parkingRecordService;

    // ========================
    // 第一部分：基础 CRUD 接口 (用于后台管理)
    // ========================

    /**
     * 获取所有停车记录 (带分页和条件查询)
     * 用于前端页面的加载、查询、重置功能
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) Byte status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        // 1. 使用 PageHelper 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 2. 调用 Service 层进行条件查询
        List<ParkingRecord> records = parkingRecordService.queryRecords(plateNumber, status);

        // 3. 封装分页信息和数据
        PageInfo<ParkingRecord> pageInfo = new PageInfo<>(records);
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList()); // 当前页数据
        result.put("total", pageInfo.getTotal()); // 总条数
        result.put("pageNum", pageInfo.getPageNum()); // 当前页码
        result.put("pageSize", pageInfo.getPageSize()); // 每页大小
        result.put("pages", pageInfo.getPages()); // 总页数

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ParkingRecord getById(@PathVariable Long id) {
        return parkingRecordService.getRecordById(id);
    }

    // 注意：通常不建议对外暴露这个原始的 create 接口，因为它绕过了业务逻辑。
    // 如果确实需要，请确保前端传入的数据是完整的。
    // @PostMapping
    // public void create(@RequestBody ParkingRecord record) {
    //     parkingRecordService.addRecord(record);
    // }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        parkingRecordService.deleteRecord(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<String> update(
            @PathVariable Long id,
            @RequestBody ParkingRecord record) {
        record.setId(id);
        parkingRecordService.updateRecord(record);
        return ResponseEntity.ok("更新成功");
    }

    // ========================
    // 第二部分：业务接口 (用于车牌识别)
    // ========================

    /**
     * 车牌识别 - 入场
     * 硬件或前端识别到车牌后，调用此接口完成入场流程。
     * 请求体示例: {"plateNumber": "皖AD09292", "regionName": "东区"}
     */
    @PostMapping("/recognize/entry")
    public ResponseEntity<Map<String, Object>> recognizeEntry(@RequestBody Map<String, String> payload) {
        String plateNumber = payload.get("plateNumber");
        String regionName = payload.get("regionName");

        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "车牌号不能为空"));
        }
        if (regionName == null || regionName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "区域名称不能为空"));
        }

        try {
            // 调用 Service 层的业务方法，该方法内部会处理重复入场校验
            ParkingRecord record = parkingRecordService.handleEntry(plateNumber, regionName);
            return ResponseEntity.ok(Map.of(
                    "message", "入场成功",
                    "data", record
            ));
        } catch (Exception e) {
            // 捕获业务异常（如车辆已在场），并返回给调用方
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 车牌识别 - 出场
     * 硬件或前端识别到车牌后，调用此接口完成出场流程。
     * 请求体示例: {"plateNumber": "皖AD09292"}
     */
    @PostMapping("/recognize/exit")
    public ResponseEntity<Map<String, Object>> recognizeExit(@RequestBody Map<String, String> payload) {
        String plateNumber = payload.get("plateNumber");

        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "车牌号不能为空"));
        }

        try {
            // 调用 Service 层的业务方法
            ParkingRecord record = parkingRecordService.handleExit(plateNumber);
            return ResponseEntity.ok(Map.of(
                    "message", "出场成功",
                    "data", record
            ));
        } catch (Exception e) {
            // 捕获业务异常（如车辆未在场），并返回给调用方
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}