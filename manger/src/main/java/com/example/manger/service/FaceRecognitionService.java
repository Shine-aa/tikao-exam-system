package com.example.manger.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FaceRecognitionService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ai.face-recognition.url:http://localhost:8001/verify}")
    private String pythonApiUrl;

    /**
     * 调用 Python AI 服务验证人脸
     *
     * @param targetBase64 摄像头抓拍的 Base64 字符串
     * @param referenceUrl 用户的基准照片 URL
     * @return 验证结果
     */
    public FaceVerifyResponse verifyFace(String targetBase64, String referenceUrl) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("target_base64", targetBase64);
            request.put("reference_url", referenceUrl);

            log.info("正在调用人脸识别服务...");
            FaceVerifyResponse response = restTemplate.postForObject(pythonApiUrl, request, FaceVerifyResponse.class);
            
            if (response != null) {
                log.info("人脸识别结果: success={}, similarity={}", response.isSuccess(), response.getSimilarity());
            }
            
            return response;
        } catch (Exception e) {
            log.error("调用人脸识别服务失败: {}", e.getMessage());
            FaceVerifyResponse errorResponse = new FaceVerifyResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("人脸识别服务连接失败: " + e.getMessage());
            return errorResponse;
        }
    }

    @Data
    public static class FaceVerifyResponse {
        private boolean success;
        private String message;
        private Double similarity;
    }
}
