package com.example.manger.service;

import com.google.code.kaptcha.Producer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CaptchaService {
    
    private final Producer captchaProducer;
    private final RedisTemplate<String, String> redisTemplate;
    
    /**
     * 生成验证码
     */
    public CaptchaResult generateCaptcha() {
        // 生成验证码文本
        String captchaText = captchaProducer.createText();
        
        // 生成验证码图片
        BufferedImage image = captchaProducer.createImage(captchaText);
        
        // 生成唯一ID
        String captchaId = UUID.randomUUID().toString();
        
        // 将验证码存储到Redis，设置5分钟过期
        String redisKey = "captcha:" + captchaId;
        redisTemplate.opsForValue().set(redisKey, captchaText, 5, TimeUnit.MINUTES);
        
        // 将图片转换为Base64
        String base64Image = convertImageToBase64(image);
        
        return new CaptchaResult(captchaId, base64Image);
    }
    
    /**
     * 验证验证码
     */
    public boolean validateCaptcha(String captchaId, String captcha) {
        if (captchaId == null || captcha == null) {
            return false;
        }
        
        String redisKey = "captcha:" + captchaId;
        String storedCaptcha = redisTemplate.opsForValue().get(redisKey);
        
        if (storedCaptcha == null) {
            return false;
        }
        
        // 验证成功后删除验证码
        redisTemplate.delete(redisKey);
        
        return storedCaptcha.equalsIgnoreCase(captcha);
    }
    
    /**
     * 将图片转换为Base64
     */
    private String convertImageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("图片转换失败", e);
        }
    }
    
    /**
     * 验证码结果
     */
    public static class CaptchaResult {
        private String captchaId;
        private String image;
        
        public CaptchaResult(String captchaId, String image) {
            this.captchaId = captchaId;
            this.image = image;
        }
        
        public String getCaptchaId() {
            return captchaId;
        }
        
        public void setCaptchaId(String captchaId) {
            this.captchaId = captchaId;
        }
        
        public String getImage() {
            return image;
        }
        
        public void setImage(String image) {
            this.image = image;
        }
    }
}
