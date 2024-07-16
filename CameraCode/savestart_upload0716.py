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
import threading
import time

# Initialize Firebase
cred = credentials.Certificate('path/to/serviceAccountKey.json')
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://anproject-846d0-default-rtdb.firebaseio.com/'
})

# Load the model
loaded_model = tf.keras.models.load_model('keras_model.h5')

# Create CSV file with headers
csv_filename = 'realtime_data.csv'
with open(csv_filename, mode='w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(['R', 'G', 'B', 'Openness', 'Width'])  # Add labels later if needed

# Load Mediapipe Face Mesh model
mp_face_mesh = mp.solutions.face_mesh
face_mesh = mp_face_mesh.FaceMesh()

# Create VideoCapture object to get video from webcam
cap = cv2.VideoCapture(0)

# Set Firebase paths
signal_ref = db.reference('devices/signal')
result_ref = db.reference('devices/result')

# Shared variable to control the loop
collecting_data = False

def process_frame():
    ret, frame = cap.read()
    if not ret:
        print("Failed to capture video, exiting.")
        return

    frame = cv2.flip(frame, 1)
    
    # Convert the frame from BGR to RGB
    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    
    # Detect faces and extract landmarks
    results = face_mesh.process(rgb_frame)

    if results.multi_face_landmarks:
        for face_landmarks in results.multi_face_landmarks:
            landmark_indices = [0, 61, 146, 91, 181, 84, 17, 314, 405, 321, 375, 291, 37, 39, 40, 267, 269, 270]
            landmarks = [face_landmarks.landmark[i] for i in landmark_indices]
            
            h, w, _ = frame.shape
            x_1 = int(np.mean([landmark.x * w for landmark in landmarks]))
            y_1 = int(np.mean([landmark.y * h for landmark in landmarks])) - 5
            
            # Extract RGB values
            rgb_value = rgb_frame[y_1, x_1]
            r, g, b = rgb_value[0], rgb_value[1], rgb_value[2]
            
            # Calculate face width
            face_width = math.sqrt(
                (face_landmarks.landmark[172].x - face_landmarks.landmark[264].x) ** 2 +
                (face_landmarks.landmark[172].y - face_landmarks.landmark[264].y) ** 2
            )
            face_width = 20 * face_width

            # Calculate mouth openness (vertical distance)
            upper_lip_bottom = (face_landmarks.landmark[12].x, face_landmarks.landmark[12].y)
            lower_lip_top = (face_landmarks.landmark[14].x, face_landmarks.landmark[14].y)
            mouth_openness = (lower_lip_top[1] - upper_lip_bottom[1]) / face_width
            mouth_openness = round(mouth_openness, 4)

            # Calculate mouth width (horizontal distance)
            lip_left = (face_landmarks.landmark[61].x, face_landmarks.landmark[61].y)
            lip_right = (face_landmarks.landmark[291].x, face_landmarks.landmark[291].y)
            mouth_width = (lip_right[0] - lip_left[0]) / face_width
            mouth_width = round(mouth_width, 4)

            # Append data to CSV file
            with open(csv_filename, mode='a', newline='') as file:
                writer = csv.writer(file)
                writer.writerow([r, g, b, mouth_openness, mouth_width])

            # Standardize data and make predictions
            new_data = pd.read_csv(csv_filename)
            X = new_data[['R', 'G', 'B', 'Openness', 'Width']].values

            scaler = StandardScaler()
            scaled_data = scaler.fit_transform(X)

            # Make predictions using the model
            predictions = loaded_model.predict(scaled_data)

            # Upload prediction results to Firebase
            for i, prediction in enumerate(predictions):
                predicted_class = np.argmax(prediction)  # Choose class with highest probability
                print(f"Predicted class for new data {i+1}: {predicted_class}")
                result_ref.set(predicted_class)  # Set prediction result in Firebase

    # Display video
    cv2.imshow('Face Mesh', frame)
    
    # Exit if 'q' key is pressed
    if cv2.waitKey(1) & 0xFF == ord('q'):
        return False
    return True

# Function to run the frame processing in a loop
def run_processing():
    global collecting_data
    while collecting_data:
        if not process_frame():
            break
        time.sleep(0.1)  # Adding a slight delay to avoid overloading the CPU

# Firebase signal listener
def listener(event):
    global collecting_data
    signal = event.data
    if signal == "start_process":
        print("Received start signal")
        if not collecting_data:
            collecting_data = True
            threading.Thread(target=run_processing).start()
    elif signal == "stop_process":
        print("Received stop signal")
        collecting_data = False
        cap.release()
        cv2.destroyAllWindows()

signal_ref.listen(listener)
