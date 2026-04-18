package com.example.springboot.mapper;

import com.example.springboot.entity.ParkingArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ParkingAreaMapper {
    List<ParkingArea> selectAll(); // 用于测试

    // <-- 保留原有的分页方法（可选，如果确定没地方用可以删掉）
    List<ParkingArea> selectByPage(int offset, int limit);



    // <-- 新增：支持关键词搜索的分页方法
    List<ParkingArea> selectByPageWithKeyword(@Param("offset") int offset,
                                              @Param("limit") int limit,
                                              @Param("keyword") String keyword);

    // <-- 保留原有的 count 方法（可选，如果确定没地方用可以删掉）
    int count();

    // <-- 新增：支持关键词搜索的 count 方法
    int countWithKeyword(@Param("keyword") String keyword);

    int updateById(ParkingArea parkingArea);
    @Select("SELECT * FROM parking_area WHERE area_name = #{areaName}")
    ParkingArea selectByAreaName(@Param("areaName") String areaName);

}