package com.example.Service.Impl;

import com.example.Helper.PaymentHelper;
import com.example.Model.Dto.PaymentDto;
import com.example.Model.Entity.Payment;
import com.example.Repository.PaymentRepository;
import com.example.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Override
    public Mono<Payment> savePayment(PaymentDto paymentDto) {
        return Mono.fromSupplier(() -> {
            return paymentRepository.save(PaymentHelper.map(paymentDto));
        })
                .onErrorResume(ex -> {
                    log.error("Error saving payment: {}", ex.getMessage());
                    return Mono.error(ex);
                });
    }

    @Override
    public Mono<Payment> getPayment(String paymentId) {
        return Mono.fromSupplier(() -> paymentRepository.findById(paymentId)
                .orElse(null));
    }

    @Override
    public Mono<List<Payment>> getAllPayments() {
        return Mono.fromSupplier(paymentRepository::findAll)
                .onErrorResume(throwable -> {
                    log.error("Error fetching user info: {}", throwable.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Void> deletePayment(String paymentId) {
        log.info("Void, service; delete payment by id");
        return Mono.fromRunnable(() -> paymentRepository.deleteById(paymentId));
    }
}
