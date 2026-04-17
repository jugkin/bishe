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

    // ========================
    // 核心业务逻辑：车辆入场
    // ========================
    public ParkingRecord handleEntry(String plateNumber, String regionName) {
        LocalDateTime now = LocalDateTime.now();

        // 1. 保存识别记录到 plate_record 表 (可选，用于审计)
        PlateRecord plate = new PlateRecord();
        plate.setPlateNumber(plateNumber);
        plate.setCreateTime(now);
        plateMapper.insert(plate);

        // 2. 【关键修改】检查该车牌是否已经在 ANY 区域处于"在场"状态
        // 这里调用的是 findAnyActiveByPlate，而不是 findActiveByPlate
        ParkingRecord anyActive = parkingRecordMapper.findAnyActiveByPlate(plateNumber);
        if (anyActive != null) {
            // 抛出异常，Controller 会捕获并返回错误信息
            throw new RuntimeException("车辆 " + plateNumber + " 已在 [" + anyActive.getRegionName() + "] 区域停车，禁止重复入场！");
        }

        // 3. 创建新的停车记录
        ParkingRecord record = new ParkingRecord();
        record.setPlateNumber(plateNumber);
        record.setRegionName(regionName); // 记录本次入场的具体区域
        record.setEntryTime(now);
        record.setStatus((byte) 0); // 0 = 在场
        record.setCreateTime(now);
        record.setUpdateTime(now);
        parkingRecordMapper.insert(record);

        return record; // 返回新创建的记录
    }

    // ========================
    // 核心业务逻辑：车辆出场
    // ========================
    public ParkingRecord handleExit(String plateNumber) {
        LocalDateTime now = LocalDateTime.now();

        // 1. 保存出口识别记录
        PlateRecord plate = new PlateRecord();
        plate.setPlateNumber(plateNumber);
        plate.setCreateTime(now);
        plateMapper.insert(plate);

        // 2. 【关键修改】查找该车牌在 ANY 区域的"在场"记录
        ParkingRecord activeRecord = parkingRecordMapper.findAnyActiveByPlate(plateNumber);
        if (activeRecord == null) {
            throw new RuntimeException("未找到车辆 " + plateNumber + " 的在场记录，无法出场！");
        }

        // 3. 计算费用（示例：5元/小时）
        long minutes = Duration.between(activeRecord.getEntryTime(), now).toMinutes();
        // 使用 Math.ceil 确保不足一小时按一小时计费，并保留两位小数
        BigDecimal fee = BigDecimal.valueOf(Math.ceil(minutes / 60.0) * 5).setScale(2, BigDecimal.ROUND_HALF_UP);

        // 4. 更新记录
        activeRecord.setExitTime(now);
        activeRecord.setFee(fee);
        activeRecord.setStatus((byte) 1); // 1 = 已离场
        activeRecord.setUpdateTime(now);
        parkingRecordMapper.updateById(activeRecord);

        return activeRecord; // 返回更新后的记录
    }

    // ========================
    // 新增：支持分页和条件查询
    // ========================
    public List<ParkingRecord> queryRecords(String plateNumber, Byte status) {
        // 此方法将被 PageHelper 拦截，自动添加分页逻辑
        return parkingRecordMapper.findByCondition(plateNumber, status);
    }

    // ========================
    // 保留基础 CRUD (供管理接口使用)
    // ========================
    public List<ParkingRecord> getAllRecords() {
        return parkingRecordMapper.findAll();
    }

    public ParkingRecord getRecordById(Long id) {
        return parkingRecordMapper.findById(id);
    }

    public void addRecord(ParkingRecord record) {
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