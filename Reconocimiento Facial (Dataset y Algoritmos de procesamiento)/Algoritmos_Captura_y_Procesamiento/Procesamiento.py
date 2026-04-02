import cv2
import os
import numpy as np
from mtcnn import MTCNN

def procesar_y_limpiar_total(carpeta, size=(160, 160)):
    # Inicializar MTCNN
    try:
        detector = MTCNN()
    except Exception as e:
        print(f"Error al inicializar MTCNN: {e}")
        return

    formatos_validos = ('.jpg', '.jpeg', '.png', '.webp')
    archivos = [f for f in os.listdir(carpeta) if f.lower().endswith(formatos_validos)]

    print(f"Iniciando limpieza TOTAL en: {carpeta}")
    print(f"Archivos a procesar: {len(archivos)}")

    procesadas = 0
    eliminadas_por_error = 0

    for i, nombre_archivo in enumerate(archivos):
        ruta_archivo = os.path.join(carpeta, nombre_archivo)

        try:
            # Leemos el archivo como bytes y luego lo decodificamos con OpenCV
            stream = open(ruta_archivo, "rb")
            bytes_img = bytearray(stream.read())
            numpyarray = np.asarray(bytes_img, dtype=np.uint8)
            img = cv2.imdecode(numpyarray, cv2.IMREAD_COLOR)
            stream.close()

            if img is None:
                print(f"Archivo corrupto o ilegible: {nombre_archivo}")
                os.remove(ruta_archivo)
                continue

            # Convertir para MTCNN
            img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

            # Detectar Rostros
            resultados = detector.detect_faces(img_rgb)

            if resultados:
                res = resultados[0]
                x, y, w, h = res['box']
                x, y = max(0, x), max(0, y)

                # Recorte y Redimensión
                rostro_recortado = img[y:y+h, x:x+w]

                if rostro_recortado.size > 0:
                    rostro_final = cv2.resize(rostro_recortado, size, interpolation=cv2.INTER_LANCZOS4)

                    # Borramos la original antes de guardar el recorte
                    os.remove(ruta_archivo)
                    nuevo_nombre = nombre_archivo  # Mantenemos el mismo nombre pero con el nuevo formato
                    ruta_guardado = os.path.join(carpeta, nuevo_nombre)
                    
                    # Codificamos y guardamos usando numpy.tofile
                    extension = os.path.splitext(nuevo_nombre)[1]
                    is_success, buffer = cv2.imencode(extension, rostro_final)
                    if is_success:
                        with open(ruta_guardado, "wb") as f:
                            f.write(buffer)
                        procesadas += 1
                else:
                    os.remove(ruta_archivo)
                    eliminadas_por_error += 1
            else:
                print(f"Sin rostro en {nombre_archivo}. Eliminando...")
                os.remove(ruta_archivo)
                eliminadas_por_error += 1

        except Exception as e:
            print(f"Error procesando {nombre_archivo}: {e}")
            if os.path.exists(ruta_archivo):
                os.remove(ruta_archivo)
            eliminadas_por_error += 1

        if (i + 1) % 5 == 0:
            print(f"Progreso: {i + 1}/{len(archivos)} archivos analizados...")

    print(f"\n" + "="*40)
    print(f"LIMPIEZA TOTAL FINALIZADA")
    print(f"Rostros extraídos con éxito: {procesadas}")
    print(f"Archivos eliminados (sin rostro/error): {eliminadas_por_error}")
    print("="*40)

# Configuración de la ruta de trabajo (ajustar según el entorno)
RUTA_TRABAJO = r"C:\Users\jesu1\OneDrive\Documentos\Tecnológico de Culiacán\Semestre 8\Inteligencia Artificial\Repositorio IA\Segundo Parcial\Reconocimiento Facial\Dataset\train\F8_AlonzoBodden"

if __name__ == "__main__":
    procesar_y_limpiar_total(RUTA_TRABAJO)