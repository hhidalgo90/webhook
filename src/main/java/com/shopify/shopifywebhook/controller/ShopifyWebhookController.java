package com.shopify.shopifywebhook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopify.shopifywebhook.model.ShopifyOrder;
import com.shopify.shopifywebhook.service.ShipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/webhook")
@Slf4j
public class ShopifyWebhookController {

    @Autowired
    public ShipmentService shipmentService;

    @PostMapping("/new-order")
    @ResponseStatus(HttpStatus.OK)
    public void handleNewOrder(@RequestBody Object shopifyOrder) {
        try {
            // Convertir el objeto genérico a JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(shopifyOrder);

            shipmentService.createShipment(jsonString);
        } catch (Exception e) {
            log.error("[ShopifyWebhookController][handleNewOrder] Error {} " , e.getMessage());
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

