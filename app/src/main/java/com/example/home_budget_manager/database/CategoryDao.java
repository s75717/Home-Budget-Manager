package com.example.home_budget_manager.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.home_budget_manager.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    
    @Insert
    long insert(Category category);
    
    @Update
    void update(Category category);
    
    @Delete
    void delete(Category category);
    
    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();
    
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name ASC")
    LiveData<List<Category>> getCategoriesByType(String type);
    
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    Category getCategoryById(int categoryId);
    
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name ASC")
    List<Category> getCategoriesByTypeSync(String type);
}
