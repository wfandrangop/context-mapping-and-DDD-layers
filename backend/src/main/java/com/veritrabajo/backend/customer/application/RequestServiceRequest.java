package com.veritrabajo.backend.customer.application;

import java.util.UUID;

public record RequestServiceRequest(UUID jobPostId, UUID addressId) { }
