package com.veritrabajo.backend.customer.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "auth_user_id", nullable = false, unique = true, length = 36)
    private String authUserId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Instant registrationDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preferredBudgetMin;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preferredBudgetMax;

    @Version
    private Long version;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "customer_interest_categories",
            joinColumns = @JoinColumn(name = "customer_id")
    )
    @Column(name = "category", nullable = false)
    private List<String> interestCategories = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "customer_addresses",
            joinColumns = @JoinColumn(name = "customer_id")
    )
    private List<SavedAddressEmbeddable> addresses = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(String authUserId) {
        this.authUserId = authUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Instant registrationDate) {
        this.registrationDate = registrationDate;
    }

    public BigDecimal getPreferredBudgetMin() {
        return preferredBudgetMin;
    }

    public void setPreferredBudgetMin(BigDecimal preferredBudgetMin) {
        this.preferredBudgetMin = preferredBudgetMin;
    }

    public BigDecimal getPreferredBudgetMax() {
        return preferredBudgetMax;
    }

    public void setPreferredBudgetMax(BigDecimal preferredBudgetMax) {
        this.preferredBudgetMax = preferredBudgetMax;
    }

    public List<String> getInterestCategories() {
        return interestCategories;
    }

    public void setInterestCategories(List<String> interestCategories) {
        this.interestCategories = interestCategories;
    }

    public List<SavedAddressEmbeddable> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<SavedAddressEmbeddable> addresses) {
        this.addresses = addresses;
    }

    public Long getVersion() {
        return version;
    }
}
