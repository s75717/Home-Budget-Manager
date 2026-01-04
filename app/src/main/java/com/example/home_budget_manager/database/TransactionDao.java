package com.example.home_budget_manager.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.home_budget_manager.model.Transaction;
import com.example.home_budget_manager.model.TransactionWithCategory;

import java.util.List;

@Dao
public interface TransactionDao {
    
    @Insert
    long insert(Transaction transaction);
    
    @Update
    void update(Transaction transaction);
    
    @Delete
    void delete(Transaction transaction);
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions();
    
    @Query("SELECT t.*, c.name as categoryName, c.color as categoryColor, c.icon as categoryIcon " +
           "FROM transactions t " +
           "INNER JOIN categories c ON t.categoryId = c.id " +
           "ORDER BY t.date DESC")
    LiveData<List<TransactionWithCategory>> getAllTransactionsWithCategory();
    
    @Query("SELECT t.*, c.name as categoryName, c.color as categoryColor, c.icon as categoryIcon " +
           "FROM transactions t " +
           "INNER JOIN categories c ON t.categoryId = c.id " +
           "WHERE t.date >= :startDate AND t.date <= :endDate " +
           "ORDER BY t.date DESC")
    LiveData<List<TransactionWithCategory>> getTransactionsByDateRange(long startDate, long endDate);
    
    @Query("SELECT t.*, c.name as categoryName, c.color as categoryColor, c.icon as categoryIcon " +
           "FROM transactions t " +
           "INNER JOIN categories c ON t.categoryId = c.id " +
           "WHERE t.categoryId = :categoryId " +
           "ORDER BY t.date DESC")
    LiveData<List<TransactionWithCategory>> getTransactionsByCategory(int categoryId);
    
    @Query("SELECT t.*, c.name as categoryName, c.color as categoryColor, c.icon as categoryIcon " +
           "FROM transactions t " +
           "INNER JOIN categories c ON t.categoryId = c.id " +
           "WHERE t.date >= :startDate AND t.date <= :endDate AND t.categoryId = :categoryId " +
           "ORDER BY t.date DESC")
    LiveData<List<TransactionWithCategory>> getTransactionsByCategoryAndDate(int categoryId, long startDate, long endDate);
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND date >= :startDate AND date <= :endDate")
    LiveData<Double> getTotalIncomeByDateRange(long startDate, long endDate);
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND date >= :startDate AND date <= :endDate")
    LiveData<Double> getTotalExpenseByDateRange(long startDate, long endDate);
    
    @Query("DELETE FROM transactions")
    void deleteAll();
}
