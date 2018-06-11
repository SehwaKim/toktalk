package com.chat.toktalk.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "upload_file")
@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class UploadFile implements Serializable {
    public UploadFile() {
        this.regdate = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @Column(name = "content_type")
    @JsonProperty("contentType")
    private String contentType;

    @JsonProperty("length")
    private Long length;

    @Column(name = "file_name")
    @JsonProperty("fileName")
    private String fileName;

    @Column(name = "save_file_name")
    @JsonProperty("saveFileName")
    private String saveFileName;

    @JsonProperty("regdate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm")
    private LocalDateTime regdate;

    @OneToOne(fetch = FetchType.LAZY)
    private Message message;
}
