package com.example.manger.service;

import com.example.manger.entity.SmsCode;
import com.example.manger.repository.SmsCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {
    
    private final SmsCodeRepository smsCodeRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    
    // 容联云配置
    @Value("${sms.cloopen.account-sid:2c94811c9860a9c401992705f6282159}")
    private String accountSid;
    
    @Value("${sms.cloopen.auth-token:03ac912e4bd741cd87ad762ba80f24f4}")
    private String authToken;
    
    @Value("${sms.cloopen.app-id:2c94811c9860a9c40199270aa2482167}")
    private String appId;
    
    @Value("${sms.cloopen.server-url:https://app.cloopen.com:8883}")
    private String serverUrl;
    
    // 验证码配置
    @Value("${sms.code.length:6}")
    private int codeLength;
    
    @Value("${sms.code.expire-minutes:5}")
    private int expireMinutes;
    
    @Value("${sms.send.limit-seconds:60}")
    private int sendLimitSeconds;
    
    /**
     * 发送短信验证码
     */
    public Map<String, Object> sendSmsCode(String phone, String type, String ipAddress, String userAgent) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 检查发送频率限制
            String limitKey = "sms:limit:" + phone;
            if (redisTemplate.hasKey(limitKey)) {
                result.put("success", false);
                result.put("message", "发送过于频繁，请稍后再试");
                return result;
            }
            
            // 2. 生成验证码
            String code = generateCode();
            
            // 3. 发送短信
            boolean sendSuccess = sendSms(phone, code);
            
            if (!sendSuccess) {
                result.put("success", false);
                result.put("message", "短信发送失败，请稍后重试");
                return result;
            }
            
            // 4. 保存验证码到数据库
            SmsCode smsCode = new SmsCode();
            smsCode.setPhone(phone);
            smsCode.setCode(code);
            smsCode.setType(type);
            smsCode.setExpireTime(LocalDateTime.now().plusMinutes(expireMinutes));
            smsCode.setIpAddress(ipAddress);
            smsCode.setUserAgent(userAgent);
            smsCodeRepository.save(smsCode);
            
            // 5. 设置发送频率限制
            redisTemplate.opsForValue().set(limitKey, "1", sendLimitSeconds, TimeUnit.SECONDS);
            
            // 6. 设置验证码到Redis（用于快速验证）
            String redisKey = "sms:code:" + phone + ":" + type;
            redisTemplate.opsForValue().set(redisKey, code, expireMinutes, TimeUnit.MINUTES);
            
            result.put("success", true);
            result.put("message", "验证码发送成功");
            
            log.info("短信验证码发送成功: phone={}, type={}, code={}", phone, type, code);
            
        } catch (Exception e) {
            log.error("发送短信验证码失败: phone={}, type={}", phone, type, e);
            result.put("success", false);
            result.put("message", "系统错误，请稍后重试");
        }
        
        return result;
    }
    
    /**
     * 验证短信验证码
     */
    public boolean verifySmsCode(String phone, String code, String type) {
        try {
            // 1. 先从Redis验证（快速）
            String redisKey = "sms:code:" + phone + ":" + type;
            String cachedCode = redisTemplate.opsForValue().get(redisKey);
            
            if (code.equals(cachedCode)) {
                // 验证成功，删除Redis中的验证码
                redisTemplate.delete(redisKey);
                return true;
            }
            
            // 2. 从数据库验证
            SmsCode smsCode = smsCodeRepository.findByPhoneAndCodeAndTypeAndIsUsedFalse(phone, code, type)
                    .stream()
                    .filter(sc -> sc.getExpireTime().isAfter(LocalDateTime.now()))
                    .findFirst()
                    .orElse(null);
            
            if (smsCode != null) {
                // 标记为已使用
                smsCode.setIsUsed(true);
                smsCode.setUsedTime(LocalDateTime.now());
                smsCodeRepository.save(smsCode);
                
                // 删除Redis中的验证码
                redisTemplate.delete(redisKey);
                
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            log.error("验证短信验证码失败: phone={}, code={}, type={}", phone, code, type, e);
            return false;
        }
    }
    
    /**
     * 生成验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    /**
     * 发送短信（容联云API）
     */
    private boolean sendSms(String phone, String code) {
        try {
            // 生成时间戳（容联云要求yyyyMMddHHmmss格式）
            String timestamp = generateTimestamp();
            
            // 生成签名（用于URL参数）
            String sig = generateSig(accountSid, authToken, timestamp);
            
            // 构建请求URL，添加sig参数
            String url = serverUrl + "/2013-12-26/Accounts/" + accountSid + "/SMS/TemplateSMS?sig=" + sig;
            
            // 生成Authorization签名
            String signature = generateSignature(accountSid, authToken, timestamp);
            
            log.info("容联云短信发送参数: accountSid={}, appId={}, phone={}, timestamp={}, sig={}, signature={}", 
                    accountSid, appId, phone, timestamp, sig, signature);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("to", phone);
            requestBody.put("appId", appId);
            requestBody.put("templateId", "1"); // 使用默认验证码模板
            requestBody.put("datas", new String[]{code, String.valueOf(expireMinutes)});
            
            log.info("容联云请求体: {}", requestBody);
            
            // 构建请求头
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Accept", "application/json");
            headers.set("Content-Type", "application/json;charset=utf-8");
            headers.set("Authorization", signature);
            
            log.info("容联云请求头: {}", headers);
            
            // 创建请求实体
            org.springframework.http.HttpEntity<Map<String, Object>> requestEntity = 
                new org.springframework.http.HttpEntity<>(requestBody, headers);
            
            // 发送请求
            org.springframework.http.ResponseEntity<Map> response = restTemplate.postForEntity(
                url, requestEntity, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            
            // 检查响应
            if (responseBody != null && "000000".equals(responseBody.get("statusCode"))) {
                log.info("容联云短信发送成功: phone={}, code={}", phone, code);
                return true;
            }
            
            log.error("容联云短信发送失败: {}", responseBody);
            return false;
            
        } catch (Exception e) {
            log.error("发送短信异常: phone={}, code={}", phone, code, e);
            return false;
        }
    }
    
    /**
     * 生成容联云要求的时间戳格式（yyyyMMddHHmmss）
     */
    private String generateTimestamp() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
    
    /**
     * 生成容联云URL中的sig参数
     */
    private String generateSig(String accountSid, String authToken, String timestamp) {
        try {
            String data = accountSid + authToken + timestamp;
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data.getBytes());
            return bytesToHex(digest).toUpperCase();
        } catch (Exception e) {
            log.error("生成sig参数失败", e);
            return "";
        }
    }
    
    /**
     * 生成容联云Authorization签名
     */
    private String generateSignature(String accountSid, String authToken, String timestamp) {
        try {
            String data = accountSid + authToken + timestamp;
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data.getBytes());
            String md5Hex = bytesToHex(digest).toUpperCase();
            
            // 容联云要求Authorization格式为：Base64(AccountSid + ":" + Timestamp)
            String signatureString = accountSid + ":" + timestamp;
            return java.util.Base64.getEncoder().encodeToString(signatureString.getBytes("UTF-8"));
        } catch (Exception e) {
            log.error("生成签名失败", e);
            return "";
        }
    }
    
    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
