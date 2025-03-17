package com.uClothes.uClothes.domain;


import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;


    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String currentTokenId;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    public User(String password, UserRole role, String currentTokenId, String email) {
        this.password = password;
        this.role = role;
        this.currentTokenId = currentTokenId;
        this.email = email;
    }
}
