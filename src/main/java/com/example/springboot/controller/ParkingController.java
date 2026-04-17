package com.example.springboot.controller;

import com.example.springboot.entity.ParkingRecord;
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

    // --- 新增的业务接口 ---

    /**
     * 车辆入场接口
     */
    @PostMapping("/entry")
    public ResponseEntity<String> vehicleEntry(@RequestBody Map<String, String> payload) {
        String plateNumber = payload.get("plateNumber");

        // TODO: 这里可以添加更详细的校验逻辑，例如：
        // 1. 检查车牌号是否为空
        // 2. 检查该车牌是否已经在场 (status=0)，防止重复入场

        ParkingRecord record = new ParkingRecord();
        record.setPlateNumber(plateNumber);
        // 示例：设置一个默认的区域，实际项目中此处逻辑需完善
        String[] regions = {"东区停车场", "西区停车场", "南区停车场", "北区停车场"};

// 2. 随机抽取一个 (0 到 3)
        Random random = new Random();
        int randomIndex = random.nextInt(regions.length);

// 3. 赋值给记录
        record.setRegionName(regions[randomIndex]);
        record.setEntryTime(LocalDateTime.now());
        // 修复：显式将 int 0 转换为 Byte
        record.setStatus(Byte.valueOf((byte) 0)); // 0 代表 "在场"

        parkingRecordService.addRecord(record);
        return ResponseEntity.ok("入场成功");
    }

    /**
     * 车辆出场接口
     */
    @PostMapping("/exit")
    public ResponseEntity<String> vehicleExit(@RequestBody Map<String, Long> payload) {
        Long id = payload.get("id");

        // 1. 查询记录
        ParkingRecord record = parkingRecordService.getRecordById(id);
        // 修复：使用 .equals() 比较 Byte 对象的值
        if (record == null || !record.getStatus().equals(Byte.valueOf((byte) 0))) {
            return ResponseEntity.badRequest().body("车辆不在场或记录不存在");
        }

        // 2. 计算费用 (示例逻辑: 5元/小时，最低收费5元)
        LocalDateTime now = LocalDateTime.now();
        long hours = Duration.between(record.getEntryTime(), now).toHours();
        // 注意：这是一个非常简化的计费逻辑，实际业务会复杂得多
        double fee = Math.max(5, hours * 5);

        // 3. 更新记录
        record.setExitTime(now);
        // 修复：使用 String 构造 BigDecimal，确保精度，并格式化为两位小数
        record.setFee(new BigDecimal(String.format("%.2f", fee)));
        // 修复：显式将 int 1 转换为 Byte
        record.setStatus(Byte.valueOf((byte) 1)); // 1 代表 "已离场"
        parkingRecordService.updateRecord(record);

        return ResponseEntity.ok("出场成功");
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