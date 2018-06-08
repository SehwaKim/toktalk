package com.chat.toktalk.service;

import com.chat.toktalk.domain.UploadFile;
import org.apache.commons.fileupload.FileUpload;

public interface UploadFileService {
    public UploadFile getUploadFileByFileName(String fileName);

    public void addUploadFile(UploadFile file);
}
