package com.volvadvit.videotubeapi.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class NewVideoRepresentation {
    private String description;
    private MultipartFile file;
}
