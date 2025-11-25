package com.example.manger.repository;

import com.example.manger.entity.SmsCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 短信验证码数据访问接口
 */
@Repository
public interface SmsCodeRepository extends JpaRepository<SmsCode, Long> {
    
    /**
     * 根据手机号、验证码、类型和未使用状态查找
     */
    List<SmsCode> findByPhoneAndCodeAndTypeAndIsUsedFalse(String phone, String code, String type);
    
    /**
     * 根据手机号和类型查找最新的验证码
     */
    @Query("SELECT s FROM SmsCode s WHERE s.phone = :phone AND s.type = :type AND s.isUsed = false AND s.expireTime > :now ORDER BY s.createTime DESC")
    List<SmsCode> findLatestByPhoneAndType(@Param("phone") String phone, @Param("type") String type, @Param("now") LocalDateTime now);
    
    /**
     * 根据手机号查找所有验证码
     */
    List<SmsCode> findByPhoneOrderByCreateTimeDesc(String phone);
    
    /**
     * 统计指定时间范围内的验证码发送次数
     */
    @Query("SELECT COUNT(s) FROM SmsCode s WHERE s.phone = :phone AND s.createTime >= :startTime")
    long countByPhoneAndCreateTimeAfter(@Param("phone") String phone, @Param("startTime") LocalDateTime startTime);
    
    /**
     * 删除过期的验证码
     */
    @Query("DELETE FROM SmsCode s WHERE s.expireTime < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);
    
    /**
     * 根据IP地址统计指定时间范围内的发送次数
     */
    @Query("SELECT COUNT(s) FROM SmsCode s WHERE s.ipAddress = :ipAddress AND s.createTime >= :startTime")
    long countByIpAddressAndCreateTimeAfter(@Param("ipAddress") String ipAddress, @Param("startTime") LocalDateTime startTime);
}
