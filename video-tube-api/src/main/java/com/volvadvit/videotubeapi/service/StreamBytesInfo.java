package com.volvadvit.videotubeapi.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Getter
@AllArgsConstructor
public class StreamBytesInfo {

    private final StreamingResponseBody responseBody;
    private final long fileSize;
    private final long rangeStart;
    private final long rangeEnd;
    private final String contentType;
}
