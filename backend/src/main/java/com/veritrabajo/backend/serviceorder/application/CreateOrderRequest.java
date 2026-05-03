package com.veritrabajo.backend.serviceorder.application;

/**
 * Request body for creating a new service order.
 */
public record CreateOrderRequest(String clientId, String workerId) {
}
