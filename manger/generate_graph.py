import re
import json
import os

# File paths
SQL_FILE = r'a:\IdeaProjects\TaiKao\manger\data.sql'
OUTPUT_FILE = r'a:\IdeaProjects\TaiKao\manger\graph.cypher'

def parse_sql_inserts(file_path):
    tables = {}
    current_table = None
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
        
    # Regex to find INSERT statements
    # INSERT INTO `table` VALUES (val1, val2, ...);
    # This is a simple parser and might need adjustment for complex SQL
    insert_pattern = re.compile(r"INSERT INTO `(\w+)` VALUES \((.*?)\);", re.DOTALL)
    
    matches = insert_pattern.findall(content)
    
    for table, values_str in matches:
        if table not in tables:
            tables[table] = []
        
        # Split values, handling quoted strings and nested parentheses if necessary
        # A simple split by comma might fail on strings containing commas
        # We'll use a custom splitter or eval if the format is standard SQL values
        
        # Quick and dirty value parser:
        # We can try to wrap it in brackets and use json.loads if we replace single quotes? 
        # No, SQL uses single quotes.
        
        values = []
        current_val = ""
        in_quote = False
        escape = False
        
        for char in values_str:
            if escape:
                current_val += char
                escape = False
                continue
            
            if char == '\\':
                escape = True
                current_val += char
                continue
            
            if char == "'" and not escape:
                in_quote = not in_quote
                current_val += char
                continue
            
            if char == ',' and not in_quote:
                values.append(clean_sql_value(current_val))
                current_val = ""
            else:
                current_val += char
        values.append(clean_sql_value(current_val))
        
        tables[table].append(values)
        
    return tables

def clean_sql_value(val):
    val = val.strip()
    if val.upper() == 'NULL':
        return None
    if val.startswith("'") and val.endswith("'"):
        return val[1:-1]
    try:
        if '.' in val:
            return float(val)
        return int(val)
    except:
        return val

