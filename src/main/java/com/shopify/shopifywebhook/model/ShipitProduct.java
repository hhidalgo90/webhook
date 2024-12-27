package com.shopify.shopifywebhook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipitProduct {
    private int sku_id;
    private int amount;
    private int warehouse_id;
    /**
     * Detalle del SKU, EJ CAM-01
     */
    private String description;
}
