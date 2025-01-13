package ru.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
