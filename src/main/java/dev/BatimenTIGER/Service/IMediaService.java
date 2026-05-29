package dev.BatimenTIGER.Service;

import org.springframework.core.io.Resource;

public interface IMediaService {

    Resource loadFileAsResource(Long mediaId);
}