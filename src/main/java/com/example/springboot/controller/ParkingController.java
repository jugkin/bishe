package com.example.springboot.controller;

import com.example.springboot.entity.ParkingArea;
import com.example.springboot.entity.ParkingRecord;
import com.example.springboot.service.ParkingAreaService;
import com.example.springboot.service.ParkingRecordService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/parking-records")
public class ParkingController {

    @Autowired
    private ParkingRecordService parkingRecordService;

    @Autowired
    private ParkingAreaService parkingAreaService;

    // --- 原有接口保持不变 ---

    @GetMapping
    public List<ParkingRecord> getAll() {
        return parkingRecordService.getAllRecords();
    }

    @GetMapping("/{id}")
    public ParkingRecord getById(@PathVariable Long id) {
        return parkingRecordService.getRecordById(id);
    }

    @PostMapping
    public void create(@RequestBody ParkingRecord record) {
        parkingRecordService.addRecord(record);
    }

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

    // --- 修改后的业务接口 ---

    /**
     * 车辆入场接口
     * 修改点：增加了 occupiedSpots + 1 的逻辑
     */
    @PostMapping("/entry")
    public ResponseEntity<Map<String, Object>> vehicleEntry(@RequestBody Map<String, String> payload) {
        String plateNumber = payload.get("plateNumber");
        Map<String, Object> result = new HashMap<>();

        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            result.put("msg", "车牌号不能为空");
            return ResponseEntity.badRequest().body(result);
        }

        // 1. 随机选择区域
        String[] regions = {"东区停车场", "西区停车场", "南区停车场", "北区停车场"};
        Random random = new Random();
        String selectedRegionName = regions[random.nextInt(regions.length)];

        // 2. 检查并更新车位
        ParkingArea area = parkingAreaService.getByAreaName(selectedRegionName);
        if (area == null) {
            result.put("msg", "系统错误：找不到区域 " + selectedRegionName);
            return ResponseEntity.status(500).body(result);
        }

        if (area.getAvailableSpots() <= 0) {
            result.put("msg", "入场失败：" + selectedRegionName + " 车位已满");
            return ResponseEntity.badRequest().body(result);
        }

        // --- 车位变动逻辑 ---
        area.setAvailableSpots(area.getAvailableSpots() - 1); // 空余 -1
        // 修改点：增加已占车位 +1 (假设实体中有此字段，如果没有这行代码会报错，请确保 ParkingArea 有此字段)
        if (area.getOccupiedSpots() != null) {
            area.setOccupiedSpots(area.getOccupiedSpots() + 1);
        }
        parkingAreaService.updateArea(area);
        // ------------------

        // 3. 创建记录
        ParkingRecord record = new ParkingRecord();
        record.setPlateNumber(plateNumber);
        record.setRegionName(selectedRegionName);
        record.setEntryTime(LocalDateTime.now());
        record.setStatus(Byte.valueOf((byte) 0));

        parkingRecordService.addRecord(record);

        result.put("msg", "入场成功");
        result.put("region", selectedRegionName);
        result.put("remainingSpots", area.getAvailableSpots());
        return ResponseEntity.ok(result);
    }

    /**
     * 车辆出场接口
     * 修改点：实现了前2小时免费逻辑
     */
    @PostMapping("/exit")
    public ResponseEntity<Map<String, Object>> vehicleExit(@RequestBody Map<String, Long> payload) {
        Long id = payload.get("id");
        Map<String, Object> result = new HashMap<>();

        // 1. 查询记录
        ParkingRecord record = parkingRecordService.getRecordById(id);

        if (record == null || !record.getStatus().equals(Byte.valueOf((byte) 0))) {
            result.put("msg", "车辆不在场或记录不存在");
            return ResponseEntity.badRequest().body(result);
        }

        // 2. 获取区域费率
        ParkingArea area = parkingAreaService.getByAreaName(record.getRegionName());
        BigDecimal hourlyRate = new BigDecimal("5.00"); // 默认费率兜底
        if (area != null && area.getHourlyRate() != null) {
            hourlyRate = area.getHourlyRate();
        }

        // 3. 计算费用 (核心修改在这里)
        LocalDateTime now = LocalDateTime.now();
        long totalHours = Duration.between(record.getEntryTime(), now).toHours();
        // 如果不满1小时按1小时算（向上取整逻辑）
        if (Duration.between(record.getEntryTime(), now).toMinutes() % 60 > 0) {
            totalHours++;
        }

        // --- 计费逻辑修改：前2小时免费 ---
        // 计费时长 = 总时长 - 2。如果结果小于0，则计费时长为0。
        long chargeableHours = Math.max(0, totalHours - 2);

        // 费用 = 计费时长 * 费率
        BigDecimal fee = hourlyRate.multiply(BigDecimal.valueOf(chargeableHours));
        // -------------------------------

        // 4. 更新记录
        record.setExitTime(now);
        record.setFee(fee);
        record.setStatus(Byte.valueOf((byte) 1));
        parkingRecordService.updateRecord(record);

        // 5. 恢复车位
        if (area != null) {
            area.setAvailableSpots(area.getAvailableSpots() + 1);
            // 对应入场时的逻辑，这里需要 -1
            if (area.getOccupiedSpots() != null && area.getOccupiedSpots() > 0) {
                area.setOccupiedSpots(area.getOccupiedSpots() - 1);
            }
            parkingAreaService.updateArea(area);
        }

        // 6. 返回结果
        result.put("msg", "出场成功");
        result.put("fee", fee);
        result.put("totalHours", totalHours); // 返回总时长
        result.put("chargeableHours", chargeableHours); // 返回计费时长
        result.put("remainingSpots", area != null ? area.getAvailableSpots() : "未知");

        return ResponseEntity.ok(result);
    }

    @GetMapping("/page")
    public Map<String, Object> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) Byte status) {

        PageInfo<ParkingRecord> pageInfo = parkingRecordService.findPage(page, size, plateNumber, status);

        Map<String, Object> result = new HashMap<>();
        result.put("data", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        return result;
    }
}