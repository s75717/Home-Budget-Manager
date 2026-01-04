package com.example.home_budget_manager.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.home_budget_manager.database.BudgetDatabase;
import com.example.home_budget_manager.database.CategoryDao;
import com.example.home_budget_manager.database.TransactionDao;
import com.example.home_budget_manager.model.Category;
import com.example.home_budget_manager.model.Transaction;
import com.example.home_budget_manager.model.TransactionWithCategory;

import java.util.List;

public class BudgetRepository {
    
    private CategoryDao categoryDao;
    private TransactionDao transactionDao;
    
    public BudgetRepository(Application application) {
        BudgetDatabase database = BudgetDatabase.getDatabase(application);
        categoryDao = database.categoryDao();
        transactionDao = database.transactionDao();
    }
    
    // Category operations
    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }
    
    public LiveData<List<Category>> getCategoriesByType(String type) {
        return categoryDao.getCategoriesByType(type);
    }
    
    public void insertCategory(Category category) {
        BudgetDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.insert(category);
        });
    }
    
    public void updateCategory(Category category) {
        BudgetDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.update(category);
        });
    }
    
    public void deleteCategory(Category category) {
        BudgetDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.delete(category);
        });
    }
    
    // Transaction operations
    public LiveData<List<TransactionWithCategory>> getAllTransactions() {
        return transactionDao.getAllTransactionsWithCategory();
    }
    
    public LiveData<List<TransactionWithCategory>> getTransactionsByDateRange(long startDate, long endDate) {
        return transactionDao.getTransactionsByDateRange(startDate, endDate);
    }
    
    public LiveData<List<TransactionWithCategory>> getTransactionsByCategory(int categoryId) {
        return transactionDao.getTransactionsByCategory(categoryId);
    }
    
    public LiveData<List<TransactionWithCategory>> getTransactionsByCategoryAndDate(int categoryId, long startDate, long endDate) {
        return transactionDao.getTransactionsByCategoryAndDate(categoryId, startDate, endDate);
    }
    
    public LiveData<Double> getTotalIncomeByDateRange(long startDate, long endDate) {
        return transactionDao.getTotalIncomeByDateRange(startDate, endDate);
    }
    
    public LiveData<Double> getTotalExpenseByDateRange(long startDate, long endDate) {
        return transactionDao.getTotalExpenseByDateRange(startDate, endDate);
    }
    
    public void insertTransaction(Transaction transaction) {
        BudgetDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.insert(transaction);
        });
    }
    
    public void updateTransaction(Transaction transaction) {
        BudgetDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.update(transaction);
        });
    }
    
    public void deleteTransaction(Transaction transaction) {
        BudgetDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.delete(transaction);
        });
    }
}
