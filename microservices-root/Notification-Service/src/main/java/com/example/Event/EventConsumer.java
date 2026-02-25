package com.example.Event;

import com.example.Constant.KafkaConstant;
import com.example.Model.Dto.EmailDetail;
import com.example.Model.Dto.PaymentDto;
import com.example.Model.Entity.PaymentStatus;
import com.example.Service.EmailService;
import com.example.Service.PaymentService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Service;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.function.Consumer;

@Service
@Slf4j
public class EventConsumer {
    Gson gson = new Gson();

    private final EmailService emailService;
    private final PaymentService paymentService;
    private final EventProducer eventProducer;

    public EventConsumer(ReceiverOptions<String, String> receiverOptions, EmailService emailService, PaymentService paymentService, EventProducer eventProducer) {
        this.emailService = emailService;
        this.paymentService = paymentService;
        this.eventProducer = eventProducer;
        subscribeToTopic(receiverOptions, KafkaConstant.PROFILE_ONBOARDING_TOPIC, this::sendEmailKafkaOnBoarding);
        subscribeToTopic(receiverOptions, KafkaConstant.STATUS_PAYMENT_SUCCESSFUL, this::paymentOrderKafkaOnBoarding);
    }

    private void subscribeToTopic(ReceiverOptions<String, String> receiverOptions, String topic, Consumer<ReceiverRecord<String, String>> handler) {
        log.info("Subscribed to Kafka topic: {}", topic);
        KafkaReceiver.create(receiverOptions.subscription(Collections.singleton(topic)))
                .receive()
                .subscribe(handler);
    }

    public void sendEmailKafkaOnBoarding(ReceiverRecord<String, String> receiverRecord) {
        log.info("USER-SERVICE Onboarding event send email on notification service.");
        EmailDetail emailDetail = gson.fromJson(receiverRecord.value(), EmailDetail.class);


        emailService.sendSimpleMail(emailDetail)
                .flatMap(email ->
                        eventProducer.send(KafkaConstant.PROFILE_ONBOARDED_TOPIC, gson.toJson(email))
                )
                .subscribe();
    }

    public void paymentOrderKafkaOnBoarding(ReceiverRecord<String, String> receiverRecord) {
        PaymentDto paymentDto = gson.fromJson(receiverRecord.value(), PaymentDto.class);

        paymentService.savePayment(paymentDto)
                .flatMap(payment -> eventProducer.send(KafkaConstant.PROFILE_ONBOARDED_TOPIC, gson.toJson(payment)))
                .subscribe();

        EmailDetail emailDetails = EmailDetail.builder()
                .recipient("dinhdang2208@gmail.com")
                .msgBody(msgBody(paymentDto.getIsPayed(), paymentDto.getPaymentStatus()))
                .subject("Payment Successfully in Order with userId: " + paymentDto.getUserId())
                .attachment("Please, check the full information in invoice: " + LocalDateTime.now())
                .build();

        emailService.sendSimpleMail(emailDetails)
                .flatMap(email ->
                        eventProducer.send(KafkaConstant.PROFILE_ONBOARDED_TOPIC, gson.toJson(email))
                )
                .subscribe();
    }

    public String msgBody(Boolean isPayed, PaymentStatus paymentStatus) {
        return "Payment in order product cart successfully: \n " +
        " + IsPays: " + isPayed +
                "\n + PaymentStatus: " + paymentStatus.getStatus() +
                "\n\nDate: " + LocalDate.now() +
                "\nTime: " + LocalTime.now();
    }

}
