// ========== 1. 创建约束 ==========
CREATE CONSTRAINT IF NOT EXISTS FOR (s:Student) REQUIRE s.id IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (t:Teacher) REQUIRE t.id IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (c:Class) REQUIRE c.id IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (co:Course) REQUIRE co.id IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (k:KnowledgePoint) REQUIRE k.id IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (q:Question) REQUIRE q.id IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (p:Paper) REQUIRE p.id IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (e:Exam) REQUIRE e.id IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (u:User) REQUIRE u.id IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (r:Role) REQUIRE r.id IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (p:Permission) REQUIRE p.id IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (m:Menu) REQUIRE m.id IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (m:Major) REQUIRE m.id IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (se:StudentExam) REQUIRE se.id IS UNIQUE;

// ========== 2. 创建知识点 ==========
MERGE (k:KnowledgePoint {id: 50}) SET k.name = '计算机基础', k.description = '计算机基础知识', k.level = 1;
MERGE (k:KnowledgePoint {id: 51}) SET k.name = '编程语言', k.description = '各种编程语言相关知识', k.level = 1;
MERGE (k:KnowledgePoint {id: 52}) SET k.name = '数据结构', k.description = '数据结构与算法', k.level = 1;
MERGE (k:KnowledgePoint {id: 53}) SET k.name = '数据库', k.description = '数据库相关知识', k.level = 1;
MERGE (k:KnowledgePoint {id: 54}) SET k.name = '网络技术', k.description = '计算机网络相关知识', k.level = 1;
MERGE (k:KnowledgePoint {id: 55}) SET k.name = '软件工程', k.description = '软件工程相关知识', k.level = 1;
MERGE (k:KnowledgePoint {id: 56}) SET k.name = 'Java基础', k.description = 'Java编程语言基础', k.level = 2;
MERGE (k:KnowledgePoint {id: 57}) SET k.name = 'Python基础', k.description = 'Python编程语言基础', k.level = 2;
MERGE (k:KnowledgePoint {id: 58}) SET k.name = 'C++基础', k.description = 'C++编程语言基础', k.level = 2;
MERGE (k:KnowledgePoint {id: 59}) SET k.name = '线性表', k.description = '线性表数据结构', k.level = 2;
MERGE (k:KnowledgePoint {id: 60}) SET k.name = '树结构', k.description = '树形数据结构', k.level = 2;
MERGE (k:KnowledgePoint {id: 61}) SET k.name = '图结构', k.description = '图形数据结构', k.level = 2;
MERGE (k:KnowledgePoint {id: 62}) SET k.name = 'MySQL', k.description = 'MySQL数据库', k.level = 2;
MERGE (k:KnowledgePoint {id: 63}) SET k.name = 'Oracle', k.description = 'Oracle数据库', k.level = 2;
MERGE (k:KnowledgePoint {id: 64}) SET k.name = 'Redis', k.description = 'Redis缓存数据库', k.level = 2;

// ========== 3. 创建课程（统一使用第一段的属性命名） ==========
MERGE (c:Course {id: 1}) SET c.code = 'CS101', c.name = 'Java程序设计', c.description = 'Java编程基础课程，包括面向对象编程、集合框架、异常处理等内容';
MERGE (c:Course {id: 2}) SET c.code = 'CS102', c.name = '数据结构与算法', c.description = '数据结构与算法基础课程，包括线性表、树、图等数据结构和常用算法';
MERGE (c:Course {id: 3}) SET c.code = 'CS103', c.name = '数据库原理', c.description = '数据库系统原理课程，包括关系数据库、SQL语言、数据库设计等';
MERGE (c:Course {id: 4}) SET c.code = 'CS104', c.name = '计算机网络', c.description = '计算机网络基础课程，包括网络协议、网络编程、网络安全等';
MERGE (c:Course {id: 5}) SET c.code = 'AI101', c.name = '机器学习', c.description = '机器学习基础课程，包括监督学习、无监督学习、深度学习等';
MERGE (c:Course {id: 6}) SET c.code = 'IS101', c.name = '密码学', c.description = '密码学基础课程，包括对称加密、非对称加密、数字签名等';
MERGE (c:Course {id: 7}) SET c.code = 'CS1088', c.name = '啊我大概', c.description = 'ASDFSGD';

// ========== 4. 创建班级 ==========
MERGE (c:Class {id: 1}) SET c.code = 'SE2022-01', c.name = '2022级软件工程1班', c.grade = '2022';
MERGE (c:Class {id: 2}) SET c.code = 'SE2022-02', c.name = '2022级软件工程2班', c.grade = '2022';
MERGE (c:Class {id: 3}) SET c.code = 'SE2023-01', c.name = '2023级软件工程1班', c.grade = '2023';
MERGE (c:Class {id: 4}) SET c.code = 'CS2022-01', c.name = '2022级计算机科学与技术1班', c.grade = '2022';
MERGE (c:Class {id: 5}) SET c.code = 'CS2022-02', c.name = '2022级计算机科学与技术2班', c.grade = '2022';
MERGE (c:Class {id: 6}) SET c.code = 'CS2023-01', c.name = '2023级计算机科学与技术1班', c.grade = '2023';
MERGE (c:Class {id: 7}) SET c.code = 'AI2022-01', c.name = '2022级人工智能1班', c.grade = '2022';
MERGE (c:Class {id: 8}) SET c.code = 'AI2022-02', c.name = '2022级人工智能2班', c.grade = '2022';
MERGE (c:Class {id: 9}) SET c.code = 'IS2022-01', c.name = '2022级信息安全1班', c.grade = '2022';
MERGE (c:Class {id: 10}) SET c.code = 'IS2022-02', c.name = '2022级信息安全2班', c.grade = '2022';

