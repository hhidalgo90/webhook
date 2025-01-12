package com.shopify.shopifywebhook.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {
    private float length = 10.0f; // Largo (cm) del envío
    private float height = 10.0f; // Alto (cm) del envío
    private float width = 10.0f;  // Ancho (cm) del envío
    private float weight = 1.0f;  // Peso (kg) del envío
    private int origin_id = 308;   // Comuna de origen (ID válido)
    private int destiny_id;        // Comuna de destino (ID válido)
    private String type_of_destiny = "domicilio"; // Tipo de destino
    private String courier_for_client;           // Nombre del courier seleccionado
    private int algorithm = 1;    // Algoritmo para realizar el envío
    private int algorithm_days = 3; // Días para el algoritmo 2
}
