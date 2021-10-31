package com.volvadvit.videotubeapi.service;

import com.volvadvit.videotubeapi.model.VideoMetadata;
import com.volvadvit.videotubeapi.model.dto.VideoMetadataRepresentation;
import com.volvadvit.videotubeapi.repository.VideoMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoService {

    @Value("${data.folder}") private String dataFolder;
    private final VideoMetadataRepository videoRepo;
    private final FrameGrabberService frameGrabberService;

    public List<VideoMetadataRepresentation> findAll() {
        return videoRepo.findAll()
                .stream()
                .map(VideoMetadataRepresentation::new)
                .collect(Collectors.toList());
    }

    public Optional<VideoMetadataRepresentation> findById(Long id) {
        return videoRepo.findById(id)
                .map(VideoMetadataRepresentation::new);
    }
}
