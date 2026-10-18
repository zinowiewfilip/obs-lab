package pl.lab.order;

import java.time.Instant;

record CreateOrderRequest(Long customerId, String sku, int quantity, String promoCode) {
}

record OrderView(Long id, String customerName, String sku, int quantity, int discountPercent, Instant createdAt) {
}
