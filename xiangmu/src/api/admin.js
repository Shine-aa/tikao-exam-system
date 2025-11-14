import request from '../utils/request'

// ==================== 用户管理 API ====================

// 获取用户列表
export function getUserList(name, username, email, isActive, role) {
  return request({
    url: '/api/admin/users',
    method: 'get',
    params: { name,username, email, isActive, role }
  })
}

// 分页获取用户列表
export function getUserListWithPagination(params) {
  return request({
    url: '/api/admin/users/page',
    method: 'get',
    params
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/api/admin/users',
    method: 'post',
    data
  })
}
// 导入用户（Excel 文件）
export function importUsers(formData) {
    return request({
        url: '/api/admin/users/import',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}
// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'delete'
  })
}

// 批量删除用户
export function batchDeleteUsers(userIds) {
  return request({
    url: '/api/admin/users/batch',
    method: 'delete',
    data: userIds
  })
}

// 切换用户状态
export function toggleUserStatus(id) {
  return request({
    url: `/api/admin/users/${id}/toggle-status`,
    method: 'put'
  })
}

// 重置用户密码
export function resetUserPassword(id, data) {
  return request({
    url: `/api/admin/users/${id}/reset-password`,
    method: 'put',
    data
  })
}

// 为用户分配角色
export function assignUserRoles(id, data) {
  return request({
    url: `/api/admin/users/${id}/assign-roles`,
    method: 'put',
    data
  })
}

// ==================== 角色管理 API ====================

// 获取角色列表
export function getRoleList(params) {
  return request({
    url: '/api/admin/roles',
    method: 'get',
    params
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/api/admin/roles',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/api/admin/roles/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/api/admin/roles/${id}`,
    method: 'delete'
  })
}

// 切换角色状态
export function toggleRoleStatus(id) {
  return request({
    url: `/api/admin/roles/${id}/toggle-status`,
    method: 'put'
  })
}

// 为角色分配权限
export function assignRolePermissions(id, data) {
  return request({
    url: `/api/admin/roles/${id}/assign-permissions`,
    method: 'put',
    data
  })
}

// ==================== 权限管理 API ====================

// 获取权限列表
export function getPermissionList(params) {
  return request({
    url: '/api/admin/permissions',
    method: 'get',
    params
  })
}

// 创建权限
export function createPermission(data) {
  return request({
    url: '/api/admin/permissions',
    method: 'post',
    data
  })
}

// 更新权限
export function updatePermission(id, data) {
  return request({
    url: `/api/admin/permissions/${id}`,
    method: 'put',
    data
  })
}

// 删除权限
export function deletePermission(id) {
  return request({
    url: `/api/admin/permissions/${id}`,
    method: 'delete'
  })
}

// 切换权限状态
export function togglePermissionStatus(id) {
  return request({
    url: `/api/admin/permissions/${id}/toggle-status`,
    method: 'put'
  })
}

// ==================== 题库管理相关API ====================

// 获取题目列表
export function getQuestions(params) {
  return request({
    url: '/api/questions',
    method: 'get',
    params
  })
}

// 获取题目详情
export function getQuestionById(id) {
  return request({
    url: `/api/questions/${id}`,
    method: 'get'
  })
}

// 创建题目
export function createQuestion(data) {
  return request({
    url: '/api/questions',
    method: 'post',
    data
  })
}

// 更新题目
export function updateQuestion(id, data) {
  return request({
    url: `/api/questions/${id}`,
    method: 'put',
    data
  })
}

// 删除题目
export function deleteQuestion(id) {
  return request({
    url: `/api/questions/${id}`,
    method: 'delete'
  })
}

// 批量删除题目
export function batchDeleteQuestions(ids) {
  return request({
    url: '/api/questions/batch',
    method: 'delete',
    data: ids
  })
}

// 获取题目统计信息
export function getQuestionStatistics() {
  return request({
    url: '/api/questions/statistics',
    method: 'get'
  })
}

