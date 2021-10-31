package com.volvadvit.videotubeapi.controller;

import com.volvadvit.videotubeapi.Exception.NotFoundException;
import com.volvadvit.videotubeapi.model.dto.NewVideoRepresentation;
import com.volvadvit.videotubeapi.model.dto.VideoMetadataRepresentation;
import com.volvadvit.videotubeapi.service.StreamBytesInfo;
import com.volvadvit.videotubeapi.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.text.DecimalFormat;
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
        log.info("Requested range [{}] for file `{}`", httpRangeHeader, id);

        List<HttpRange> httpRangeList = HttpRange.parseRanges(httpRangeHeader);

        StreamBytesInfo streamBytesInfo = videoService
                .getStreamBytes(id, httpRangeList.size() > 0 ? httpRangeList.get(0) : null)
                .orElseThrow(NotFoundException::new);

        long byteLength = streamBytesInfo.getRangeEnd() - streamBytesInfo.getRangeStart() + 1;

        ResponseEntity.BodyBuilder builder = ResponseEntity
                .status(httpRangeList.size() > 0 ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK)
                .header("Content-Type", streamBytesInfo.getContentType())
                .header("Accept-Ranges", "bytes")
                .header("Content-Length", Long.toString(byteLength));

        if (httpRangeList.size() > 0) {
            builder.header(
                    "Content-Range",
                    "bytes " + streamBytesInfo.getRangeStart() +
                            "-" + streamBytesInfo.getRangeEnd() +
                            "/" + streamBytesInfo.getFileSize());
        }
        log.info("Providing bytes from {} to {}. We are at {}% of overall video.",
                streamBytesInfo.getRangeStart(),
                streamBytesInfo.getRangeEnd(),
                new DecimalFormat("###.##")
                        .format(100.0 * streamBytesInfo.getRangeStart() / streamBytesInfo.getFileSize()));

        return builder.body(streamBytesInfo.getResponseBody());
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
