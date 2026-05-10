package com.veritrabajo.backend.customer.domain.service;

import com.veritrabajo.backend.customer.domain.exception.CustomerNotEligibleException;
import com.veritrabajo.backend.customer.domain.model.Customer;

import java.math.BigDecimal;

/**
 * Stateless domain service that decides whether a Customer is eligible
 * to request a new service. Encapsulates rules that span multiple inputs
 * (account status, report history, outstanding debts) and therefore do not
 * belong to the aggregate alone.
 */
public class ClientValidationService {

    private static final int MAX_REPORTS_ALLOWED = 3;

    public void ensureEligibleForNewService(
            Customer customer,
            int reportCount,
            BigDecimal outstandingDebt
    ) {
        if (!customer.isActive()) {
            throw new CustomerNotEligibleException(customer.id().value(),
                    "Account is banned");
        }
        if (reportCount >= MAX_REPORTS_ALLOWED) {
            throw new CustomerNotEligibleException(customer.id().value(),
                    "Excessive reports against this customer (" + reportCount + ")");
        }
        if (outstandingDebt != null && outstandingDebt.signum() > 0) {
            throw new CustomerNotEligibleException(customer.id().value(),
                    "Outstanding debts: " + outstandingDebt);
        }
    }
}
