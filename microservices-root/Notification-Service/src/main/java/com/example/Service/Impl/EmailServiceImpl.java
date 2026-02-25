package com.example.Service.Impl;

import com.example.Model.Dto.EmailDetail;
import com.example.Service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public Mono<String> sendSimpleMail(EmailDetail details) {
        return Mono.fromCallable(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(details.getRecipient());
            message.setText(details.getMsgBody());
            message.setSubject(details.getSubject());

            javaMailSender.send(message);
            return "Mail send successfully.";
        }).onErrorResume(ex -> {
            log.error("Error while sending mail", ex);
            return Mono.just("Error while Sending Mail");
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<String> sendMailWithAttachment(EmailDetail details) {
        return Mono.fromCallable(() -> {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(details.getSubject());

            FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));
            mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);

            javaMailSender.send(message);
            return "Mail Sent Successfully";
        })
                .onErrorResume(ex -> {
                    log.error("Error while sending mail", ex);
                    return Mono.just("Error while Sending Mail");
                }).subscribeOn(Schedulers.boundedElastic());
    }



    @Override
    public Mono<String> sendMail(MultipartFile[] files, String to, String[] cc, String subject, String body) {
        return Mono.fromCallable(() -> {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setCc(cc);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body);

            for (MultipartFile file : files) {
                mimeMessageHelper.addAttachment(
                        Objects.requireNonNull(file.getOriginalFilename()),
                        new ByteArrayResource(file.getBytes())
                );
            }
            return "Mail Send Successfully";
        })
                .onErrorResume(ex -> {
                    log.error("Error while sending mail", ex);
                    return Mono.just("Error while Sending Mail");
                })
                .subscribeOn(Schedulers.boundedElastic());
    }


}
