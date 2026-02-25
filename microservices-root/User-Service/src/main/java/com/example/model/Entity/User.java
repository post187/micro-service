package com.example.model.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "unique_username", columnNames = "userName"),
        @UniqueConstraint(name =  "unique_email", columnNames = "email"),
        @UniqueConstraint(name = "unique_phone", columnNames = "phone")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    Long id;

    @NotBlank(message = "Full name must not be blank")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    @Column(name = "fullName")
    String fullName;

    @NotBlank(message = "User name must not be blank")
    @Size(min = 3, max = 100, message = "User name must be between 3 and 100 characters")
    @Column(name = "userName")
    String userName;

    @NaturalId
    @NotBlank
    @Size(max = 50)
    @Email(message = "Inputs must be email format")
    @Column(name = "email")
    String email;

    @JsonIgnore
    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Column(name = "password")
    String password;

    @NotBlank(message = "Gender must be not blank")
    @Column(name = "gender")
    String gender;

    @Pattern(regexp = "^\\+84[0-9]{9,10}$|^0[0-9]{9,10}$", message = "The phone number is not in the correct format")
    @Size(min = 10, max = 10, message = "Phone number have length 10 digits")
    @Column(name = "phone")
    String phone;

    @Pattern(regexp = "^(http|https)://.*$", message = "Avatar URL must be a valid HTTP or HTTPS URL")
    @Lob
    @Column(name = "imageUrl")
    String avatar;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();


}
