package pl.lab.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final CustomerRepository customers;
    private final OrderRepository orders;
    private final PromotionService promotions;
    private final InventoryClient inventory;

    OrderService(CustomerRepository customers, OrderRepository orders,
                 PromotionService promotions, InventoryClient inventory) {
        this.customers = customers;
        this.orders = orders;
        this.promotions = promotions;
        this.inventory = inventory;
    }

    @Transactional
    public PurchaseOrder placeOrder(CreateOrderRequest request) {
        Customer customer = customers.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown customer " + request.customerId()));

        int discount = promotions.discountFor(request.promoCode());
        inventory.reserve(request.sku(), request.quantity());

        PurchaseOrder saved = orders.save(new PurchaseOrder(customer, request.sku(), request.quantity(), discount));
        // TODO(ćw. 2): zlicz złożone zamówienie metryką orders.placed
        log.info("Order {} placed: sku={} qty={} discount={}%", saved.getId(), request.sku(), request.quantity(), discount);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<OrderView> recentOrders() {
        return orders.findTop50ByOrderByIdDesc().stream()
                .map(o -> new OrderView(o.getId(), o.getCustomer().getName(), o.getSku(),
                        o.getQuantity(), o.getDiscountPercent(), o.getCreatedAt()))
                .toList();
    }
}
