package com.shopify.shopifywebhook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopify.shopifywebhook.config.ShipitConfig;
import com.shopify.shopifywebhook.feignclient.ShipitClient;
import com.shopify.shopifywebhook.feignclient.ShipitRegionsClient;
import com.shopify.shopifywebhook.feignclient.ShipitSkuClient;
import com.shopify.shopifywebhook.model.*;
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

    @Autowired
    private ShipitSkuClient shipitSkuClient;

    @Autowired
    private ShipitRegionsClient shipitRegionsClient;

    private List<Regions> regionsList;

    private Regions findedRegion;

    public ShipmentService(ShipitConfig shipitConfig) {
        this.shipitConfig = shipitConfig;
    }

    public void createShipment(String shopifyOrder) throws Exception {
        ShipitOrder shipitOrder = createShipitOrder(shopifyOrder);
        log.info(objectMapper.writeValueAsString(shipitOrder));
        shipitClient.sendOrder(objectMapper.writeValueAsString(shipitOrder), shipitConfig.getEmail(), shipitConfig.getAccessToken());
    }

    public ShipitOrder createShipitOrder(String jsonString) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonString);
        ShipitOrder shipitOrder = new ShipitOrder();

        try {
            JsonNode shipping_address = rootNode.path("shipping_address");
            JsonNode customer = rootNode.path("customer");

            shipitOrder.setKind(1);
            shipitOrder.setPlatform(2);//API
            shipitOrder.setReference("200");//Nro de pedido

            Destiny destiny = new Destiny();
            destiny.setStreet(shipping_address.path("address1").asText()); //TODO separar calle y nro
            destiny.setNumber("123");
            //destiny.setNumber(shipping_address.path("address2").asText());
            destiny.setComplement(rootNode.path("note").asText());

            log.debug("[ShipmentService] Antes de ir a buscar regiones ");
            this.regionsList = getRegions().getBody();
            findNameAndIdRegion(shipping_address.path("city").asText());

            destiny.setCommune_id(findedRegion != null ? findedRegion.getId() : 2);
            destiny.setCommune_name(findedRegion != null ? findedRegion.getName(): "Quilpue");
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

            Courier courier = new Courier();
            courier.setId(1); //TODO consumir api couriers
            courier.setClient("starken"); //TODO consumir api couriers
            courier.setPayable(false);
            courier.setSelected(false);
            courier.setAlgorithm(2); // 2 mas barato a X días
            courier.setAlgorithmDays(2);
            courier.setWithoutCourier(false);

            shipitOrder.setCourier(courier);

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

                        Sizes sizes = new Sizes(); //TODO ver de donde saco las medidas
                        sizes.setHeight(findedProduct.get().getHeight());
                        sizes.setWeight((float) findedProduct.get().getWeight());
                        sizes.setLength(findedProduct.get().getLength());
                        sizes.setWidth(findedProduct.get().getWidth());
                        shipitOrder.setSizes(sizes);
                    }
                });

                shipitOrder.setProducts(shipitProducts);
                shipitOrder.setItems(shipitProducts.size());
            }
        } catch (Exception e) {
            log.error("Ocurrio un error al crear shipitOrder: {}", e.getMessage());
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
        return shipitRegionsClient.getRegions(shipitConfig.getEmail(), shipitConfig.getAccessToken());
    }

    private ResponseEntity<List<Sku>> getAllSku() {
        return shipitSkuClient.getAllSku(shipitConfig.getEmail(), shipitConfig.getAccessToken());
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
}


