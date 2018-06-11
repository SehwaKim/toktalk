package com.chat.toktalk.service.impl;

import com.chat.toktalk.domain.UploadFile;
import com.chat.toktalk.repository.ChannelRepository;
import com.chat.toktalk.repository.UploadFileRepository;
import com.chat.toktalk.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class UploadFileServiceImpl implements UploadFileService {

    @Autowired
    UploadFileRepository uploadFileRepository;

    @Autowired
    ChannelRepository channelRepository;

    private final Path rootLocation = Paths.get("filestorage");

    @Override
    public Stream<UploadFile> getUploadFileByFileName(String fileName) {
        return uploadFileRepository.findUploadFileByFileName(fileName);
    }

    @Override
    public void addUploadFile(UploadFile file) {
        uploadFileRepository.save(file);
    }

}
