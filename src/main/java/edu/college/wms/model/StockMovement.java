package edu.college.wms.model;

import java.time.LocalDateTime;

public class StockMovement {
    private final int productId;
    private final String productName;
    private final int change;
    private final int balance;
    private final String note;
    private final LocalDateTime time;

    public StockMovement(int productId, String productName, int change, int balance,
                         String note, LocalDateTime time) {
        this.productId = productId;
        this.productName = productName;
        this.change = change;
        this.balance = balance;
        this.note = note;
        this.time = time;
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getChange() { return change; }
    public int getBalance() { return balance; }
    public String getNote() { return note; }
    public LocalDateTime getTime() { return time; }
}
