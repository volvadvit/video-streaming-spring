package com.volvadvit.videotubeapi.model.dto;

import com.volvadvit.videotubeapi.model.VideoMetadata;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VideoMetadataRepresentation {
    private Long id;
    private String description;
    private String contentType;
    private String previewUrl;
    private String streamUrl;

    public VideoMetadataRepresentation (VideoMetadata vmd) {
        this.id = vmd.getId();
        this.description = vmd.getDescription();
        this.contentType = vmd.getContentType();
        this.setPreviewUrl("/api/v1/video/preview/" + vmd.getId());
        this.setStreamUrl("/api/v1/video/stream/" + vmd.getId());
    }
}
