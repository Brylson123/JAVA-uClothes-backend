package com.uClothes.uClothes.service;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.uClothes.uClothes.domain.ClothesOffer;
import com.uClothes.uClothes.domain.Order;
import com.uClothes.uClothes.domain.User;
import com.uClothes.uClothes.repositories.ClothesOfferRepository;
import com.uClothes.uClothes.repositories.OrderRepository;
import com.uClothes.uClothes.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class WebhookService {

    private final OrderRepository orderRepository;
    private final ClothesOfferRepository clothesOfferRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;

    public WebhookService(OrderRepository orderRepository, ClothesOfferRepository clothesOfferRepository,
                          OrderService orderService, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.clothesOfferRepository = clothesOfferRepository;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    public void handleStripeEvent(Event event) {
        if (event.getType().equals("checkout.session.completed")) {
            handleCheckoutSessionCompleted(event);
        } else {
            System.out.println("Unhandled event type: " + event.getType());
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null) {
            System.out.println("Session is null in checkout.session.completed event.");
            return;
        }

        if (orderRepository.existsBySessionId(session.getId())) {
            System.out.println("Order already exists for session ID: " + session.getId());
            return;
        }

        String productId = session.getClientReferenceId();
        String customerEmail = session.getCustomerDetails().getEmail();

        Map<String, String> metadata = session.getMetadata();
        String customerFirstName = metadata.get("customerFirstName");
        String customerLastName = metadata.get("customerLastName");
        String addressStreet = metadata.get("addressStreet");
        String addressCity = metadata.get("addressCity");
        String addressZipCode = metadata.get("addressZipCode");
        String parcelLocker = metadata.get("addressParcelLockerNumber");
        String customerPhoneNumber = metadata.get("customerPhoneNumber");

        Optional<ClothesOffer> optionalProduct = clothesOfferRepository.findById(UUID.fromString(productId));
        if (optionalProduct.isEmpty()) {
            System.out.println("Product not found for ID: " + productId);
            return;
        }
        Optional<User> optionalUser = userRepository.findOptionalByEmail(customerEmail);
        if (optionalUser.isEmpty()) {
            System.out.println("User not found for email: " + customerEmail);
            return;
        }
        User user = optionalUser.get();
        ClothesOffer product = optionalProduct.get();

        product.setActive(false);
        clothesOfferRepository.save(product);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setSessionId(session.getId());
        order.setProduct(product);
        order.setUser(user);
        order.setCustomerFirstName(customerFirstName);
        order.setCustomerLastName(customerLastName);
        order.setAddressStreet(addressStreet);
        order.setCustomerPhoneNumber(customerPhoneNumber);
        order.setAddressCity(addressCity);
        order.setAddressZipCode(addressZipCode);
        order.setAddressParcelLockerNumber(parcelLocker);
        order.setTotalPrice(product.getPrice());
        orderRepository.save(order);

        orderService.sendEmailsForOrder(order, product);
    }
}