// ========== 5. 创建题目（使用第一段的完整属性） ==========
MERGE (q:Question {id: 1}) SET q.content = '以下哪个是Java中的关键字？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 2}) SET q.content = '以下哪种数据结构的时间复杂度是O(1)？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 3}) SET q.content = '数据库事务具有ACID特性。', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 1;
MERGE (q:Question {id: 4}) SET q.content = '请简述时间复杂度和空间复杂度的概念。', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 5}) SET q.content = 'Java中用于声明常量的关键字是____。', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 6}) SET q.content = 'Java中，____关键字用于定义类，____关键字用于定义接口，____关键字用于实现接口，____关键字用于继承类。', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 4;
MERGE (q:Question {id: 9}) SET q.content = 'Java中，以下哪个不是基本数据类型？', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 10}) SET q.content = 'Java中，以下哪个关键字用于继承？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 11}) SET q.content = 'Java中，以下哪个是数组的正确声明方式？', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 12}) SET q.content = 'Java中，以下哪个关键字用于实现接口？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 13}) SET q.content = 'Java中，以下哪个是访问修饰符？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 14}) SET q.content = 'Java中，以下哪个关键字用于创建对象？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 15}) SET q.content = 'Java中，以下哪个是异常处理的关键字？', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 16}) SET q.content = 'Java中，以下哪个集合类不是线程安全的？', q.type = 'SINGLE_CHOICE', q.difficulty = 'HARD', q.points = 2;
MERGE (q:Question {id: 17}) SET q.content = 'Java中，以下哪个关键字用于方法重写？', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 18}) SET q.content = 'Java中，以下哪个是包装类？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 19}) SET q.content = 'Java中，以下哪个关键字用于静态方法？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 20}) SET q.content = 'Java中，以下哪个是字符串的不可变类？', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 21}) SET q.content = 'Java中，以下哪个关键字用于抽象类？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 22}) SET q.content = 'Java中，以下哪些是基本数据类型？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 23}) SET q.content = 'Java中，以下哪些是访问修饰符？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 24}) SET q.content = 'Java中，以下哪些集合类是线程安全的？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'HARD', q.points = 3;
MERGE (q:Question {id: 25}) SET q.content = 'Java中，以下哪些是异常类型？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 26}) SET q.content = 'Java中，以下哪些关键字用于循环？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 27}) SET q.content = 'Java中，以下哪些是面向对象的特性？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 28}) SET q.content = 'Java中，以下哪些是字符串类？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 29}) SET q.content = 'Java中，以下哪些是集合接口？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 30}) SET q.content = 'Java中，以下哪些是关键字？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 31}) SET q.content = 'Java中，以下哪些是包装类？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 32}) SET q.content = 'Java中，一个类只能继承一个父类。', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 33}) SET q.content = 'Java中，String类是不可变的。', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 34}) SET q.content = 'Java中，抽象类不能被实例化。', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 35}) SET q.content = 'Java中，接口可以包含具体方法的实现。', q.type = 'TRUE_FALSE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 36}) SET q.content = 'Java中，数组的长度是固定的。', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 37}) SET q.content = 'Java中，用于定义常量的关键字是______。', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 38}) SET q.content = 'Java中，用于继承的关键字是______。', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 39}) SET q.content = 'Java中，用于实现接口的关键字是______。', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 40}) SET q.content = 'Java中，用于创建对象的关键字是______。', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 41}) SET q.content = 'Java中，用于定义抽象类的关键字是______。', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 42}) SET q.content = '请简述Java中面向对象的三大特性，并举例说明。', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 43}) SET q.content = '请说明Java中异常处理机制，并写出try-catch-finally的基本语法。', q.type = 'SUBJECTIVE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 44}) SET q.content = '请比较String、StringBuffer和StringBuilder的区别。', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 45}) SET q.content = '请说明Java中集合框架的层次结构。', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 46}) SET q.content = '请说明Java中垃圾回收机制的基本原理。', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 47}) SET q.content = '以下哪种数据结构是先进先出（FIFO）的？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 48}) SET q.content = '以下哪种数据结构是后进先出（LIFO）的？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 49}) SET q.content = '以下哪种排序算法的时间复杂度是O(n²)？', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 50}) SET q.content = '以下哪种排序算法的时间复杂度是O(n log n)？', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 51}) SET q.content = '以下哪种数据结构不支持随机访问？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 52}) SET q.content = '以下哪种树的每个节点最多有两个子节点？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 53}) SET q.content = '以下哪种算法用于查找图中的最短路径？', q.type = 'SINGLE_CHOICE', q.difficulty = 'HARD', q.points = 2;
MERGE (q:Question {id: 54}) SET q.content = '以下哪种数据结构用于实现优先队列？', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 55}) SET q.content = '以下哪种算法的时间复杂度是O(1)？', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 56}) SET q.content = '以下哪种数据结构是线性的？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 57}) SET q.content = '以下哪种算法用于拓扑排序？', q.type = 'SINGLE_CHOICE', q.difficulty = 'HARD', q.points = 2;
MERGE (q:Question {id: 58}) SET q.content = '以下哪种数据结构支持快速插入和删除？', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 59}) SET q.content = '以下哪种排序算法是稳定的？', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 60}) SET q.content = '以下哪种算法用于检测图中是否有环？', q.type = 'SINGLE_CHOICE', q.difficulty = 'HARD', q.points = 2;
MERGE (q:Question {id: 61}) SET q.content = '以下哪种数据结构用于实现LRU缓存？', q.type = 'SINGLE_CHOICE', q.difficulty = 'HARD', q.points = 2;
MERGE (q:Question {id: 62}) SET q.content = '以下哪些是线性数据结构？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 63}) SET q.content = '以下哪些排序算法的时间复杂度是O(n log n)？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 64}) SET q.content = '以下哪些是稳定的排序算法？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 65}) SET q.content = '以下哪些是图的遍历算法？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 66}) SET q.content = '以下哪些数据结构支持动态大小？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 67}) SET q.content = '以下哪些是树的性质？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 68}) SET q.content = '以下哪些算法用于查找？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 69}) SET q.content = '以下哪些是堆的性质？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 70}) SET q.content = '以下哪些是哈希表的优点？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 71}) SET q.content = '以下哪些是动态规划的特点？', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'HARD', q.points = 3;
MERGE (q:Question {id: 72}) SET q.content = '栈是先进先出的数据结构。', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 73}) SET q.content = '快速排序是稳定的排序算法。', q.type = 'TRUE_FALSE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 74}) SET q.content = '二叉搜索树的中序遍历是有序的。', q.type = 'TRUE_FALSE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 75}) SET q.content = '哈希表的查找时间复杂度是O(1)。', q.type = 'TRUE_FALSE', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 76}) SET q.content = '动态规划总是比贪心算法更优。', q.type = 'TRUE_FALSE', q.difficulty = 'HARD', q.points = 2;
MERGE (q:Question {id: 77}) SET q.content = '栈是______进______出的数据结构。', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 78}) SET q.content = '队列是______进______出的数据结构。', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 2;
MERGE (q:Question {id: 79}) SET q.content = '快速排序的平均时间复杂度是______。', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 80}) SET q.content = '二叉搜索树的查找时间复杂度是______。', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 81}) SET q.content = '哈希表的平均查找时间复杂度是______。', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 2;
MERGE (q:Question {id: 82}) SET q.content = '请说明快速排序算法的原理，并分析其时间复杂度。', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 83}) SET q.content = '请说明二叉搜索树的性质，并说明如何进行查找操作。', q.type = 'SUBJECTIVE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 84}) SET q.content = '请说明动态规划的基本思想，并举例说明。', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 85}) SET q.content = '请说明图的深度优先搜索和广度优先搜索的区别。', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 86}) SET q.content = '请说明哈希表的实现原理，并分析其优缺点。', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 87}) SET q.content = 'Java中用于继承的关键字是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 88}) SET q.content = '以下哪种数据结构的时间复杂度是O(log n)?', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 89}) SET q.content = '以下哪些是Java的基本数据类型?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 90}) SET q.content = '以下哪些排序算法是稳定的?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 91}) SET q.content = 'Java中String类是不可变的', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 92}) SET q.content = '栈是先进先出的数据结构', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 93}) SET q.content = '一元二次方程ax²+bx+c=0的判别式Δ=______', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 94}) SET q.content = '∫x dx = ______', q.type = 'FILL_BLANK', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 95}) SET q.content = '简述牛顿第二定律的内容并写出其数学表达式', q.type = 'SUBJECTIVE', q.difficulty = 'MEDIUM', q.points = 8;
MERGE (q:Question {id: 96}) SET q.content = 'The capital of France is London', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 97}) SET q.content = 'The sun is much ______ than the moon', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 98}) SET q.content = 'What is the past tense of \"go\"?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 99}) SET q.content = '苍白无力的正确成语用法是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 100}) SET q.content = '以下哪些是《红楼梦》中的人物?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 101}) SET q.content = '抗日战争开始于1931年九一八事变', q.type = 'TRUE_FALSE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 102}) SET q.content = '第一次世界大战爆发于______年', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 103}) SET q.content = '简述第二次世界大战的起因', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 10;
MERGE (q:Question {id: 104}) SET q.content = '以下哪种化学元素在常温下是液态?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 105}) SET q.content = 'H₂O的化学名称是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 106}) SET q.content = '地球是太阳系中最大的行星', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 107}) SET q.content = '人体细胞中有______对染色体', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 108}) SET q.content = 'Java中用于继承的关键字是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 109}) SET q.content = '以下哪种数据结构的时间复杂度是O(log n)?', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 110}) SET q.content = '以下哪些是Java的基本数据类型?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 111}) SET q.content = '以下哪些排序算法是稳定的?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 112}) SET q.content = 'Java中String类是不可变的', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 113}) SET q.content = '栈是先进先出的数据结构', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 114}) SET q.content = '一元二次方程ax²+bx+c=0的判别式Δ=______', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 115}) SET q.content = '∫x dx = ______', q.type = 'FILL_BLANK', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 116}) SET q.content = '简述牛顿第二定律的内容并写出其数学表达式', q.type = 'SUBJECTIVE', q.difficulty = 'MEDIUM', q.points = 8;
MERGE (q:Question {id: 117}) SET q.content = 'The capital of France is London', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 118}) SET q.content = 'The sun is much ______ than the moon', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 119}) SET q.content = 'What is the past tense of \"go\"?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 120}) SET q.content = '苍白无力的正确成语用法是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 121}) SET q.content = '以下哪些是《红楼梦》中的人物?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 122}) SET q.content = '抗日战争开始于1931年九一八事变', q.type = 'TRUE_FALSE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 123}) SET q.content = '第一次世界大战爆发于______年', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 124}) SET q.content = '简述第二次世界大战的起因', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 10;
MERGE (q:Question {id: 125}) SET q.content = '以下哪种化学元素在常温下是液态?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 126}) SET q.content = 'H₂O的化学名称是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 127}) SET q.content = '地球是太阳系中最大的行星', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 128}) SET q.content = '人体细胞中有______对染色体', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 129}) SET q.content = 'Java中用于继承的关键字是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 130}) SET q.content = '以下哪种数据结构的时间复杂度是O(log n)?', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 131}) SET q.content = '以下哪些是Java的基本数据类型?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 132}) SET q.content = '以下哪些排序算法是稳定的?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 133}) SET q.content = 'Java中String类是不可变的', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 134}) SET q.content = '栈是先进先出的数据结构', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 135}) SET q.content = '一元二次方程ax²+bx+c=0的判别式Δ=______', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 136}) SET q.content = '∫x dx = ______', q.type = 'FILL_BLANK', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 137}) SET q.content = '求函数f(x)=x²-4x+3的极值点和极值', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 10;
MERGE (q:Question {id: 138}) SET q.content = '简述牛顿第二定律的内容并写出其数学表达式', q.type = 'SUBJECTIVE', q.difficulty = 'MEDIUM', q.points = 8;
MERGE (q:Question {id: 139}) SET q.content = 'The capital of France is London', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 140}) SET q.content = 'The sun is much ______ than the moon', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 141}) SET q.content = 'What is the past tense of \"go\"?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 142}) SET q.content = '苍白无力的正确成语用法是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 143}) SET q.content = '以下哪些是《红楼梦》中的人物?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 144}) SET q.content = '抗日战争开始于1931年九一八事变', q.type = 'TRUE_FALSE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 145}) SET q.content = '第一次世界大战爆发于______年', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 146}) SET q.content = '简述第二次世界大战的起因', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 10;
MERGE (q:Question {id: 147}) SET q.content = '以下哪种化学元素在常温下是液态?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 148}) SET q.content = 'H₂O的化学名称是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 149}) SET q.content = '地球是太阳系中最大的行星', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 150}) SET q.content = '人体细胞中有______对染色体', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 159}) SET q.content = '求函数f(x)=x²-4x+3的极值点和极值', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 10;
MERGE (q:Question {id: 161}) SET q.content = 'The capital of France is London', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 173}) SET q.content = 'Java中用于继承的关键字是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 174}) SET q.content = '以下哪种数据结构的时间复杂度是O(log n)?', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 175}) SET q.content = '以下哪些是Java的基本数据类型?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 176}) SET q.content = '以下哪些排序算法是稳定的?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 177}) SET q.content = 'Java中String类是不可变的', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 178}) SET q.content = '栈是先进先出的数据结构', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 179}) SET q.content = '一元二次方程ax²+bx+c=0的判别式Δ=______', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 180}) SET q.content = '∫x dx = ______', q.type = 'FILL_BLANK', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 181}) SET q.content = '求函数f(x)=x²-4x+3的极值点和极值', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 10;
MERGE (q:Question {id: 182}) SET q.content = '简述牛顿第二定律的内容并写出其数学表达式', q.type = 'SUBJECTIVE', q.difficulty = 'MEDIUM', q.points = 8;
MERGE (q:Question {id: 183}) SET q.content = 'The capital of France is London', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 184}) SET q.content = 'The sun is much ______ than the moon', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 185}) SET q.content = 'What is the past tense of \"go\"?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 186}) SET q.content = '苍白无力的正确成语用法是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 187}) SET q.content = '以下哪些是《红楼梦》中的人物?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 188}) SET q.content = '抗日战争开始于1931年九一八事变', q.type = 'TRUE_FALSE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 189}) SET q.content = '第一次世界大战爆发于______年', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 190}) SET q.content = '简述第二次世界大战的起因', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 10;
MERGE (q:Question {id: 191}) SET q.content = '以下哪种化学元素在常温下是液态?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 192}) SET q.content = 'H₂O的化学名称是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 193}) SET q.content = '地球是太阳系中最大的行星', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 194}) SET q.content = '人体细胞中有______对染色体', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 195}) SET q.content = '编写一个Java程序，计算两个整数的和并输出结果。程序需要从标准输入读取两个整数，计算它们的和，然后输出结果。\n\n要求：\n1. 使用Scanner类读取输入\n2. 计算两数之和\n3. 输出结果', q.type = 'PROGRAMMING', q.difficulty = 'EASY', q.points = 5;
MERGE (q:Question {id: 196}) SET q.content = '编写一个Java方法，接收一个整数数组作为参数，返回数组中的最大值。\n\n要求：\n1. 方法签名：public static int findMax(int[] arr)\n2. 如果数组为空，返回Integer.MIN_VALUE\n3. 遍历数组找出最大值', q.type = 'PROGRAMMING', q.difficulty = 'EASY', q.points = 5;
MERGE (q:Question {id: 197}) SET q.content = '编写一个Java方法，实现字符串反转功能。\n\n要求：\n1. 方法签名：public static String reverse(String str)\n2. 使用StringBuilder实现\n3. 返回反转后的字符串', q.type = 'PROGRAMMING', q.difficulty = 'MEDIUM', q.points = 8;
MERGE (q:Question {id: 198}) SET q.content = '实现二分查找算法，在一个已排序的整数数组中查找目标值。\n\n要求：\n1. 方法签名：public static int binarySearch(int[] arr, int target)\n2. 如果找到目标值，返回其索引；否则返回-1\n3. 数组必须是有序的', q.type = 'PROGRAMMING', q.difficulty = 'MEDIUM', q.points = 10;
MERGE (q:Question {id: 199}) SET q.content = '编写一个Java方法，使用递归计算斐波那契数列的第n项。\n\n要求：\n1. 方法签名：public static int fibonacci(int n)\n2. 斐波那契数列定义：F(0)=0, F(1)=1, F(n)=F(n-1)+F(n-2)\n3. 需要考虑边界情况', q.type = 'PROGRAMMING', q.difficulty = 'MEDIUM', q.points = 8;
MERGE (q:Question {id: 200}) SET q.content = '实现冒泡排序算法，对整数数组进行升序排序。\n\n要求：\n1. 方法签名：public static void bubbleSort(int[] arr)\n2. 原地排序，修改原数组\n3. 使用冒泡排序算法', q.type = 'PROGRAMMING', q.difficulty = 'MEDIUM', q.points = 10;
MERGE (q:Question {id: 201}) SET q.content = '编写一个Python函数，计算列表中所有元素的和。\n\n要求：\n1. 函数签名：def sum_list(lst)\n2. 如果列表为空，返回0\n3. 处理列表中的整数和浮点数', q.type = 'PROGRAMMING', q.difficulty = 'EASY', q.points = 5;
MERGE (q:Question {id: 202}) SET q.content = '编写一个Python函数，判断一个字符串是否为回文（正读反读都一样）。\n\n要求：\n1. 函数签名：def is_palindrome(s)\n2. 忽略大小写和空格\n3. 返回True或False', q.type = 'PROGRAMMING', q.difficulty = 'MEDIUM', q.points = 8;
MERGE (q:Question {id: 203}) SET q.content = '给定一个整数数组和一个目标值，找出数组中两个数的索引，使得它们的和等于目标值。\n\n要求：\n1. 函数签名：def two_sum(nums, target)\n2. 假设每种输入只会对应一个答案\n3. 不能使用同一个元素两次\n4. 返回两个索引的列表', q.type = 'PROGRAMMING', q.difficulty = 'MEDIUM', q.points = 10;
MERGE (q:Question {id: 204}) SET q.content = '创建一个Student类，包含以下属性和方法：\n\n属性：\n- name (String): 学生姓名\n- age (int): 学生年龄\n- grade (double): 学生成绩\n\n方法：\n- 构造方法：初始化所有属性\n- getName(): 返回姓名\n- getAge(): 返回年龄\n- getGrade(): 返回成绩\n- setGrade(double grade): 设置成绩\n\n要求：\n1. 使用封装原则\n2. 提供getter和setter方法', q.type = 'PROGRAMMING', q.difficulty = 'MEDIUM', q.points = 10;
MERGE (q:Question {id: 205}) SET q.content = '定义一个简单的链表节点类Node，然后实现反转链表的方法。\n\nNode类结构：\nclass Node {\n    int val;\n    Node next;\n    Node(int val) { this.val = val; }\n}\n\n要求：\n1. 方法签名：public static Node reverseList(Node head)\n2. 反转链表中的所有节点\n3. 返回新的头节点', q.type = 'PROGRAMMING', q.difficulty = 'HARD', q.points = 15;
MERGE (q:Question {id: 206}) SET q.content = '使用列表实现一个栈（Stack）类，包含以下方法：\n\n方法：\n- push(item): 将元素压入栈顶\n- pop(): 弹出并返回栈顶元素，如果栈为空抛出异常\n- peek(): 返回栈顶元素但不移除，如果栈为空返回None\n- is_empty(): 判断栈是否为空\n- size(): 返回栈中元素的个数\n\n要求：\n1. 使用列表作为底层存储\n2. 栈顶在列表末尾', q.type = 'PROGRAMMING', q.difficulty = 'MEDIUM', q.points = 10;
MERGE (q:Question {id: 207}) SET q.content = 'Java中用于继承的关键字是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 208}) SET q.content = '以下哪种数据结构的时间复杂度是O(log n)?', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 209}) SET q.content = '以下哪些是Java的基本数据类型?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 210}) SET q.content = '以下哪些排序算法是稳定的?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 212}) SET q.content = '栈是先进先出的数据结构', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 213}) SET q.content = '一元二次方程ax²+bx+c=0的判别式Δ=______', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 214}) SET q.content = '∫x dx = ______', q.type = 'FILL_BLANK', q.difficulty = 'HARD', q.points = 5;
MERGE (q:Question {id: 215}) SET q.content = '求函数f(x)=x²-4x+3的极值点和极值', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 10;
MERGE (q:Question {id: 216}) SET q.content = '简述牛顿第二定律的内容并写出其数学表达式', q.type = 'SUBJECTIVE', q.difficulty = 'MEDIUM', q.points = 8;
MERGE (q:Question {id: 217}) SET q.content = 'The capital of France is London', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 218}) SET q.content = 'The sun is much ______ than the moon', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 219}) SET q.content = 'What is the past tense of \"go\"?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 220}) SET q.content = '苍白无力的正确成语用法是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 221}) SET q.content = '以下哪些是《红楼梦》中的人物?', q.type = 'MULTIPLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 5;
MERGE (q:Question {id: 222}) SET q.content = '抗日战争开始于1931年九一八事变', q.type = 'TRUE_FALSE', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 223}) SET q.content = '第一次世界大战爆发于______年', q.type = 'FILL_BLANK', q.difficulty = 'MEDIUM', q.points = 3;
MERGE (q:Question {id: 224}) SET q.content = '简述第二次世界大战的起因', q.type = 'SUBJECTIVE', q.difficulty = 'HARD', q.points = 10;
MERGE (q:Question {id: 225}) SET q.content = '以下哪种化学元素在常温下是液态?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 226}) SET q.content = 'H₂O的化学名称是?', q.type = 'SINGLE_CHOICE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 227}) SET q.content = '地球是太阳系中最大的行星', q.type = 'TRUE_FALSE', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 228}) SET q.content = '人体细胞中有______对染色体', q.type = 'FILL_BLANK', q.difficulty = 'EASY', q.points = 3;
MERGE (q:Question {id: 229}) SET q.content = '编写一个Java程序，计算两个整数的和并输出结果。程序需要从标准输入读取两个整数，计算它们的和，然后输出结果。\n要求：\n1. 使用Scanner类读取输入\n2. 计算两数之和\n3. 输出结果', q.type = 'PROGRAMMING', q.difficulty = 'MEDIUM', q.points = 10;
MERGE (q:Question {id: 230}) SET q.content = '编写一个Python程序，实现冒泡排序算法，对整数数组进行升序排序。\n要求：\n1. 方法签名：def bubble_sort(arr)\n2. 原地排序，修改原数组\n3. 使用冒泡排序算法\n4. 从标准输入读取整数（用空格分隔），排序后输出', q.type = 'PROGRAMMING', q.difficulty = 'MEDIUM', q.points = 10;
MERGE (q:Question {id: 231}) SET q.content = '编写一个C++程序，计算斐波那契数列的第n项。\n要求：\n1. 使用递归或循环实现\n2. 从标准输入读取一个整数n（n >= 0）\n3. 输出斐波那契数列的第n项\n4. 斐波那契数列定义：F(0)=0, F(1)=1, F(n)=F(n-1)+F(n-2)', q.type = 'PROGRAMMING', q.difficulty = 'MEDIUM', q.points = 10;
MERGE (q:Question {id: 244}) SET q.content = '12312312', q.type = 'SINGLE_CHOICE', q.difficulty = 'MEDIUM', q.points = 3;

