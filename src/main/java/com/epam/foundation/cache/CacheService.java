package com.epam.foundation.cache;

import java.util.Optional;

public interface CacheService<K, V> {
    void put(K key, V value);
    Optional<V> get(K key);
    long getEvictionCount();
    double getAveragePutTimeNanos();
    int size();
}
