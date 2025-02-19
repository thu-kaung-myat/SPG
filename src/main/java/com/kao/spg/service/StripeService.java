package com.kao.spg.service;

import com.kao.spg.dto.ProductRequest;
import com.kao.spg.dto.StripeResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;


@Service
public class StripeService {

    public static StripeResponse checkoutProducts(ProductRequest productRequest) throws StripeException {
        //ProductData
        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(productRequest.getName()).build();
        //PriceData
        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(productRequest.getCurrency()==null?"USD":productRequest.getCurrency())
                .setUnitAmount(productRequest.getUnitAmount())
                .setProductData(productData).build();
        //LineItem
        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(productRequest.getQuantity())
                .setPriceData(priceData).build();
        //SessionParameters
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success")
                .setCancelUrl("http://localhost:8080/cancel")
                .setCustomerEmail(productRequest.getCusEmail())
                .addLineItem(lineItem).build();
        //Session
        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException ex) {
            System.out.println("Stripe Exception: " + ex.getMessage());
            throw ex; // Re-throw exception for better debugging
        }

        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment Session Created!!!!!")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }
}
