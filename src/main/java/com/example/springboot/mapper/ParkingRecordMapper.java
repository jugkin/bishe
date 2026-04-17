package com.example.springboot.mapper;

import com.example.springboot.entity.ParkingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ParkingRecordMapper {

    void insert(ParkingRecord record);

    // ========================
    // 【关键】新增方法 1：查询任意区域中状态为“在场”(status=0) 的车辆记录
    // 注意：方法名是 findAnyActiveByPlate，不是 findActiveByPlate
    // SQL 中没有限定 region_name，确保全局唯一性检查
    // 添加 LIMIT 1 提高效率
    @Select("SELECT * FROM parking_record WHERE plate_number = #{plateNumber} AND status = 0 LIMIT 1")
    ParkingRecord findAnyActiveByPlate(@Param("plateNumber") String plateNumber);

    // ========================
    // 【关键】新增方法 2：支持分页和条件查询（车牌号模糊 + 状态精确）
    // 使用 MyBatis 动态 SQL (<script> + <if>)
    @Select("<script>" +
            "SELECT * FROM parking_record " +
            "WHERE 1=1 " +
            "<if test='plateNumber != null and plateNumber != \"\"'>" +
            "AND plate_number LIKE CONCAT('%', #{plateNumber}, '%') " +
            "</if>" +
            "<if test='status != null'>" +
            "AND status = #{status} " +
            "</if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    List<ParkingRecord> findByCondition(
            @Param("plateNumber") String plateNumber,
            @Param("status") Byte status
    );

    // ========================
    // 原有方法（保持不变）
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