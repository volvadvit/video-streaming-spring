package com.volvadvit.videotubeapi.repository;

import com.volvadvit.videotubeapi.model.VideoMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoMetadataRepository extends JpaRepository<VideoMetadata, Long> {
}
