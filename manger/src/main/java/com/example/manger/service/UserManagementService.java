package com.example.manger.service;

import com.example.manger.dto.*;
import com.example.manger.entity.Class;
import com.example.manger.entity.Role;
import com.example.manger.entity.StudentClass;
import com.example.manger.entity.User;
import com.example.manger.exception.BusinessException;
import com.example.manger.exception.ErrorCode;
import com.example.manger.repository.ClassRepository;
import com.example.manger.repository.MajorRepository;
import com.example.manger.repository.RoleRepository;
import com.example.manger.repository.StudentClassRepository;
import com.example.manger.repository.UserRepository;
import com.example.manger.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClassRepository classRepository;
    private final MajorRepository majorRepository;
    private final StudentClassRepository studentClassRepository;
    private final PasswordUtil passwordUtil;
    
    /**
     * 创建用户
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        // 加密密码
        String salt = passwordUtil.generateSalt();
        String hashedPassword = passwordUtil.hashPassword(request.getPassword(), salt);
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        
        // 分配角色
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(request.getRoleIds());
            user.setRoles(roles.stream().collect(Collectors.toSet()));
        }
        
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    /**
     * 更新用户
     */
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查用户名是否已被其他用户使用
        if (request.getUsername() != null && 
            !request.getUsername().equals(user.getUsername()) &&
            userRepository.existsByUsernameAndIdNot(request.getUsername(), userId)) {
            throw new RuntimeException("用户名已被其他用户使用");
        }
        
        // 检查邮箱是否已被其他用户使用
        if (request.getEmail() != null && 
            !request.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
            throw new RuntimeException("邮箱已被其他用户使用");
        }
        
        // 更新基本信息
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        
        // 更新密码
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            String salt = passwordUtil.generateSalt();
            String hashedPassword = passwordUtil.hashPassword(request.getPassword(), salt);
            user.setPassword(hashedPassword);
            user.setSalt(salt);
        }
        
        // 更新角色
        if (request.getRoleIds() != null) {
            List<Role> roles = roleRepository.findAllById(request.getRoleIds());
            user.setRoles(roles.stream().collect(Collectors.toSet()));
        }
        
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(userId);
    }
    
    /**
     * 批量删除用户
     */
    @Transactional
    public void batchDeleteUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new RuntimeException("用户ID列表不能为空");
        }
        
        // 检查所有用户是否存在
        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new RuntimeException("部分用户不存在");
        }
        
        userRepository.deleteAllById(userIds);
    }
    
    /**
     * 获取用户详情
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToResponse(user);
    }
    
    /**
     * 获取所有用户
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAllWithRoles();
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据条件搜索用户
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(String username, String email, Boolean isActive, String role) {
        List<User> users;
        
        // 如果指定了角色，使用按角色查询的方法
        if (role != null && !role.isEmpty()) {
            users = userRepository.findByRolesRoleCodeAndIsActiveTrue(role);
        } else {
            users = userRepository.findAllWithRoles();
        }
        
        // 应用搜索过滤条件
        return users.stream()
                .filter(user -> username == null || user.getUsername().contains(username))
                .filter(user -> email == null || user.getEmail().contains(email))
                .filter(user -> isActive == null || user.getIsActive().equals(isActive))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 分页查询用户
     */
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsersWithPagination(String username, String email, Boolean isActive, 
                                                           Integer page, Integer size, String sortBy, String sortOrder) {
        // 构建查询条件
        Pageable pageable = PageRequest.of(page - 1, size);
        
        // 如果有排序字段，添加排序
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        }
        
        // 执行分页查询
        Page<User> userPage = userRepository.findAllWithRoles(pageable);
        
        // 应用搜索过滤条件
        List<UserResponse> filteredUsers = userPage.getContent().stream()
                .filter(user -> username == null || user.getUsername().contains(username))
                .filter(user -> email == null || user.getEmail().contains(email))
                .filter(user -> isActive == null || user.getIsActive().equals(isActive))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(filteredUsers, page, size, userPage.getTotalElements());
    }
    
    /**
     * 获取启用的用户
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        List<User> users = userRepository.findByIsActiveTrue();
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 启用/禁用用户
     */
    @Transactional
    public UserResponse toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setIsActive(!user.getIsActive());
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    /**
     * 为用户分配角色
     */
    @Transactional
    public UserResponse assignRolesToUser(Long userId, AssignRolesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        List<Role> roles = roleRepository.findAllById(request.getRoleIds());
        user.setRoles(roles.stream().collect(Collectors.toSet()));
        
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    /**
     * 从用户移除角色
     */
    @Transactional
    public UserResponse removeRolesFromUser(Long userId, Set<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Set<Role> roles = user.getRoles();
        roles.removeIf(role -> roleIds.contains(role.getId()));
        user.setRoles(roles);
        
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    /**
     * 重置用户密码
     */
    @Transactional
    public void resetUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        String salt = passwordUtil.generateSalt();
        String hashedPassword = passwordUtil.hashPassword(newPassword, salt);
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        
        userRepository.save(user);
    }
    
    /**
     * 转换为响应DTO
     */
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setIsActive(user.getIsActive());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setCreateTime(user.getCreateTime());
        response.setUpdateTime(user.getUpdateTime());
        
        // 转换角色信息
        if (user.getRoles() != null) {
            Set<RoleResponse> roleResponses = user.getRoles().stream()
                    .map(this::convertRoleToResponse)
                    .collect(Collectors.toSet());
            response.setRoles(roleResponses);
        }
        
        return response;
    }
    
    /**
     * 获取所有学生（普通用户）
     */
    public List<UserResponse> getStudents() {
        // 查找所有普通用户（假设普通用户的角色代码是"USER"）
        List<User> students = userRepository.findByRolesRoleCodeAndIsActiveTrue("USER");
        return students.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 分页获取学生列表（带班级信息）
     */
    public PageResponse<UserResponse> getStudentsWithPagination(int page, int size, String keyword, 
                                                               Long classId, String status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        
        // 构建查询条件
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 只查询普通用户（学生）- 通过角色关系表查询
            predicates.add(cb.equal(root.join("roles").get("roleCode"), "USER"));
            
            // 只查询激活的用户
            predicates.add(cb.equal(root.get("isActive"), true));
            
            // 关键词搜索
            if (keyword != null && !keyword.trim().isEmpty()) {
                String keywordPattern = "%" + keyword.trim() + "%";
                Predicate usernamePredicate = cb.like(root.get("username"), keywordPattern);
                Predicate emailPredicate = cb.like(root.get("email"), keywordPattern);
                predicates.add(cb.or(usernamePredicate, emailPredicate));
            }
            
            // 状态筛选
            if (status != null && !status.trim().isEmpty()) {
                if ("assigned".equals(status)) {
                    // 已分配班级的学生
                    predicates.add(cb.isNotNull(root.get("classId")));
                } else if ("unassigned".equals(status)) {
                    // 未分配班级的学生
                    predicates.add(cb.isNull(root.get("classId")));
                }
            }
            
            // 班级筛选
            if (classId != null) {
                predicates.add(cb.equal(root.get("classId"), classId));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<User> userPage = userRepository.findAll(spec, pageable);
        
        // 转换为响应DTO
        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(this::convertUserToResponseWithClassInfo)
                .collect(Collectors.toList());
        
        return PageResponse.of(
                userResponses,
                page,
                size,
                userPage.getTotalElements()
        );
    }
    
    /**
     * 分配学生到班级
     */
    @Transactional
    public void assignStudentToClass(Long studentId, Long classId) {
        // 验证学生是否存在
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "学生不存在"));
        
        // 验证学生是否为普通用户
        boolean isStudent = student.getRoles().stream()
                .anyMatch(role -> "USER".equals(role.getRoleCode()));
        if (!isStudent) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "只能分配普通用户到班级");
        }
        
        // 验证班级是否存在
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND, "班级不存在"));
        
        // 如果学生已经在同一个班级，无需操作
        if (student.getClassId() != null && student.getClassId().equals(classId)) {
            return; // 学生已经在目标班级中，无需重复分配
        }
        
        // 如果学生之前在其他班级，先删除旧的关联记录并更新旧班级人数
        if (student.getClassId() != null) {
            Long oldClassId = student.getClassId();
            studentClassRepository.deleteByClassIdAndStudentId(oldClassId, studentId);
            
            // 更新旧班级的学生人数
            Class oldClass = classRepository.findById(oldClassId).orElse(null);
            if (oldClass != null) {
                oldClass.setCurrentStudents(Math.max(0, oldClass.getCurrentStudents() - 1));
                classRepository.save(oldClass);
            }
        }
        
        // 分配学生到班级
        student.setClassId(classId);
        student.setUpdateTime(LocalDateTime.now());
        userRepository.save(student);
        
        // 创建学生班级关联记录
        StudentClass studentClass = new StudentClass();
        studentClass.setStudentId(studentId);
        studentClass.setClassId(classId);
        studentClass.setIsActive(true);
        studentClass.setCreatedAt(LocalDateTime.now());
        studentClassRepository.save(studentClass);
        
        // 更新新班级的学生人数
        classEntity.setCurrentStudents(classEntity.getCurrentStudents() + 1);
        classRepository.save(classEntity);
    }
    
    /**
     * 批量分配学生到班级
     */
    @Transactional
    public void batchAssignStudentsToClass(List<Long> studentIds, Long classId) {
        for (Long studentId : studentIds) {
            assignStudentToClass(studentId, classId);
        }
    }
    
    /**
     * 将学生移出班级
     */
    @Transactional
    public void removeStudentFromClass(Long studentId) {
        // 验证学生是否存在
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "学生不存在"));
        
        if (student.getClassId() == null) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "学生未分配班级");
        }
        
        // 保存班级ID，因为之后会设置为null
        Long classId = student.getClassId();
        
        // 移除学生班级关联
        student.setClassId(null);
        student.setUpdateTime(LocalDateTime.now());
        userRepository.save(student);
        
        // 软删除学生班级关联记录
        studentClassRepository.deleteByClassIdAndStudentId(classId, studentId);
        
        // 更新班级的学生人数
        Class classEntity = classRepository.findById(classId).orElse(null);
        if (classEntity != null) {
            classEntity.setCurrentStudents(Math.max(0, classEntity.getCurrentStudents() - 1));
            classRepository.save(classEntity);
        }
    }
    
    /**
     * 批量将学生移出班级
     */
    @Transactional
    public void batchRemoveStudentsFromClass(List<Long> studentIds) {
        for (Long studentId : studentIds) {
            removeStudentFromClass(studentId);
        }
    }
    
    /**
     * 转换用户为响应DTO
     */
    private UserResponse convertUserToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setIsActive(user.getIsActive());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setCreateTime(user.getCreateTime());
        response.setUpdateTime(user.getUpdateTime());
        // 设置角色信息
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            response.setRoles(user.getRoles().stream()
                    .map(this::convertRoleToResponse)
                    .collect(Collectors.toSet()));
            
            // 设置主要角色信息
            Role primaryRole = user.getRoles().iterator().next();
            response.setRole(primaryRole.getRoleCode());
            response.setRoleName(primaryRole.getRoleName());
        }
        
        return response;
    }
    
    /**
     * 转换用户为响应DTO（带班级信息）
     */
    private UserResponse convertUserToResponseWithClassInfo(User user) {
        UserResponse response = convertUserToResponse(user);
        
        // 设置班级信息
        if (user.getClassId() != null) {
            classRepository.findById(user.getClassId()).ifPresent(classEntity -> {
                response.setClassName(classEntity.getClassName());
                response.setClassId(classEntity.getId());
                
                // 设置专业信息
                if (classEntity.getMajorId() != null) {
                    majorRepository.findById(classEntity.getMajorId()).ifPresent(major -> {
                        response.setMajorName(major.getMajorName());
                        response.setMajorId(major.getId());
                    });
                }
            });
        }
        
        return response;
    }
    
    /**
     * 转换角色为响应DTO
     */
    private RoleResponse convertRoleToResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setRoleName(role.getRoleName());
        response.setRoleCode(role.getRoleCode());
        response.setDescription(role.getDescription());
        response.setIsActive(role.getIsActive());
        response.setCreateTime(role.getCreateTime());
        response.setUpdateTime(role.getUpdateTime());
        return response;
    }
}
