package com.epam.foundation.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class CacheServiceTest {

    @Test
    void testSimpleJavaLfuEviction() {
        // Small cache of size 2 to test LFU eviction
        CacheService<String, CacheEntry> cache = new SimpleJavaLfuCache<>(2, Duration.ofSeconds(5));

        cache.put("k1", new CacheEntry("v1"));
        cache.put("k2", new CacheEntry("v2"));

        // Increase frequency of k1
        cache.get("k1");
        cache.get("k1");

        // Put k3 -> should evict k2 (lowest frequency)
        cache.put("k3", new CacheEntry("v3"));

        assertTrue(cache.get("k1").isPresent());
        assertFalse(cache.get("k2").isPresent());
        assertTrue(cache.get("k3").isPresent());
        assertEquals(1, cache.getEvictionCount());
        assertTrue(cache.getAveragePutTimeNanos() > 0);
    }

    @Test
    void testSimpleJavaTimeExpiration() throws InterruptedException {
        // Cache with 100ms idle timeout
        CacheService<String, CacheEntry> cache = new SimpleJavaLfuCache<>(10, Duration.ofMillis(100));

        cache.put("key", new CacheEntry("value"));
        assertTrue(cache.get("key").isPresent());

        Thread.sleep(150); // wait past expiration

        assertFalse(cache.get("key").isPresent());
        assertEquals(1, cache.getEvictionCount());
    }

    @Test
    void testGuavaLruEviction() {
        CacheService<String, CacheEntry> cache = new GuavaLruCache<>(2, Duration.ofSeconds(5));

        cache.put("k1", new CacheEntry("v1"));
        cache.put("k2", new CacheEntry("v2"));

        // Access k1, so k2 becomes least recently used
        cache.get("k1");

        cache.put("k3", new CacheEntry("v3"));

        assertTrue(cache.get("k1").isPresent());
        assertFalse(cache.get("k2").isPresent());
        assertTrue(cache.get("k3").isPresent());
        assertEquals(1, cache.getEvictionCount());
    }

    @Test
    void testConcurrency() throws InterruptedException {
        CacheService<Integer, CacheEntry> cache = new SimpleJavaLfuCache<>(100, Duration.ofSeconds(5));
        int threads = 10;
        int operations = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                for (int i = 0; i < operations; i++) {
                    int key = (threadId * operations + i) % 150;
                    cache.put(key, new CacheEntry("val_" + key));
                    cache.get(key);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertTrue(cache.size() <= 100);
        assertTrue(cache.getEvictionCount() > 0);
    }
}
