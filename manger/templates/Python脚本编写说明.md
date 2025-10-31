# Python 脚本编写说明

## 概述

本文档详细说明如何编写和修改 `generate_test_excel.py` 脚本，用于生成符合系统要求的题库导入 Excel 文件。

## 脚本功能

`generate_test_excel.py` 脚本的主要功能：
- 生成符合系统格式要求的 Excel 文件
- 包含多种题目类型的测试数据
- 自动设置列宽和格式
- 提供完整的示例数据

## 环境要求

### Python 版本
- Python 3.6 或更高版本
- 推荐使用 Python 3.8+

### 依赖库
```bash
pip install openpyxl
```

### 安装命令
```bash
# 方式1: 使用 pip
pip install openpyxl

# 方式2: 使用 conda
conda install openpyxl

# 方式3: 使用 requirements.txt
pip install -r requirements.txt
```

## 脚本结构

### 1. 导入模块
```python
import openpyxl
from openpyxl import Workbook
```

### 2. 创建工作簿
```python
wb = Workbook()
ws = wb.active
ws.title = "题库导入"
```

### 3. 设置表头
```python
headers = ['题目类型', '题目内容', '选项A', '选项B', '选项C', '选项D', '选项E', '正确答案', '难度', '分值', '知识点', '标签', '答案解析', '图片路径']
ws.append(headers)
```

### 4. 设置列宽
```python
column_widths = [12, 50, 20, 20, 20, 20, 20, 12, 10, 8, 15, 15, 30, 15]
for idx, width in enumerate(column_widths, start=1):
    ws.column_dimensions[openpyxl.utils.get_column_letter(idx)].width = width
```

### 5. 题目数据
```python
test_questions = [
    # 题目数据列表
]
```

### 6. 保存文件
```python
output_file = '题库导入测试.xlsx'
wb.save(output_file)
print(f'Excel文件已生成: {output_file}')
```

## 数据格式规范

### 题目数据结构
每个题目是一个包含 14 个元素的列表：

```python
[
    '题目类型',      # 第1列: SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, SUBJECTIVE
    '题目内容',      # 第2列: 题目的具体内容
    '选项A',        # 第3列: 第一个选项
    '选项B',        # 第4列: 第二个选项
    '选项C',        # 第5列: 第三个选项
    '选项D',        # 第6列: 第四个选项
    '选项E',        # 第7列: 第五个选项（可选）
    '正确答案',      # 第8列: 正确答案内容
    '难度',         # 第9列: EASY, MEDIUM, HARD
    '分值',         # 第10列: 数字
    '知识点',       # 第11列: 知识点名称
    '标签',         # 第12列: 标签，用|分隔
    '答案解析',     # 第13列: 题目解析
    '图片路径'      # 第14列: 图片路径（可选）
]
```

### 题目类型规范

#### 1. 单选题 (SINGLE_CHOICE)
```python
['SINGLE_CHOICE', '题目内容', '选项A', '选项B', '选项C', '选项D', '', '正确答案', '难度', '分值', '知识点', '标签', '解析', '']
```

**要求**:
- 必须填写选项A、B、C、D
- 选项E可以为空字符串
- 正确答案填写正确选项的内容

#### 2. 多选题 (MULTIPLE_CHOICE)
```python
['MULTIPLE_CHOICE', '题目内容', '选项A', '选项B', '选项C', '选项D', '选项E', '答案1;答案2;答案3', '难度', '分值', '知识点', '标签', '解析', '']
```

**要求**:
- 必须填写选项A、B、C、D
- 选项E可选
- 正确答案用分号(;)分隔

#### 3. 判断题 (TRUE_FALSE)
```python
['TRUE_FALSE', '题目内容', '正确', '错误', '', '', '', '正确', '难度', '分值', '知识点', '标签', '解析', '']
```

**要求**:
- 选项A填写"正确"
- 选项B填写"错误"
- 选项C、D、E为空字符串
- 正确答案填写"正确"或"错误"

