package pl.lab.order;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.Instant;

@Entity
public class PurchaseOrder {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    private String sku;
    private int quantity;
    private int discountPercent;
    private Instant createdAt;

    protected PurchaseOrder() {
    }

    public PurchaseOrder(Customer customer, String sku, int quantity, int discountPercent) {
        this.customer = customer;
        this.sku = sku;
        this.quantity = quantity;
        this.discountPercent = discountPercent;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
