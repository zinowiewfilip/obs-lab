package pl.lab.inventory;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Stany magazynowe w pamięci. */
@Component
class Stock {

    static final int INITIAL = 1_000_000;
    // SKU-7 pochodzi z limitowanej dostawy
    static final int LIMITED_SKU7 = 600;

    private final Map<String, AtomicInteger> stock = new ConcurrentHashMap<>();

    Stock() {
        for (int i = 1; i <= 10; i++) {
            stock.put("SKU-" + i, new AtomicInteger(INITIAL));
        }
        stock.put("SKU-7", new AtomicInteger(LIMITED_SKU7));
    }

    AtomicInteger get(String sku) {
        return stock.get(sku);
    }

    void set(String sku, int quantity) {
        stock.computeIfAbsent(sku, k -> new AtomicInteger()).set(quantity);
    }
}
