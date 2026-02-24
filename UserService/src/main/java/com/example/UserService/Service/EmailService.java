package com.example.UserService.Service;

import com.example.UserService.model.dto.Request.EmailDetails;

public interface EmailService {
    String sendMail(EmailDetails emailDetails);
}
