package com.example.springboot.entity;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ParkingArea {
    private Long id;
    private String areaName;        // 对应前端 prop="Carname"
    private Integer totalSpots;     // 总车位数（可选）
    private Integer availableSpots; // 对应前端 prop="Carfree"
    private Integer occupiedSpots;  // 对应前端 prop="Caruse"
    private BigDecimal hourlyRate;  // 对应前端 prop="Hourprice"
    private Byte status;            // 0=禁用, 1=启用；对应前端 prop="State" (需转换)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String statusDesc;

    // 👇 可选：提供一个辅助方法，方便设置（非必须，但更优雅）
    public void setStatusDescFromStatus() {
        if (this.status != null) {
            this.statusDesc = this.status == 1 ? "可用" : "禁用";
        } else {
            this.statusDesc = "未知";
        }
    }


}
