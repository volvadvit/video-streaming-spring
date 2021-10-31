package com.volvadvit.videotubeapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "video_metadata")
@Data
@NoArgsConstructor
public class VideoMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String contentType;
    private String description;
    private Long fileSize;
    private Long videoLength;
}
