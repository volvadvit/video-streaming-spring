package com.volvadvit.videotubeapi.controller;

import com.volvadvit.videotubeapi.Exception.NotFoundException;
import com.volvadvit.videotubeapi.model.dto.NewVideoRepresentation;
import com.volvadvit.videotubeapi.model.dto.VideoMetadataRepresentation;
import com.volvadvit.videotubeapi.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1/video")
@Slf4j
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @GetMapping("/all")
    private List<VideoMetadataRepresentation> findAllVideoMetadata() {
        return videoService.findAllRepresentation();
    }

    @GetMapping("/{id}")
    public VideoMetadataRepresentation findVideoMetadataById(@PathVariable("id") Long id) {
        return videoService.findRepresentationById(id).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/preview/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<StreamingResponseBody> getPreviewPicture(@PathVariable("id") Long id) {
        InputStream inputStream = videoService.getPreviewInputStream(id)
                .orElseThrow(NotFoundException::new);

        return ResponseEntity.ok(inputStream::transferTo);
    }

    @GetMapping("/stream/{id}")
    public ResponseEntity<StreamingResponseBody> streamVideo(
            @RequestHeader(value = "Range", required = false) String httpRangeHeader,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadVideo(NewVideoRepresentation newVideoRepresentation) {
        log.info(newVideoRepresentation.getDescription());
        try {
            videoService.saveNewVideo(newVideoRepresentation);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }
}
