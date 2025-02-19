package com.kao.spg.controller;

import com.kao.spg.dto.ProductRequest;
import com.kao.spg.dto.StripeResponse;
import com.kao.spg.service.StripeService;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stripe")
public class ProductCheckoutController {
    @PostMapping("/payment")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody ProductRequest productRequest) throws StripeException {
        StripeResponse stripeResponse = StripeService.checkoutProducts(productRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }
}
