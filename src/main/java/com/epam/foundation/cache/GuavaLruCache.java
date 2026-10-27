package com.epam.foundation.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Guava-based LRU cache with removal listener and metrics.
 */
public class GuavaLruCache<K, V> implements CacheService<K, V> {

    private static final Logger log = LoggerFactory.getLogger(GuavaLruCache.class);

    private final Cache<K, V> cache;
    private final AtomicLong evictionCount = new AtomicLong(0);
    private final AtomicLong totalPutTimeNanos = new AtomicLong(0);
    private final AtomicLong putCount = new AtomicLong(0);

    public GuavaLruCache(int maxSize, Duration expiryDuration) {
        RemovalListener<K, V> listener = notification -> {
            if (notification.wasEvicted()) {
                evictionCount.incrementAndGet();
                log.info("Guava evicted entry: key={}, cause={}", notification.getKey(), notification.getCause());
            }
        };

        this.cache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterAccess(expiryDuration.toMillis(), TimeUnit.MILLISECONDS)
                .removalListener(listener)
                .build();
    }

    public GuavaLruCache() {
        this(100_000, Duration.ofSeconds(5));
    }

    @Override
    public void put(K key, V value) {
        long start = System.nanoTime();
        try {
            cache.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
        } finally {
            totalPutTimeNanos.addAndGet(System.nanoTime() - start);
            putCount.incrementAndGet();
        }
    }

    @Override
    public Optional<V> get(K key) {
        return Optional.ofNullable(cache.getIfPresent(Objects.requireNonNull(key)));
    }

    @Override
    public long getEvictionCount() {
        cache.cleanUp(); // Forces eviction evaluation
        return evictionCount.get();
    }

    @Override
    public double getAveragePutTimeNanos() {
        long count = putCount.get();
        return count == 0 ? 0.0 : (double) totalPutTimeNanos.get() / count;
    }

    @Override
    public int size() {
        cache.cleanUp();
        return (int) cache.size();
    }
}