#### 4. 填空题 (FILL_BLANK)
```python
['FILL_BLANK', '题目内容', '', '', '', '', '', '答案', '难度', '分值', '知识点', '标签', '解析', '']
```

**要求**:
- 选项列都为空字符串
- 正确答案填写填空答案

#### 5. 主观题 (SUBJECTIVE)
```python
['SUBJECTIVE', '题目内容', '', '', '', '', '', '参考答案', '难度', '分值', '知识点', '标签', '解析', '']
```

**要求**:
- 选项列都为空字符串
- 正确答案填写参考答案

## 自定义题目数据

### 添加新题目
在 `test_questions` 列表中添加新的题目数据：

```python
test_questions = [
    # 现有题目...
    
    # 新增题目
    ['SINGLE_CHOICE', '新题目内容', '选项A', '选项B', '选项C', '选项D', '', '正确答案', 'EASY', '3', '知识点', '标签', '解析', ''],
]
```

### 修改现有题目
直接修改 `test_questions` 列表中对应题目的数据：

```python
# 修改第1题
test_questions[0] = ['SINGLE_CHOICE', '修改后的题目', '新选项A', '新选项B', '新选项C', '新选项D', '', '新答案', 'MEDIUM', '5', '新知识点', '新标签', '新解析', '']
```

### 批量生成题目
可以使用循环批量生成相似题目：

```python
# 生成10道数学题
math_questions = []
for i in range(1, 11):
    question = [
        'SINGLE_CHOICE',
        f'数学题第{i}题',
        f'选项A{i}',
        f'选项B{i}',
        f'选项C{i}',
        f'选项D{i}',
        '',
        f'答案{i}',
        'MEDIUM',
        '5',
        '数学',
        '数学|基础',
        f'解析{i}',
        ''
    ]
    math_questions.append(question)

# 添加到总题目列表
test_questions.extend(math_questions)
```

## 列宽设置

### 默认列宽
```python
column_widths = [12, 50, 20, 20, 20, 20, 20, 12, 10, 8, 15, 15, 30, 15]
```

### 列宽说明
| 列号 | 列名 | 宽度 | 说明 |
|------|------|------|------|
| 1 | 题目类型 | 12 | 固定宽度 |
| 2 | 题目内容 | 50 | 较宽，容纳长题目 |
| 3-7 | 选项A-E | 20 | 中等宽度 |
| 8 | 正确答案 | 12 | 固定宽度 |
| 9 | 难度 | 10 | 固定宽度 |
| 10 | 分值 | 8 | 较窄 |
| 11 | 知识点 | 15 | 中等宽度 |
| 12 | 标签 | 15 | 中等宽度 |
| 13 | 答案解析 | 30 | 最宽，容纳长解析 |
| 14 | 图片路径 | 15 | 中等宽度 |

### 自定义列宽
```python
# 修改列宽
column_widths = [15, 60, 25, 25, 25, 25, 25, 15, 12, 10, 20, 20, 40, 20]
```

## 文件输出

### 默认输出
```python
output_file = '题库导入测试.xlsx'
wb.save(output_file)
```

### 自定义输出
```python
# 自定义文件名
import datetime
timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
output_file = f'题库导入_{timestamp}.xlsx'

# 自定义路径
output_file = '/path/to/output/题库导入测试.xlsx'
```

## 错误处理

### 常见错误
1. **openpyxl 未安装**
   ```
   ModuleNotFoundError: No module named 'openpyxl'
   ```
   解决: `pip install openpyxl`

2. **文件权限错误**
   ```
   PermissionError: [Errno 13] Permission denied
   ```
   解决: 检查文件权限，确保有写入权限

3. **数据格式错误**
   ```
   ValueError: Invalid data format
   ```
   解决: 检查题目数据格式是否正确

### 添加错误处理
```python
try:
    wb.save(output_file)
    print(f'Excel文件已生成: {output_file}')
    print(f'共包含 {len(test_questions)} 道测试题目')
except Exception as e:
    print(f'生成文件失败: {e}')
```

