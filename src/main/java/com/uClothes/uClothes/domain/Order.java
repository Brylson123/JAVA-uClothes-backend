package com.uClothes.uClothes.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ClothesOffer product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "orders"})
    private User user;

    private String customerFirstName;
    private String customerLastName;
    private String customerPhoneNumber;

    private String addressStreet;
    private String addressCity;
    private String addressZipCode;
    private String addressParcelLockerNumber;

    @Column(unique = true, nullable = false)
    private String sessionId;

    private double totalPrice;

    public Order(UUID id, ClothesOffer product, User user, String customerFirstName,
                 String customerLastName, String customerPhoneNumber, String addressStreet,
                 String addressCity, String addressZipCode, String addressParcelLockerNumber, double totalPrice) {
        this.id = id;
        this.product = product;
        this.user = user;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.addressStreet = addressStreet;
        this.addressCity = addressCity;
        this.addressZipCode = addressZipCode;
        this.addressParcelLockerNumber = addressParcelLockerNumber;
        this.totalPrice = totalPrice;
    }
}
