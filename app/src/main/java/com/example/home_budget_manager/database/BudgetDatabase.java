package com.example.home_budget_manager.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.home_budget_manager.model.Category;
import com.example.home_budget_manager.model.Transaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Category.class, Transaction.class}, version = 1, exportSchema = false)
public abstract class BudgetDatabase extends RoomDatabase {
    
    public abstract CategoryDao categoryDao();
    public abstract TransactionDao transactionDao();
    
    private static volatile BudgetDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = 
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    public static BudgetDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BudgetDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BudgetDatabase.class, "budget_database")
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@androidx.annotation.NonNull androidx.sqlite.db.SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseWriteExecutor.execute(() -> {
                                        // Prepopulate default categories
                                        CategoryDao categoryDao = INSTANCE.categoryDao();
                                        
                                        // Expense categories
                                        categoryDao.insert(new Category("Food", "EXPENSE", "#FF6B6B", "🍔"));
                                        categoryDao.insert(new Category("Transport", "EXPENSE", "#4ECDC4", "🚗"));
                                        categoryDao.insert(new Category("Bills", "EXPENSE", "#FFE66D", "💡"));
                                        categoryDao.insert(new Category("Shopping", "EXPENSE", "#95E1D3", "🛍️"));
                                        categoryDao.insert(new Category("Entertainment", "EXPENSE", "#C7CEEA", "🎬"));
                                        categoryDao.insert(new Category("Health", "EXPENSE", "#FF8B94", "🏥"));
                                        categoryDao.insert(new Category("Education", "EXPENSE", "#B4A7D6", "📚"));
                                        categoryDao.insert(new Category("Other Expense", "EXPENSE", "#A8DADC", "📦"));
                                        
                                        // Income categories
                                        categoryDao.insert(new Category("Salary", "INCOME", "#2ECC71", "💰"));
                                        categoryDao.insert(new Category("Business", "INCOME", "#3498DB", "💼"));
                                        categoryDao.insert(new Category("Investment", "INCOME", "#9B59B6", "📈"));
                                        categoryDao.insert(new Category("Gift", "INCOME", "#F39C12", "🎁"));
                                        categoryDao.insert(new Category("Other Income", "INCOME", "#1ABC9C", "💵"));
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
