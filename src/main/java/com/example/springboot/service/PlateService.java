package com.example.springboot.service;

import com.example.springboot.entity.PlateRecord;
import com.example.springboot.mapper.PlateMapper;
import com.example.springboot.service.PythonService; // 👈 新增导入
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PlateService {

    @Resource
    private PlateMapper plateMapper;

    @Resource
    private PythonService pythonService; // 👈 注入 Python 服务

    // ========== 【新增】核心识别与保存方法 ==========

    /**
     * 保存上传的图片，调用 Python 脚本进行车牌识别，并将结果存入数据库。
     *
     * @param file 前端上传的图片文件
     * @return 保存到数据库的记录对象
     * @throws IOException          文件操作异常
     * @throws InterruptedException Python 进程被中断
     */
    public PlateRecord saveAndRecognize(MultipartFile file) throws IOException, InterruptedException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }

        // 1. 定义上传目录 (确保此目录存在且可写)
        String uploadDir = "D:/daima/xm-tingcheguanli/springboot/uploads";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs(); // 如果目录不存在，则创建
        }

        // 2. 生成唯一的文件名，防止冲突
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        String relativeImagePath = "/uploads/" + uniqueFileName; // 数据库存储的相对路径
        String absoluteImagePath = uploadDir + "/" + uniqueFileName; // 传递给 Python 的绝对路径

        // 3. 将文件保存到服务器
        Path savePath = Paths.get(absoluteImagePath);
        Files.write(savePath, file.getBytes());

        // 4. 【关键】调用 Python 服务进行识别
        PythonService.PlateRecognitionResult result = pythonService.recognizePlate(absoluteImagePath);

        // 5. 构建数据库记录并保存
        PlateRecord record = new PlateRecord();
        record.setPlateNumber(result.getPlateNumber());
        record.setImageUrl(relativeImagePath); // 存储如 "/uploads/xxx.jpg"
        record.setCroppedImageUrl(result.getCroppedImageUrl()); // 存储如 "/results/cropped_xxx.jpg"
        record.setConfidence(result.getConfidence());
        record.setCreateTime(LocalDateTime.now());

        plateMapper.insert(record);
        return record;
    }

    // ========== 以下是您原有的方法，保持不变 ==========

    // 查询所有
    public List<PlateRecord> selectAll() {
        return plateMapper.selectAll();
    }

    // 【注意】原有的 saveRecord 方法可能不再直接使用，因为识别逻辑已移到 saveAndRecognize
    // 但为了兼容性，此处保留。实际业务中应调用 saveAndRecognize。
    public void saveRecord(PlateRecord record) {
        plateMapper.insert(record);
    }

    // 【新增】根据 ID 查询单条记录（为了获取图片路径）
    public PlateRecord getById(Long id) {
        return plateMapper.selectById(id);
    }

    // 【修改】根据 ID 删除
    public void removeById(Long id) {
        plateMapper.deleteById(id);
    }

    public List<PlateRecord> selectByPlateNumber(String plateNumber) {
        // 直接调用 Mapper 层的方法
        return plateMapper.selectByPlateNumber(plateNumber);
    }
}