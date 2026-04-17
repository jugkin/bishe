package com.example.springboot.mapper;


// 文件路径: src/main/java/com/example/springboot/mapper/ParkingRecordMapper.java


import com.example.springboot.entity.ParkingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface ParkingRecordMapper {
    void insert(ParkingRecord record);

    // 查询 status=0（在场）且 plate_number 匹配的记录
    @Select("SELECT * FROM parking_record WHERE plate_number = #{plateNumber} AND status = 0")
    ParkingRecord findActiveByPlate(@Param("plateNumber") String plateNumber);

    @Update("UPDATE parking_record SET " +
            "exit_time = #{exitTime}, " +
            "fee = #{fee}, " +
            "status = #{status}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    void updateById(ParkingRecord record);



    List<ParkingRecord> findAll();
    ParkingRecord findById(Long id);
    void update(ParkingRecord record);
    void deleteById(Long id);
}