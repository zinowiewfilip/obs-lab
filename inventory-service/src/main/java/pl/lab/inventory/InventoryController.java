package pl.lab.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/inventory")
class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private final Map<String, AtomicInteger> stock = new ConcurrentHashMap<>();
    private final StockChecker stockChecker;

    InventoryController(StockChecker stockChecker) {
        this.stockChecker = stockChecker;
        for (int i = 1; i <= 10; i++) {
            stock.put("SKU-" + i, new AtomicInteger(1_000_000));
        }
    }

    @PostMapping("/{sku}/reserve")
    ResponseEntity<Map<String, Object>> reserve(@PathVariable String sku, @RequestParam int quantity) {
        AtomicInteger available = stock.get(sku);
        if (available == null) {
            log.warn("Unknown SKU {}", sku);
            return ResponseEntity.notFound().build();
        }
        stockChecker.verifyWarehouse(sku);
        int left = available.addAndGet(-quantity);
        if (left < 0) {
            available.addAndGet(quantity);
            log.warn("Insufficient stock for {}: requested {}", sku, quantity);
            return ResponseEntity.status(409).body(Map.of("sku", sku, "reserved", false));
        }
        log.debug("Reserved {} x {}, {} left", quantity, sku, left);
        return ResponseEntity.ok(Map.of("sku", sku, "reserved", true));
    }
}
