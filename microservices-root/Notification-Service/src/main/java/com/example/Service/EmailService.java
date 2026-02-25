package com.example.Service;

import com.example.Model.Dto.EmailDetail;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface EmailService {
    Mono<String> sendSimpleMail(EmailDetail details);
    Mono<String> sendMailWithAttachment(EmailDetail details);
    Mono<String> sendMail(MultipartFile[] file, String to, String[] cc, String subject, String body);
}
