package com.example.manger.repository;

import com.example.manger.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    
    Optional<UserSession> findBySessionToken(String sessionToken);
    
    Optional<UserSession> findByRefreshToken(String refreshToken);
    
    List<UserSession> findByUserIdAndIsActiveTrue(Long userId);
    
    @Query("SELECT us FROM UserSession us WHERE us.userId = ?1 AND us.isActive = true AND us.expiresAt > ?2")
    List<UserSession> findActiveSessionsByUserId(Long userId, LocalDateTime now);
    
    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false WHERE us.userId = ?1 AND us.isActive = true")
    void deactivateAllSessionsByUserId(Long userId);
    
    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false WHERE us.expiresAt < ?1")
    void deactivateExpiredSessions(LocalDateTime now);
}
