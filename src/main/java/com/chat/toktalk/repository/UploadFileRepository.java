package com.chat.toktalk.repository;

import com.chat.toktalk.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {
    UploadFile findUploadFileByFileName(String fileName);
}
