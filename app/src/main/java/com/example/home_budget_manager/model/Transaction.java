package com.example.home_budget_manager.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE))
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int categoryId;
    private double amount;
    private String type; // "INCOME" or "EXPENSE"
    private long date; // timestamp
    private String note;
    
    public Transaction(int categoryId, double amount, String type, long date, String note) {
        this.categoryId = categoryId;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.note = note;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public long getDate() {
        return date;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
}
