package com.veritrabajo.backend.workerprofile.domain.model;

/**
 * Immutable free-text experience narrative supplied by the worker (non-empty).
 */
public final class RawDescription {

    private final String text;

    private RawDescription(String text) {
        this.text = text;
    }

    public static RawDescription of(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Description cannot be blank"
            );
        }
        return new RawDescription(text.trim());
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RawDescription that)) {
            return false;
        }
        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public String toString() {
        return "RawDescription{text='" + text + "'}";
    }
}
