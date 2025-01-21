package com.shopify.shopifywebhook.service;

import com.shopify.shopifywebhook.model.entity.ProcessedOrder;
import com.shopify.shopifywebhook.repository.ProcessedOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessor {

    @Autowired
    private ProcessedOrderRepository repository;

    public synchronized boolean isOrderProcessed(Long orderId) {
        if (repository.existsById(orderId)) {
            return true; // Ya procesado
        }
        repository.save(new ProcessedOrder(orderId));
        return false; // Nuevo
    }
}
