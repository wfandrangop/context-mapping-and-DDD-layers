package com.veritrabajo.backend.customer.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object that bundles the contact channels of a customer.
 * Immutable; validates email format and required phone number.
 */
public final class ContactInfo {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final String email;
    private final String phoneNumber;

    private ContactInfo(String email, String phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public static ContactInfo of(String email, String phoneNumber) {
        return new ContactInfo(validateEmail(email), validatePhone(phoneNumber));
    }

    public String email() {
        return email;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    private static String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        String normalized = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        return normalized;
    }

    private static String validatePhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        return phoneNumber.trim();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ContactInfo that)) {
            return false;
        }
        return email.equals(that.email) && phoneNumber.equals(that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phoneNumber);
    }
}
