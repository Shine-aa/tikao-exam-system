package com.example.manger.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {
    
    @Bean
    public DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        
        // 是否有边框
        properties.setProperty("kaptcha.border", "no");
        // 字体颜色 - 使用深色提高对比度
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        // 图片宽度 - 稍微增加尺寸
        properties.setProperty("kaptcha.image.width", "130");
        // 图片高度 - 稍微增加尺寸
        properties.setProperty("kaptcha.image.height", "45");
        // 字体大小 - 稍微增大字体
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        // 字符长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 字符间距 - 增加间距
        properties.setProperty("kaptcha.textproducer.char.space", "6");
        // 字符集 - 移除容易混淆的字符
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ");
        // 字体 - 使用更清晰的字体
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Helvetica,sans-serif");
        // 干扰线颜色 - 使用浅色减少干扰
        properties.setProperty("kaptcha.noise.color", "200,200,200");
        // 干扰线实现类 - 减少干扰线
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.DefaultNoise");
        // 图片样式 - 使用更简单的样式
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
        // 文字渲染器
        properties.setProperty("kaptcha.word.impl", "com.google.code.kaptcha.text.impl.DefaultWordRenderer");
        // 背景实现类
        properties.setProperty("kaptcha.background.impl", "com.google.code.kaptcha.impl.DefaultBackground");
        // 背景颜色渐变开始
        properties.setProperty("kaptcha.background.clear.from", "white");
        // 背景颜色渐变结束
        properties.setProperty("kaptcha.background.clear.to", "white");
        
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        
        return defaultKaptcha;
    }
}
