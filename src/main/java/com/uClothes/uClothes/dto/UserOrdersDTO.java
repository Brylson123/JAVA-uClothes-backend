package com.uClothes.uClothes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserOrdersDTO {
    private UUID id;
    private String productName;
    private double totalPrice;
    private String customerFirstName;
    private String customerLastName;
}
