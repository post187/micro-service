package com.example.Service;

import com.example.model.dto.Request.EmailDetails;

public interface EmailService {
    String sendMail(EmailDetails emailDetails);
}
