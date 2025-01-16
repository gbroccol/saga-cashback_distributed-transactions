package ru.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseResponse { // todo record

    private Long purchaseId;
    private PurchaseState state;
}
