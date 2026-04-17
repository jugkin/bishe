package com.example.springboot.mapper;

import com.example.springboot.entity.ParkingArea;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ParkingAreaMapper {
    List<ParkingArea> selectAll(); // 用于测试
    List<ParkingArea> selectByPage(int offset, int limit);
    int count();
}