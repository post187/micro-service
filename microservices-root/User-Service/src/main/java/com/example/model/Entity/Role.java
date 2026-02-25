package com.example.model.Entity;

import jakarta.annotation.security.DenyAll;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NaturalId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "roles")
@ToString
@With
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    Long id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(name  = "roleName", length = 60)
    RoleName roleName;
}
