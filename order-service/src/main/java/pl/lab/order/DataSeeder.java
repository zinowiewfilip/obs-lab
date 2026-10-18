package pl.lab.order;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Dane startowe: 20 klientów i 200 zamówień, żeby GET /orders miał co zwracać od pierwszej minuty. */
@Component
class DataSeeder implements ApplicationRunner {

    private final CustomerRepository customers;
    private final OrderRepository orders;

    DataSeeder(CustomerRepository customers, OrderRepository orders) {
        this.customers = customers;
        this.orders = orders;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Customer> saved = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            saved.add(customers.save(new Customer("Customer " + i)));
        }
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < 200; i++) {
            Customer c = saved.get(rnd.nextInt(saved.size()));
            orders.save(new PurchaseOrder(c, "SKU-" + (1 + rnd.nextInt(10)), 1 + rnd.nextInt(5), 0));
        }
    }
}
