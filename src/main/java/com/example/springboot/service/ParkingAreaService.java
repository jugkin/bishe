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

    // ✅ 修正：调用正确的方法名 selectByPageWithKeyword
    public List<ParkingArea> list(int page, int size, String keyword) {
        int offset = (page - 1) * size;
        return parkingAreaMapper.selectByPageWithKeyword(offset, size, keyword);
    }

    // ✅ 修正：调用正确的方法名 countWithKeyword
    public int count(String keyword) {
        return parkingAreaMapper.countWithKeyword(keyword);
    }

    /**
     * 新增：更新车位区域信息
     *
     * @param parkingArea 包含 id, hourlyRate, status 的对象
     * @return 是否更新成功
     */
    public boolean update(ParkingArea parkingArea) {
        int rowsAffected = parkingAreaMapper.updateById(parkingArea);
        return rowsAffected > 0;
    }

    public ParkingArea getByAreaName(String areaName) {
        return parkingAreaMapper.selectByAreaName(areaName);

    }
}