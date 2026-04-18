package com.example.springboot.mapper;


// 文件路径: src/main/java/com/example/springboot/mapper/ParkingRecordMapper.java


import com.example.springboot.entity.ParkingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;


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
    List<ParkingRecord> findByCondition(@Param("plateNumber") String plateNumber, @Param("status") Byte status);


    List<ParkingRecord> findAll();
    ParkingRecord findById(Long id);
    void update(ParkingRecord record);
    void deleteById(Long id);
    // --- 新增：查询最新 N 条记录 (用于实时列表) ---
    @Select("SELECT * FROM parking_record ORDER BY create_time DESC LIMIT #{limit}")
    List<ParkingRecord> selectRecentRecords(@Param("limit") int limit);

    // --- 新增：查询近 10 天每天的记录总数 (用于折线图) ---
    // 注意：这里假设有一个 create_time 字段。MySQL 语法，如果是其他数据库需调整日期函数
    @Select("SELECT DATE(create_time) as date, COUNT(*) as count " +
            "FROM parking_record " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 10 DAY) " +
            "GROUP BY DATE(create_time) ORDER BY date ASC")
    List<Map<String, Object>> countLast10Days();

}