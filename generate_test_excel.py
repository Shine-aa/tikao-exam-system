import openpyxl
from openpyxl import Workbook

wb = Workbook()
ws = wb.active
ws.title = "题库导入"

headers = ['题目类型', '题目内容', '选项A', '选项B', '选项C', '选项D', '选项E', '正确答案', '难度', '分值', '知识点', '标签', '答案解析', '编程语言', '图片路径', '测试用例']
ws.append(headers)

column_widths = [12, 50, 20, 20, 20, 20, 20, 12, 10, 8, 15, 15, 30, 12, 15, 40]
for idx, width in enumerate(column_widths, start=1):
    ws.column_dimensions[openpyxl.utils.get_column_letter(idx)].width = width

test_questions = [
    ['SINGLE_CHOICE', 'Java中用于继承的关键字是?', 'extends', 'implements', 'inherits', 'super', '', 'extends', 'EASY', '3', 'Java基础', 'Java|继承', 'Java使用extends关键字实现继承', '', '', ''],
    ['SINGLE_CHOICE', '以下哪种数据结构的时间复杂度是O(log n)?', '数组', '哈希表', '二叉搜索树', '链表', '', '二叉搜索树', 'MEDIUM', '5', '数据结构', '树|时间复杂度', '二叉搜索树的查找时间复杂度是O(log n)', '', '', ''],
    ['MULTIPLE_CHOICE', '以下哪些是Java的基本数据类型?', 'int', 'String', 'boolean', 'Double', 'double', 'int;boolean;double', 'MEDIUM', '5', 'Java基础', '数据类型', 'String和Double是包装类，不是基本类型', '', '', ''],
    ['MULTIPLE_CHOICE', '以下哪些排序算法是稳定的?', '冒泡排序', '快速排序', '归并排序', '插入排序', '选择排序', '冒泡排序;归并排序;插入排序', 'MEDIUM', '5', '算法', '排序|稳定性', '稳定的排序算法保持相等元素的相对顺序', '', '', ''],
    ['TRUE_FALSE', 'Java中String类是不可变的', '正确', '错误', '', '', '', '正确', 'EASY', '3', 'Java基础', 'String', 'String对象一旦创建就不能被修改', '', '', ''],
    ['TRUE_FALSE', '栈是先进先出的数据结构', '正确', '错误', '', '', '', '错误', 'EASY', '3', '数据结构', '栈', '栈是后进先出LIFO的数据结构', '', '', ''],
    ['FILL_BLANK', '一元二次方程ax²+bx+c=0的判别式Δ=______', '', '', '', '', '', 'b²-4ac', 'MEDIUM', '5', '数学', '代数|方程', '判别式用于判断方程的根的情况', '', '', ''],
    ['FILL_BLANK', '∫x dx = ______', '', '', '', '', '', 'x² Leitstelle/2+C', 'HARD', '5', '数学', '微积分', '这是基本的不定积分公式', '', '', ''],
    ['SUBJECTIVE', '求函数f(x)=x²-4x+3的极值点和极值', '', '', '', '', '', '对f(x)求导得f\'(x)=2x-4，令f\'(x)=0得x=2。将x=2代入f(x)得f(2)=-1。二阶导数f\'\'(x)=2>0，所以x=2是极小值点，极小值为-1。', 'HARD', '10', '数学', '微积分|极值', '通过求导和二阶导数判断极值', '', '', ''],
    ['SUBJECTIVE', '简述牛顿第二定律的内容并写出其数学表达式', '', '', '', '', '', '牛顿第二定律：物体的加速度与所受合力成正比，与质量成反比，方向与合力方向相同。表达式：F=ma，其中F表示合力，m表示质量，a表示加速度。', 'MEDIUM', '8', '物理', '力学', '牛顿第二定律是经典力学的基础定律之一', '', '', ''],
    ['TRUE_FALSE', 'The capital of France is London', '正确', '错误', '', '', '', '错误', 'EASY', '3', '地理', '欧洲|首都', 'Paris is the capital of France, not London', '', '', ''],
    ['FILL_BLANK', 'The sun is much ______ than the moon', '', '', '', '', '', 'larger', 'EASY', '3', '英语', '比较级', 'sun is much larger than moon', '', '', ''],
    ['SINGLE_CHOICE', 'What is the past tense of "go"?', 'went', 'goed', 'gone', 'go', '', 'went', 'EASY', '3', '英语', '语法|时态', 'went是go的不规则过去式', '', '', ''],
    ['SINGLE_CHOICE', '苍白无力的正确成语用法是?', '形容人因害怕而脸色发白，没有力气', '形容文章缺乏说服力', '形容食物没有味道', '形容天气很冷', '', '形容人因害怕而脸色发白，没有力气', 'MEDIUM', '5', '语文', '成语', '这个成语主要用来形容人的状态', '', '', ''],
    ['MULTIPLE_CHOICE', '以下哪些是《红楼梦》中的人物?', '贾宝玉', '林黛玉', '宋江', '薛宝钗', '孙悟空', '贾宝玉;林黛玉;薛宝钗', 'MEDIUM', '5', '文学', '名著|人物', '宋江出自水浒传，孙悟空出自西游记', '', '', ''],
    ['TRUE_FALSE', '抗日战争开始于1931年九一八事变', '正确', '错误', '', '', '', '正确', 'MEDIUM', '3', '历史', '近代史|抗战', '九一八事变标志着日本侵华战争的开始', '', '', ''],
    ['FILL_BLANK', '第一次世界大战爆发于______年', '', '', '', '', '', '1914', 'MEDIUM', '3', '历史', '世界史', '一战于1914年8月爆发', '', '', ''],
    ['SUBJECTIVE', '简述第二次世界大战的起因', '', '', '', '', '', '第二次世界大战的起因主要包括：1)凡尔赛条约对德国的苛刻惩罚；2)经济大萧条导致法西斯主义兴起；3)德国希特勒的扩张野心；4)日本军国买断主义的侵略政策；5)盟国的绥靖政策助长侵略。', 'HARD', '10', '历史', '世界史|二战', '二战起因复杂，涉及经济、政治、军事等多方面因素', '', '', ''],
    ['SINGLE_CHOICE', '以下哪种化学元素在常温下是液态?', '汞', '铁', '铅', '铜', '', '汞', 'EASY', '3', '化学', '元素', '汞是常温下唯一的液态金属元素', '', '', ''],
    ['SINGLE_CHOICE', 'H₂O的化学名称是?', '双氢氧', '水', '过氧化氢', '氢氧酸', '', '水', 'EASY', '3', '化学', '化合物', 'H₂O是水的分子式', '', '', ''],
    ['TRUE_FALSE', '地球是太阳系中最大的行星', '正确', '错误', '', '', '', '错误', 'EASY', '3', '地理', '太阳系', '木星是太阳系中最大的行星', '', '', ''],
    ['FILL_BLANK', '人体细胞中有______对染色体', '', '', '', '', '', '23', 'EASY', '3', '生物', '遗传', '正常人体细胞有23对46条染色体', '', '', ''],
    ['PROGRAMMING', '编写一个Java程序，计算两个整数的和并输出结果。程序需要从标准输入读取两个整数，计算它们的和，然后输出结果。\n要求：\n1. 使用Scanner类读取输入\n2. 计算两数之和\n3. 输出结果', '', '', '', '', '', 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        int sum = a + b;\n        System.out.println(sum);\n        scanner.close();\n    }\n}', 'MEDIUM', '10', 'Java基础', 'Java|输入输出|Scanner', '使用Scanner类的nextInt()方法读取两个整数，然后计算它们的和并输出。', 'JAVA', '', '1\n2|3;5\n7|12;10\n20|30'],
    ['PROGRAMMING', '编写一个Python程序，实现冒泡排序算法，对整数数组进行升序排序。\n要求：\n1. 方法签名：def bubble_sort(arr)\n2. 原地排序，修改原数组\n3. 使用冒泡排序算法\n4. 从标准输入读取整数（用空格分隔），排序后输出', '', '', '', '', '', 'def bubble_sort(arr):\n    n = len(arr)\n    for i in range(n):\n        for j in range(0, n - i - 1):\n            if arr[j] > arr[j + 1]:\n                arr[j], arr[j + 1] = arr[j + 1], arr[j]\n\nif __name__ == "__main__":\n    arr = list(map(int, input().split()))\n    bubble_sort(arr)\n    print(" ".join(map(str, arr)))', 'MEDIUM', '10', '算法', 'Python|排序|冒泡排序', '冒泡排序通过多次遍历数组，比较相邻元素并交换位置，将较大的元素逐步"冒泡"到数组末尾。', 'PYTHON', '', '3 1 2|1 2 3;5 4 3 2 1|1 2 3 4 5'],
    ['PROGRAMMING', '编写一个C++程序，计算斐波那契数列的第n项。\n要求：\n1. 使用递归或循环实现\n2. 从标准输入读取一个整数n（n >= 0）\n3. 输出斐波那契数列的第n项\n4. 斐波那契数列定义：F(0)=0, F(1)=1, F(n)=F(n-1)+F(n-2)', '', '', '', '', '', '#include <iostream>\nusing namespace std;\n\nint fibonacci(int n) {\n    if (n <= 1) return n;\n    int a = 0, b = 1, c;\n    for (int i = 2; i <= n; i++) {\n        c = a + b;\n        a = b;\n        b = c;\n    }\n    return b;\n}\n\nint main() {\n    int n;\n    cin >> n;\n    cout << fibonacci(n) << endl;\n    return 0;\n}', 'MEDIUM', '10', '算法', 'C++|递归|斐波那契', '斐波那契数列可以使用循环或递归实现。这里使用循环方式，时间复杂度为O(n)，空间复杂度为O(1)。', 'CPP', '', '0|0;1|1;5|5;10|55'],
    # 从程序题.xlsx添加的程序题
    ['PROGRAMMING', '输入n值，计算1到n之间(不包括n)所有偶数之和。', '', '', '', '', '', 'n = int(input())\nsum_even = 0\nfor i in range(2, n, 2):\n    sum_even += i\nprint(f"1到{n}之间所有的偶数和为{sum_even}")', 'EASY', '10', 'Python基础', 'Python|循环|条件判断', '使用range(2, n, 2)可以生成从2开始到n-1的所有偶数，然后累加求和。', 'PYTHON', '', '11|1到11之间所有的偶数和为30;10|1到10之间所有的偶数和为20'],
    ['PROGRAMMING', '写一个函数，判断某个年份是否是闰年。从控制台接收用户输入的一个年份y(y>1900)，调用函数，统计从1900年~y年(包括y)之间有多少个闰年。\n闰年判断规则：能被4整除但不能被100整除，或者能被400整除的年份是闰年。', '', '', '', '', '', 'def is_leap_year(year):\n    return (year % 4 == 0 and year % 100 != 0) or (year % 400 == 0)\n\ny = int(input())\ncount = 0\nfor year in range(1900, y + 1):\n    if is_leap_year(year):\n        count += 1\nprint(f"从1900年到{y}年之间有{count}个闰年")', 'MEDIUM', '10', 'Python基础', 'Python|函数|循环|条件判断', '闰年判断需要同时考虑能被4整除、不能被100整除，或者能被400整除的情况。', 'PYTHON', '', '2000|从1900年到2000年之间有25个闰年'],
    ['PROGRAMMING', '写一个函数，判断某个年份是否是闰年。从控制台接收用户输入的一个年份y(y<2080)，调用函数，统计从y到2100年(包括y和2100)之间有多少个闰年。\n闰年判断规则：能被4整除但不能被100整除，或者能被400整除的年份是闰年。', '', '', '', '', '', 'def is_leap_year(year):\n    return (year % 4 == 0 and year % 100 != 0) or (year % 400 == 0)\n\ny = int(input())\ncount = 0\nfor year in range(y, 2101):\n    if is_leap_year(year):\n        count += 1\nprint(f"从{y}年到2100年之间有{count}个闰年")', 'MEDIUM', '10', 'Python基础', 'Python|函数|循环|条件判断', '闰年判断需要同时考虑能被4整除、不能被100整除，或者能被400整除的情况。', 'PYTHON', '', '2023|从2023年到2100年之间有19个闰年'],
    ['PROGRAMMING', '编写一个函数判定某个整数是否是素数。从控制台接收用户输入的两个正整数n1和n2，调用函数，统计n1~n2(包括n1和n2)之间有多少个素数，输出结果。\n素数指大于1且仅能被1和自己整除的整数。', '', '', '', '', '', 'def is_prime(num):\n    if num < 2:\n        return False\n    for i in range(2, int(num ** 0.5) + 1):\n        if num % i == 0:\n            return False\n    return True\n\nn1 = int(input())\nn2 = int(input())\ncount = 0\nfor num in range(n1, n2 + 1):\n    if is_prime(num):\n        count += 1\nprint(f"{n1}和{n2}之间有{count}个素数")', 'MEDIUM', '10', '算法', 'Python|函数|素数|循环', '判断素数时，只需要检查到sqrt(num)即可，因为如果num有大于sqrt(num)的因子，那么必然有小于sqrt(num)的因子。', 'PYTHON', '', '9\n30|9和30之间有6个素数'],
    ['PROGRAMMING', '定义一个判定某个整数是否是素数的函数。要求获取用户输入的正整数n，求n以内(不含n)所有素数之和并输出。\n素数指大于1且仅能被1和自己整除的整数。', '', '', '', '', '', 'def is_prime(num):\n    if num < 2:\n        return False\n    for i in range(2, int(num ** 0.5) + 1):\n        if num % i == 0:\n            return False\n    return True\n\nn = int(input())\nsum_prime = 0\nfor i in range(2, n):\n    if is_prime(i):\n        sum_prime += i\nprint(f"{n}以内所有素数之和为{sum_prime}")', 'MEDIUM', '10', '算法', 'Python|函数|素数|循环', '判断素数时，只需要检查到sqrt(num)即可，因为如果num有大于sqrt(num)的因子，那么必然有小于sqrt(num)的因子。', 'PYTHON', '', '7|7以内所有素数之和为10;2|2以内所有素数之和为0;3|3以内所有素数之和为2;10|10以内所有素数之和为17'],
    ['PROGRAMMING', '先输入n值，然后输入n个整数，求这n个整数的和。', '', '', '', '', '', 'n = int(input())\nsum_val = 0\nfor i in range(n):\n    num = int(input())\n    sum_val += num\nprint(f"输入的{n}个整数和为{sum_val}")', 'EASY', '10', 'Python基础', 'Python|循环|输入输出', '使用循环读取n个整数并累加求和。', 'PYTHON', '', '5\n1\n6\n7\n8\n9|输入的5个整数和为35;3\n2\n6\n7|输入的3个整数和为15'],
]

for question in test_questions:
    ws.append(question)

output_file = '题库导入测试.xlsx'
wb.save(output_file)
print('Excel文件已生成: 题库导入测试.xlsx')
print('共包含 31 道测试题目（包含22道常规题目和9道程序题）')
