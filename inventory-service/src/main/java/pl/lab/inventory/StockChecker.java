package pl.lab.inventory;

import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

/** Sprawdza dostępność w magazynie zewnętrznym. Część SKU leży w magazynie, którego API bywa wolne. */
@Component
public class StockChecker {

    private final AtomicReference<String> remoteWarehouseSku = new AtomicReference<>("");
    private volatile Duration remoteLatency = Duration.ofMillis(1500);

    @Observed(name = "warehouse.check", contextualName = "warehouse-check")
    public void verifyWarehouse(String sku) {
        if (sku.equals(remoteWarehouseSku.get())) {
            try {
                Thread.sleep(remoteLatency);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    void configure(String sku, Duration latency) {
        remoteWarehouseSku.set(sku == null ? "" : sku);
        remoteLatency = latency;
    }

    String currentSku() {
        return remoteWarehouseSku.get();
    }

    Duration currentLatency() {
        return remoteLatency;
    }
}
