package pl.lab.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient {

    private final RestClient restClient;

    // Ważne: RestClient.Builder z autokonfiguracji propaguje nagłówek traceparent.
    // RestClient.create() zbudowany ręcznie -> trace urywa się na granicy serwisu.
    InventoryClient(RestClient.Builder builder, @Value("${inventory.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    // TODO(ćw. 2): dodaj @Observed(name = "inventory.reserve", contextualName = "reserve-stock")
    public void reserve(String sku, int quantity) {
        restClient.post()
                .uri("/inventory/{sku}/reserve?quantity={q}", sku, quantity)
                .retrieve()
                .toBodilessEntity();
    }
}
