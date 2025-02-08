package com.laem.conversor.controller;

import com.laem.conversor.domain.formats.FormatosPermitidos;
import com.laem.conversor.infra.services.PdfToImageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/pdf")
public class PdfToJpgController {

    private final PdfToImageService pdfToImageService;

    public PdfToJpgController(PdfToImageService pdfToImageService) {
        this.pdfToImageService = pdfToImageService;
    }

    @PostMapping(value="/tojpg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> pdfToJpg(@RequestParam("file") MultipartFile pdf) {
        try{
            ByteArrayOutputStream imageStream = pdfToImageService.convertPdfToJpg(pdf.getInputStream());
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageStream.toByteArray());
        }catch (IOException e){
            return ResponseEntity.badRequest().build();
        }
    }
/*
    @PostMapping(value="/multi/tojpg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> multiPdfToJpg(@RequestParam("files") MultipartFile[] files) {
        try{
            InputStream[] pdfStreams = new InputStream[files.length];
            String[] fileNames = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                pdfStreams[i] = files[i].getInputStream();
                fileNames[i] = files[i].getOriginalFilename();
            }

            ByteArrayOutputStream zipStream = pdfToImageService.convertMultiPdfToJpg(pdfStreams, fileNames, "jpg");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted_images.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipStream.toByteArray());

        }catch (IOException e){
            return ResponseEntity.badRequest().build();
        }
    }
*/
    @PostMapping(value="/multi/toimg/{format}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> multiPdfToPng(@RequestParam("files") MultipartFile[] files, @PathVariable String format) {
        String imgFormat = String.valueOf(FormatosPermitidos.getFormato(format.trim()));
        try{
            InputStream[] pdfStreams = new InputStream[files.length];
            String[] fileNames = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                pdfStreams[i] = files[i].getInputStream();
                fileNames[i] = files[i].getOriginalFilename();
            }

            ByteArrayOutputStream zipStream = pdfToImageService.convertMultiPdfToJpg(pdfStreams, fileNames,imgFormat);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted_images.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipStream.toByteArray());

        }catch (IOException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
