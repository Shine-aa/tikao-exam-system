import cv2
import face_recognition
import numpy as np


def start_face_recognition():
    print("正在加载基准照片，提取特征向量...")
    # 1. 加载考生的基准照片（从文件系统中读取）
    # 1. 加载考生的基准照片（使用 OpenCV 安全加载并强制转为 RGB）
    img_bgr = cv2.imread("Zhou.jpg")
    if img_bgr is None:
        print("错误：无法读取 face.jpg，请检查图片路径或名称是否正确！")
        return
    # OpenCV 默认读取的是 BGR 格式，我们需要转换为人脸识别库要求的 RGB 格式
    reference_image = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2RGB)

    # 获取基准照片中的人脸特征编码 (128维向量)
    # [0] 表示取第一张脸，如果照片里有多人会报错，所以基准照必须是单人
    try:
        reference_encoding = face_recognition.face_encodings(reference_image)[0]
    except IndexError:
        print("错误：在基准照片中没有检测到人脸，请更换照片！")
        return

    print("特征提取成功！正在启动摄像头...")
    # 2. 打开电脑默认摄像头 (0代表默认摄像头)
    video_capture = cv2.VideoCapture(0)

    while True:
        # 3. 逐帧读取摄像头画面
        ret, frame = video_capture.read()
        if not ret:
            print("无法获取摄像头画面")
            break

        # 为了加快处理速度，将画面缩小到 1/4
        small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)
        # OpenCV 使用 BGR 颜色，而 face_recognition 需要 RGB 颜色，进行转换
        rgb_small_frame = np.ascontiguousarray(small_frame[:, :, ::-1])

        # 4. 在当前帧中寻找所有的人脸位置和特征编码
        face_locations = face_recognition.face_locations(rgb_small_frame)
        face_encodings = face_recognition.face_encodings(rgb_small_frame, face_locations)

        # 5. 遍历画面中检测到的每一张脸
        for (top, right, bottom, left), face_encoding in zip(face_locations, face_encodings):
            # 放大坐标，因为我们刚才把画面缩小了 1/4
            top *= 4
            right *= 4
            bottom *= 4
            left *= 4

            # 默认标记为“未知作弊者”
            name = "Unknown / Warning"
            color = (0, 0, 255)  # 红色警告框 (BGR格式)

            # 将当前帧中的人脸与基准人脸进行比对 (默认容错率 tolerance=0.6，越小越严格)
            matches = face_recognition.compare_faces([reference_encoding], face_encoding, tolerance=0.5)

            # 如果匹配成功，说明是考生本人
            if True in matches:
                name = "Verified Student"
                color = (0, 255, 0)  # 绿色通过框

            # 6. 使用 OpenCV 在画面上画框并标注名字
            cv2.rectangle(frame, (left, top), (right, bottom), color, 2)
            cv2.rectangle(frame, (left, bottom - 35), (right, bottom), color, cv2.FILLED)
            font = cv2.FONT_HERSHEY_DUPLEX
            cv2.putText(frame, name, (left + 6, bottom - 6), font, 0.6, (255, 255, 255), 1)

        # 7. 在屏幕上显示处理后的画面
        cv2.imshow('TaiKao Face Recognition Test', frame)

        # 监听键盘，按下 'q' 键退出程序
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    # 释放摄像头资源并关闭窗口
    video_capture.release()
    cv2.destroyAllWindows()


if __name__ == '__main__':
    start_face_recognition()