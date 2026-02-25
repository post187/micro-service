package com.example.Model.Entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    private String content;
    private String recipientId;
    private boolean read;
    @Field(name = "timestamp")
    private LocalDateTime timestamp;
    private String notificationType;
    private String link;

    public Notification() {
        this.timestamp = LocalDateTime.now();
    }
}
