package com.example.Service;

import com.example.Model.Dto.PaymentDto;
import com.example.Model.Entity.Payment;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PaymentService {
    Mono<Payment> savePayment(PaymentDto paymentDto);
    Mono<Payment> getPayment(String paymentId);
    Mono<List<Payment>> getAllPayments();
    Mono<Void> deletePayment(String paymentId);
}
