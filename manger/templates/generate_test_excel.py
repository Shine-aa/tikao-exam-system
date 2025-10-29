import openpyxl
from openpyxl import Workbook

wb = Workbook()
ws = wb.active
ws.title = "题库导入"

headers = ['题目类型', '题目内容', '选项A', '选项B', '选项C', '选项D', '选项E', '正确答案', '难度', '分值', '知识点', '标签', '答案解析', '图片路径']
ws.append(headers)

column_widths = [12, 50, 20, 20, 20, 20, 20, 12, 10, 8, 15, 15, 30, 15]
for idx, width in enumerate(column_widths, start=1):
    ws.column_dimensions[openpyxl.utils.get_column_letter(idx)].width = width

test_questions = [
    ['SINGLE_CHOICE', 'Java中用于继承的关键字是?', 'extends', 'implements', 'inherits', 'super', '', 'extends', 'EASY', '3', 'Java基础', 'Java|继承', 'Java使用extends关键字实现继承', ''],
    ['SINGLE_CHOICE', '以下哪种数据结构的时间复杂度是O(log n)?', '数组', '哈希表', '二叉搜索树', '链表', '', '二叉搜索树', 'MEDIUM', '5', '数据结构', '树|时间复杂度', '二叉搜索树的查找时间复杂度是O(log n)', ''],
    ['MULTIPLE_CHOICE', '以下哪些是Java的基本数据类型?', 'int', 'String', 'boolean', 'Double', 'double', 'int;boolean;double', 'MEDIUM', '5', 'Java基础', '数据类型', 'String和Double是包装类，不是基本类型', ''],
    ['MULTIPLE_CHOICE', '以下哪些排序算法是稳定的?', '冒泡排序', '快速排序', '归并排序', '插入排序', '选择排序', '冒泡排序;归并排序;插入排序', 'MEDIUM', '5', '算法', '排序|稳定性', '稳定的排序算法保持相等元素的相对顺序', ''],
    ['TRUE_FALSE', 'Java中String类是不可变的', '正确', '错误', '', '', '', '正确', 'EASY', '3', 'Java基础', 'String', 'String对象一旦创建就不能被修改', ''],
    ['TRUE_FALSE', '栈是先进先出的数据结构', '正确', '错误', '', '', '', '错误', 'EASY', '3', '数据结构', '栈', '栈是后进先出LIFO的数据结构', ''],
    ['FILL_BLANK', '一元二次方程ax²+bx+c=0的判别式Δ=______', '', '', '', '', '', 'b²-4ac', 'MEDIUM', '5', '数学', '代数|方程', '判别式用于判断方程的根的情况', ''],
    ['FILL_BLANK', '∫x dx = ______', '', '', '', '', '', 'x² Leitstelle/2+C', 'HARD', '5', '数学', '微积分', '这是基本的不定积分公式', ''],
    ['SUBJECTIVE', '求函数f(x)=x²-4x+3的极值点和极值', '', '', '', '', '', '对f(x)求导得f\'(x)=2x-4，令f\'(x)=0得x=2。将x=2代入f(x)得f(2)=-1。二阶导数f\'\'(x)=2>0，所以x=2是极小值点，极小值为-1。', 'HARD', '10', '数学', '微积分|极值', '通过求导和二阶导数判断极值', ''],
    ['SUBJECTIVE', '简述牛顿第二定律的内容并写出其数学表达式', '', '', '', '', '', '牛顿第二定律：物体的加速度与所受合力成正比，与质量成反比，方向与合力方向相同。表达式：F=ma，其中F表示合力，m表示质量，a表示加速度。', 'MEDIUM', '8', '物理', '力学', '牛顿第二定律是经典力学的基础定律之一', ''],
    ['TRUE_FALSE', 'The capital of France is London', '正确', '错误', '', '', '', '错误', 'EASY', '3', '地理', '欧洲|首都', 'Paris is the capital of France, not London', ''],
    ['FILL_BLANK', 'The sun is much ______ than the moon', '', '', '', '', '', 'larger', 'EASY', '3', '英语', '比较级', 'sun is much larger than moon', ''],
    ['SINGLE_CHOICE', 'What is the past tense of "go"?', 'went', 'goed', 'gone', 'go', '', 'went', 'EASY', '3', '英语', '语法|时态', 'went是go的不规则过去式', ''],
    ['SINGLE_CHOICE', '苍白无力的正确成语用法是?', '形容人因害怕而脸色发白，没有力气', '形容文章缺乏说服力', '形容食物没有味道', '形容天气很冷', '', '形容人因害怕而脸色发白，没有力气', 'MEDIUM', '5', '语文', '成语', '这个成语主要用来形容人的状态', ''],
    ['MULTIPLE_CHOICE', '以下哪些是《红楼梦》中的人物?', '贾宝玉', '林黛玉', '宋江', '薛宝钗', '孙悟空', '贾宝玉;林黛玉;薛宝钗', 'MEDIUM', '5', '文学', '名著|人物', '宋江出自水浒传，孙悟空出自西游记', ''],
    ['TRUE_FALSE', '抗日战争开始于1931年九一八事变', '正确', '错误', '', '', '', '正确', 'MEDIUM', '3', '历史', '近代史|抗战', '九一八事变标志着日本侵华战争的开始', ''],
    ['FILL_BLANK', '第一次世界大战爆发于______年', '', '', '', '', '', '1914', 'MEDIUM', '3', '历史', '世界史', '一战于1914年8月爆发', ''],
    ['SUBJECTIVE', '简述第二次世界大战的起因', '', '', '', '', '', '第二次世界大战的起因主要包括：1)凡尔赛条约对德国的苛刻惩罚；2)经济大萧条导致法西斯主义兴起；3)德国希特勒的扩张野心；4)日本军国买断主义的侵略政策；5)盟国的绥靖政策助长侵略。', 'HARD', '10', '历史', '世界史|二战', '二战起因复杂，涉及经济、政治、军事等多方面因素', ''],
    ['SINGLE_CHOICE', '以下哪种化学元素在常温下是液态?', '汞', '铁', '铅', '铜', '', '汞', 'EASY', '3', '化学', '元素', '汞是常温下唯一的液态金属元素', ''],
    ['SINGLE_CHOICE', 'H₂O的化学名称是?', '双氢氧', '水', '过氧化氢', '氢氧酸', '', '水', 'EASY', '3', '化学', '化合物', 'H₂O是水的分子式', ''],
    ['TRUE_FALSE', '地球是太阳系中最大的行星', '正确', '错误', '', '', '', '错误', 'EASY', '3', '地理', '太阳系', '木星是太阳系中最大的行星', ''],
    ['FILL_BLANK', '人体细胞中有______对染色体', '', '', '', '', '', '23', 'EASY', '3', '生物', '遗传', '正常人体细胞有23对46条染色体', ''],
]

for question in test_questions:
    ws.append(question)

output_file = '题库导入测试.xlsx'
wb.save(output_file)
print('Excel文件已生成: 题库导入测试.xlsx')
print('共包含 22 道测试题目')