## 运行脚本

### 基本运行
```bash
python generate_test_excel.py
```

### 指定Python版本
```bash
python3 generate_test_excel.py
```

### 在IDE中运行
- 使用 PyCharm、VSCode 等IDE
- 直接运行脚本文件
- 查看输出结果

## 脚本优化

### 1. 添加配置
```python
# 配置文件
CONFIG = {
    'output_file': '题库导入测试.xlsx',
    'sheet_name': '题库导入',
    'column_widths': [12, 50, 20, 20, 20, 20, 20, 12, 10, 8, 15, 15, 30, 15],
    'default_difficulty': 'MEDIUM',
    'default_points': 5
}
```

### 2. 添加日志
```python
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

logger.info(f'开始生成Excel文件...')
logger.info(f'共处理 {len(test_questions)} 道题目')
```

### 3. 添加验证
```python
def validate_question(question):
    """验证题目数据格式"""
    if len(question) != 14:
        return False, "题目数据长度不正确"
    
    if question[0] not in ['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE', 'FILL_BLANK', 'SUBJECTIVE']:
        return False, "题目类型不正确"
    
    return True, "验证通过"

# 验证所有题目
for i, question in enumerate(test_questions):
    is_valid, message = validate_question(question)
    if not is_valid:
        print(f"第{i+1}题验证失败: {message}")
```

## 扩展功能

### 1. 从文件读取数据
```python
import json

def load_questions_from_json(file_path):
    """从JSON文件加载题目数据"""
    with open(file_path, 'r', encoding='utf-8') as f:
        return json.load(f)

# 使用示例
questions = load_questions_from_json('questions.json')
test_questions.extend(questions)
```

### 2. 生成多个文件
```python
def generate_multiple_files():
    """生成多个Excel文件"""
    subjects = ['数学', '语文', '英语', '物理', '化学']
    
    for subject in subjects:
        # 筛选该学科的题目
        subject_questions = [q for q in test_questions if subject in q[10]]
        
        # 生成文件
        wb = Workbook()
        ws = wb.active
        ws.title = f"{subject}题库"
        
        # 添加表头和数据
        ws.append(headers)
        for question in subject_questions:
            ws.append(question)
        
        # 保存文件
        output_file = f'{subject}题库导入.xlsx'
        wb.save(output_file)
        print(f'已生成: {output_file}')
```

### 3. 添加样式
```python
from openpyxl.styles import Font, PatternFill, Alignment

def add_styles(ws):
    """添加样式"""
    # 表头样式
    header_font = Font(bold=True, color="FFFFFF")
    header_fill = PatternFill(start_color="366092", end_color="366092", fill_type="solid")
    
    for cell in ws[1]:
        cell.font = header_font
        cell.fill = header_fill
        cell.alignment = Alignment(horizontal="center")
    
    # 数据样式
    for row in ws.iter_rows(min_row=2):
        for cell in row:
            cell.alignment = Alignment(horizontal="left", vertical="top", wrap_text=True)
```

## 最佳实践

### 1. 代码组织
- 使用函数封装功能
- 添加注释说明
- 使用常量定义配置

### 2. 数据管理
- 使用列表存储题目数据
- 保持数据格式一致
- 添加数据验证

### 3. 错误处理
- 添加异常处理
- 提供友好的错误信息
- 记录错误日志

### 4. 性能优化
- 批量处理数据
- 避免重复计算
- 使用合适的数据结构

## 总结

`generate_test_excel.py` 脚本是生成题库导入文件的重要工具。通过本文档的说明，您可以：

1. 理解脚本的结构和功能
2. 自定义题目数据
3. 修改输出格式
4. 添加新功能
5. 处理常见错误

建议在实际使用中：
- 先使用默认的测试数据
- 根据需要修改题目内容
- 添加数据验证
- 优化脚本性能
