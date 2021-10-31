package com.volvadvit.videotubeapi.controller;

import com.volvadvit.videotubeapi.model.dto.NewVideoRepresentation;
import com.volvadvit.videotubeapi.model.dto.VideoMetadataRepresentation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping("/api/v1/video")
@Slf4j
public class VideoController {

    @GetMapping("/all")
    private List<VideoMetadataRepresentation> findAllVideoMetadata() {
        return null;
    }

    @GetMapping("/{id}")
    public VideoMetadataRepresentation findVideoMetadataById(@PathVariable("id") Long id) {
        return null;
    }

    @GetMapping(value = "/preview/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<StreamingResponseBody> getPreviewPicture(@PathVariable("id") Long id) {
        return null;
    }

    @GetMapping("/stream/{id}")
    public ResponseEntity<StreamingResponseBody> streamVideo(
            @RequestHeader(value = "Range", required = false) String httpRangeHeader,
            @PathVariable("id") Long id
    ) {
        return null;
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadVideo(NewVideoRepresentation newVideoRepresentation) {
        return null;
    }
}
