package com.shopify.shopifywebhook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShipitOrder {

    private int kind;
    private int platform;
    private String reference;
    private Destiny destiny;
    private Sizes sizes;
    private int items;
    private boolean sandbox;
    private Seller seller;
    private Courier courier;
    private Insurance insurance;
    private List<ShipitProduct> products;
    private Origin origin;

}