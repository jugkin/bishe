package com.example.springboot.service;

import com.example.springboot.entity.ParkingRecord;
import com.example.springboot.entity.PlateRecord;
import com.example.springboot.mapper.ParkingRecordMapper;
import com.example.springboot.mapper.PlateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ParkingRecordService {

    @Autowired
    private PlateMapper plateMapper;

    @Autowired
    private ParkingRecordMapper parkingRecordMapper;

    public void handleEntry(String plateNumber, String regionName, LocalDateTime now) {
        // 1. 保存识别记录（可选：如果入口不存 plate_record，可跳过）
        PlateRecord plate = new PlateRecord();
        plate.setPlateNumber(plateNumber);
        plate.setCreateTime(now);
        plateMapper.insert(plate); // 存入 plate_record

        // 2. 检查是否已在场
        ParkingRecord active = parkingRecordMapper.findActiveByPlate(plateNumber);
        if (active != null) {
            throw new RuntimeException("车辆 " + plateNumber + " 已在场内！");
        }

        // 3. 创建停车记录
        ParkingRecord record = new ParkingRecord();
        record.setPlateNumber(plateNumber);
        record.setRegionName(regionName);
        record.setEntryTime(now);
        record.setStatus((byte) 0); // 0 = 在场
        parkingRecordMapper.insert(record);
    }

    public void handleExit(String plateNumber, LocalDateTime now) {
        // 1. 保存出口识别记录
        PlateRecord plate = new PlateRecord();
        plate.setPlateNumber(plateNumber);
        plate.setCreateTime(now);
        plateMapper.insert(plate);

        // 2. 查找在场记录
        ParkingRecord active = parkingRecordMapper.findActiveByPlate(plateNumber);
        if (active == null) {
            throw new RuntimeException("未找到车辆 " + plateNumber + " 的在场记录！");
        }

        // 3. 计算费用（示例）
        long minutes = Duration.between(active.getEntryTime(), now).toMinutes();
        BigDecimal fee = BigDecimal.valueOf(Math.ceil(minutes / 60.0) * 5); // 5元/小时

        // 4. 更新停车记录
        active.setExitTime(now);
        active.setFee(fee);
        active.setStatus((byte) 1); // 1 = 已离场
        parkingRecordMapper.updateById(active);
    }
    // ====== 新增：基础 CRUD 方法（供 Controller 调用）======
    public List<ParkingRecord> getAllRecords() {
        return parkingRecordMapper.findAll();
    }

    public ParkingRecord getRecordById(Long id) {
        return parkingRecordMapper.findById(id);
    }

    public void addRecord(ParkingRecord record) {

        // 可选：这里可以复用 handleEntry 的逻辑，或直接插入
        // 为简单起见，我们直接调用 Mapper 插入
        LocalDateTime now = LocalDateTime.now();
        record.setCreateTime(now);
        record.setUpdateTime(now);
        parkingRecordMapper.insert(record);
    }

    public void updateRecord(ParkingRecord record) {

        record.setUpdateTime(LocalDateTime.now());
        parkingRecordMapper.update(record);
    }

    public void deleteRecord(Long id) {
        parkingRecordMapper.deleteById(id);
    }


}