// ========== 6. 创建试卷 ==========
MERGE (p:Paper {id: 3}) SET p.code = 'PAPER_1760497928873', p.name = '艾斯但', p.description = '啊达瓦';
MERGE (p:Paper {id: 4}) SET p.code = 'PAPER_1760498852204', p.name = '啊达瓦', p.description = '啊达瓦';
MERGE (p:Paper {id: 5}) SET p.code = 'PAPER_1760499497356', p.name = '阿达', p.description = '啊达瓦';
MERGE (p:Paper {id: 6}) SET p.code = 'PAPER_1760581135392', p.name = '阿瓦达伟大', p.description = '挖的';
MERGE (p:Paper {id: 7}) SET p.code = 'PAPER_1760667513310', p.name = 'sfsfff', p.description = 'awdawd';
MERGE (p:Paper {id: 13}) SET p.code = 'PAPER_1760668616263', p.name = 'dawdawawfawf', p.description = 'awdawdawd';
MERGE (p:Paper {id: 14}) SET p.code = 'PAPER_1761273662405', p.name = 'fejaefdja', p.description = 'awd1';
MERGE (p:Paper {id: 15}) SET p.code = 'PAPER_1761535341493', p.name = '打撒德瓦达', p.description = '阿瓦达';
MERGE (p:Paper {id: 16}) SET p.code = 'PAPER_1761613568556', p.name = 'asdgfdhfgjhgfds', p.description = 'dasd';
MERGE (p:Paper {id: 17}) SET p.code = 'PAPER_1761614572173', p.name = 'dasfsgfdhgfgfdsd', p.description = '';
MERGE (p:Paper {id: 18}) SET p.code = 'PAPER_1761620522907', p.name = 'asdasdasdas', p.description = 'asdasdasdasd';
MERGE (p:Paper {id: 19}) SET p.code = 'PAPER_1761621106391', p.name = 'sadgsfdgfsadfsdg', p.description = 'sadgsdsfgrthsdgh';
MERGE (p:Paper {id: 20}) SET p.code = 'PAPER_1761702418047', p.name = '发噶哇撒旦', p.description = 'asdsfhdgfhm';
MERGE (p:Paper {id: 21}) SET p.code = 'PAPER_1761789387004', p.name = '啊大苏打被发现工会人工费', p.description = '暗室逢灯公司发货的钢化膜';
MERGE (p:Paper {id: 22}) SET p.code = 'PAPER_1762132800615', p.name = 'adhsfngdjmfhgjhfdgs', p.description = 'fdsgfdhgfjhgj';
MERGE (p:Paper {id: 23}) SET p.code = 'PAPER_1762220261642', p.name = 'aghfsgafd', p.description = 'afgdshfdgjfhkgj';
MERGE (p:Paper {id: 24}) SET p.code = 'PAPER_1762303699843', p.name = '三大法宝给你换个马甲，可见光和法国的发生的', p.description = '啊但是犯得上广泛的不合格奶粉';

