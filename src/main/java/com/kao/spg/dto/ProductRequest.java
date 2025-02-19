package com.kao.spg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {


    private float amount;

    private long quantity;

    private String name;

    private String currency;

    private String cusEmail;

    public long getUnitAmount() {
        return (long) (amount * 1000);
    }
}
