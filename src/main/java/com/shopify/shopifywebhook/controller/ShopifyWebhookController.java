package com.shopify.shopifywebhook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopify.shopifywebhook.service.OrderProcessor;
import com.shopify.shopifywebhook.service.ShipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@Slf4j
public class ShopifyWebhookController {

    @Autowired
    public ShipmentService shipmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderProcessor orderProcessor;

    @PostMapping("/new-order")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> handleNewOrder(@RequestBody Object shopifyOrder) {
        String jsonString = null;
        Long orderId = null;
        try {
            jsonString = objectMapper.writeValueAsString(shopifyOrder);
            JsonNode rootNode = objectMapper.readTree(jsonString);
            orderId = rootNode.path("id").asLong();

            if(orderId <= 0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not possible get oder ID.");
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        if (orderProcessor.isOrderProcessed(orderId)) {
            return ResponseEntity.status(HttpStatus.OK).body("Order already processed.");
        }
        shipmentService.createShipment(shopifyOrder);
        return ResponseEntity.status(HttpStatus.OK).body("Order processed successfully.");
    }
}