// ========== 7. 创建考试 ==========
MERGE (e:Exam {id: 1}) SET e.code = 'EXAM_1760581686303', e.name = '阿瓦达伟大 - 考试';
MERGE (e:Exam {id: 2}) SET e.code = 'EXAM_1760667541194', e.name = 'sfsfff - 考试';
MERGE (e:Exam {id: 3}) SET e.code = 'EXAM_1760668637960', e.name = 'dawdawawfawf - 考试';
MERGE (e:Exam {id: 4}) SET e.code = 'EXAM_1761273752639', e.name = 'fejaefdja - 考试';
MERGE (e:Exam {id: 5}) SET e.code = 'EXAM_1761613608950', e.name = 'asdgfdhfgjhgfds - 考试';
MERGE (e:Exam {id: 6}) SET e.code = 'EXAM_1761614600663', e.name = 'dasfsgfdhgfgfdsd - 考试';
MERGE (e:Exam {id: 7}) SET e.code = 'EXAM_1761620541828', e.name = 'asdasdasdas - 考试';
MERGE (e:Exam {id: 8}) SET e.code = 'EXAM_1761621126174', e.name = 'sadgsfdgfsadfsdg - 考试';
MERGE (e:Exam {id: 9}) SET e.code = 'EXAM_1761623419895', e.name = 'sadgsfdgfsadfsdg - 考试';
MERGE (e:Exam {id: 10}) SET e.code = 'EXAM_1761702443598', e.name = '发噶哇撒旦 - 考试';
MERGE (e:Exam {id: 11}) SET e.code = 'EXAM_1761703139404', e.name = '阿瓦达伟大 - 考试';
MERGE (e:Exam {id: 12}) SET e.code = 'EXAM_1761789416123', e.name = '啊大苏打被发现工会人工费 - 考试';
MERGE (e:Exam {id: 13}) SET e.code = 'EXAM_1762131092531', e.name = '啊大苏打被发现工会人工费 - 考试';
MERGE (e:Exam {id: 14}) SET e.code = 'EXAM_1762132848596', e.name = 'adhsfngdjmfhgjhfdgs - 考试';
MERGE (e:Exam {id: 15}) SET e.code = 'EXAM_1762220270424', e.name = 'aghfsgafd - 考试';
MERGE (e:Exam {id: 16}) SET e.code = 'EXAM_1762303708837', e.name = '三大法宝给你换个马甲，可见光和法国的发生的 - 考试';
MERGE (e:Exam {id: 17}) SET e.code = 'EXAM_1762390413707', e.name = '三大法宝给你换个马甲，可见光和法国的发生的 - 考试';
MERGE (e:Exam {id: 18}) SET e.code = 'EXAM_1762390424133', e.name = '三大法宝给你换个马甲，可见光和法国的发生的 - 考试';
MERGE (e:Exam {id: 19}) SET e.code = 'EXAM_1762390854253', e.name = '三大法宝给你换个马甲，可见光和法国的发生的 - 考试';
MERGE (e:Exam {id: 20}) SET e.code = 'EXAM_1762391096654', e.name = '三大法宝给你换个马甲，可见光和法国的发生的 - 考试';