// 导入题目
export function importQuestions(formData) {
  return request({
    url: '/api/questions/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 根据课程ID获取题目列表
export function getQuestionsByCourse(courseId) {
  return request({
    url: `/api/questions/course/${courseId}`,
    method: 'get'
  })
}

// ==================== 菜单管理 API ====================

// 获取菜单列表
export function getMenuList(params) {
  return request({
    url: '/api/menus',
    method: 'get',
    params
  })
}

// 获取启用菜单列表
export function getActiveMenuList() {
  return request({
    url: '/api/menus/active',
    method: 'get'
  })
}

// 创建菜单
export function createMenu(data) {
  return request({
    url: '/api/menus',
    method: 'post',
    data
  })
}

// 更新菜单
export function updateMenu(id, data) {
  return request({
    url: `/api/menus/${id}`,
    method: 'put',
    data
  })
}

// 删除菜单
export function deleteMenu(id) {
  return request({
    url: `/api/menus/${id}`,
    method: 'delete'
  })
}

// 获取菜单详情
export function getMenuById(id) {
  return request({
    url: `/api/menus/${id}`,
    method: 'get'
  })
}

// 根据角色ID获取菜单
export function getMenusByRoleId(roleId) {
  return request({
    url: `/api/menus/role/${roleId}`,
    method: 'get'
  })
}

// 根据用户ID获取菜单
export function getMenusByUserId(userId) {
  return request({
    url: `/api/menus/user/${userId}`,
    method: 'get'
  })
}

// 获取所有学生（普通用户）
export function getStudents() {
  return request({
    url: '/api/admin/users/students',
    method: 'get'
  })
}

// 分页获取学生列表（带班级信息）
export function getStudentsWithPagination(page, size, keyword, classId, status) {
  return request({
    url: '/api/admin/users/students/page',
    method: 'get',
    params: { page, size, keyword, classId, status }
  })
}

// 分配学生到班级
export function assignStudentToClass(studentId, classId) {
  return request({
    url: '/api/admin/users/students/assign',
    method: 'post',
    data: { studentId, classId }
  })
}

// 批量分配学生到班级
export function batchAssignStudentsToClass(studentIds, classId) {
  return request({
    url: '/api/admin/users/students/batch-assign',
    method: 'post',
    data: { studentIds, classId }
  })
}

// 将学生移出班级
export function removeStudentFromClass(studentId) {
  return request({
    url: `/api/admin/users/students/${studentId}/remove-from-class`,
    method: 'delete'
  })
}

// 批量将学生移出班级
export function batchRemoveStudentsFromClass(studentIds) {
  return request({
    url: '/api/admin/users/students/batch-remove-from-class',
    method: 'delete',
    data: { studentIds }
  })
}

// 为角色分配菜单
export function assignMenusToRole(data) {
  return request({
    url: '/api/menus/assign',
    method: 'post',
    data
  })
}

// 获取角色的菜单ID列表
export function getMenuIdsByRoleId(roleId) {
  return request({
    url: `/api/menus/role/${roleId}/ids`,
    method: 'get'
  })
}

// 角色管理分页和批处理API
// 分页获取角色列表
export function getRoleListWithPagination(params) {
  return request({
    url: '/api/admin/roles/page',
    method: 'get',
    params
  })
}

// 批量删除角色
export function batchDeleteRoles(roleIds) {
  return request({
    url: '/api/admin/roles/batch',
    method: 'delete',
    data: roleIds
  })
}

// 权限管理分页和批处理API
// 分页获取权限列表
export function getPermissionListWithPagination(params) {
  return request({
    url: '/api/admin/permissions/page',
    method: 'get',
    params
  })
}

// 批量删除权限
export function batchDeletePermissions(permissionIds) {
  return request({
    url: '/api/admin/permissions/batch',
    method: 'delete',
    data: permissionIds
  })
}

// ==================== 课程管理 API ====================

// 创建课程
export function createCourse(data) {
  return request({
    url: '/api/courses',
    method: 'post',
    data
  })
}

// 更新课程
export function updateCourse(id, data) {
  return request({
    url: `/api/courses/${id}`,
    method: 'put',
    data
  })
}

// 删除课程
export function deleteCourse(id) {
  return request({
    url: `/api/courses/${id}`,
    method: 'delete'
  })
}

// 获取课程详情
export function getCourseById(id) {
  return request({
    url: `/api/courses/${id}`,
    method: 'get'
  })
}

// 分页获取课程列表
export function getCoursesWithPagination(page, size, keyword) {
  return request({
    url: '/api/courses/page',
    method: 'get',
    params: { page, size, keyword }
  })
}

// 获取所有课程
export function getCourses() {
  return request({
    url: '/api/courses',
    method: 'get'
  })
}

// ==================== 班级管理 API ====================

// 创建班级
export function createClass(data) {
  return request({
    url: '/api/classes',
    method: 'post',
    data
  })
}

// 更新班级
export function updateClass(id, data) {
  return request({
    url: `/api/classes/${id}`,
    method: 'put',
    data
  })
}

// 删除班级
export function deleteClass(id) {
  return request({
    url: `/api/classes/${id}`,
    method: 'delete'
  })
}

// 批量删除班级
export function batchDeleteClasses(ids) {
  return request({
    url: '/api/classes/batch',
    method: 'delete',
    data: ids
  })
}

// 获取班级详情
export function getClassById(id) {
  return request({
    url: `/api/classes/${id}`,
    method: 'get'
  })
}

// 分页获取班级列表
export function getClassesWithPagination(page, size, keyword, majorId, grade) {
  return request({
    url: '/api/classes/page',
    method: 'get',
    params: { page, size, keyword, majorId, grade }
  })
}

// 获取所有班级
export function getClasses() {
  return request({
    url: '/api/classes',
    method: 'get'
  })
}

// 根据课程获取班级
export function getClassesByCourse(courseId) {
  return request({
    url: `/api/classes/course/${courseId}`,
    method: 'get'
  })
}

// ==================== 导出API对象 ====================

export const courseApi = {
  createCourse,
  updateCourse,
  deleteCourse,
  getCourseById,
  getCoursesWithPagination,
  getCourses
}

export const classApi = {
  createClass,
  updateClass,
  deleteClass,
  batchDeleteClasses,
  getClassById,
  getClassesWithPagination,
  getClasses,
  getClassesByCourse
}

export const userApi = {
  getUserList,
  getUserListWithPagination,
  createUser,
  updateUser,
  deleteUser,
  batchDeleteUsers,
  toggleUserStatus,
  resetUserPassword,
  assignUserRoles,
  getStudents,
  getStudentsWithPagination,
  assignStudentToClass,
  batchAssignStudentsToClass,
  removeStudentFromClass,
  batchRemoveStudentsFromClass
}

// ==================== 专业管理 API ====================

// 获取专业列表
export function getMajorList(params) {
  return request({
    url: '/api/majors',
    method: 'get',
    params
  })
}

// 分页获取专业
export function getMajorsWithPagination(params) {
  return request({
    url: '/api/majors/page',
    method: 'get',
    params
  })
}

// 创建专业
export function createMajor(data) {
  return request({
    url: '/api/majors',
    method: 'post',
    data
  })
}

// 更新专业
export function updateMajor(id, data) {
  return request({
    url: `/api/majors/${id}`,
    method: 'put',
    data
  })
}

// 删除专业
export function deleteMajor(id) {
  return request({
    url: `/api/majors/${id}`,
    method: 'delete'
  })
}

// 批量删除专业
export function batchDeleteMajors(ids) {
  return request({
    url: '/api/majors/batch',
    method: 'delete',
    data: ids
  })
}

// 获取专业详情
export function getMajorById(id) {
  return request({
    url: `/api/majors/${id}`,
    method: 'get'
  })
}

export const majorApi = {
  getMajorList,
  getMajorsWithPagination,
  createMajor,
  updateMajor,
  deleteMajor,
  batchDeleteMajors,
  getMajorById
}

// ==================== 班级课程关联 API ====================

// 获取班级课程关联列表
export function getClassCourseList(params) {
  return request({
    url: '/api/class-courses',
    method: 'get',
    params
  })
}

// 获取所有课程列表
export function getAllCourses() {
  return request({
    url: '/api/class-courses',
    method: 'get'
  })
}

// 分页获取班级课程关联
export function getClassCoursesWithPagination(params) {
  return request({
    url: '/api/class-courses/page',
    method: 'get',
    params
  })
}

// 创建班级课程关联
export function createClassCourse(data) {
  return request({
    url: '/api/class-courses',
    method: 'post',
    data
  })
}

// 更新班级课程关联
export function updateClassCourse(id, data) {
  return request({
    url: `/api/class-courses/${id}`,
    method: 'put',
    data
  })
}

// 删除班级课程关联
export function deleteClassCourse(id) {
  return request({
    url: `/api/class-courses/${id}`,
    method: 'delete'
  })
}

// 批量删除班级课程关联
export function batchDeleteClassCourses(ids) {
  return request({
    url: '/api/class-courses/batch',
    method: 'delete',
    data: ids
  })
}

// 根据班级ID获取课程列表
export function getClassCoursesByClassId(classId) {
  return request({
    url: `/api/class-courses/class/${classId}`,
    method: 'get'
  })
}

// 根据课程ID获取班级列表
export function getClassCoursesByCourseId(courseId) {
  return request({
    url: `/api/class-courses/course/${courseId}`,
    method: 'get'
  })
}

// 删除班级所有课程关联
export function deleteClassCoursesByClassId(classId) {
  return request({
    url: `/api/class-courses/class/${classId}`,
    method: 'delete'
  })
}

// 删除课程所有班级关联
export function deleteClassCoursesByCourseId(courseId) {
  return request({
    url: `/api/class-courses/course/${courseId}`,
    method: 'delete'
  })
}

export const classCourseApi = {
  getClassCourseList,
  getAllCourses,
  getClassCoursesWithPagination,
  createClassCourse,
  updateClassCourse,
  deleteClassCourse,
  batchDeleteClassCourses,
  getClassCoursesByClassId,
  getClassCoursesByCourseId,
  deleteClassCoursesByClassId,
  deleteClassCoursesByCourseId
}

// ==================== 试卷生成相关API ====================

// 生成试卷
export function generatePaper(data) {
  return request({
    url: '/api/papers/generate',
    method: 'post',
    data
  })
}

// 分页获取试卷列表
export function getPapersWithPagination(page, size, keyword) {
  return request({
    url: '/api/papers/page',
    method: 'get',
    params: { page, size, keyword }
  })
}

// 根据ID获取试卷详情
export function getPaperById(id) {
  return request({
    url: `/api/papers/${id}`,
    method: 'get'
  })
}

// 删除试卷
export function deletePaper(id) {
  return request({
    url: `/api/papers/${id}`,
    method: 'delete'
  })
}

export const paperApi = {
  generatePaper,
  getPapersWithPagination,
  getPaperById,
  deletePaper
}

// ==================== 考试管理 API ====================

// 创建考试
export function createExam(data) {
  return request({
    url: '/api/exams',
    method: 'post',
    data
  })
}

// 分页获取考试列表
export function getExamsWithPagination(page, size, keyword, status) {
  return request({
    url: '/api/exams/page',
    method: 'get',
    params: { page, size, keyword, status } 
  })
}

// 根据ID获取考试详情
export function getExamById(id) {
  return request({
    url: `/api/exams/${id}`,
    method: 'get'
  })
}

// 删除考试
export function deleteExam(id) {
  return request({
    url: `/api/exams/${id}`,
    method: 'delete'
  })
}

// 开始考试
export function startExam(id) {
  return request({
    url: `/api/exams/${id}/start`,
    method: 'post'
  })
}

// 结束考试
export function endExam(id) {
  return request({
    url: `/api/exams/${id}/end`,
    method: 'post'
  })
}

// 获取考试学生列表
export function getExamStudents(id) {
  return request({
    url: `/api/exams/${id}/students`,
    method: 'get'
  })
}

// 更新考试
export function updateExam(examId, examData) {
  return request({
    url: `/api/exams/${examId}`,
    method: 'put',
    data: examData
  })
}

// 获取判卷考试列表
export function getGradingExams(page, size, keyword, classId, status) {
  return request({
    url: '/api/exams/grading',
    method: 'get',
    params: { page, size, keyword, classId, status }
  })
}

// 获取考试学生列表（用于判卷）
export function getExamStudentsForGrading(examId, page, size, keyword, status, gradingStatus) {
  return request({
    url: `/api/exams/${examId}/students/grading`,
    method: 'get',
    params: { page, size, keyword, status, gradingStatus }
  })
}

// 获取学生答案
export function getStudentAnswers(examId, studentId) {
  return request({
    url: `/api/exams/${examId}/students/${studentId}/answers`,
    method: 'get'
  })
}

// 保存判卷结果
export function saveGradingResult(gradingData) {
  return request({
    url: '/api/exams/grading/save',
    method: 'post',
    data: gradingData
  })
}

// 提交判卷结果
export function submitGradingResult(gradingData) {
  return request({
    url: '/api/exams/grading/submit',
    method: 'post',
    data: gradingData
  })
}

// 获取用户信息
export function getUserById(id) {
  return request({
    url: `/api/user/${id}`,
    method: 'get'
  })
}

// 获取学生信息
export function getStudentInfo(studentId) {
  return request({
    url: `/api/user/${studentId}`,
    method: 'get'
  })
}

// 获取学生考试结果（仅显示分数）
export function getStudentExamResult(examId) {
  return request({
    url: `/api/exams/${examId}/student-result`,
    method: 'get'
  })
}

// 获取老师端仪表盘统计数据
export function getDashboardStats() {
  return request({
    url: '/api/exams/dashboard/stats',
    method: 'get'
  })
}

// 获取老师端最近活动
export function getRecentActivities() {
  return request({
    url: '/api/exams/dashboard/activities',
    method: 'get'
  })
}

export const examApi = {
  createExam,
  getExamsWithPagination,
  getExamById,
  updateExam,
  deleteExam,
  startExam,
  endExam,
  getExamStudents,
  getGradingExams,
  getExamStudentsForGrading,
  getStudentAnswers,
  saveGradingResult,
  submitGradingResult,
  getStudentInfo,
  getStudentExamResult,
  getDashboardStats,
  getRecentActivities
}

// ==================== 学生端考试 API ====================

// 获取学生的考试列表（按状态分类）
export function getStudentExams() {
  return request({
    url: '/api/student/exams',
    method: 'get'
  })
}

// 获取学生考试详情
export function getStudentExamDetail(examId) {
  return request({
    url: `/api/student/exams/${examId}`,
    method: 'get'
  })
}

// 开始考试
export function startStudentExam(examId) {
  return request({
    url: `/api/student/exams/${examId}/start`,
    method: 'post'
  })
}

// 提交考试答案
export function submitStudentExam(examId, data) {
  return request({
    url: `/api/student/exams/${examId}/submit`,
    method: 'post',
    data: data
  })
}

// 保存考试答卷（草稿）
export function saveStudentExamDraft(examId, data) {
  return request({
    url: `/api/student/exams/${examId}/save`,
    method: 'post',
    data: data
  })
}

// 获取学生考试统计
export function getStudentExamStats() {
  return request({
    url: '/api/student/exams/stats',
    method: 'get'
  })
}

// 获取客户端IP地址
export function getClientIP() {
  return request({
    url: '/api/common/client-ip',
    method: 'get'
  })
}

// 获取学生考试试卷题目
export function getStudentExamPaper(examId) {
  return request({
    url: `/api/student/exam/${examId}/paper`,
    method: 'get',
    params: {
      t: Date.now() // 添加时间戳防止缓存
    }
  })
}

export const studentExamApi = {
  getStudentExams,
  getStudentExamDetail,
  startStudentExam,
  submitStudentExam,
  saveStudentExamDraft,
  getStudentExamStats,
  getStudentExamPaper
}

// ==================== 题库API对象 ====================
export const questionApi = {
  getQuestions,
  getQuestionById,
  createQuestion,
  updateQuestion,
  deleteQuestion,
  batchDeleteQuestions,
  getQuestionStatistics,
  getQuestionsByCourse,
  importQuestions
}