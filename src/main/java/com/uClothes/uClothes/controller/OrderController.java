package com.uClothes.uClothes.controller;

import com.uClothes.uClothes.dto.OrderRequestDTO;
import com.uClothes.uClothes.dto.UserOrdersDTO;
import com.uClothes.uClothes.service.OrderService;
import com.uClothes.uClothes.service.JwtUtilService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Value("${WEB_URL}")
    private String webUrl;

    private final OrderService orderService;
    private final JwtUtilService jwtUtilService;

    public OrderController(OrderService orderService, JwtUtilService jwtUtilService) {
        this.orderService = orderService;
        this.jwtUtilService = jwtUtilService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> createOrderWithPayment(@RequestBody OrderRequestDTO orderRequestDTO) {
        try {
            String successUrl = webUrl + "checkout/success";
            String cancelUrl = webUrl + "checkout/cancel";

            Map<String, String> response = orderService.createPaymentSession(orderRequestDTO, successUrl, cancelUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to process payment"));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getOrdersByUser(HttpServletRequest request) {
        String token = jwtUtilService.extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Brak autoryzacji"));
        }

        UUID userId = jwtUtilService.extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Nieprawidłowy token"));
        }

        List<UserOrdersDTO> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(Map.of("success", true, "orders", orders));
    }

}
