package com.sportsinventory.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class FileResponse {
    private Long id;
    private String originalFileName;
    private String contentType;
    private Long sizeBytes;
    private LocalDateTime uploadedAt;
    private String downloadUrl;
}
