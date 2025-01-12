package com.shopify.shopifywebhook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopify.shopifywebhook.config.ShipitConfig;
import com.shopify.shopifywebhook.feignclient.ShipitClient;
import com.shopify.shopifywebhook.model.*;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class ShipmentService {

    private final ShipitConfig shipitConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShipitClient shipitClient;

    private List<Regions> regionsList;

    private Regions findedRegion;

    private static String BASE_REFERENCE = "141";

    public ShipmentService(ShipitConfig shipitConfig) {
        this.shipitConfig = shipitConfig;
    }

    public void createShipment(Object shopifyOrder) {
        ShipitOrder shipitOrder = null;
        try {
                String jsonString = objectMapper.writeValueAsString(shopifyOrder);
                shipitOrder = createShipitOrder(jsonString);

                String reference = getLastReference();
                reference = String.valueOf(Integer.parseInt(reference) + 1);
                shipitOrder.setReference(reference);//Nro de pedido

            shipitClient.sendOrder(objectMapper.writeValueAsString(shipitOrder), shipitConfig.getEmail(), shipitConfig.getAccessToken());
        } catch (FeignException e) {
            log.error(e.getMessage());
            if(e.status() == 400 && e.getMessage().contains("Envío ya utilizado") ){
                assert shipitOrder != null;
                createShipment(shopifyOrder, String.valueOf(Integer.parseInt(shipitOrder.getReference()) + 1));
            }
            else {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sobrecarga del metodo createShipment
     * @param shopifyOrder
     * @param reference
     */
    public void createShipment(Object shopifyOrder, String reference) {
        ShipitOrder shipitOrder = null;
        try {
            String jsonString = objectMapper.writeValueAsString(shopifyOrder);
            shipitOrder = createShipitOrder(jsonString);
            shipitOrder.setReference(reference);//Nro de pedido

            shipitClient.sendOrder(objectMapper.writeValueAsString(shipitOrder), shipitConfig.getEmail(), shipitConfig.getAccessToken());
        } catch (FeignException e) {
            log.error(e.getMessage());
            if(e.status() == 400 && e.getMessage().contains("Envío ya utilizado") ){
                createShipment(shopifyOrder, String.valueOf(Integer.parseInt(shipitOrder.getReference()) + 1));
            }
            else {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ShipitOrder createShipitOrder(String jsonString) throws Exception {
        log.info("[createShipitOrder] Order {}" , jsonString);
        JsonNode rootNode = objectMapper.readTree(jsonString);
        ShipitOrder shipitOrder = new ShipitOrder();

        try {
            JsonNode shipping_address = rootNode.path("shipping_address");
            JsonNode customer = rootNode.path("customer");

            shipitOrder.setKind(1);
            shipitOrder.setPlatform(2);//API


            Destiny destiny = new Destiny();
            String direccion = shipping_address.path("address1").asText();
            String calle = direccion.replaceAll("\\d", "").trim();
            String nroCalle = direccion.replaceAll("\\D", "");
            destiny.setStreet(calle);
            destiny.setNumber(nroCalle);
            destiny.setComplement(shipping_address.path("address2").asText());

            log.debug("[ShipmentService] Antes de ir a buscar regiones ");
            this.regionsList = getRegions().getBody();
            String comunaFromShopify = shipping_address.path("city").asText();
            findNameAndIdRegion(comunaFromShopify);

            destiny.setCommune_id(findedRegion != null ? findedRegion.getRegion_id() : 2);
            destiny.setCommune_name(findedRegion != null ? findedRegion.getRegion_name(): "Quilpue");
            destiny.setFull_name(shipping_address.path("name").asText());
            destiny.setEmail(customer.path("email").asText());
            destiny.setPhone(shipping_address.path("phone").asText());
            destiny.setKind("home_delivery");
            destiny.setCourierBranchOfficeId(0);

            shipitOrder.setDestiny(destiny);

            JsonNode line_tems = rootNode.get("line_items");
            List<ShopifyProduct> shopifyProducts = getShopifyProducts(line_tems);

            shipitOrder.setSandbox(true);

            Seller seller = new Seller();
            seller.setSellerId(Integer.parseInt(rootNode.path("name").asText().split("#")[1])); //#12345 id de la venta de shopify
            seller.setSellerName("shopify");

            shipitOrder.setSeller(seller);

            Insurance insurance = new Insurance();
            insurance.setTicket_number("1232131");//nro boleta
            insurance.setTicket_amount(rootNode.path("current_total_price").asInt());
            insurance.setDetail("Productos para mascotas");
            insurance.setExtra(false);

            shipitOrder.setInsurance(insurance);

            if (!shopifyProducts.isEmpty()) {
                List<Sku> skuList = getAllSku().getBody();

                List<ShipitProduct> shipitProducts = new ArrayList<>();
                shopifyProducts.forEach(shopifyProduct -> {

                    Optional<Sku> findedProduct = skuList.stream().filter(sku -> sku.getName().equalsIgnoreCase(shopifyProduct.getSku())).findFirst();

                    if(findedProduct.isPresent()){
                        ShipitProduct shipitProduct = new ShipitProduct();
                        shipitProduct.setSku_id(findedProduct.get().getId());
                        shipitProduct.setAmount(shopifyProduct.getQuantity());
                        shipitProduct.setDescription(shopifyProduct.getName());
                        shipitProduct.setWarehouse_id(findedProduct.get().getWarehouse_id());

                        shipitProducts.add(shipitProduct);

                        Sizes sizes = new Sizes();
                        sizes.setHeight(findedProduct.get().getHeight());
                        sizes.setWeight((float) findedProduct.get().getWeight());
                        sizes.setLength(findedProduct.get().getLength());
                        sizes.setWidth(findedProduct.get().getWidth());
                        shipitOrder.setSizes(sizes);
                    }
                });

                shipitOrder.setProducts(shipitProducts);
                shipitOrder.setItems(shipitProducts.size());


                // obtener el courier mas barato
                ResponseQuotation responseQuotation = getBestPriceForDelivery(shipitOrder.getSizes(), destiny.getCommune_id());
                Courier courier = new Courier();
                courier.setId(1); //TODO consumir api couriers
                courier.setClient(responseQuotation.getLower_price().getCourier().getDisplay_name());
                courier.setPayable(false);
                courier.setSelected(false);
                courier.setAlgorithm(responseQuotation.getAlgorithm()); // 2 mas barato a X días
                courier.setAlgorithmDays(responseQuotation.getAlgorithm_days());
                courier.setWithoutCourier(false);

                shipitOrder.setCourier(courier);
            }
        } catch (Exception e) {
            log.error("Ocurrio un error al crear shipitOrder: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return shipitOrder;
    }

    private List<ShopifyProduct> getShopifyProducts(JsonNode lineItemsNode) {
        // Convertir a lista de objetos LineItem
        List<ShopifyProduct> lineItems = new ArrayList<>();
        try {
            if (lineItemsNode != null && lineItemsNode.isArray()) {
                for (JsonNode itemNode : lineItemsNode) {
                    ShopifyProduct lineItem = objectMapper.treeToValue(itemNode, ShopifyProduct.class);
                    lineItems.add(lineItem);
                }
            }
            // Imprimir los resultados
            lineItems.forEach(item -> System.out.println(item.getName() + " - " + item.getPrice()));

        } catch (Exception e) {
            log.error("Ocurrio un error al extraer productos: {}", e.getMessage());
        }
        return lineItems;
    }

    private ResponseEntity<List<Regions>> getRegions() {
        return shipitClient.getRegions(shipitConfig.getEmail(), shipitConfig.getAccessToken());
    }

    private ResponseEntity<List<Sku>> getAllSku() {
        return shipitClient.getAllSku(shipitConfig.getEmail(), shipitConfig.getAccessToken());
    }

    private void findNameAndIdRegion(String regionNameFromShopify) {

        if (!regionsList.isEmpty()) {
            regionsList.forEach(region -> {
                        if (region.getName().equalsIgnoreCase(regionNameFromShopify)) {
                            findedRegion = new Regions();
                            findedRegion.setRegion_name(region.getName());
                            findedRegion.setRegion_id(region.getId());
                        }
                    }
            );
        }
    }

    @Deprecated
    private String getReference() {
        String reference = "";
        String auxReference = String.valueOf(Integer.parseInt(BASE_REFERENCE) + 1);
        BASE_REFERENCE = auxReference;
        try {
            Shipment shipment = getShipment(auxReference).getBody();
            reference = shipment.getShipments().get(0).getReference();
        if (reference == null || reference.isEmpty()) {
            return auxReference;
        }
        } catch (Exception e){
            log.error("[getReference] no se encontro reference, se procede a enviar auxiliar: {}", e.getMessage());
            return auxReference;
        }
        return getReference();
    }

    @Deprecated
    private ResponseEntity<Shipment> getShipment(String reference){
        return shipitClient.getShipment(reference, shipitConfig.getEmail(), shipitConfig.getAccessToken());
    }

    private String getLastReference() {
        return getShipments().getBody().getShipments().stream().findFirst().orElseThrow().getReference();
    }

    private ResponseEntity<Shipment> getShipments() {
        ShipmentRequest query = ShipmentRequest.createDefaultQuery();
        return shipitClient.getShipments(shipitConfig.getEmail(), shipitConfig.getAccessToken(),
                query.getReference(),
                query.getPer(),
                query.getPage(),
                query.getType(),
                query.getId(),
                query.getQuery(),
                query.getService(),
                query.getDays(),
                query.getAnalytics(),
                query.getFilters().toString(), // Convert Map to JSON string if needed
                query.getState(),
                query.getShipment());
    }

    private ResponseQuotation getBestPriceForDelivery(Sizes sizes, int communeId) throws JsonProcessingException {
        Rates rates = new Rates();
        Quotation quotation = new Quotation();
        quotation.setLength(sizes.getLength());
        quotation.setHeight(sizes.getHeight());
        quotation.setWidth(sizes.getWidth());
        quotation.setWeight(sizes.getWeight());
        quotation.setAlgorithm(1);
        quotation.setAlgorithm_days(3);
        quotation.setType_of_destiny("domicilio");
        quotation.setOrigin_id(1);//TODO OBTENER COMUNA ORIGEN
        quotation.setDestiny_id(communeId);

        rates.setParcel(quotation);

        return shipitClient.getRates(objectMapper.writeValueAsString(rates), shipitConfig.getEmail(), shipitConfig.getAccessToken()).getBody();
    }
}


