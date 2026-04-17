package com.example.springboot.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * 负责调用外部 Python 脚本进行车牌识别的服务。
 */
@Service
public class PythonService {

    // ✅【重要】请根据您的实际环境修改以下两个路径！
    private static final String PYTHON_EXECUTABLE = "D:\\anaconda\\envs\\yolo\\python.exe";
    private static final String SCRIPT_PATH = "C:\\Users\\86150\\Downloads\\LPRNet_Pytorch-master\\LPRNet_Pytorch-master\\pipeline_recognize.py";

    /**
     * 调用 Python 脚本识别车牌。
     *
     * @param absoluteImagePath 图片在服务器上的绝对路径。
     * @return 包含识别结果的 DTO 对象。
     * @throws IOException          IO异常
     * @throws InterruptedException 线程中断异常
     */
    public PlateRecognitionResult recognizePlate(String absoluteImagePath) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                PYTHON_EXECUTABLE,
                SCRIPT_PATH,
                "--img", absoluteImagePath,
                "--quiet"
        );

        // ✅ 设置工作目录，确保脚本在正确的上下文中运行
        processBuilder.directory(new File("C:\\Users\\86150\\Downloads\\LPRNet_Pytorch-master\\LPRNet_Pytorch-master"));

        Process process = processBuilder.start();

        // 读取标准输出
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        // 等待进程结束，设置超时防止卡死
        boolean finished = process.waitFor(30, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("Python script execution timed out.");
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            // 读取错误信息
            StringBuilder error = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    error.append(line).append(System.lineSeparator());
                }
            }
            throw new RuntimeException("Python script failed with exit code " + exitCode + ". Error: " + error.toString());
        }

        // ✅ 解析输出（增强容错版）
        String outputStr = output.toString().trim();
        if (outputStr.isEmpty()) {
            throw new RuntimeException("Python script produced no output.");
        }

        // === 增强容错解析逻辑（替换开始）===
        // 只取最后一行（防止 Python 脚本意外输出多行日志）
        String[] lines = outputStr.split(System.lineSeparator());
        String lastLine = lines[lines.length - 1].trim();

        // 使用 -1 保留尾部空字符串，例如 "A||" 会得到 ["A", "", ""]
        String[] parts = lastLine.split("\\|", -1);

        String plateNumber = (parts.length > 0 && parts[0] != null) ? parts[0].trim() : "";
        Double confidence = null;
        String croppedImageUrl = null;

        // 安全解析置信度
        if (parts.length > 1 && parts[1] != null) {
            String confStr = parts[1].trim();
            if (!confStr.isEmpty() && !"None".equalsIgnoreCase(confStr)) {
                try {
                    confidence = Double.parseDouble(confStr);
                } catch (NumberFormatException e) {
                    // 忽略无效数字，保持 confidence 为 null
                }
            }
        }

        // 安全解析裁剪图路径
        if (parts.length > 2 && parts[2] != null) {
            String urlStr = parts[2].trim();
            if (!urlStr.isEmpty() && !"None".equalsIgnoreCase(urlStr)) {
                croppedImageUrl = urlStr;
            }
        }

        // 验证车牌号是否有效
        if (plateNumber.isEmpty()) {
            throw new RuntimeException("Plate number is empty or invalid in Python output. Full output: [" + outputStr + "]");
        }
        // === 增强容错解析逻辑（替换结束）===

        return new PlateRecognitionResult(plateNumber, confidence, croppedImageUrl);
    }

    /**
     * 车牌识别结果的数据传输对象 (DTO)。
     */
    public static class PlateRecognitionResult {
        private final String plateNumber;
        private final Double confidence;
        private final String croppedImageUrl;

        public PlateRecognitionResult(String plateNumber, Double confidence, String croppedImageUrl) {
            this.plateNumber = plateNumber;
            this.confidence = confidence;
            this.croppedImageUrl = croppedImageUrl;
        }

        // Getters
        public String getPlateNumber() { return plateNumber; }
        public Double getConfidence() { return confidence; }
        public String getCroppedImageUrl() { return croppedImageUrl; }
    }
}