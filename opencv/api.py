from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import cv2
import numpy as np
import face_recognition
import base64
import requests
from io import BytesIO
from PIL import Image

# 初始化 FastAPI 应用
app = FastAPI(title="钛考人脸识别 AI 服务")

print("正在初始化 AI 服务...")

# 定义请求数据格式
class FaceVerifyRequest(BaseModel):
    target_base64: str      # 摄像头抓拍的实时照片 (Base64)
    reference_url: str      # 考生的基准照片 URL (OSS 路径)

def download_image(url):
    """从 URL 下载图片并转换为 RGB 格式的 numpy 数组"""
    try:
        response = requests.get(url, timeout=10)
        response.raise_for_status()
        img = Image.open(BytesIO(response.content))
        # 转换为 RGB 格式
        img = img.convert('RGB')
        return np.array(img)
    except Exception as e:
        print(f"下载基准照片失败: {e}")
        return None

def base64_to_image(base64_str):
    """将 Base64 字符串转换为 RGB 格式的 numpy 数组"""
    try:
        if "," in base64_str:
            base64_str = base64_str.split(",")[1]
        img_data = base64.b64decode(base64_str)
        nparr = np.frombuffer(img_data, np.uint8)
        frame = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        # OpenCV 默认 BGR，转为 RGB
        return cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    except Exception as e:
        print(f"解析 Base64 图片失败: {e}")
        return None

@app.post("/verify")
async def verify_face(request: FaceVerifyRequest):
    try:
        # 1. 解析实时抓拍照片
        target_image = base64_to_image(request.target_base64)
        if target_image is None:
            return {"success": False, "message": "实时照片解析失败"}

        # 2. 下载基准照片
        reference_image = download_image(request.reference_url)
        if reference_image is None:
            return {"success": False, "message": "无法获取考生的基准照片"}

        # 3. 提取特征编码
        target_encodings = face_recognition.face_encodings(target_image)
        reference_encodings = face_recognition.face_encodings(reference_image)

        if not target_encodings:
            return {"success": False, "message": "实时照片中未检测到人脸，请正对摄像头"}
        if not reference_encodings:
            return {"success": False, "message": "基准照片中未检测到有效人脸，请联系管理员重录"}

        # 4. 比对人脸 (取第一张检测到的人脸)
        # tolerance 越小越严格，0.45-0.5 比较适合考试场景
        matches = face_recognition.compare_faces([reference_encodings[0]], target_encodings[0], tolerance=0.45)
        
        # 计算距离（可选，用于展示相似度）
        face_distances = face_recognition.face_distance([reference_encodings[0]], target_encodings[0])
        similarity = 1 - face_distances[0]

        if matches[0]:
            return {
                "success": True, 
                "message": "人脸验证通过", 
                "similarity": float(similarity)
            }
        else:
            return {
                "success": False, 
                "message": "人脸不匹配，请确保是考生本人", 
                "similarity": float(similarity)
            }

    except Exception as e:
        print(f"验证过程发生错误: {e}")
        return {"success": False, "message": f"系统繁忙: {str(e)}"}

if __name__ == "__main__":
    import uvicorn
    # 启动服务，监听 8001 端口
    uvicorn.run(app, host="0.0.0.0", port=8001)
