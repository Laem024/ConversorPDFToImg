# PDF to Image Converter API

Este proyecto proporciona una API REST desarrollada con **Spring Boot** que permite convertir archivos PDF a imágenes en formato **JPG** o **PNG**. Los usuarios pueden cargar varios archivos PDF y recibir un archivo comprimido (ZIP) que contiene las imágenes correspondientes.

## Descripción

La API permite:
- **Convertir PDFs a imágenes** (JPG o PNG).
- Soporte para múltiples archivos PDF en una sola solicitud.
- Generación de un archivo ZIP con las imágenes convertidas.

## Endpoints

### 1. **Convertir PDFs a imágenes**

- **Método**: `POST`
- **Ruta**: `/multi/toimg/{format}`
- **Parámetros**:
    - `{format}`: El formato de la imagen de salida. Puede ser `jpg` o `png`.
    - `files`: Un arreglo de archivos PDF a convertir.

#### Ejemplo de solicitud:

```bash
curl -X POST "http://localhost:8080/multi/toimg/jpg" \
     -H "Content-Type: multipart/form-data" \
     -F "files=@/ruta/a/tu/archivo1.pdf" \
     -F "files=@/ruta/a/tu/archivo2.pdf" \
     --output salida.zip
