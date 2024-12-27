package com.shopify.shopifywebhook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sizes {

    private float length = 10; // Largo del producto a despachar
    private float height = 10; // Alto del producto a despachar
    private float width = 10; // Ancho del producto a despachar
    private float weight = 1; // Peso del producto a despachar
}
