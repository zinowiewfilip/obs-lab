# obs-lab – projekt startowy do zajęć z observability

Dwa serwisy Spring Boot 4.1 (Java 21) i stack Grafana LGTM w Dockerze.

```
k6 ──► order-service :8080 ──HTTP──► inventory-service :8081
            │  (H2, Hikari pool = 5)          │
            └──────── OTLP :4318 ◄────────────┘
                         │
              grafana/otel-lgtm: Collector → Prometheus / Tempo / Loki → Grafana :3000
```

## Przed zajęciami (najlepiej w domu)

```bash
docker compose pull                 # ok. 1 GB obrazów
cd order-service && mvn -q package -DskipTests && cd ..
cd inventory-service && mvn -q package -DskipTests && cd ..
```

Pierwszy build pobiera zależności. Jeśli któraś wersja w `pom.xml` nie istnieje
(np. `opentelemetry-logback-appender-1.0` albo `datasource-micrometer-spring-boot`),
podbij ją do najnowszej dostępnej w Maven Central.

## Uruchomienie

```bash
docker compose up -d lgtm           # Grafana: http://localhost:3000
# w IDE: uruchom InventoryServiceApplication, potem OrderServiceApplication
docker compose --profile load run --rm k6     # ruch ~10 req/s
```

Sprawdzenie ręczne:

```bash
curl -X POST localhost:8080/orders -H 'Content-Type: application/json' \
     -d '{"customerId":1,"sku":"SKU-3","quantity":2,"promoCode":"WELCOME10"}'
curl localhost:8080/orders
curl localhost:8080/actuator/prometheus | head -40
```

## Endpointy

| Serwis | Endpoint | Opis |
|---|---|---|
| order-service | `POST /orders` | składa zamówienie (`customerId` 1–20, `sku` SKU-1…SKU-10, `promoCode` opcjonalny) |
| order-service | `GET /orders` | 50 ostatnich zamówień |
| inventory-service | `POST /inventory/{sku}/reserve?quantity=` | rezerwacja stanu |

Miejsca do uzupełnienia na ćwiczeniach są oznaczone `TODO(ćw. N)`.
