package com.example.springboot.entity;

import java.time.LocalDateTime;

// 对应数据库表 plate_record
public class PlateRecord {

    private Long id;
    private String plateNumber;     // 对应字段 plate_number
    private Double confidence;      // 👈【新增】对应数据库字段 confidence
    private String imageUrl;        // 原始图片URL, 对应字段 image_url
    private String croppedImageUrl; // 👈【新增】对应数据库字段 cropped_image_url
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    // 👇【新增】confidence 的 Getter/Setter
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // 👇【新增】croppedImageUrl 的 Getter/Setter
    public String getCroppedImageUrl() { return croppedImageUrl; }
    public void setCroppedImageUrl(String croppedImageUrl) { this.croppedImageUrl = croppedImageUrl; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}