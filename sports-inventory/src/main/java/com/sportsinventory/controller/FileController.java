package com.sportsinventory.controller;

import com.sportsinventory.dto.response.FileResponse;
import com.sportsinventory.entity.StoredFile;
import com.sportsinventory.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController  //для предоставления внешнего API создан REST-контроллер, обрабатывающий загрузку, скачивание и удаление файлов.
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileStorageService.upload(file));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        StoredFile metadata = fileStorageService.getMetadata(id);
        Resource resource = fileStorageService.download(id);
        String contentType = metadata.getContentType() == null
                ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                : metadata.getContentType();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, //при скачивании контроллер формирует заголовок Content-Disposition с 
                        ContentDisposition.attachment()  //исходным именем файла, чтобы браузер предложил сохранить файл под его
                                .filename(metadata.getOriginalFileName(), StandardCharsets.UTF_8)  //первоначальным названием
                                .build()
                                .toString())
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fileStorageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
