package com.shopify.shopifywebhook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopifyOrder {
    private String street;
    private String number;
    private int communeId;
    private String communeName;
    private String fullName;
    private String email;
    private String phone;
    private String complement;
    private List<ShopifyProduct> line_items;
}
