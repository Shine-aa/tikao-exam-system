<template>
  <div class="menu-management">
    <div class="page-header">
      <h1>菜单管理</h1>
      <el-button type="primary" @click="handleAdd" :icon="Plus">
        新增菜单
      </el-button>
    </div>

    <!-- 菜单树形表格 -->
    <el-card class="table-card">
      <el-table
        :data="menuList"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        v-loading="loading"
        border
        stripe
      >
        <el-table-column prop="menuName" label="菜单名称" width="200">
          <template #default="{ row }">
            <el-icon v-if="row.icon" class="menu-icon">
              <component :is="row.icon" />
            </el-icon>
            {{ row.menuName }}
          </template>
        </el-table-column>
        
        <el-table-column prop="menuCode" label="菜单代码" width="150" />
        
        <el-table-column prop="menuType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.menuType === 'menu' ? 'primary' : 'success'">
              {{ row.menuType === 'menu' ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="path" label="路径" width="200" />
        
        <el-table-column prop="component" label="组件" width="200" />
        
        <el-table-column prop="sortOrder" label="排序" width="80" />
        
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">
              {{ row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="description" label="描述" min-width="200" />
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
              :icon="Edit"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
              :icon="Delete"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑菜单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑菜单' : '新增菜单'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        
        <el-form-item label="菜单代码" prop="menuCode">
          <el-input v-model="form.menuCode" placeholder="请输入菜单代码" />
        </el-form-item>
        
        <el-form-item label="父菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="parentMenuOptions"
            :props="{ value: 'id', label: 'menuName', children: 'children' }"
            placeholder="请选择父菜单"
            clearable
            check-strictly
          />
        </el-form-item>
        
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio value="menu">菜单</el-radio>
            <el-radio value="button">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="路径" prop="path">
          <el-input v-model="form.path" placeholder="请输入路径" />
        </el-form-item>
        
        <el-form-item label="组件" prop="component">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        
        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入图标名称" />
        </el-form-item>
        
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        
        <el-form-item label="状态" prop="isActive">
          <el-radio-group v-model="form.isActive">
            <el-radio :value="true">启用</el-radio>
            <el-radio :value="false">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getMenuList,
  createMenu,
  updateMenu,
  deleteMenu
} from '@/api/admin'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const menuList = ref([])
const parentMenuOptions = ref([])

// 表单数据
const form = reactive({
  id: null,
  menuName: '',
  menuCode: '',
  parentId: null,
  menuType: 'menu',
  path: '',
  component: '',
  icon: '',
  sortOrder: 0,
  isActive: true,
  description: ''
})

// 表单验证规则
const rules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  menuCode: [
    { required: true, message: '请输入菜单代码', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_:]+$/, message: '菜单代码只能包含字母、数字、下划线和冒号', trigger: 'blur' }
  ],
  menuType: [
    { required: true, message: '请选择菜单类型', trigger: 'change' }
  ],
  isActive: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const formRef = ref()

// 计算属性
const filteredParentOptions = computed(() => {
  return parentMenuOptions.value.filter(menu => {
    if (isEdit.value && form.id) {
      return menu.id !== form.id
    }
    return true
  })
})

// 方法
const loadMenuList = async () => {
  try {
    loading.value = true
    const response = await getMenuList()
    const allMenus = response.data || []
    // 构建树形结构
    menuList.value = buildMenuTree(allMenus)
    parentMenuOptions.value = buildParentOptions(allMenus)
  } catch (error) {
    ElMessage.error('加载菜单列表失败')
    console.error('Load menu list error:', error)
  } finally {
    loading.value = false
  }
}

// 构建菜单树形结构
const buildMenuTree = (menus, parentId = null) => {
  const tree = []
  const filteredMenus = menus.filter(menu => menu.parentId === parentId)
  
  // 按排序字段排序
  filteredMenus.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
  
  for (const menu of filteredMenus) {
    const children = buildMenuTree(menus, menu.id)
    if (children.length > 0) {
      menu.children = children
    }
    tree.push(menu)
  }
  
  return tree
}

const buildParentOptions = (menus, parentId = null, level = 0) => {
  const options = []
  const prefix = '　'.repeat(level)
  
  for (const menu of menus) {
    if (menu.parentId === parentId && menu.menuType === 'menu') {
      options.push({
        id: menu.id,
        menuName: prefix + menu.menuName,
        children: buildParentOptions(menus, menu.id, level + 1)
      })
    }
  }
  
  return options
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(form, {
    id: row.id,
    menuName: row.menuName,
    menuCode: row.menuCode,
    parentId: row.parentId,
    menuType: row.menuType,
    path: row.path || '',
    component: row.component || '',
    icon: row.icon || '',
    sortOrder: row.sortOrder || 0,
    isActive: row.isActive,
    description: row.description || ''
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除菜单 "${row.menuName}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteMenu(row.id)
    ElMessage.success('删除成功')
    loadMenuList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('Delete menu error:', error)
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    const data = { ...form }
    if (data.parentId === null || data.parentId === '') {
      data.parentId = null
    }
    
    if (isEdit.value) {
      await updateMenu(data.id, data)
      ElMessage.success('更新成功')
    } else {
      await createMenu(data)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    loadMenuList()
  } catch (error) {
    if (error !== false) { // 不是表单验证错误
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      console.error('Submit menu error:', error)
    }
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    menuName: '',
    menuCode: '',
    parentId: null,
    menuType: 'menu',
    path: '',
    component: '',
    icon: '',
    sortOrder: 0,
    isActive: true,
    description: ''
  })
  formRef.value?.clearValidate()
}

// 生命周期
onMounted(() => {
  loadMenuList()
})
</script>

<style scoped>
.menu-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  color: #303133;
}

.table-card {
  margin-bottom: 20px;
}

.menu-icon {
  margin-right: 8px;
  color: #409eff;
}

:deep(.el-table .el-table__row) {
  height: 50px;
}

:deep(.el-tree-select) {
  width: 100%;
}
</style>
