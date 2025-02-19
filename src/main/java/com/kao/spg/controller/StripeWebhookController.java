package com.kao.spg.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kao.spg.service.EmailService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.LineItem;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionListLineItemsParams;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final EmailService emailService; // You'll create this service.

    public StripeWebhookController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

                if (dataObjectDeserializer.getObject().isPresent()) {
                    Session session = (Session) dataObjectDeserializer.getObject().get();

                    // Retrieve email, amount, and line items from the session
                    String customerEmail = session.getCustomerDetails().getEmail();
                    double totalAmount = session.getAmountTotal() / 100.0;  // Convert cents to dollars

                    // Retrieve line items for product details
                    List<LineItem> lineItems = session.listLineItems().getData();

                    // Assuming single item purchase for now
                    if (!lineItems.isEmpty()) {
                        LineItem item = lineItems.get(0);
                        String itemName = item.getDescription();
                        long quantity = item.getQuantity();

                        // Generate a random voucher code
                        String voucherCode = generateVoucherCode();

                        // Send receipt email with live data
                        emailService.sendVoucherEmail(customerEmail, voucherCode, itemName, quantity, totalAmount);
                    }
                }

            }
            return ResponseEntity.ok("Success");
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error parsing event");
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateVoucherCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}



