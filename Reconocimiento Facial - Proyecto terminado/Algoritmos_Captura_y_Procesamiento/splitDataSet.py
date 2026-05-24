# Esta es una clase para separar las imágenes de entrenamiento en train y validación (80/20)
import os
import shutil
import random

# Ruta actual
ruta_train = r'C:\Users\jorge\Documents\IA\Inteligencia_Artificial-Astorga_Ramos_Jorge_Abel\Reconocimiento Facial - Proyecto terminado\data\train'

# Nueva ruta
ruta_val = r'C:\Users\jorge\Documents\IA\Inteligencia_Artificial-Astorga_Ramos_Jorge_Abel\Reconocimiento Facial - Proyecto terminado\data\val'

# Porcentaje que se irá a validación (20%)
porcentaje_val = 0.2

print("Iniciando separación de imágenes...")

# Crear la carpeta 'val' principal si no existe
if not os.path.exists(ruta_val):
    os.makedirs(ruta_val)

# Iterar sobre cada subcarpeta
for clase in os.listdir(ruta_train):
    ruta_clase_train = os.path.join(ruta_train, clase)
    # Asegurarnos de que estamos leyendo una carpeta 
    if os.path.isdir(ruta_clase_train):

        # Crear la subcarpeta correspondiente en 'val'
        ruta_clase_val = os.path.join(ruta_val, clase)
        if not os.path.exists(ruta_clase_val):
            os.makedirs(ruta_clase_val)

        # Obtener todas las fotos que hay actualmente de esta persona
        imagenes = os.listdir(ruta_clase_train)
        
        # Calcular exactamente cuántas son el 20%
        cantidad_a_mover = int(len(imagenes) * porcentaje_val)

        # Seleccionar imágenes al azar para asegurar variedad
        imagenes_a_mover = random.sample(imagenes, cantidad_a_mover)

        # Cortar y pegar las imágenes de train a val
        for img in imagenes_a_mover:
            origen = os.path.join(ruta_clase_train, img)
            destino = os.path.join(ruta_clase_val, img)
            shutil.move(origen, destino)

        print(f"✔️ Clase '{clase}': {cantidad_a_mover} fotos movidas a validación.")

print("\nFotos separadas en 80/20.")