package com.example.springai.service;

import com.example.springai.dto.PaperGenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * TaiKao 后端 API 服务
 */
@Service
@RequiredArgsConstructor
public class TaiKaoApiService {

    private final RestTemplate restTemplate;

    @Value("${taikao.api.base-url:http://localhost:8080}")
    private String taikaoBaseUrl;

    /**
     * 调用 TaiKao 后端生成试卷
     */
    public Map<String, Object> generatePaper(PaperGenerationRequest request, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        }

        HttpEntity<PaperGenerationRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    taikaoBaseUrl + "/api/papers/generate",
                    entity,
                    Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("调用 TaiKao 后端失败: " + e.getMessage(), e);
        }
    }
}
