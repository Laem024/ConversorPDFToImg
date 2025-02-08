package com.laem.conversor.infra.services;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PdfToImageService {
    private static final int DPI = 600; // Puedes subir a 600 o más si necesitas más calidad

    public ByteArrayOutputStream convertPdfToJpg(InputStream pdfInputStream) throws IOException {
        try(PDDocument document = Loader.loadPDF(pdfInputStream.readAllBytes())){
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 600, ImageType.RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            return outputStream;
        }
    }

    public ByteArrayOutputStream convertMultiPdfToJpg(InputStream[] pdfInputStream, String[] fileNames, String IMAGE_FORMAT) throws IOException {
        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();

        try(ZipOutputStream zipOut = new ZipOutputStream(zipOutputStream)){

            for (int i = 0; i < pdfInputStream.length; i++) {
                try(PDDocument document = Loader.loadPDF(pdfInputStream[i].readAllBytes())) {
                    PDFRenderer renderer = new PDFRenderer(document);
                    String baseName = fileNames[i].replace(".pdf", "");

                    for (int page = 0; page < document.getNumberOfPages(); page++) {
                        BufferedImage image = renderer.renderImageWithDPI(page, DPI); // ⬆ Incrementar DPI mejora calidad
                        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();

                        if (IMAGE_FORMAT.equals("jpg")) {
                            writeJpgWithQuality(image, imageStream, 0.95f); // ⬆ 95% calidad, menos compresión
                        } else {
                            ImageIO.write(image, IMAGE_FORMAT, imageStream); // ⬆ PNG para calidad máxima sin pérdida
                        }

                        // Agregar la imagen al ZIP
                        ZipEntry zipEntry = new ZipEntry(baseName + "_page" + (page + 1) + "." + IMAGE_FORMAT);
                        zipOut.putNextEntry(zipEntry);
                        zipOut.write(imageStream.toByteArray());
                        zipOut.closeEntry();
                    }
                }
            }
            return zipOutputStream;
        }
    }

    //Método para escribir imágenes JPG con mejor calidad
    private void writeJpgWithQuality(BufferedImage image, OutputStream outputStream, float quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No se encontró un escritor de imágenes JPG");
        }
        ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality); // ⬆ Controlar la calidad (0.0 = peor, 1.0 = mejor)
            }
            writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }
}
