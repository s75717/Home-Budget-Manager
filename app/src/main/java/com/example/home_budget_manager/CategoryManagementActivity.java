package com.example.home_budget_manager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home_budget_manager.adapter.CategoryAdapter;
import com.example.home_budget_manager.model.Category;
import com.example.home_budget_manager.viewmodel.BudgetViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class CategoryManagementActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryActionListener {
    
    private BudgetViewModel viewModel;
    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private MaterialButton btnAddCategory;
    private FloatingActionButton fabAddCategory;
    
    private String currentType = "EXPENSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);
        
        initializeViews();
        setupToolbar();
        initializeViewModel();
        setupRecyclerView();
        setupListeners();
        
        loadCategories();
    }
    
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        rvCategories = findViewById(R.id.rvCategories);
        tabLayout = findViewById(R.id.tabLayout);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        fabAddCategory = findViewById(R.id.fabAddCategory);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
    }
    
    private void setupRecyclerView() {
        adapter = new CategoryAdapter(this);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        rvCategories.setAdapter(adapter);
    }
    
    private void setupListeners() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    currentType = "EXPENSE";
                } else {
                    currentType = "INCOME";
                }
                loadCategories();
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        
        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());
        fabAddCategory.setOnClickListener(v -> showAddCategoryDialog());
    }
    
    private void loadCategories() {
        viewModel.getCategoriesByType(currentType).observe(this, categories -> {
            if (categories != null) {
                adapter.setCategories(categories);
            }
        });
    }
    
    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        
        EditText etCategoryName = dialogView.findViewById(R.id.etCategoryName);
        EditText etCategoryIcon = dialogView.findViewById(R.id.etCategoryIcon);
        RadioGroup rgCategoryType = dialogView.findViewById(R.id.rgCategoryType);
        RadioButton rbIncome = dialogView.findViewById(R.id.rbIncome);
        RadioButton rbExpense = dialogView.findViewById(R.id.rbExpense);
        Spinner spinnerColor = dialogView.findViewById(R.id.spinnerColor);
        
        // Pre-select current type
        if (currentType.equals("INCOME")) {
            rbIncome.setChecked(true);
        } else {
            rbExpense.setChecked(true);
        }
        
        // Setup color spinner
        String[] colors = {"#FF6B6B", "#4ECDC4", "#FFE66D", "#95E1D3", "#C7CEEA", 
                          "#FF8B94", "#B4A7D6", "#A8DADC", "#2ECC71", "#3498DB",
                          "#9B59B6", "#F39C12", "#1ABC9C", "#E74C3C", "#16A085"};
        String[] colorNames = {"Red", "Teal", "Yellow", "Mint", "Lavender",
                              "Pink", "Purple", "Blue", "Green", "Sky Blue",
                              "Violet", "Orange", "Turquoise", "Crimson", "Sea Green"};
        
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, colorNames);
        spinnerColor.setAdapter(colorAdapter);
        
        builder.setView(dialogView)
                .setTitle("Add New Category")
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etCategoryName.getText().toString().trim();
                    String icon = etCategoryIcon.getText().toString().trim();
                    
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Please enter category name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (icon.isEmpty()) {
                        icon = "📦"; // Default icon
                    }
                    
                    String type = rbIncome.isChecked() ? "INCOME" : "EXPENSE";
                    int colorIndex = spinnerColor.getSelectedItemPosition();
                    String color = colors[colorIndex];
                    
                    Category category = new Category(name, type, color, icon);
                    viewModel.insertCategory(category);
                    
                    Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onCategoryClick(Category category) {
        // Could implement edit functionality
        Toast.makeText(this, "Edit feature coming soon", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onCategoryDelete(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure? All transactions in this category will also be deleted.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteCategory(category);
                    Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
