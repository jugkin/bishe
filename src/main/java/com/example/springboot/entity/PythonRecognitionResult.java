package com.example.springboot.entity;




import java.time.LocalDateTime;

public class PythonRecognitionResult{
    private Long id;
    private String licensePlate; // 车牌号
    private LocalDateTime entryTime; // 入场时间
    private LocalDateTime exitTime; // 出场时间
    private Double fee; // 费用

    // Getter 和 Setter（此处省略，实际需生成）
    // ... (可使用 Lombok @Data 或 IDE 自动生成)
}