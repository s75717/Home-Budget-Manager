package com.example.home_budget_manager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.home_budget_manager.model.Category;
import com.example.home_budget_manager.model.Transaction;
import com.example.home_budget_manager.model.TransactionWithCategory;
import com.example.home_budget_manager.repository.BudgetRepository;

import java.util.List;

public class BudgetViewModel extends AndroidViewModel {
    
    private BudgetRepository repository;
    
    public BudgetViewModel(@NonNull Application application) {
        super(application);
        repository = new BudgetRepository(application);
    }
    
    // Category methods
    public LiveData<List<Category>> getAllCategories() {
        return repository.getAllCategories();
    }
    
    public LiveData<List<Category>> getCategoriesByType(String type) {
        return repository.getCategoriesByType(type);
    }
    
    public void insertCategory(Category category) {
        repository.insertCategory(category);
    }
    
    public void updateCategory(Category category) {
        repository.updateCategory(category);
    }
    
    public void deleteCategory(Category category) {
        repository.deleteCategory(category);
    }
    
    // Transaction methods
    public LiveData<List<TransactionWithCategory>> getAllTransactions() {
        return repository.getAllTransactions();
    }
    
    public LiveData<List<TransactionWithCategory>> getTransactionsByDateRange(long startDate, long endDate) {
        return repository.getTransactionsByDateRange(startDate, endDate);
    }
    
    public LiveData<List<TransactionWithCategory>> getTransactionsByCategory(int categoryId) {
        return repository.getTransactionsByCategory(categoryId);
    }
    
    public LiveData<List<TransactionWithCategory>> getTransactionsByCategoryAndDate(int categoryId, long startDate, long endDate) {
        return repository.getTransactionsByCategoryAndDate(categoryId, startDate, endDate);
    }
    
    public LiveData<Double> getTotalIncomeByDateRange(long startDate, long endDate) {
        return repository.getTotalIncomeByDateRange(startDate, endDate);
    }
    
    public LiveData<Double> getTotalExpenseByDateRange(long startDate, long endDate) {
        return repository.getTotalExpenseByDateRange(startDate, endDate);
    }
    
    public void insertTransaction(Transaction transaction) {
        repository.insertTransaction(transaction);
    }
    
    public void updateTransaction(Transaction transaction) {
        repository.updateTransaction(transaction);
    }
    
    public void deleteTransaction(Transaction transaction) {
        repository.deleteTransaction(transaction);
    }
}
