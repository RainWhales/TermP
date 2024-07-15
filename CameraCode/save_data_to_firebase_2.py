import cv2
import mediapipe as mp
import csv
import firebase_admin
from firebase_admin import credentials, firestore
from datetime import datetime, timedelta
import math
import numpy as np

# Initialize Firebase
cred = credentials.Certificate('C:\Users\Jung\Desktop\TermP-git\New_TermP\\app\google-services.json')
firebase_admin.initialize_app(cred)
db = firestore.client()

ref = db.collection('devices').document('signal')

# CSV file path
csv_filename = 'Camera.csv'

# Mediapipe Face Mesh setup
mp_face_mesh = mp.solutions.face_mesh
face_mesh = mp_face_mesh.FaceMesh()

cap = cv2.VideoCapture(0)
start_time = datetime.now()
elapsed_time = timedelta(seconds=0)

def record_video():
    global elapsed_time, start_time
    while cap.isOpened():
        ret, frame = cap.read()
        if not ret:
            print("Failed to capture video.")
            break

        frame = cv2.flip(frame, 1)
        rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        results = face_mesh.process(rgb_frame)

        if results.multi_face_landmarks:
            for face_landmarks in results.multi_face_landmarks:
                landmark_indices = [0, 61, 146, 91, 181, 84, 17, 314, 405, 321, 375, 291, 37, 39, 40, 267, 269, 270]
                landmarks = [face_landmarks.landmark[i] for i in landmark_indices]
                h, w, _ = frame.shape
                x_1 = int(np.mean([landmark.x * w for landmark in landmarks]))
                y_1 = int(np.mean([landmark.y * h for landmark in landmarks])) - 5

                cv2.circle(frame, (x_1, y_1), 2, (0, 255, 0), -1)

                rgb_value = rgb_frame[y_1, x_1]
                r, g, b = rgb_value[0], rgb_value[1], rgb_value[2]

                face_width = math.sqrt(
                    (face_landmarks.landmark[172].x - face_landmarks.landmark[264].x) ** 2 +
                    (face_landmarks.landmark[172].y - face_landmarks.landmark[264].y) ** 2
                )
                face_width = 20 * face_width

                upper_lip_bottom = (face_landmarks.landmark[12].x, face_landmarks.landmark[12].y)
                lower_lip_top = (face_landmarks.landmark[14].x, face_landmarks.landmark[14].y)
                mouth_openness = (lower_lip_top[1] - upper_lip_bottom[1]) / face_width
                mouth_openness = round(mouth_openness, 4)

                lip_left = (face_landmarks.landmark[61].x, face_landmarks.landmark[61].y)
                lip_right = (face_landmarks.landmark[291].x, face_landmarks.landmark[291].y)
                mouth_width = (lip_right[0] - lip_left[0]) / face_width
                mouth_width = round(mouth_width, 4)

                if elapsed_time.total_seconds() >= 0.2:
                    # Upload data to Firestore
                    doc_ref = ref.collection('signal_data').document()  # Automatically generate document ID
                    doc_ref.set({
                        'R': int(r),
                        'G': int(g),
                        'B': int(b),
                        'Openness': float(mouth_openness),
                        'Width': float(mouth_width),
                        'Timestamp': datetime.now()
                    })

                    # Write data to CSV (optional)
                    with open(csv_filename, mode='a', newline='') as file:
                        writer = csv.writer(file)
                        writer.writerow([r, g, b, mouth_openness, mouth_width, datetime.now()])

                    elapsed_time = timedelta(seconds=0)
                else:
                    elapsed_time += datetime.now() - start_time
                    start_time = datetime.now()

                cv2.putText(frame, f"Mouth Openness: {mouth_openness:.4f}", (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
                cv2.putText(frame, f"Mouth Width: {mouth_width:.4f}", (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 255), 2)
                cv2.putText(frame, f"RGB: ({r:},{g:},{b:})", (10, 90), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)

def save_video_data():
    cap.release()
    cv2.destroyAllWindows()

def listener(event):
    if event.data == 'start_process':
        record_video()
    elif event.data == 'stop_process':
        save_video_data()

ref.listen(listener)
print("Listening for Firebase signals...")
