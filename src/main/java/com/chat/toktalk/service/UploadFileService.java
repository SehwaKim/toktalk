package com.chat.toktalk.service;

import com.chat.toktalk.domain.UploadFile;
import org.apache.commons.fileupload.FileUpload;

import java.util.stream.Stream;

public interface UploadFileService {
    public Stream<UploadFile> getUploadFileByFileName(String fileName);

    public void addUploadFile(UploadFile file);

}
