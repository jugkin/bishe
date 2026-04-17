package com.example.springboot.mapper;

import com.example.springboot.entity.PlateRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PlateMapper {
    // 只保留方法声明，移除所有 @Select, @Insert, @Delete 注解

    List<PlateRecord> selectAll();

    void insert(PlateRecord record);

    PlateRecord selectById(Long id);

    void deleteById(Long id);

    List<PlateRecord> selectByPlateNumber(@Param("plateNumber") String plateNumber);
}