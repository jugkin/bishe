package com.example.springboot.service;

import com.example.springboot.entity.ParkingArea;
import com.example.springboot.mapper.ParkingAreaMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ParkingAreaService {

    private final ParkingAreaMapper parkingAreaMapper;

    // 构造器注入（保持原有风格）
    public ParkingAreaService(ParkingAreaMapper parkingAreaMapper) {
        this.parkingAreaMapper = parkingAreaMapper;
    }

    // --- 原有的分页和更新方法保持不变 ---

    public List<ParkingArea> list(int page, int size, String keyword) {
        int offset = (page - 1) * size;
        return parkingAreaMapper.selectByPageWithKeyword(offset, size, keyword);
    }

    public int count(String keyword) {
        return parkingAreaMapper.countWithKeyword(keyword);
    }

    public boolean update(ParkingArea parkingArea) {
        int rowsAffected = parkingAreaMapper.updateById(parkingArea);
        return rowsAffected > 0;
    }

    public ParkingArea getByAreaName(String areaName) {
        return parkingAreaMapper.selectByAreaName(areaName);
    }

    // --- 新增：专门用于更新车位的方法 ---
    // 你的 Controller 报错就是因为找不到这个方法
    public void updateArea(ParkingArea parkingArea) {
        parkingAreaMapper.updateById(parkingArea);
    }

}