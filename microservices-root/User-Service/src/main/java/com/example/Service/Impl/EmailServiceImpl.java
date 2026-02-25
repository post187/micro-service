package com.example.Service.Impl;

import com.example.Constant.AppConstant;
import com.example.Service.EmailService;
import com.example.model.dto.Request.EmailDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private WebClient.Builder webClient;

    @Override
    public String sendMail(EmailDetails emailDetails) {
        return webClient.baseUrl(AppConstant.DiscoveredDomainsApi.API_GATEWAY_HOST).build()
                .post()
                .uri("/api/email/sendMail")
                .bodyValue(emailDetails)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
