package com.veritrabajo.backend.workerprofile.domain.port;

import com.veritrabajo.backend.workerprofile.domain.model.AuthUserId;

public interface AuthenticatedIdentityProvider {

    AuthUserId currentAuthUserId();
}
