package ru.example.purchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.purchase.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