// ========== 8. 创建用户系统节点（来自第二段） ==========
// Users
MERGE (u:User {id: 1}) SET u.username = '', u.real_name = 'Wib17jaGy+f+dqRRLlU31LtkyMXXhRH5P7mBqVJSWZA=', u.email = '3310530272@qq.com', u.phone = 'VENfs5/WIp94Y3epBDQPz3lIHiL+ldNUQq4hn1TXgAk=';
MERGE (u:User {id: 2}) SET u.username = '', u.real_name = 'kOVDpxTPMCxh154PMx2L/llhfHP2GouehIqgfxRUNLE=', u.email = '3310530279@qq.com', u.phone = 'CAyH7i3GuDBGp5dTu/6pa8x2XQG7oNx7u0Hj2SxlNeU=';
MERGE (u:User {id: 6}) SET u.username = '', u.real_name = 'GTOr1vxr5noxerTXtqffVJzzEVefZDZnIihbw1lizFU=', u.email = '3310530270@qq.com', u.phone = 'iNA1TSxIOglENt+5axq4cIuFVeyHYQT1kNSH+/UIrBA=';
MERGE (u:User {id: 8}) SET u.username = '', u.real_name = 'VEcZdfPU8OkbKyUYHXZI+7wGVwiiq5zL1H8tFtu9zzE=', u.email = '3310530275@qq.com', u.phone = 'lM5NWTBXSBnqD85Z132j82IKiH9y1dhTCCjzaGZMqtc=';

// Roles
MERGE (r:Role {id: 1}) SET r.name = '超级管理员', r.code = 'SUPER_ADMIN';
MERGE (r:Role {id: 6}) SET r.name = '普通用户', r.code = 'USER';
MERGE (r:Role {id: 7}) SET r.name = '教师', r.code = 'TEACHER';

// Permissions
MERGE (p:Permission {id: 1}) SET p.name = '查看系统配置', p.code = 'system:config:read';
MERGE (p:Permission {id: 2}) SET p.name = '修改系统配置', p.code = 'system:config:update';
MERGE (p:Permission {id: 3}) SET p.name = '查看系统日志', p.code = 'system:log:read';
MERGE (p:Permission {id: 4}) SET p.name = '创建用户', p.code = 'user:create';
MERGE (p:Permission {id: 5}) SET p.name = '查看用户', p.code = 'user:read';
MERGE (p:Permission {id: 6}) SET p.name = '修改用户', p.code = 'user:update';
MERGE (p:Permission {id: 7}) SET p.name = '删除用户', p.code = 'user:delete';
MERGE (p:Permission {id: 8}) SET p.name = '分配角色', p.code = 'user:role:assign';
MERGE (p:Permission {id: 9}) SET p.name = '重置密码', p.code = 'user:password:reset';
MERGE (p:Permission {id: 10}) SET p.name = '创建角色', p.code = 'role:create';
MERGE (p:Permission {id: 11}) SET p.name = '查看角色', p.code = 'role:read';
MERGE (p:Permission {id: 12}) SET p.name = '修改角色', p.code = 'role:update';
MERGE (p:Permission {id: 13}) SET p.name = '删除角色', p.code = 'role:delete';
MERGE (p:Permission {id: 14}) SET p.name = '分配权限', p.code = 'role:permission:assign';
MERGE (p:Permission {id: 15}) SET p.name = '导出数据', p.code = 'data:export';
MERGE (p:Permission {id: 16}) SET p.name = '导入数据', p.code = 'data:import';
MERGE (p:Permission {id: 17}) SET p.name = '数据备份', p.code = 'data:backup';
MERGE (p:Permission {id: 18}) SET p.name = '查看菜单', p.code = 'menu:read';
MERGE (p:Permission {id: 19}) SET p.name = '创建菜单', p.code = 'menu:create';
MERGE (p:Permission {id: 20}) SET p.name = '修改菜单', p.code = 'menu:edit';
MERGE (p:Permission {id: 21}) SET p.name = '删除菜单', p.code = 'menu:delete';

// Menus
MERGE (m:Menu {id: 1}) SET m.name = 'system', m.path = '';
MERGE (m:Menu {id: 3}) SET m.name = 'dashboard', m.path = '1';
MERGE (m:Menu {id: 4}) SET m.name = 'user_management', m.path = '1';
MERGE (m:Menu {id: 5}) SET m.name = 'role_management', m.path = '1';
MERGE (m:Menu {id: 6}) SET m.name = 'permission_management', m.path = '1';
MERGE (m:Menu {id: 7}) SET m.name = 'menu_management', m.path = '1';
MERGE (m:Menu {id: 10}) SET m.name = 'user_profile', m.path = '1';
MERGE (m:Menu {id: 11}) SET m.name = 'user:create', m.path = '4';
MERGE (m:Menu {id: 12}) SET m.name = 'user:edit', m.path = '4';
MERGE (m:Menu {id: 13}) SET m.name = 'user:delete', m.path = '4';
MERGE (m:Menu {id: 14}) SET m.name = 'role:create', m.path = '5';
MERGE (m:Menu {id: 15}) SET m.name = 'role:edit', m.path = '5';
MERGE (m:Menu {id: 16}) SET m.name = 'role:delete', m.path = '5';
MERGE (m:Menu {id: 17}) SET m.name = 'menu:create', m.path = '7';
MERGE (m:Menu {id: 18}) SET m.name = 'menu:edit', m.path = '7';
MERGE (m:Menu {id: 19}) SET m.name = 'menu:delete', m.path = '7';

// Majors
MERGE (m:Major {id: 1}) SET m.name = 'SE', m.code = '软件工程';
MERGE (m:Major {id: 2}) SET m.name = 'CS', m.code = '计算机科学与技术';
MERGE (m:Major {id: 3}) SET m.name = 'AI', m.code = '人工智能';
MERGE (m:Major {id: 4}) SET m.name = 'IS', m.code = '信息安全';

// StudentExams
MERGE (se:StudentExam {id: 1}) SET se.score = 1, se.status = '2025-10-17 09:20:25';
MERGE (se:StudentExam {id: 2}) SET se.score = 1, se.status = '2025-10-17 10:15:04';
MERGE (se:StudentExam {id: 3}) SET se.score = 1, se.status = '2025-10-17 10:29:46';
MERGE (se:StudentExam {id: 4}) SET se.score = 1, se.status = '2025-10-17 10:30:11';
MERGE (se:StudentExam {id: 5}) SET se.score = 1, se.status = '2025-10-17 10:38:06';
MERGE (se:StudentExam {id: 6}) SET se.score = 1, se.status = '2025-10-17 10:39:06';
MERGE (se:StudentExam {id: 7}) SET se.score = 1, se.status = '2025-10-24 10:43:08';
MERGE (se:StudentExam {id: 8}) SET se.score = 1, se.status = '';
MERGE (se:StudentExam {id: 9}) SET se.score = 1, se.status = '2025-10-28 09:07:30';
MERGE (se:StudentExam {id: 10}) SET se.score = 1, se.status = '';
MERGE (se:StudentExam {id: 11}) SET se.score = 1, se.status = '2025-10-28 09:24:11';
MERGE (se:StudentExam {id: 12}) SET se.score = 1, se.status = '';
MERGE (se:StudentExam {id: 13}) SET se.score = 1, se.status = '2025-10-28 11:03:25';
MERGE (se:StudentExam {id: 14}) SET se.score = 1, se.status = '';
MERGE (se:StudentExam {id: 15}) SET se.score = 1, se.status = '2025-10-28 11:13:08';
MERGE (se:StudentExam {id: 16}) SET se.score = 1, se.status = '2025-10-28 11:19:28';
MERGE (se:StudentExam {id: 17}) SET se.score = 1, se.status = '2025-10-28 11:51:12';
MERGE (se:StudentExam {id: 18}) SET se.score = 1, se.status = '2025-10-28 11:52:19';
MERGE (se:StudentExam {id: 19}) SET se.score = 1, se.status = '2025-10-29 09:48:06';
MERGE (se:StudentExam {id: 20}) SET se.score = 1, se.status = '2025-10-29 09:55:32';
MERGE (se:StudentExam {id: 21}) SET se.score = 1, se.status = '2025-10-29 09:59:21';
MERGE (se:StudentExam {id: 22}) SET se.score = 1, se.status = '';
MERGE (se:StudentExam {id: 23}) SET se.score = 1, se.status = '2025-10-30 10:00:19';
MERGE (se:StudentExam {id: 24}) SET se.score = 1, se.status = '2025-10-30 10:05:27';
MERGE (se:StudentExam {id: 25}) SET se.score = 1, se.status = '2025-11-03 08:52:17';
MERGE (se:StudentExam {id: 26}) SET se.score = 1, se.status = '';
MERGE (se:StudentExam {id: 27}) SET se.score = 1, se.status = '2025-11-03 09:21:37';
MERGE (se:StudentExam {id: 28}) SET se.score = 1, se.status = '';
MERGE (se:StudentExam {id: 29}) SET se.score = 1, se.status = '2025-11-04 09:38:25';
MERGE (se:StudentExam {id: 30}) SET se.score = 1, se.status = '';
MERGE (se:StudentExam {id: 31}) SET se.score = 1, se.status = '2025-11-05 08:48:57';
MERGE (se:StudentExam {id: 32}) SET se.score = 1, se.status = '';
MERGE (se:StudentExam {id: 33}) SET se.score = 1, se.status = '2025-11-06 08:54:10';
MERGE (se:StudentExam {id: 34}) SET se.score = 1, se.status = '2025-11-06 08:54:56';
MERGE (se:StudentExam {id: 35}) SET se.score = 1, se.status = '2025-11-06 09:01:11';
MERGE (se:StudentExam {id: 36}) SET se.score = 1, se.status = '2025-11-06 09:05:11';

