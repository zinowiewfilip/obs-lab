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

/** Endpoint techniczny dla prowadzącego zajęcia - przełącza magazyn zdalny dla wybranego SKU. */
@RestController
@RequestMapping("/internal/warehouse")
class MaintenanceController {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceController.class);

    private final StockChecker stockChecker;

    MaintenanceController(StockChecker stockChecker) {
        this.stockChecker = stockChecker;
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
}
