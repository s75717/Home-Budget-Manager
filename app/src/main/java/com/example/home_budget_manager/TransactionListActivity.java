package com.example.home_budget_manager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home_budget_manager.adapter.TransactionAdapter;
import com.example.home_budget_manager.database.BudgetDatabase;
import com.example.home_budget_manager.model.Category;
import com.example.home_budget_manager.model.TransactionWithCategory;
import com.example.home_budget_manager.viewmodel.BudgetViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionListActivity extends AppCompatActivity implements TransactionAdapter.OnTransactionClickListener {
    
    private BudgetViewModel viewModel;
    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private LinearLayout emptyState;
    private Toolbar toolbar;
    
    private Button btnFilterMonth, btnClearFilters;
    private Spinner spinnerCategoryFilter;
    
    private List<Category> allCategories = new ArrayList<>();
    private ArrayAdapter<String> categoryFilterAdapter;
    private List<String> categoryFilterNames = new ArrayList<>();
    
    private Calendar filterStartDate = null;
    private Calendar filterEndDate = null;
    private int selectedCategoryId = -1;
    
    private SimpleDateFormat monthYearFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        
        initializeViews();
        setupToolbar();
        initializeViewModel();
        setupRecyclerView();
        
        monthYearFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        
        setupListeners();
        loadCategories();
        loadTransactions();
    }
    
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        rvTransactions = findViewById(R.id.rvTransactions);
        emptyState = findViewById(R.id.emptyState);
        btnFilterMonth = findViewById(R.id.btnFilterMonth);
        btnClearFilters = findViewById(R.id.btnClearFilters);
        spinnerCategoryFilter = findViewById(R.id.spinnerCategoryFilter);
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
        adapter = new TransactionAdapter(this);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);
    }
    
    private void setupListeners() {
        btnFilterMonth.setOnClickListener(v -> showMonthPicker());
        
        btnClearFilters.setOnClickListener(v -> {
            filterStartDate = null;
            filterEndDate = null;
            selectedCategoryId = -1;
            btnFilterMonth.setText("All Time");
            spinnerCategoryFilter.setSelection(0);
            loadTransactions();
        });
    }
    
    private void loadCategories() {
        categoryFilterNames.clear();
        categoryFilterNames.add("All Categories");
        
        BudgetDatabase.databaseWriteExecutor.execute(() -> {
            List<Category> expenseCategories = BudgetDatabase.getDatabase(getApplicationContext())
                    .categoryDao()
                    .getCategoriesByTypeSync("EXPENSE");
            List<Category> incomeCategories = BudgetDatabase.getDatabase(getApplicationContext())
                    .categoryDao()
                    .getCategoriesByTypeSync("INCOME");
            
            allCategories.clear();
            allCategories.addAll(expenseCategories);
            allCategories.addAll(incomeCategories);
            
            runOnUiThread(() -> {
                for (Category category : allCategories) {
                    categoryFilterNames.add(category.getIcon() + " " + category.getName());
                }
                
                categoryFilterAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, categoryFilterNames);
                spinnerCategoryFilter.setAdapter(categoryFilterAdapter);
                
                spinnerCategoryFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            selectedCategoryId = -1;
                        } else {
                            selectedCategoryId = allCategories.get(position - 1).getId();
                        }
                        loadTransactions();
                    }
                    
                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    }
                });
            });
        });
    }
    
    private void showMonthPicker() {
        Calendar calendar = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(Calendar.YEAR, year);
                    selectedCal.set(Calendar.MONTH, month);
                    
                    // Set filter dates to start and end of month
                    filterStartDate = (Calendar) selectedCal.clone();
                    filterStartDate.set(Calendar.DAY_OF_MONTH, 1);
                    filterStartDate.set(Calendar.HOUR_OF_DAY, 0);
                    filterStartDate.set(Calendar.MINUTE, 0);
                    filterStartDate.set(Calendar.SECOND, 0);
                    filterStartDate.set(Calendar.MILLISECOND, 0);
                    
                    filterEndDate = (Calendar) selectedCal.clone();
                    filterEndDate.set(Calendar.DAY_OF_MONTH, filterEndDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                    filterEndDate.set(Calendar.HOUR_OF_DAY, 23);
                    filterEndDate.set(Calendar.MINUTE, 59);
                    filterEndDate.set(Calendar.SECOND, 59);
                    filterEndDate.set(Calendar.MILLISECOND, 999);
                    
                    btnFilterMonth.setText(monthYearFormat.format(filterStartDate.getTime()));
                    loadTransactions();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private void loadTransactions() {
        if (filterStartDate != null && filterEndDate != null && selectedCategoryId != -1) {
            // Filter by both date and category
            viewModel.getTransactionsByCategoryAndDate(selectedCategoryId, 
                    filterStartDate.getTimeInMillis(), 
                    filterEndDate.getTimeInMillis())
                    .observe(this, this::updateUI);
        } else if (filterStartDate != null && filterEndDate != null) {
            // Filter by date only
            viewModel.getTransactionsByDateRange(
                    filterStartDate.getTimeInMillis(), 
                    filterEndDate.getTimeInMillis())
                    .observe(this, this::updateUI);
        } else if (selectedCategoryId != -1) {
            // Filter by category only
            viewModel.getTransactionsByCategory(selectedCategoryId)
                    .observe(this, this::updateUI);
        } else {
            // No filter - show all
            viewModel.getAllTransactions().observe(this, this::updateUI);
        }
    }
    
    private void updateUI(List<TransactionWithCategory> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            rvTransactions.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvTransactions.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            adapter.setTransactions(transactions);
        }
    }
    
    @Override
    public void onTransactionClick(TransactionWithCategory transaction) {
        // Could open edit transaction screen
        Toast.makeText(this, "Transaction clicked", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onTransactionLongClick(TransactionWithCategory transaction) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteTransaction(transaction.transaction);
                    Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