// ========== 9. 创建兼容性节点（第一段的Student/Teacher） ==========
// 基于User ID创建对应的Student/Teacher节点（为了兼容第一段的关系）
MERGE (s:Student {id: 2}) SET s.name = 'user';
MERGE (s:Student {id: 8}) SET s.name = 'user123';
MERGE (t:Teacher {id: 1}) SET t.name = 'Teacher_1';
MERGE (t:Teacher {id: 6}) SET t.name = 'Teacher_6';

// ========== 10. 创建关系 ==========
// 知识点层级关系（来自第一段）
MATCH (k1:KnowledgePoint {id: 51}), (k2:KnowledgePoint {id: 56}) MERGE (k1)-[:HAS_CHILD]->(k2);
MATCH (k1:KnowledgePoint {id: 51}), (k2:KnowledgePoint {id: 56}) MERGE (k2)-[:PRECEDES]->(k1);
MATCH (k1:KnowledgePoint {id: 51}), (k2:KnowledgePoint {id: 57}) MERGE (k1)-[:HAS_CHILD]->(k2);
MATCH (k1:KnowledgePoint {id: 51}), (k2:KnowledgePoint {id: 57}) MERGE (k2)-[:PRECEDES]->(k1);
MATCH (k1:KnowledgePoint {id: 51}), (k2:KnowledgePoint {id: 58}) MERGE (k1)-[:HAS_CHILD]->(k2);
MATCH (k1:KnowledgePoint {id: 51}), (k2:KnowledgePoint {id: 58}) MERGE (k2)-[:PRECEDES]->(k1);
MATCH (k1:KnowledgePoint {id: 52}), (k2:KnowledgePoint {id: 59}) MERGE (k1)-[:HAS_CHILD]->(k2);
MATCH (k1:KnowledgePoint {id: 52}), (k2:KnowledgePoint {id: 59}) MERGE (k2)-[:PRECEDES]->(k1);
MATCH (k1:KnowledgePoint {id: 52}), (k2:KnowledgePoint {id: 60}) MERGE (k1)-[:HAS_CHILD]->(k2);
MATCH (k1:KnowledgePoint {id: 52}), (k2:KnowledgePoint {id: 60}) MERGE (k2)-[:PRECEDES]->(k1);
MATCH (k1:KnowledgePoint {id: 52}), (k2:KnowledgePoint {id: 61}) MERGE (k1)-[:HAS_CHILD]->(k2);
MATCH (k1:KnowledgePoint {id: 52}), (k2:KnowledgePoint {id: 61}) MERGE (k2)-[:PRECEDES]->(k1);
MATCH (k1:KnowledgePoint {id: 53}), (k2:KnowledgePoint {id: 62}) MERGE (k1)-[:HAS_CHILD]->(k2);
MATCH (k1:KnowledgePoint {id: 53}), (k2:KnowledgePoint {id: 62}) MERGE (k2)-[:PRECEDES]->(k1);
MATCH (k1:KnowledgePoint {id: 53}), (k2:KnowledgePoint {id: 63}) MERGE (k1)-[:HAS_CHILD]->(k2);
MATCH (k1:KnowledgePoint {id: 53}), (k2:KnowledgePoint {id: 63}) MERGE (k2)-[:PRECEDES]->(k1);
MATCH (k1:KnowledgePoint {id: 53}), (k2:KnowledgePoint {id: 64}) MERGE (k1)-[:HAS_CHILD]->(k2);
MATCH (k1:KnowledgePoint {id: 53}), (k2:KnowledgePoint {id: 64}) MERGE (k2)-[:PRECEDES]->(k1);

// 题目与知识点关系（合并第一段的TESTS和第二段的COVERS）
MATCH (q:Question {id: 4}), (k:KnowledgePoint {id: 52}) MERGE (q)-[:TESTS]->(k);
MATCH (q:Question {id: 1}), (k:KnowledgePoint {id: 56}) MERGE (q)-[:TESTS]->(k);
MATCH (q:Question {id: 5}), (k:KnowledgePoint {id: 56}) MERGE (q)-[:TESTS]->(k);
MATCH (q:Question {id: 6}), (k:KnowledgePoint {id: 56}) MERGE (q)-[:TESTS]->(k);
MATCH (q:Question {id: 2}), (k:KnowledgePoint {id: 59}) MERGE (q)-[:TESTS]->(k);
MATCH (q:Question {id: 3}), (k:KnowledgePoint {id: 62}) MERGE (q)-[:TESTS]->(k);
// 也创建COVERS关系以兼容第二段
MATCH (q:Question {id: 4}), (k:KnowledgePoint {id: 52}) MERGE (q)-[:COVERS]->(k);
MATCH (q:Question {id: 1}), (k:KnowledgePoint {id: 56}) MERGE (q)-[:COVERS]->(k);
MATCH (q:Question {id: 5}), (k:KnowledgePoint {id: 56}) MERGE (q)-[:COVERS]->(k);
MATCH (q:Question {id: 6}), (k:KnowledgePoint {id: 56}) MERGE (q)-[:COVERS]->(k);
MATCH (q:Question {id: 2}), (k:KnowledgePoint {id: 59}) MERGE (q)-[:COVERS]->(k);
MATCH (q:Question {id: 3}), (k:KnowledgePoint {id: 62}) MERGE (q)-[:COVERS]->(k);

// 题目与课程关系（合并两段的关系，使用BELONGS_TO）
// Java相关题目 -> Course 1
MATCH (q:Question {id: 9}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 10}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 11}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 12}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 13}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 14}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 15}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 16}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 17}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 18}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 19}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 20}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 21}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 22}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 23}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 24}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 25}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 26}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 27}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 28}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 29}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 30}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 31}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 32}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 33}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 34}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 35}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 36}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 37}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 38}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 39}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 40}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 41}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 42}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 43}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 44}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 45}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 46}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);

// 数据结构相关题目 -> Course 2
MATCH (q:Question {id: 47}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 48}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 49}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 50}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 51}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 52}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 53}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 54}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 55}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 56}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 57}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 58}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 59}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 60}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 61}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 62}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 63}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 64}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 65}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 66}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 67}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 68}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 69}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 70}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 71}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 72}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 73}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 74}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 75}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 76}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 77}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 78}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 79}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 80}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 81}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 82}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 83}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 84}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 85}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 86}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);

// 基础题目 -> Course 3
MATCH (q:Question {id: 1}), (c:Course {id: 3}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 2}), (c:Course {id: 3}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 3}), (c:Course {id: 3}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 4}), (c:Course {id: 3}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 5}), (c:Course {id: 3}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 6}), (c:Course {id: 3}) MERGE (q)-[:BELONGS_TO]->(c);

// 编程题 -> 相应课程
MATCH (q:Question {id: 195}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 196}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 197}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 198}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 199}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 200}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 204}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 205}), (c:Course {id: 1}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 201}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 202}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 203}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);
MATCH (q:Question {id: 206}), (c:Course {id: 2}) MERGE (q)-[:BELONGS_TO]->(c);

// 班级与课程关系（合并两段的关系，创建LEARNS和OFFERS两种关系）
MATCH (c:Class {id: 1}), (co:Course {id: 1}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 1}), (co:Course {id: 2}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 1}), (co:Course {id: 3}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 2}), (co:Course {id: 1}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 2}), (co:Course {id: 2}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 2}), (co:Course {id: 4}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 3}), (co:Course {id: 1}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 3}), (co:Course {id: 2}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 4}), (co:Course {id: 2}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 4}), (co:Course {id: 3}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 4}), (co:Course {id: 4}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 5}), (co:Course {id: 1}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 5}), (co:Course {id: 2}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 5}), (co:Course {id: 3}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 6}), (co:Course {id: 1}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 6}), (co:Course {id: 2}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 7}), (co:Course {id: 5}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 7}), (co:Course {id: 2}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 7}), (co:Course {id: 3}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 8}), (co:Course {id: 5}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 8}), (co:Course {id: 1}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 8}), (co:Course {id: 2}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 9}), (co:Course {id: 6}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 9}), (co:Course {id: 4}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 9}), (co:Course {id: 2}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 10}), (co:Course {id: 6}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 10}), (co:Course {id: 3}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);
MATCH (c:Class {id: 10}), (co:Course {id: 1}) MERGE (c)-[:LEARNS]->(co) MERGE (c)-[:OFFERS]->(co);

