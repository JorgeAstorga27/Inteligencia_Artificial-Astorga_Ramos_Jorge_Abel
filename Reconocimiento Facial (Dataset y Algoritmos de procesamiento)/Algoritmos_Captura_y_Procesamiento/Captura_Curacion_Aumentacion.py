import cv2
import os
import random
from PIL import Image, ImageEnhance

# Configuración de la ruta de trabajo (ajustar según el entorno)
FOLDER_BASE = r"C:\Users\jesu1\OneDrive\Documentos\Tecnológico de Culiacán\Semestre 8\Inteligencia Artificial\Repositorio IA\Segundo Parcial\Reconocimiento Facial\Dataset\train\A2_JesusVelazquez"
os.makedirs(FOLDER_BASE, exist_ok=True)

def generar_dataset_completo(objetivo_base=100):
    face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
    cap = cv2.VideoCapture(0)

    if not cap.isOpened():
        print("Error: Cámara no detectada.")
        return

    # Lectura de último índice
    archivos_existentes = [f for f in os.listdir(FOLDER_BASE) if f.endswith('.jpg')]
    ultimo_indice = 0
    
    for f in archivos_existentes:
        try:
            # Intentamos extraer el número antes del primer '_' (formato 0001_01.jpg)
            numero_archivo = int(f.split('_')[0])
            if numero_archivo > ultimo_indice:
                ultimo_indice = numero_archivo
        except ValueError:
            continue
    
    siguiente_id = ultimo_indice + 1
    print(f"Iniciando secuencia desde el índice: {siguiente_id:04d}")

    # Fase 1 y 2: Captura de Rostros Base (100 imágenes en RAM) y recorte a 160x160
    rostros_en_memoria = []
    print(f"Fase 1 & 2: Capturando {objetivo_base} rostros base en RAM...")

    while len(rostros_en_memoria) < objetivo_base:
        ret, frame = cap.read()
        if not ret: break

        # Guía Visual (Cuadro Blanco)
        height, width, _ = frame.shape
        # Definimos el tamaño del cuadro de guía (p.ej. 250x250)
        size = 250
        x1 = (width - size) // 2
        y1 = (height - size) // 2
        x2 = x1 + size
        y2 = y1 + size

        # Dibujar el cuadro blanco de referencia (BGR: 255, 255, 255)
        cv2.rectangle(frame, (x1, y1), (x2, y2), (255, 255, 255), 2)
        cv2.putText(frame, "Posiciona tu rostro aqui", (x1, y1 - 10), 
                    cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 2)

        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        rects = face_cascade.detectMultiScale(gray, 1.3, 5)

        for (x, y, w, h) in rects:
            # Recorte y redimensión a 160x160 
            recorte = frame[y:y+h, x:x+w]
            rostro_160 = cv2.resize(recorte, (160, 160), interpolation=cv2.INTER_AREA)
            rostros_en_memoria.append(rostro_160)
            # Feedback visual del rostro detectado (en azul)
            cv2.rectangle(frame, (x, y), (x+w, y+h), (255, 0, 0), 2)
            # Contador de progreso en pantalla
            cv2.putText(frame, f"Capturas: {len(rostros_en_memoria)}/{objetivo_base}", (10, 30),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)
            break

        cv2.imshow('Captura de Rostros (Punto 2)', frame)
        if cv2.waitKey(1) & 0xFF == ord('q'): break

    cap.release()
    cv2.destroyAllWindows()

    # Fase 3: Aumentación de Datos (Generar 400 archivos) 
    if len(rostros_en_memoria) > 0:
        print("\nFase 3: Generando 400 imágenes con Espejo, Brillo y Rotación...")

        for i, rostro_cv in enumerate(rostros_en_memoria):
            rostro_pil = Image.fromarray(cv2.cvtColor(rostro_cv, cv2.COLOR_BGR2RGB))
            
            # Calculamos el ID actual sumando el contador al ID inicial detectado
            id_actual = siguiente_id + i
            # El formato será XXXX_YY (donde YY es la variante 01 a 04)
            prefix = f"{id_actual:04d}"

            # Rostro Original (Variante 01)
            rostro_pil.save(os.path.join(FOLDER_BASE, f"{prefix}_01.jpg"))

            # Espejo + Brillo Alto (Variante 02)
            espejo_brillante = rostro_pil.transpose(Image.FLIP_LEFT_RIGHT)
            espejo_brillante = ImageEnhance.Brightness(espejo_brillante).enhance(1.2)
            espejo_brillante.save(os.path.join(FOLDER_BASE, f"{prefix}_02.jpg"))

            # Brillo Bajo (Variante 03)
            brillo_bajo = ImageEnhance.Brightness(rostro_pil).enhance(0.5)
            brillo_bajo.save(os.path.join(FOLDER_BASE, f"{prefix}_03.jpg"))

            # Rotación Aleatoria (Variante 04)
            angulo = random.randint(-20, 20)
            rotada = rostro_pil.rotate(angulo, resample=Image.BICUBIC, expand=False)
            rotada.save(os.path.join(FOLDER_BASE, f"{prefix}_04.jpg"))

        print(f"\n¡Éxito! 400 imágenes listas en: {FOLDER_BASE}")

if __name__ == "__main__":
    generar_dataset_completo(100)