# Neo4j 数据库配置

## 安装 Neo4j

下载并安装 Neo4j Desktop 或 Neo4j Community Edition：
https://neo4j.com/download/

## 创建数据库

- 数据库名称：`neo4j`
- 用户名：`neo4j`
- 密码：`test1234`（请修改为安全密码）

## 导入数据

### 方法 1：使用 Neo4j Browser（推荐）

1. 打开 Neo4j Browser：`http://localhost:7474`
2. 登录（用户名：`neo4j`，密码：您设置的密码）
3. 依次执行以下文件：
   - 复制 `TaiKao/neo4j.cypher` 的内容，粘贴到查询框并运行

### 方法 2：使用命令行

```bash
# 导入数据
cypher-shell -u neo4j -p your_password < TaiKao/neo4j.cypher
```

## 配置 Spring AI 连接

修改 `Spring AI/src/main/resources/application.yml`：

```yaml
spring:
  neo4j:
    uri: bolt://127.0.0.1:7687
    authentication:
      username: neo4j
      password: your_password  # 修改为您的密码
```

## 验证数据

在 Neo4j Browser 中执行：

```cypher
// 查看节点数量
MATCH (n) RETURN labels(n) AS label, count(n) AS count

// 查看关系数量
MATCH ()-[r]->() RETURN type(r) AS relationship, count(r) AS count

// 查看学生错题数据
MATCH (s:Student {id: 2})-[r:WRONG_ON]->(kp:KnowledgePoint)
RETURN s.name, kp.name
```

## 注意事项

- ⚠️ 不要将密码硬编码在配置文件中提交到 Git
- 建议使用环境变量：`password: ${NEO4J_PASSWORD:test1234}`
- 确保 Neo4j 中的题目 ID 与 MySQL 中的题目 ID 一致
