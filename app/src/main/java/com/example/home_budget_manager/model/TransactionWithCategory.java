package com.example.home_budget_manager.model;

import androidx.room.Embedded;

public class TransactionWithCategory {
    @Embedded
    public Transaction transaction;
    
    public String categoryName;
    public String categoryColor;
    public String categoryIcon;
    
    public TransactionWithCategory() {
    }
}
