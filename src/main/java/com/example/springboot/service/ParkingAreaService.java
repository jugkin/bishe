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
}