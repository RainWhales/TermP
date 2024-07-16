import tensorflow as tf
from tensorflow import keras
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from imblearn.over_sampling import SMOTE
from sklearn.utils.class_weight import compute_class_weight
from sklearn.preprocessing import StandardScaler

# 데이터 로드
raw_data = pd.read_csv('702.csv')
raw_data = np.array(raw_data)

scaler = StandardScaler()
scaled_data = scaler.fit_transform(raw_data[:,:5])


train = scaled_data
target =raw_data[:, 5]


smote = SMOTE(k_neighbors=15,random_state=42)
train, target = smote.fit_resample(train,target)

class_weights = {0: 1, 1: 1, 2:1,3:1, 4:1, 5:1, 6:0.8, 7:2}

# 모델 생성
model = keras.Sequential([

    keras.layers.Dense(1024, activation='relu', input_shape=(5,)),
    keras.layers.BatchNormalization(),
    keras.layers.Dropout(0.5),
    
    keras.layers.Dense(1024, activation='relu'),
    keras.layers.BatchNormalization(),
    keras.layers.Dropout(0.5),
    
    keras.layers.Dense(512, activation='relu'),
    keras.layers.BatchNormalization(),
    keras.layers.Dropout(0.5),

    keras.layers.Dense(256, activation='relu'),
    keras.layers.BatchNormalization(),
    keras.layers.Dropout(0.5),

    keras.layers.Dense(128, activation='relu'),
    keras.layers.BatchNormalization(),
    keras.layers.Dropout(0.5),
    
    keras.layers.Dense(8, activation='softmax')  # 출력 차원
])




# Adagrad 옵티마이저 사용

optimizer = tf.keras.optimizers.Adagrad(learning_rate=0.005)

# 모델 컴파일
model.compile(optimizer='rmsprop', loss='sparse_categorical_crossentropy', metrics=['accuracy'])



# 모델 training
history = model.fit(train, target, epochs=100, validation_split=0.2,class_weight=class_weights)



# 모델 평가
test_loss, test_acc = model.evaluate(train, target)
print('테스트 정확도:', test_acc)

model.save('keras_model.h5')
