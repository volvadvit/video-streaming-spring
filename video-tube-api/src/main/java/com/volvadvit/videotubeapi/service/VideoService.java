package com.volvadvit.videotubeapi.service;

import com.volvadvit.videotubeapi.model.VideoMetadata;
import com.volvadvit.videotubeapi.model.dto.NewVideoRepresentation;
import com.volvadvit.videotubeapi.model.dto.VideoMetadataRepresentation;
import com.volvadvit.videotubeapi.repository.VideoMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.volvadvit.videotubeapi.utils.Utils.getFileNameWithoutExt;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoService {

    @Value("${data.folder}")
    private String dataFolder;

    private final VideoMetadataRepository videoRepo;
    private final FrameGrabberService frameGrabberService;

    public List<VideoMetadataRepresentation> findAllRepresentation() {
        return videoRepo.findAll()
                .stream()
                .map(VideoMetadataRepresentation::new)
                .collect(Collectors.toList());
    }

    public Optional<VideoMetadataRepresentation> findRepresentationById(Long id) {
        return videoRepo.findById(id)
                .map(VideoMetadataRepresentation::new);
    }

    @Transactional
    public void saveNewVideo(NewVideoRepresentation newVideoRepr) {
        VideoMetadata metadata = new VideoMetadata();

        metadata.setFileName(newVideoRepr.getFile().getOriginalFilename());
        metadata.setContentType(newVideoRepr.getFile().getContentType());
        metadata.setFileSize(newVideoRepr.getFile().getSize());
        metadata.setDescription(newVideoRepr.getDescription());

        videoRepo.save(metadata);

        Path directory = Path.of(dataFolder, metadata.getId().toString());
        try {
            Files.createDirectory(directory);
            Path file = Path.of(directory.toString(), newVideoRepr.getFile().getOriginalFilename());
            try (OutputStream output = Files.newOutputStream(file, CREATE, WRITE)) {
                newVideoRepr.getFile().getInputStream().transferTo(output);
            }
            long videoLength = frameGrabberService.generatePreviewPictures(file);
            metadata.setVideoLength(videoLength);
            videoRepo.save(metadata);
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new IllegalStateException(ex);
        }
    }

    public Optional<InputStream> getPreviewInputStream(Long id) {
        return videoRepo.findById(id)
                .flatMap(vmd -> {
                    Path previewPicturePath = Path.of(
                            dataFolder,
                            vmd.getId().toString(),
                            getFileNameWithoutExt(vmd.getFileName()) + ".jpeg");
                    if (!Files.exists(previewPicturePath)) {
                        return Optional.empty();
                    }
                    try {
                        return Optional.of(Files.newInputStream(previewPicturePath));
                    } catch (IOException ex) {
                        log.error(ex.getMessage());
                        return Optional.empty();
                    }
                });
    }

    public Optional<StreamBytesInfo> getStreamBytes(Long id, HttpRange range) {
        Optional<VideoMetadata> vmdById = videoRepo.findById(id);
        if (vmdById.isEmpty()) {
            log.error("Video with id: {} not found", id);
            return Optional.empty();
        }

        Path filePath = Path.of(dataFolder, String.valueOf(id), vmdById.get().getFileName());
        if (!Files.exists(filePath)) {
            log.error("File {} not found", filePath);
            return Optional.empty();
        }

        try {
            long fileSize = Files.size(filePath);
            long chunkSize = fileSize / 100; // took 1/100 part of file size

            //If no range is specified
            if (range == null) {
                return Optional.of(new StreamBytesInfo(
                        out -> Files.newInputStream(filePath).transferTo(out),
                        fileSize,
                        0,
                        fileSize,
                        vmdById.get().getContentType()));
            }

            long rangeStart = range.getRangeStart(0); // Will be 0 if not specified
            long rangeEnd = rangeStart + chunkSize; // range.getRangeEnd(fileSize);

            if (rangeEnd >= fileSize) {
                rangeEnd = fileSize - 1;
            }

            final long finalRangeEnd = rangeEnd;

            return Optional.of(new StreamBytesInfo(
                    out -> {
                        try (InputStream inputStream = Files.newInputStream(filePath)) {
                            inputStream.skip(rangeStart);
                            byte[] bytes = inputStream.readNBytes((int) ((finalRangeEnd - rangeStart) + 1));
                            out.write(bytes);
                        }
                    },
                    fileSize, rangeStart, rangeEnd, vmdById.get().getContentType()));
        } catch (IOException ex) {
            log.error(ex.getMessage());
            return Optional.empty();
        }
    }
}
