package com.veritrabajo.backend.customer.domain.port;

import com.veritrabajo.backend.customer.domain.model.AuthUserId;

public interface AuthenticatedIdentityProvider {

    AuthUserId currentAuthUserId();
}
