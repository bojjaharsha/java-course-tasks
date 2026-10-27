package com.epam.foundation.cache;

import java.util.Objects;

/**
 * Simple custom class representing cache entry value with one String field.
 * Follows Effective Java Item 17 (Immutable object).
 */
public final class CacheEntry {
    private final String payload;

    public CacheEntry(String payload) {
        this.payload = Objects.requireNonNull(payload, "payload must not be null");
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheEntry that)) return false;
        return Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payload);
    }

    @Override
    public String toString() {
        return "CacheEntry{" + "payload='" + payload + '\'' + '}';
    }
}