// 试卷与课程关系
MATCH (p:Paper {id: 3}), (c:Course {id: 2}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 4}), (c:Course {id: 1}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 5}), (c:Course {id: 2}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 6}), (c:Course {id: 1}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 7}), (c:Course {id: 2}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 13}), (c:Course {id: 1}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 14}), (c:Course {id: 1}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 15}), (c:Course {id: 1}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 16}), (c:Course {id: 2}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 17}), (c:Course {id: 1}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 18}), (c:Course {id: 1}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 19}), (c:Course {id: 2}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 20}), (c:Course {id: 1}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 21}), (c:Course {id: 1}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 22}), (c:Course {id: 1}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 23}), (c:Course {id: 1}) MERGE (p)-[:BELONGS_TO]->(c);
MATCH (p:Paper {id: 24}), (c:Course {id: 1}) MERGE (p)-[:BELONGS_TO]->(c);

// 考试关系（第一段）
MATCH (e:Exam {id: 1}), (p:Paper {id: 6}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 1}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 2}), (p:Paper {id: 7}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 2}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 3}), (p:Paper {id: 13}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 3}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 4}), (p:Paper {id: 14}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 4}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 5}), (p:Paper {id: 16}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 5}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 6}), (p:Paper {id: 17}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 6}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 7}), (p:Paper {id: 18}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 7}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 8}), (p:Paper {id: 19}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 8}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 9}), (p:Paper {id: 19}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 9}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 10}), (p:Paper {id: 20}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 10}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 11}), (p:Paper {id: 6}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 11}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 12}), (p:Paper {id: 21}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 12}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 13}), (p:Paper {id: 21}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 13}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 14}), (p:Paper {id: 22}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 14}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 15}), (p:Paper {id: 23}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 15}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 16}), (p:Paper {id: 24}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 16}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 17}), (p:Paper {id: 24}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 17}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 18}), (p:Paper {id: 24}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 18}), (c:Class {id: 2}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 19}), (p:Paper {id: 24}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 19}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);
MATCH (e:Exam {id: 20}), (p:Paper {id: 24}) MERGE (e)-[:USES_PAPER]->(p);
MATCH (e:Exam {id: 20}), (c:Class {id: 1}) MERGE (e)-[:FOR_CLASS]->(c);

// 学生考试关系（第二段）
MATCH (u:User {id: 2}), (se:StudentExam {id: 1}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 1}), (e:Exam {id: 1}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 2}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 2}), (e:Exam {id: 1}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 3}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 3}), (e:Exam {id: 2}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 4}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 4}), (e:Exam {id: 2}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 5}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 5}), (e:Exam {id: 3}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 6}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 6}), (e:Exam {id: 3}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 7}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 7}), (e:Exam {id: 4}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 8}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 8}), (e:Exam {id: 4}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 9}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 9}), (e:Exam {id: 5}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 10}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 10}), (e:Exam {id: 5}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 11}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 11}), (e:Exam {id: 6}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 12}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 12}), (e:Exam {id: 6}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 13}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 13}), (e:Exam {id: 7}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 14}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 14}), (e:Exam {id: 7}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 15}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 15}), (e:Exam {id: 8}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 16}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 16}), (e:Exam {id: 8}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 17}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 17}), (e:Exam {id: 9}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 18}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 18}), (e:Exam {id: 9}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 19}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 19}), (e:Exam {id: 10}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 20}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 20}), (e:Exam {id: 10}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 21}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 21}), (e:Exam {id: 11}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 22}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 22}), (e:Exam {id: 11}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 23}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 23}), (e:Exam {id: 12}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 24}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 24}), (e:Exam {id: 12}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 25}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 25}), (e:Exam {id: 13}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 26}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 26}), (e:Exam {id: 13}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 27}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 27}), (e:Exam {id: 14}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 28}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 28}), (e:Exam {id: 14}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 29}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 29}), (e:Exam {id: 15}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 30}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 30}), (e:Exam {id: 15}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 31}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 31}), (e:Exam {id: 16}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 32}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 32}), (e:Exam {id: 16}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 33}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 33}), (e:Exam {id: 17}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 8}), (se:StudentExam {id: 34}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 34}), (e:Exam {id: 18}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 35}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 35}), (e:Exam {id: 19}) MERGE (se)-[:FOR_EXAM]->(e);
MATCH (u:User {id: 2}), (se:StudentExam {id: 36}) MERGE (u)-[:TAKES]->(se);
MATCH (se:StudentExam {id: 36}), (e:Exam {id: 20}) MERGE (se)-[:FOR_EXAM]->(e);

// 学生与班级关系（第一段）
MATCH (s:Student {id: 2}), (c:Class {id: 1}) MERGE (s)-[:BELONGS_TO]->(c);
MATCH (s:Student {id: 8}), (c:Class {id: 1}) MERGE (s)-[:BELONGS_TO]->(c);

// 学生参加考试关系（第一段）
MATCH (s:Student {id: 2}), (e:Exam {id: 1}) MERGE (s)-[:TOOK]->(e);
MATCH (s:Student {id: 8}), (e:Exam {id: 1}) MERGE (s)-[:TOOK]->(e);
MATCH (s:Student {id: 2}), (e:Exam {id: 4}) MERGE (s)-[:TOOK]->(e);
MATCH (s:Student {id: 2}), (e:Exam {id: 5}) MERGE (s)-[:TOOK]->(e);
MATCH (s:Student {id: 2}), (e:Exam {id: 6}) MERGE (s)-[:TOOK]->(e);
MATCH (s:Student {id: 8}), (e:Exam {id: 8}) MERGE (s)-[:TOOK]->(e);
MATCH (s:Student {id: 2}), (e:Exam {id: 8}) MERGE (s)-[:TOOK]->(e);
MATCH (s:Student {id: 2}), (e:Exam {id: 7}) MERGE (s)-[:TOOK]->(e);
MATCH (s:Student {id: 8}), (e:Exam {id: 9}) MERGE (s)-[:TOOK]->(e);
MATCH (s:Student {id: 2}), (e:Exam {id: 9}) MERGE (s)-[:TOOK]->(e);
MATCH (s:Student {id: 2}), (e:Exam {id: 15}) MERGE (s)-[:TOOK]->(e);

// 用户系统关系（第二段）
MATCH (u:User {id: 1}), (r:Role {id: 1}) MERGE (u)-[:HAS_ROLE]->(r);
MATCH (u:User {id: 2}), (r:Role {id: 6}) MERGE (u)-[:HAS_ROLE]->(r);
MATCH (u:User {id: 8}), (r:Role {id: 6}) MERGE (u)-[:HAS_ROLE]->(r);
MATCH (u:User {id: 6}), (r:Role {id: 7}) MERGE (u)-[:HAS_ROLE]->(r);

// 角色权限关系（第二段）
MATCH (r:Role {id: 1}), (p:Permission {id: 1}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 2}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 3}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 4}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 5}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 6}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 7}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 8}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 9}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 10}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 11}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 12}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 13}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 14}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 15}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 16}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 17}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 18}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 19}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 20}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 1}), (p:Permission {id: 21}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 6}), (p:Permission {id: 5}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 6}), (p:Permission {id: 6}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 6}), (p:Permission {id: 9}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 6}), (p:Permission {id: 15}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 6}), (p:Permission {id: 16}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 6}), (p:Permission {id: 17}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 7}), (p:Permission {id: 4}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 7}), (p:Permission {id: 5}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 7}), (p:Permission {id: 9}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 7}), (p:Permission {id: 15}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 7}), (p:Permission {id: 16}) MERGE (r)-[:HAS_PERMISSION]->(p);
MATCH (r:Role {id: 7}), (p:Permission {id: 17}) MERGE (r)-[:HAS_PERMISSION]->(p);

// 角色菜单关系（第二段）
MATCH (r:Role {id: 1}), (m:Menu {id: 1}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 3}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 4}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 5}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 6}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 7}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 10}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 11}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 12}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 13}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 14}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 15}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 16}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 17}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 18}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 1}), (m:Menu {id: 19}) MERGE (r)-[:CAN_ACCESS]->(m);
MATCH (r:Role {id: 6}), (m:Menu {id: 10}) MERGE (r)-[:CAN_ACCESS]->(m);

// 班级与专业关系（修复第二段的语法错误）
MATCH (c:Class {id: 1}), (m:Major {id: 1}) MERGE (c)-[:BELONGS_TO_MAJOR]->(m);
MATCH (c:Class {id: 2}), (m:Major {id: 1}) MERGE (c)-[:BELONGS_TO_MAJOR]->(m);
MATCH (c:Class {id: 3}), (m:Major {id: 1}) MERGE (c)-[:BELONGS_TO_MAJOR]->(m);
MATCH (c:Class {id: 4}), (m:Major {id: 2}) MERGE (c)-[:BELONGS_TO_MAJOR]->(m);
MATCH (c:Class {id: 5}), (m:Major {id: 2}) MERGE (c)-[:BELONGS_TO_MAJOR]->(m);
MATCH (c:Class {id: 6}), (m:Major {id: 2}) MERGE (c)-[:BELONGS_TO_MAJOR]->(m);
MATCH (c:Class {id: 7}), (m:Major {id: 3}) MERGE (c)-[:BELONGS_TO_MAJOR]->(m);
MATCH (c:Class {id: 8}), (m:Major {id: 3}) MERGE (c)-[:BELONGS_TO_MAJOR]->(m);
MATCH (c:Class {id: 9}), (m:Major {id: 4}) MERGE (c)-[:BELONGS_TO_MAJOR]->(m);
MATCH (c:Class {id: 10}), (m:Major {id: 4}) MERGE (c)-[:BELONGS_TO_MAJOR]->(m);

