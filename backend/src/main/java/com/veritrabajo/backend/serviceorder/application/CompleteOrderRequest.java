package com.veritrabajo.backend.serviceorder.application;

/**
 * Request body for completing a service order.
 * Carries the client's rating and comment, required to publish the
 * {@code ServiceExecutionCompleted} integration event consumed by Reputation.
 */
public record CompleteOrderRequest(int clientRating, String clientComment) {
}
