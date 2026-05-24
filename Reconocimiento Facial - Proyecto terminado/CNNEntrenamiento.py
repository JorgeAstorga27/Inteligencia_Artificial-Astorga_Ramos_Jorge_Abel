# En esta clase se hacen dos cosas:
# 1. CONGELAR (Estabilización): Bloqueamos el modelo pre-entrenado (MobileNetV2) 
# para proteger su conocimiento visual general, mientras nuestras capas nuevas 
# aprenden a clasificar desde cero sin "arruinar" la base.

# 2. FINE-TUNING (Ajuste Fino): Descongelamos las capas finales del modelo base 
# y las re-entrenamos muy lentamente (con un learning rate bajo) para que 
# se especialicen en los detalles únicos de nuestros rostros.

import tensorflow as tf
from tensorflow.keras import layers, models
from tensorflow.keras.preprocessing.image import ImageDataGenerator

# Configuración inicial para el entrenamiento de la CNN.
# Tamaño de las imágenes.
IMG_SIZE = (160, 160)
# Cantidad de imágenes por lote.
BATCH_SIZE = 32

# Rutas de los conjuntos de entrenamiento y validación.
TRAIN_DIR = r'C:\Users\jorge\Documents\IA\Inteligencia_Artificial-Astorga_Ramos_Jorge_Abel\Reconocimiento Facial - Proyecto terminado\data\train'
VAL_DIR = r'C:\Users\jorge\Documents\IA\Inteligencia_Artificial-Astorga_Ramos_Jorge_Abel\Reconocimiento Facial - Proyecto terminado\data\val'

# Carga y Normalización de las imágenes con aumentos de datos para mejorar la generalización.
# zoom, shift y brillo para simular condiciones reales de webcam.
train_datagen = ImageDataGenerator(
    rescale=1./255, 
    rotation_range=30, 
    horizontal_flip=True,
    width_shift_range=0.2,
    height_shift_range=0.2,    
    zoom_range=0.2,
    brightness_range=[0.8, 1.2]
)
val_datagen = ImageDataGenerator(rescale=1./255)

# Tomamos las imágenes directamente de las carpetas, aplicando los aumentos solo al conjunto de entrenamiento.
train_gen = train_datagen.flow_from_directory(
    TRAIN_DIR, 
    target_size=IMG_SIZE, 
    batch_size=BATCH_SIZE, 
    class_mode='sparse',
    color_mode='rgb' 
)
# Para validación, no aplicamos aumentos, solo normalización.
val_gen = val_datagen.flow_from_directory(
    VAL_DIR, 
    target_size=IMG_SIZE, 
    batch_size=BATCH_SIZE, 
    class_mode='sparse',
    color_mode='rgb'
)

# Cargar modelo base pre-entrenado con ImageNet, quitando la última capa para aplicar Transfer Learning a nuestras imágenes.
base_model = tf.keras.applications.MobileNetV2(
    input_shape=(160, 160, 3), 
    include_top=False, 
    weights='imagenet'
)

# Primera Fase: Congelamos para evitar que se actualicen los pesos durante la primera fase.
print("\nFase 1: Entrenamiento del clasificador con base congelada.")
base_model.trainable = False 

model = models.Sequential([
    base_model,
    layers.GlobalAveragePooling2D(),
    layers.Dense(128, activation='relu'), # Capa extra para más capacidad.
    layers.Dropout(0.5), # Dropout para evitar sobreajuste.
    layers.Dense(len(train_gen.class_indices), activation='softmax')
])

# Utilizamos adam para optimizar y sparse_categorical_crossentropy para clasificación multiclase con etiquetas enteras.
model.compile(
    optimizer='adam', 
    loss='sparse_categorical_crossentropy', 
    metrics=['accuracy']
)

# Entrenamos solo el clasificador durante pocas épocas para estabilizarlo.
model.fit(train_gen, validation_data=val_gen, epochs=10) # 10 épocas son suficientes aquí.

# Segunda Fase: Fine-Tuning Profundo para mejorar la precisión al permitir que algunas capas del modelo base se actualicen.
# Fine-tuning significa descongelar algunas de las capas superiores del modelo pre-entrenado para que puedan adaptarse mejor
# a nuestro conjunto de datos específico, lo que puede mejorar la precisión final del modelo.
print("\nFase 2: Fine-Tuning profundo para mejorar la precisión.")

# Descongelamos la base completa para poder elegir cuáles capas descongelar.
base_model.trainable = True

# Determinamos desde qué capa vamos a descongelar (las últimas capas son menos genéricas).
# Para MobileNetV2, descongelar desde la capa 100 en adelante es una buena técnica.
fine_tune_at = 100 
print(f"Número de capas en el modelo base: {len(base_model.layers)}")
print(f"Descongelando desde la capa {fine_tune_at} en adelante.")

# Congelamos las primeras 100 capas y descongelamos el resto
for layer in base_model.layers[:fine_tune_at]:
    layer.trainable = False

# Volvemos a compilar el modelo con un learning rate muy bajo para no "destruir" las características pre-entrenadas.
# Es crucial que sea muy baja (1e-5 o 1e-4) para no "destruir" las características pre-entrenadas.
model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=0.00001), # Learning Rate muy bajo.
    loss='sparse_categorical_crossentropy', 
    metrics=['accuracy']
)

# Continuamos el entrenamiento profundizando en las capas descongeladas.
total_epochs = 35 # Sumaremos 25 épocas más a las 10 iniciales.
history_fine = model.fit(
    train_gen, 
    validation_data=val_gen, 
    epochs=total_epochs,
    initial_epoch=10 # Indica dónde empezar de la fase 1.
)

# Guardamos el modelo definitivo. Este modelo ya ha pasado por ambas fases de entrenamiento,
# incluyendo el fine-tuning profundo, lo que debería resultar en una mejor precisión para el reconocimiento facial.
model.save('modelo_facial_profundo.h5')

# Imprimimos las clases identificadas para confirmar que el modelo reconoce correctamente las categorías de personas.
class_names = list(train_gen.class_indices.keys())
print("Entrenamiento finalizado. Modelo guardado como 'modelo_facial_profundo.h5'. Clases identificadas:", class_names)