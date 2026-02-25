package com.example.Api;

import com.example.Model.Dto.PaymentDto;
import com.example.Model.Entity.Payment;
import com.example.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Mono<Payment> savePayment(@RequestBody PaymentDto paymentDto) {
        return paymentService.savePayment(paymentDto);
    }

    @GetMapping("/{paymentId}")
    public Mono<Payment> getPayment(@PathVariable String paymentId) {
        return paymentService.getPayment(paymentId);
    }

    @GetMapping
    public Mono<List<Payment>> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @DeleteMapping("/{paymentId}")
    public Mono<Void> deletePayment(@PathVariable String paymentId) {
        return paymentService.deletePayment(paymentId);
    }

}