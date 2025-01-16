package ru.example.purchase.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;

    private String productName;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PurchaseState state;

}
