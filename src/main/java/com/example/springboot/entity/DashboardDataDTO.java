package com.example.springboot.entity;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 数据大屏专用 DTO
 */
@Data
public class DashboardDataDTO {

    /**
     * 区域卡片列表
     * 展示：区域名、总车位、空闲、占用、单价、状态
     */
    private List<AreaCardVO> areaCards;

    /**
     * 饼图数据
     * Key: 区域名称, Value: 已占用数量
     */
    private Map<String, Integer> pieChartData;

    /**
     * 折线图数据
     * Key: 日期 (如 "2023-10-01"), Value: 数量
     */
    private Map<String, Integer> trendChartData;

    /**
     * 实时滚动列表
     */
    private List<ParkingRecord> recentRecords;

    // 内部类：区域卡片视图对象
    @Data
    public static class AreaCardVO {
        private Long id;
        private String areaName;
        private int totalSpaces;   // 总车位
        private int availableSpaces; // 剩余车位
        private int occupiedSpaces;  // 已占车位
        private Double price;      // 单价
        private String status;     // 状态
    }
}