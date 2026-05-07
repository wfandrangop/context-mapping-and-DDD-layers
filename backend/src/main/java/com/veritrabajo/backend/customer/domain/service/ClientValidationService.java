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

    /**
     * Validates eligibility according to the platform rules.
     *
     * @param customer the customer aggregate
     * @param reportCount number of active reports filed against the customer
     * @param outstandingDebt amount currently owed by the customer (zero if none)
     * @throws CustomerNotEligibleException if any rule fails
     */
    public void ensureEligibleForNewService(
            Customer customer,
            int reportCount,
            BigDecimal outstandingDebt
    ) {
        if (!customer.isActive()) {
            throw new CustomerNotEligibleException(customer.id(),
                    "Account is banned");
        }
        if (reportCount >= MAX_REPORTS_ALLOWED) {
            throw new CustomerNotEligibleException(customer.id(),
                    "Excessive reports against this customer (" + reportCount + ")");
        }
        if (outstandingDebt != null && outstandingDebt.signum() > 0) {
            throw new CustomerNotEligibleException(customer.id(),
                    "Outstanding debts: " + outstandingDebt);
        }
    }
}
