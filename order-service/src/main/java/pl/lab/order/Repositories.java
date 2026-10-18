package pl.lab.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface CustomerRepository extends JpaRepository<Customer, Long> {
}

interface OrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findTop50ByOrderByIdDesc();
}