// 用户与班级关系（第二段）
MATCH (u:User {id: 2}), (c:Class {id: 1}) MERGE (u)-[:ENROLLED_IN]->(c);
MATCH (u:User {id: 8}), (c:Class {id: 2}) MERGE (u)-[:ENROLLED_IN]->(c);

// 课程与教师关系（第二段）
MATCH (c:Course {id: 1}), (u:User {id: 6}) MERGE (c)-[:TAUGHT_BY]->(u);
MATCH (c:Course {id: 2}), (u:User {id: 6}) MERGE (c)-[:TAUGHT_BY]->(u);
MATCH (c:Course {id: 3}), (u:User {id: 6}) MERGE (c)-[:TAUGHT_BY]->(u);
MATCH (c:Course {id: 4}), (u:User {id: 6}) MERGE (c)-[:TAUGHT_BY]->(u);
MATCH (c:Course {id: 5}), (u:User {id: 6}) MERGE (c)-[:TAUGHT_BY]->(u);
MATCH (c:Course {id: 6}), (u:User {id: 6}) MERGE (c)-[:TAUGHT_BY]->(u);
MATCH (c:Course {id: 7}), (u:User {id: 1}) MERGE (c)-[:TAUGHT_BY]->(u);

// ========== 11. 创建错题记录（来自第一段演示） ==========
MATCH (s:Student {id: 2})
MATCH (k1:KnowledgePoint {id: 56})
MATCH (k2:KnowledgePoint {id: 59})
MERGE (s)-[:WRONG_ON {times: 3, last_wrong: timestamp()}]->(k1)
MERGE (s)-[:WRONG_ON {times: 2, last_wrong: timestamp()}]->(k2);

// ========== 12. 验证查询（可选） ==========
// 验证数据是否插入成功
MATCH (s:Student {id: 2})-[r:WRONG_ON]->(k)
RETURN s.name, k.name, r.times;

// ==================================================================================
// 学生 ID 2 综合考试案例数据 (模拟数据)
// ==================================================================================
// 本脚本模拟了学生 2 (User ID 2) 的一次“Java与数据结构期中考试”。
// 包含 10 道题目，混合了正确和错误的回答，用于展示 AI 分析能力。
// 注意：本脚本假设数据库中已经存在相应的 Question 和 User 节点。

// ----------------------------------------------------------------------------------
// 1. 创建考试记录 (StudentExam)
// ----------------------------------------------------------------------------------

// 关联到现有的试卷 (假设试卷 ID 为 8888，如果不存在则创建占位符)
MERGE (p:Paper {id: 8888})
ON CREATE SET p.name = "Java与数据结构期中考试", p.total_score = 100

// 关联到现有的考试定义
MERGE (e:Exam {id: 8888})
ON CREATE SET e.title = "2025秋季期中考试"
MERGE (e)-[:USES_PAPER]->(p)

// 创建学生 2 的考试记录
CREATE (se_case:StudentExam {
    id: 888802,
    score: 60, // 总分 60/100 (6题对, 4题错)
    status: 'COMPLETED',
    createTime: datetime('2025-11-27T14:30:00')
})

// 将学生 2 关联到此考试
WITH se_case, e
MATCH (u:User {id: 2})
CREATE (u)-[:TAKES]->(se_case)
CREATE (se_case)-[:FOR_EXAM]->(e)


// ----------------------------------------------------------------------------------
// 2. 创建学生答题记录 (包含正确和错误的情况)
// ----------------------------------------------------------------------------------

// Q1: Java 基础语法 (错误)
// 学生选了 'B' (错误), 正确答案是 'A'
// 考察点: Java基础
WITH se_case
MATCH (q1:Question {id: 1})
CREATE (a1:StudentAnswer {id: "SA_888802_1", score: 0, studentContent: "B", isCorrect: false})
CREATE (se_case)-[:HAS_ANSWER]->(a1)
CREATE (a1)-[:FOR_QUESTION]->(q1)

// Q2: 数据结构复杂度 (正确)
// 考察点: 数据结构
WITH se_case
MATCH (q2:Question {id: 2})
CREATE (a2:StudentAnswer {id: "SA_888802_2", score: 10, studentContent: "A,C", isCorrect: true})
CREATE (se_case)-[:HAS_ANSWER]->(a2)
CREATE (a2)-[:FOR_QUESTION]->(q2)

// Q3: 数据库事务 (正确)
// 考察点: 数据库
WITH se_case
MATCH (q3:Question {id: 3})
CREATE (a3:StudentAnswer {id: "SA_888802_3", score: 10, studentContent: "True", isCorrect: true})
CREATE (se_case)-[:HAS_ANSWER]->(a3)
CREATE (a3)-[:FOR_QUESTION]->(q3)

// Q10: Java 继承 (正确)
// 考察点: 面向对象
WITH se_case
MATCH (q10:Question {id: 10})
CREATE (a10:StudentAnswer {id: "SA_888802_10", score: 10, studentContent: "extends", isCorrect: true})
CREATE (se_case)-[:HAS_ANSWER]->(a10)
CREATE (a10)-[:FOR_QUESTION]->(q10)

// Q11: Java 数组 (错误)
// 学生写了 "Array<int>" (语法错误), 正确是 "int[]"
// 考察点: Java基础, 语法
WITH se_case
MATCH (q11:Question {id: 11})
CREATE (a11:StudentAnswer {id: "SA_888802_11", score: 0, studentContent: "Array<int>", isCorrect: false})
CREATE (se_case)-[:HAS_ANSWER]->(a11)
CREATE (a11)-[:FOR_QUESTION]->(q11)

// Q12: Java 接口 (正确)
// 考察点: 面向对象
WITH se_case
MATCH (q12:Question {id: 12})
CREATE (a12:StudentAnswer {id: "SA_888802_12", score: 10, studentContent: "implements", isCorrect: true})
CREATE (se_case)-[:HAS_ANSWER]->(a12)
CREATE (a12)-[:FOR_QUESTION]->(q12)

// Q47: FIFO 数据结构 (错误)
// 学生选了 'Stack' (栈), 正确是 'Queue' (队列)
// 考察点: 数据结构, 栈与队列
WITH se_case
MATCH (q47:Question {id: 47})
CREATE (a47:StudentAnswer {id: "SA_888802_47", score: 0, studentContent: "Stack", isCorrect: false})
CREATE (se_case)-[:HAS_ANSWER]->(a47)
CREATE (a47)-[:FOR_QUESTION]->(q47)

// Q48: LIFO 数据结构 (正确)
// 考察点: 数据结构, 栈
WITH se_case
MATCH (q48:Question {id: 48})
CREATE (a48:StudentAnswer {id: "SA_888802_48", score: 10, studentContent: "Stack", isCorrect: true})
CREATE (se_case)-[:HAS_ANSWER]->(a48)
CREATE (a48)-[:FOR_QUESTION]->(q48)

// Q51: 链表特性 (正确)
// 考察点: 数据结构, 链表
WITH se_case
MATCH (q51:Question {id: 51})
CREATE (a51:StudentAnswer {id: "SA_888802_51", score: 10, studentContent: "Dynamic Size", isCorrect: true})
CREATE (se_case)-[:HAS_ANSWER]->(a51)
CREATE (a51)-[:FOR_QUESTION]->(q51)

// Q55: 哈希表查找 (错误)
// 学生选了 'O(n)' (错误), 正确是 'O(1)'
// 考察点: 数据结构, 算法复杂度
WITH se_case
MATCH (q55:Question {id: 55})
CREATE (a55:StudentAnswer {id: "SA_888802_55", score: 0, studentContent: "O(n)", isCorrect: false})
CREATE (se_case)-[:HAS_ANSWER]->(a55)
CREATE (a55)-[:FOR_QUESTION]->(q55)

// ==================================================================================
// AI 分析摘要 (预期结果)
// ==================================================================================
// 总分: 60/100
// 薄弱点:
// 1. Java 基础语法 (Q1, Q11): 数组声明语法混淆
// 2. 数据结构 (Q47, Q55): 混淆栈和队列，不清楚哈希表的时间复杂度
//
// 强项:
// 1. Java 面向对象 (Q10, Q12): 熟悉继承和接口关键字
// 2. 基础概念 (Q2, Q3, Q48, Q51): 理解 LIFO 和链表特性
// ==================================================================================