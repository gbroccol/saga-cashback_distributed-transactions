package ru.example.purchase.model;

public enum PurchaseState {

    CREATING("Creating"),
    CREATED("Created"),
    REJECTED("Rejected"),
    CANCELING("Canceling"),
    CANCELED("Canceled");

    public final String label;

    private PurchaseState(String label) { // todo fix
        this.label = label;
    }

}
