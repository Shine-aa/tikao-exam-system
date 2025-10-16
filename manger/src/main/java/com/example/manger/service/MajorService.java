package com.example.manger.service;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.PageResponse;
import com.example.manger.dto.MajorRequest;
import com.example.manger.dto.MajorResponse;
import com.example.manger.entity.Major;
import com.example.manger.exception.BusinessException;
import com.example.manger.exception.ErrorCode;
import com.example.manger.repository.MajorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 专业管理服务
 */
@Service
@Transactional
public class MajorService {
    
    @Autowired
    private MajorRepository majorRepository;
    
    /**
     * 创建专业
     */
    public MajorResponse createMajor(MajorRequest request) {
        // 检查专业代码是否已存在
        if (majorRepository.existsByMajorCode(request.getMajorCode())) {
            throw new BusinessException(ErrorCode.MAJOR_CODE_EXISTS, "专业代码已存在");
        }
        
        Major major = new Major();
        BeanUtils.copyProperties(request, major);
        major.setIsActive(true);
        major.setCreatedAt(LocalDateTime.now());
        major.setUpdatedAt(LocalDateTime.now());
        
        Major savedMajor = majorRepository.save(major);
        return convertToResponse(savedMajor);
    }
    
    /**
     * 更新专业
     */
    public MajorResponse updateMajor(Long id, MajorRequest request) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_NOT_FOUND, "专业不存在"));
        
        // 检查专业代码是否被其他专业使用
        if (!major.getMajorCode().equals(request.getMajorCode()) && 
            majorRepository.existsByMajorCode(request.getMajorCode())) {
            throw new BusinessException(ErrorCode.MAJOR_CODE_EXISTS, "专业代码已存在");
        }
        
        BeanUtils.copyProperties(request, major);
        major.setUpdatedAt(LocalDateTime.now());
        
        Major savedMajor = majorRepository.save(major);
        return convertToResponse(savedMajor);
    }
    
    /**
     * 删除专业
     */
    public void deleteMajor(Long id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_NOT_FOUND, "专业不存在"));
        
        major.setIsActive(false);
        major.setUpdatedAt(LocalDateTime.now());
        majorRepository.save(major);
    }
    
    /**
     * 批量删除专业
     */
    public void batchDeleteMajors(List<Long> ids) {
        List<Major> majors = majorRepository.findAllById(ids);
        for (Major major : majors) {
            major.setIsActive(false);
            major.setUpdatedAt(LocalDateTime.now());
        }
        majorRepository.saveAll(majors);
    }
    
    /**
     * 根据ID获取专业
     */
    @Transactional(readOnly = true)
    public MajorResponse getMajorById(Long id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_NOT_FOUND, "专业不存在"));
        return convertToResponse(major);
    }
    
    /**
     * 获取所有专业
     */
    @Transactional(readOnly = true)
    public List<MajorResponse> getAllMajors() {
        List<Major> majors = majorRepository.findByIsActiveTrue();
        return majors.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 分页获取专业
     */
    @Transactional(readOnly = true)
    public PageResponse<MajorResponse> getMajorsWithPagination(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Major> majorPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            majorPage = majorRepository.findByMajorNameContaining(keyword.trim(), pageable);
        } else {
            majorPage = majorRepository.findByIsActiveTrue(pageable);
        }
        
        List<MajorResponse> responses = majorPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<MajorResponse>builder()
                .content(responses)
                .total(majorPage.getTotalElements())
                .page(page)
                .size(size)
                .totalPages(majorPage.getTotalPages())
                .first(page == 1)
                .last(page == majorPage.getTotalPages())
                .hasNext(page < majorPage.getTotalPages())
                .hasPrevious(page > 1)
                .build();
    }
    
    /**
     * 转换为响应对象
     */
    private MajorResponse convertToResponse(Major major) {
        MajorResponse response = new MajorResponse();
        BeanUtils.copyProperties(major, response);
        return response;
    }
}
