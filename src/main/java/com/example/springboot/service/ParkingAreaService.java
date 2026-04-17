package com.example.springboot.service;

import com.example.springboot.entity.ParkingArea;
import com.example.springboot.mapper.ParkingAreaMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingAreaService {

    private final ParkingAreaMapper parkingAreaMapper;

    public ParkingAreaService(ParkingAreaMapper parkingAreaMapper) {
        this.parkingAreaMapper = parkingAreaMapper;
    }

    public List<ParkingArea> list(int page, int size) {
        int offset = (page - 1) * size;
        return parkingAreaMapper.selectByPage(offset, size);
    }

    public int count() {
        return parkingAreaMapper.count();
    }

    /**
     * 新增：更新车位区域信息
     * @param parkingArea 包含 id, hourlyRate, status 的对象
     * @return 是否更新成功
     */
    public boolean update(ParkingArea parkingArea) {
        // 调用 MyBatis-Plus 提供的 updateById 方法
        // 它会根据实体中的 id 字段，更新所有非空字段
        int rowsAffected = parkingAreaMapper.updateById(parkingArea);
        return rowsAffected > 0;
    }
}