package com.chat.toktalk.repository;

import com.chat.toktalk.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {
    @Query("select f from UploadFile f where f.fileName = :fileName")
    Stream<UploadFile> findUploadFileByFileName(@Param(value="fileName") String fileName);
}
