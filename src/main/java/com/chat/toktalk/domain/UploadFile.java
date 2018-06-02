package com.chat.toktalk.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "upload_file")
@Data
public class UploadFile {
    public UploadFile() {
        this.regdate = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_type")
    private String contentType;

    private Long length;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "save_file_name")
    private String saveFileName;

    private LocalDateTime regdate;
}
