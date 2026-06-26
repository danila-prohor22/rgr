package com.sportsinventory.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder  //для формирования ответа клиенту разработан класс FileResponse, не раскрывающий внутренний путь хранения файла 
public class FileResponse { //на сервере и  содержащий ссылку для скачивания
    private Long id;
    private String originalFileName;
    private String contentType;
    private Long sizeBytes;
    private LocalDateTime uploadedAt;
    private String downloadUrl;
}
