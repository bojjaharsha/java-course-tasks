package com.epam.foundation.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe LFU (Least Frequently Used) cache with time-based (5s idle) eviction.
 * Follows Effective Java concurrency guidelines (Item 78, 79, 81).
 */
public class SimpleJavaLfuCache<K, V> implements CacheService<K, V> {

    private static final Logger log = LoggerFactory.getLogger(SimpleJavaLfuCache.class);

    private final int maxSize;
    private final Duration expiryDuration;

    // Node representation
    private static class Node<K, V> {
        final K key;
        V value;
        long frequency;
        long lastAccessTimeNanos;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.frequency = 1;
            this.lastAccessTimeNanos = System.nanoTime();
        }

        void touch() {
            this.frequency++;
            this.lastAccessTimeNanos = System.nanoTime();
        }

        boolean isExpired(long nowNanos, long expiryNanos) {
            return (nowNanos - lastAccessTimeNanos) >= expiryNanos;
        }
    }

    private final Map<K, Node<K, V>> map = new ConcurrentHashMap<>();
    private final ReentrantLock evictionLock = new ReentrantLock();

    // Statistics metrics
    private final AtomicLong evictionCount = new AtomicLong(0);
    private final AtomicLong totalPutTimeNanos = new AtomicLong(0);
    private final AtomicLong putCount = new AtomicLong(0);

    public SimpleJavaLfuCache(int maxSize, Duration expiryDuration) {
        if (maxSize <= 0) throw new IllegalArgumentException("maxSize must be positive");
        this.maxSize = maxSize;
        this.expiryDuration = Objects.requireNonNull(expiryDuration);
    }

    public SimpleJavaLfuCache() {
        this(100_000, Duration.ofSeconds(5));
    }

    @Override
    public void put(K key, V value) {
        long startNanos = System.nanoTime();
        try {
            Objects.requireNonNull(key, "Key must not be null");
            Objects.requireNonNull(value, "Value must not be null");

            evictionLock.lock();
            try {
                evictExpired();

                if (map.containsKey(key)) {
                    Node<K, V> node = map.get(key);
                    node.value = value;
                    node.touch();
                } else {
                    if (map.size() >= maxSize) {
                        evictLfu();
                    }
                    map.put(key, new Node<>(key, value));
                }
            } finally {
                evictionLock.unlock();
            }
        } finally {
            long duration = System.nanoTime() - startNanos;
            totalPutTimeNanos.addAndGet(duration);
            putCount.incrementAndGet();
        }
    }

    @Override
    public Optional<V> get(K key) {
        Objects.requireNonNull(key, "Key must not be null");
        long now = System.nanoTime();
        long expiryNanos = expiryDuration.toNanos();

        evictionLock.lock();
        try {
            Node<K, V> node = map.get(key);
            if (node == null) {
                return Optional.empty();
            }

            if (node.isExpired(now, expiryNanos)) {
                map.remove(key);
                evictionCount.incrementAndGet();
                log.info("Removed entry due to 5s idle timeout: key={}", key);
                return Optional.empty();
            }

            node.touch();
            return Optional.of(node.value);
        } finally {
            evictionLock.unlock();
        }
    }

    private void evictExpired() {
        long now = System.nanoTime();
        long expiryNanos = expiryDuration.toNanos();
        Iterator<Map.Entry<K, Node<K, V>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, Node<K, V>> entry = it.next();
            if (entry.getValue().isExpired(now, expiryNanos)) {
                it.remove();
                evictionCount.incrementAndGet();
                log.info("Evicted expired entry: key={}", entry.getKey());
            }
        }
    }

    private void evictLfu() {
        Node<K, V> lfuNode = null;
        for (Node<K, V> node : map.values()) {
            if (lfuNode == null) {
                lfuNode = node;
            } else if (node.frequency < lfuNode.frequency) {
                lfuNode = node;
            } else if (node.frequency == lfuNode.frequency && node.lastAccessTimeNanos < lfuNode.lastAccessTimeNanos) {
                lfuNode = node; // Tie-breaker: oldest last access
            }
        }

        if (lfuNode != null) {
            map.remove(lfuNode.key);
            evictionCount.incrementAndGet();
            log.info("Evicted LFU entry: key={}, frequency={}", lfuNode.key, lfuNode.frequency);
        }
    }

    @Override
    public long getEvictionCount() {
        return evictionCount.get();
    }

    @Override
    public double getAveragePutTimeNanos() {
        long count = putCount.get();
        return count == 0 ? 0.0 : (double) totalPutTimeNanos.get() / count;
    }

    @Override
    public int size() {
        return map.size();
    }
}
