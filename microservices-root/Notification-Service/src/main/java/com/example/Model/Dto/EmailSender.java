package com.example.Model.Dto;

import org.springframework.web.multipart.MultipartFile;

public class EmailSender {
    private MultipartFile [] files;
    private String to;
    private String cc;
    private String subject;
    private String body;
}
