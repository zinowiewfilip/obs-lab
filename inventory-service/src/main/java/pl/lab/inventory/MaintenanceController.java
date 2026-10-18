package pl.lab.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

/** Endpointy techniczne dla prowadzącego zajęcia - magazyn zdalny dla wybranego SKU i stany magazynowe. */
@RestController
@RequestMapping("/internal/warehouse")
class MaintenanceController {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceController.class);

    private final StockChecker stockChecker;
    private final Stock stock;

    MaintenanceController(StockChecker stockChecker, Stock stock) {
        this.stockChecker = stockChecker;
        this.stock = stock;
    }

    @GetMapping
    Map<String, Object> current() {
        return Map.of("remoteSku", stockChecker.currentSku(), "latencyMs", stockChecker.currentLatency().toMillis());
    }

    @PostMapping
    Map<String, Object> set(@RequestParam(defaultValue = "") String sku,
                            @RequestParam(defaultValue = "1500") long latencyMs) {
        stockChecker.configure(sku, Duration.ofMillis(latencyMs));
        log.debug("Remote warehouse configuration changed");
        return current();
    }

    // Ręczne ustawienie stanu, np. uzupełnienie SKU-7: sku=SKU-7&quantity=1000000.
    @PostMapping("/stock")
    Map<String, Object> setStock(@RequestParam String sku,
                                 @RequestParam(defaultValue = "300") int quantity) {
        stock.set(sku, quantity);
        log.debug("Stock level adjusted");
        return Map.of("sku", sku, "quantity", stock.get(sku).get());
    }
}
