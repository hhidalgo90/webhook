package com.shopify.shopifywebhook.feignclient;

import com.shopify.shopifywebhook.model.Regions;
import com.shopify.shopifywebhook.model.ResponseQuotation;
import com.shopify.shopifywebhook.model.Shipment;
import com.shopify.shopifywebhook.model.Sku;
import jakarta.websocket.server.PathParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "shipitClient", url = "${shipit.api.url}")
public interface ShipitClient {

    @PostMapping(value = "/shipments", consumes = "application/json", headers = {
            "Content-Type=application/json","Accept=application/vnd.shipit.v4"
    })
    ResponseEntity<Void> sendOrder(
            @RequestBody String body,
            @RequestHeader("X-Shipit-Email") String email,
            @RequestHeader("X-Shipit-Access-Token") String accessToken
    );

    @GetMapping(value = "/shipments/reference/{reference}", consumes = "application/json", headers = {
            "Content-Type=application/json","Accept=application/vnd.shipit.v4"
    })
    ResponseEntity<Shipment> getShipment(
            @PathVariable("reference") String reference,
            @RequestHeader("X-Shipit-Email") String email,
            @RequestHeader("X-Shipit-Access-Token") String accessToken
    );

    @GetMapping(value = "/communes",consumes = "application/json", headers = {
            "Content-Type=application/json","Accept=application/vnd.shipit.v4"
    })
    ResponseEntity<List<Regions>> getRegions(
            @RequestHeader("X-Shipit-Email") String email,
            @RequestHeader("X-Shipit-Access-Token") String accessToken
    );

    @GetMapping(value = "/fulfillment/skus/all", consumes = "application/json", headers = {
            "Content-Type=application/json","Accept=application/vnd.shipit.v4"
    })
    ResponseEntity<List<Sku>> getAllSku(
            @RequestHeader("X-Shipit-Email") String email,
            @RequestHeader("X-Shipit-Access-Token") String accessToken
    );

    @GetMapping(value = "/shipments", consumes = "application/json", headers = {
            "Content-Type=application/json","Accept=application/vnd.shipit.v4"
    })
    ResponseEntity<Shipment> getShipments(
            @RequestHeader("X-Shipit-Email") String email,
            @RequestHeader("X-Shipit-Access-Token") String accessToken,
            @RequestParam("reference") String reference,
            @RequestParam("per") int per,
            @RequestParam("page") int page,
            @RequestParam("type") String type,
            @RequestParam("id") String id,
            @RequestParam("query") String query,
            @RequestParam("service") String service,
            @RequestParam("days") String days,
            @RequestParam("analytics") String analytics,
            @RequestParam("filters") String filters,
            @RequestParam("state") String state,
            @RequestParam("shipment") String shipment
    );

    @PostMapping(value = "/rates", consumes = "application/json", headers = {
            "Content-Type=application/json","Accept=application/vnd.shipit.v4"
    })
    ResponseEntity<ResponseQuotation> getRates(
            @RequestBody String body,
            @RequestHeader("X-Shipit-Email") String email,
            @RequestHeader("X-Shipit-Access-Token") String accessToken
    );
}