def generate_cypher(tables):
    cypher = []
    cypher.append("// 1. Constraints")
    cypher.append("CREATE CONSTRAINT IF NOT EXISTS FOR (s:Student) REQUIRE s.id IS UNIQUE;")
    cypher.append("CREATE CONSTRAINT IF NOT EXISTS FOR (t:Teacher) REQUIRE t.id IS UNIQUE;")
    cypher.append("CREATE CONSTRAINT IF NOT EXISTS FOR (c:Class) REQUIRE c.id IS UNIQUE;")
    cypher.append("CREATE CONSTRAINT IF NOT EXISTS FOR (co:Course) REQUIRE co.id IS UNIQUE;")
    cypher.append("CREATE CONSTRAINT IF NOT EXISTS FOR (k:KnowledgePoint) REQUIRE k.id IS UNIQUE;")
    cypher.append("CREATE CONSTRAINT IF NOT EXISTS FOR (q:Question) REQUIRE q.id IS UNIQUE;")
    cypher.append("CREATE CONSTRAINT IF NOT EXISTS FOR (p:Paper) REQUIRE p.id IS UNIQUE;")
    cypher.append("CREATE CONSTRAINT IF NOT EXISTS FOR (e:Exam) REQUIRE e.id IS UNIQUE;")
    cypher.append("")

    # --- Nodes ---
    
    # 1. Knowledge Points
    # Schema: id, name, description, parent_id, level, ...
    cypher.append("// 2. Knowledge Points")
    kp_map = {} # id -> name
    if 'knowledge_points' in tables:
        for row in tables['knowledge_points']:
            kp_id = row[0]
            name = row[1]
            desc = row[2] or ""
            level = row[4]
            kp_map[kp_id] = name
            cypher.append(f"MERGE (k:KnowledgePoint {{id: {kp_id}}}) SET k.name = '{escape_cypher(name)}', k.description = '{escape_cypher(desc)}', k.level = {level};")

    # 2. Courses
    # Schema: id, course_code, course_name, description, teacher_id, ...
    cypher.append("\n// 3. Courses")
    if 'courses' in tables:
        for row in tables['courses']:
            c_id = row[0]
            code = row[1]
            name = row[2]
            desc = row[3] or ""
            cypher.append(f"MERGE (c:Course {{id: {c_id}}}) SET c.code = '{escape_cypher(code)}', c.name = '{escape_cypher(name)}', c.description = '{escape_cypher(desc)}';")

    # 3. Classes
    # Schema: id, class_code, class_name, grade, major_id, class_number, teacher_id, ...
    cypher.append("\n// 4. Classes")
    if 'classes' in tables:
        for row in tables['classes']:
            c_id = row[0]
            code = row[1]
            name = row[2]
            grade = row[3]
            cypher.append(f"MERGE (c:Class {{id: {c_id}}}) SET c.code = '{escape_cypher(code)}', c.name = '{escape_cypher(name)}', c.grade = '{grade}';")

    # 4. Questions
    # Schema: id, title, content, images, type, difficulty, points, options, correct_answer, ...
    cypher.append("\n// 5. Questions")
    if 'questions' in tables:
        for row in tables['questions']:
            q_id = row[0]
            content = row[2]
            q_type = row[4]
            difficulty = row[5]
            points = row[6]
            cypher.append(f"MERGE (q:Question {{id: {q_id}}}) SET q.content = '{escape_cypher(content)}', q.type = '{q_type}', q.difficulty = '{difficulty}', q.points = {points};")

    # 5. Papers
    # Schema: id, paper_code, paper_name, description, class_id, course_id, questions(json), ...
    cypher.append("\n// 6. Papers")
    paper_questions = {} # paper_id -> list of question objects
    if 'papers' in tables:
        for row in tables['papers']:
            p_id = row[0]
            code = row[1]
            name = row[2]
            desc = row[3] or ""
            questions_json = row[6]
            if questions_json:
                try:
                    paper_questions[p_id] = json.loads(questions_json)
                except:
                    pass
            cypher.append(f"MERGE (p:Paper {{id: {p_id}}}) SET p.code = '{escape_cypher(code)}', p.name = '{escape_cypher(name)}', p.description = '{escape_cypher(desc)}';")

    # 6. Exams
    # Schema: id, exam_code, exam_name, ..., paper_id, class_id, teacher_id, ...
    cypher.append("\n// 7. Exams")
    if 'exams' in tables:
        for row in tables['exams']:
            e_id = row[0]
            code = row[1]
            name = row[2]
            cypher.append(f"MERGE (e:Exam {{id: {e_id}}}) SET e.code = '{escape_cypher(code)}', e.name = '{escape_cypher(name)}';")

    # 7. Users (Inferred)
    # Students from grading_results
    cypher.append("\n// 8. Users (Inferred)")
    students = {} # id -> name
    if 'grading_results' in tables:
        for row in tables['grading_results']:
            s_id = row[2]
            s_name = row[5]
            students[s_id] = s_name
    
    for s_id, s_name in students.items():
        cypher.append(f"MERGE (s:Student {{id: {s_id}}}) SET s.name = '{escape_cypher(s_name)}';")

    # Teachers from classes, courses, papers, exams
    teacher_ids = set()
    if 'classes' in tables:
        for row in tables['classes']: teacher_ids.add(row[6])
    if 'courses' in tables:
        for row in tables['courses']: teacher_ids.add(row[4])
    if 'papers' in tables:
        for row in tables['papers']: teacher_ids.add(row[7])
    if 'exams' in tables:
        for row in tables['exams']: teacher_ids.add(row[6])
        
    for t_id in teacher_ids:
        # Check if this ID is already a student (simple heuristic: if in students map, maybe it's a student? 
        # But in this system users are likely in one 'users' table with roles. 
        # For graph clarity, we'll create Teacher nodes. If id overlaps, Neo4j handles labels separately unless we use a generic User label.
        # Let's stick to specific labels for clarity as requested.)
        cypher.append(f"MERGE (t:Teacher {{id: {t_id}}}) SET t.name = 'Teacher_{t_id}';")


    # --- Relationships ---
    cypher.append("\n// --- Relationships ---")

    # KP Hierarchy
    if 'knowledge_points' in tables:
        for row in tables['knowledge_points']:
            kp_id = row[0]
            parent_id = row[3]
            if parent_id:
                cypher.append(f"MATCH (k1:KnowledgePoint {{id: {parent_id}}}), (k2:KnowledgePoint {{id: {kp_id}}}) MERGE (k1)-[:HAS_CHILD]->(k2);")
                cypher.append(f"MATCH (k1:KnowledgePoint {{id: {parent_id}}}), (k2:KnowledgePoint {{id: {kp_id}}}) MERGE (k2)-[:PRECEDES]->(k1);") # Optional: Precedes logic? Usually parent covers child.

    # Question -> KP
    if 'question_knowledge_points' in tables:
        for row in tables['question_knowledge_points']:
            q_id = row[1]
            kp_id = row[2]
            cypher.append(f"MATCH (q:Question {{id: {q_id}}}), (k:KnowledgePoint {{id: {kp_id}}}) MERGE (q)-[:TESTS]->(k);")

    # Question -> Course
    if 'question_courses' in tables:
        for row in tables['question_courses']:
            q_id = row[1]
            c_id = row[2]
            cypher.append(f"MATCH (q:Question {{id: {q_id}}}), (c:Course {{id: {c_id}}}) MERGE (q)-[:BELONGS_TO]->(c);")

    # Class -> Course
    if 'class_courses' in tables:
        for row in tables['class_courses']:
            class_id = row[1]
            course_id = row[2]
            cypher.append(f"MATCH (c:Class {{id: {class_id}}}), (co:Course {{id: {course_id}}}) MERGE (c)-[:LEARNS]->(co);")

    # Paper -> Course
    if 'papers' in tables:
        for row in tables['papers']:
            p_id = row[0]
            c_id = row[5]
            cypher.append(f"MATCH (p:Paper {{id: {p_id}}}), (c:Course {{id: {c_id}}}) MERGE (p)-[:BELONGS_TO]->(c);")

    # Exam -> Paper, Exam -> Class
    if 'exams' in tables:
        for row in tables['exams']:
            e_id = row[0]
            p_id = row[4]
            c_id = row[5]
            cypher.append(f"MATCH (e:Exam {{id: {e_id}}}), (p:Paper {{id: {p_id}}}) MERGE (e)-[:USES_PAPER]->(p);")
            cypher.append(f"MATCH (e:Exam {{id: {e_id}}}), (c:Class {{id: {c_id}}}) MERGE (e)-[:FOR_CLASS]->(c);")

    # Paper -> Questions (from JSON)
    for p_id, questions in paper_questions.items():
        for q in questions:
            q_id = q.get('questionId')
            score = q.get('points', 0)
            if q_id:
                cypher.append(f"MATCH (p:Paper {{id: {p_id}}}), (q:Question {{id: {q_id}}}) MERGE (p)-[:CONTAINS {{score: {score}}}]->(q);")

    # Student -> Exam, Student -> Class (Inferred from exam)
    # Also Student -> Wrong KP
    
    # We need a map of Question -> List[KP_ID] to link wrong answers to KPs
    q_kp_map = {}
    if 'question_knowledge_points' in tables:
        for row in tables['question_knowledge_points']:
            q_id = row[1]
            kp_id = row[2]
            if q_id not in q_kp_map:
                q_kp_map[q_id] = []
            q_kp_map[q_id].append(kp_id)

    if 'grading_results' in tables:
        for row in tables['grading_results']:
            e_id = row[1]
            s_id = row[2]
            grading_json = row[7]
            
            cypher.append(f"MATCH (s:Student {{id: {s_id}}}), (e:Exam {{id: {e_id}}}) MERGE (s)-[:TOOK]->(e);")
            
            # Infer Class relationship: Student -> Class (via Exam)
            # Find class for this exam
            # This is O(N*M), but dataset is small.
            if 'exams' in tables:
                for ex in tables['exams']:
                    if ex[0] == e_id:
                        class_id = ex[5]
                        cypher.append(f"MATCH (s:Student {{id: {s_id}}}), (c:Class {{id: {class_id}}}) MERGE (s)-[:BELONGS_TO]->(c);")
                        break
            
            # Analyze Wrong Answers
            if grading_json:
                try:
                    grading_data = json.loads(grading_json)
                    # format: {"question_id": {"givenScore": 0.0, ...}}
                    for q_id_str, result in grading_data.items():
                        q_id = int(q_id_str)
                        given_score = result.get('givenScore', 0)
                        # Assuming if score is 0 or significantly less than max, it's wrong.
                        # Or we can just check if givenScore == 0 for simplicity.
                        if given_score == 0:
                            # Link to KPs
                            if q_id in q_kp_map:
                                for kp_id in q_kp_map[q_id]:
                                    cypher.append(f"MATCH (s:Student {{id: {s_id}}}), (k:KnowledgePoint {{id: {kp_id}}}) "
                                                  f"MERGE (s)-[r:WRONG_ON]->(k) "
                                                  f"ON CREATE SET r.times = 1, r.last_wrong = timestamp() "
                                                  f"ON MATCH SET r.times = r.times + 1, r.last_wrong = timestamp();")
                except:
                    pass

    return "\n".join(cypher)

def escape_cypher(text):
    if not text:
        return ""
    return str(text).replace("'", "\\'").replace("\n", " ").replace("\r", "")

if __name__ == "__main__":
    tables = parse_sql_inserts(SQL_FILE)
    cypher_script = generate_cypher(tables)
    
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write(cypher_script)
    
    print(f"Generated {len(cypher_script)} chars to {OUTPUT_FILE}")
