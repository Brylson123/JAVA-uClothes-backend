package com.uClothes.uClothes.service;

import com.uClothes.uClothes.domain.ClothesOffer;
import com.uClothes.uClothes.domain.Order;
import com.uClothes.uClothes.dto.OrderRequestDTO;
import com.uClothes.uClothes.dto.UserOrdersDTO;
import com.uClothes.uClothes.repositories.ClothesOfferRepository;
import com.uClothes.uClothes.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final ClothesOfferRepository clothesOfferRepository;
    private final EmailService emailService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    public OrderService(ClothesOfferRepository clothesOfferRepository,
                        EmailService emailService, PaymentService paymentService, OrderRepository orderRepository) {
        this.clothesOfferRepository = clothesOfferRepository;
        this.emailService = emailService;
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
    }

    public Map<String, String> createPaymentSession(OrderRequestDTO orderRequestDTO, String successUrl, String cancelUrl) throws Exception {

        ClothesOffer product = clothesOfferRepository.findById(orderRequestDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + orderRequestDTO.getProductId()));

        String productImageUrl = "https://storage.googleapis.com/uclothes/" + product.getImageName();
        Map<String, String> metadata = extractOrderMetadata(orderRequestDTO);

        String sessionUrl = paymentService.createCheckoutSession(
                product.getName(),
                (long) (product.getPrice() * 100),
                "PLN",
                successUrl,
                cancelUrl,
                orderRequestDTO.getCustomerEmail(),
                productImageUrl,
                product.getId(),
                metadata
        );

        return Map.of("checkoutUrl", sessionUrl);
    }

    private static Map<String, String> extractOrderMetadata(OrderRequestDTO orderRequestDTO) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("customerFirstName", orderRequestDTO.getCustomerFirstName());
        metadata.put("customerLastName", orderRequestDTO.getCustomerLastName());
        metadata.put("addressStreet", orderRequestDTO.getAddressStreet());
        metadata.put("addressCity", orderRequestDTO.getAddressCity());
        metadata.put("addressZipCode", orderRequestDTO.getAddressZipCode());
        metadata.put("addressParcelLockerNumber", orderRequestDTO.getAddressParcelLockerNumber());
        metadata.put("customerPhoneNumber", orderRequestDTO.getCustomerPhoneNumber());
        return metadata;
    }

    public void sendEmailsForOrder(Order order, ClothesOffer product) {
        emailService.sendHtmlEmail(
                order.getUser().getEmail(),
                "Potwierdzenie zamówienia " + order.getId() + " - uClothes",
                order,
                product
        );

        String adminMessage = String.format(
                "Nowe zamówienie zostało przetworzone:\n\n" +
                        "Produkt: %s\n" +
                        "Cena: %.2f zł\n" +
                        "Imię i nazwisko klienta: %s %s\n" +
                        "Numer telefonu klienta: %s\n" +
                        "Email klienta: %s\n" +
                        "Miasto: %s\n" +
                        "Adres paczkomatu: %s",
                product.getName(),
                order.getTotalPrice(),
                order.getCustomerFirstName(),
                order.getCustomerLastName(),
                order.getCustomerPhoneNumber(),
                order.getUser().getEmail(),
                order.getAddressCity(),
                order.getAddressParcelLockerNumber()
        );

        emailService.sendEmail(
                "uclothes254@gmail.com",
                "Nowe zamówienie " + order.getId() + " - uClothes",
                adminMessage
        );
    }

    public List<UserOrdersDTO> getOrdersByUserId(UUID userId) {
        List<Order> orders = orderRepository.findByUser_Id(userId);
        return orders.stream()
                .map(order -> new UserOrdersDTO(order.getId(), order.getProduct().getName(), order.getTotalPrice(), order.getCustomerFirstName(), order.getCustomerLastName()))
                .collect(Collectors.toList());
    }


}
