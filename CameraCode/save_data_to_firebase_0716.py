import cv2
import mediapipe as mp
import pandas as pd
import numpy as np
import math
import csv
from sklearn.preprocessing import StandardScaler
import tensorflow as tf
import firebase_admin
from firebase_admin import credentials, db

# Firebase 초기화
cred = credentials.Certificate(r'C:\Users\Jung\Desktop\TermP-git\CameraCode\firebasepy.json')
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://anproject-846d0-default-rtdb.firebaseio.com/'
})

# 모델 로드
loaded_model = tf.keras.models.load_model('keras_model.h5')

# CSV 파일에 헤더를 추가
csv_filename = 'realtime_data.csv'
with open(csv_filename, mode='w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(['R', 'G', 'B', 'Openness', 'Width'])  # Label은 추후 예측 시 추가할 수 있음

# Mediapipe의 Face Mesh 모델 로드
mp_face_mesh = mp.solutions.face_mesh
face_mesh = mp_face_mesh.FaceMesh()

# 웹캠에서 영상을 받아오는 VideoCapture 객체 생성
cap = cv2.VideoCapture(0)

# Firebase 경로 설정
signal_ref = db.reference('devices/signal')
result_ref = db.reference('devices/result')

def process_frame():
    ret, frame = cap.read()
    if not ret:
        print("비디오 캡처 실패, 종료합니다.")
        return

    frame = cv2.flip(frame, 1)
    
    # 프레임을 BGR에서 RGB로 변환
    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    
    # 얼굴 검출 및 랜드마크 추출
    results = face_mesh.process(rgb_frame)

    if results.multi_face_landmarks:
        for face_landmarks in results.multi_face_landmarks:
            landmark_indices = [0, 61, 146, 91, 181, 84, 17, 314, 405, 321, 375, 291, 37, 39, 40, 267, 269, 270]
            landmarks = [face_landmarks.landmark[i] for i in landmark_indices]
            
            h, w, _ = frame.shape
            x_1 = int(np.mean([landmark.x * w for landmark in landmarks]))
            y_1 = int(np.mean([landmark.y * h for landmark in landmarks])) - 5
            
            # RGB 값 추출
            rgb_value = rgb_frame[y_1, x_1]
            r, g, b = rgb_value[0], rgb_value[1], rgb_value[2]
            
            # 얼굴 너비 계산
            face_width = math.sqrt(
                (face_landmarks.landmark[172].x - face_landmarks.landmark[264].x) ** 2 +
                (face_landmarks.landmark[172].y - face_landmarks.landmark[264].y) ** 2
            )
            face_width = 20 * face_width

            # 입의 세로길이 계산 (고중저모음 판단기준)
            upper_lip_bottom = (face_landmarks.landmark[12].x, face_landmarks.landmark[12].y)
            lower_lip_top = (face_landmarks.landmark[14].x, face_landmarks.landmark[14].y)
            mouth_openness = (lower_lip_top[1] - upper_lip_bottom[1]) / face_width
            mouth_openness = round(mouth_openness, 4)

            # 입의 가로길이 계산 (원순/평순 판단기준)
            lip_left = (face_landmarks.landmark[61].x, face_landmarks.landmark[61].y)
            lip_right = (face_landmarks.landmark[291].x, face_landmarks.landmark[291].y)
            mouth_width = (lip_right[0] - lip_left[0]) / face_width
            mouth_width = round(mouth_width, 4)

            # CSV 파일에 데이터 추가
            with open(csv_filename, mode='a', newline='') as file:
                writer = csv.writer(file)
                writer.writerow([r, g, b, mouth_openness, mouth_width])

            # 데이터 표준화 및 예측
            new_data = pd.read_csv(csv_filename)
            X = new_data[['R', 'G', 'B', 'Openness', 'Width']].values

            scaler = StandardScaler()
            scaled_data = scaler.fit_transform(X)

            # 모델을 사용하여 예측 수행
            predictions = loaded_model.predict(scaled_data)

            # 예측 결과 출력 및 Firebase에 업로드
            for i, prediction in enumerate(predictions):
                predicted_class = np.argmax(prediction)  # 가장 높은 값(확률)을 가지는 클래스 선택
                print(f"새로운 데이터 {i+1}의 예측 클래스: {predicted_class}")
                result_ref.set(predicted_class)  # 결과값을 Firebase에 업로드

    # 영상 출력
    cv2.imshow('Face Mesh', frame)
    
    # 종료 키 (q) 입력 시 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        return False
    return True

# Firebase에서 신호를 감시
def listener(event):
    signal = event.data
    if signal == "start_process":
        print("녹음 시작 신호 수신")
        while process_frame():
            pass
    elif signal == "stop_process":
        print("녹음 중지 신호 수신")
        cap.release()
        cv2.destroyAllWindows()

signal_ref.listen(listener)
