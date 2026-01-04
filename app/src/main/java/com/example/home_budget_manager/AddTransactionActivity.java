package com.example.home_budget_manager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.home_budget_manager.database.BudgetDatabase;
import com.example.home_budget_manager.model.Category;
import com.example.home_budget_manager.model.Transaction;
import com.example.home_budget_manager.viewmodel.BudgetViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {
    
    private BudgetViewModel viewModel;
    private RadioGroup rgTransactionType;
    private RadioButton rbIncome, rbExpense;
    private TextInputEditText etAmount, etNote;
    private Spinner spinnerCategory;
    private Button btnSelectDate;
    private TextView tvSelectedDate;
    private MaterialButton btnSave;
    private Toolbar toolbar;
    
    private List<Category> categoryList = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;
    private List<String> categoryNames = new ArrayList<>();
    
    private Calendar selectedDate;
    private SimpleDateFormat dateFormat;
    private String transactionType = "EXPENSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        
        initializeViews();
        setupToolbar();
        initializeViewModel();
        
        selectedDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        updateDateDisplay();
        
        // Check if transaction type was passed
        String passedType = getIntent().getStringExtra("TRANSACTION_TYPE");
        if (passedType != null) {
            transactionType = passedType;
            if (passedType.equals("INCOME")) {
                rbIncome.setChecked(true);
            } else {
                rbExpense.setChecked(true);
            }
        }
        
        setupListeners();
        loadCategories();
    }
    
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        rgTransactionType = findViewById(R.id.rgTransactionType);
        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);
        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        btnSave = findViewById(R.id.btnSave);
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
    
    private void setupListeners() {
        rgTransactionType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbIncome) {
                transactionType = "INCOME";
            } else {
                transactionType = "EXPENSE";
            }
            loadCategories();
        });
        
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        
        btnSave.setOnClickListener(v -> saveTransaction());
    }
    
    private void loadCategories() {
        // Load categories based on transaction type
        BudgetDatabase.databaseWriteExecutor.execute(() -> {
            List<Category> categories = BudgetDatabase.getDatabase(getApplicationContext())
                    .categoryDao()
                    .getCategoriesByTypeSync(transactionType);
            
            runOnUiThread(() -> {
                categoryList.clear();
                categoryNames.clear();
                
                categoryList.addAll(categories);
                for (Category category : categories) {
                    categoryNames.add(category.getIcon() + " " + category.getName());
                }
                
                if (categoryAdapter == null) {
                    categoryAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item, categoryNames);
                    spinnerCategory.setAdapter(categoryAdapter);
                } else {
                    categoryAdapter.notifyDataSetChanged();
                }
            });
        });
    }
    
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private void updateDateDisplay() {
        tvSelectedDate.setText(dateFormat.format(selectedDate.getTime()));
    }
    
    private void saveTransaction() {
        // Validate inputs
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (categoryList.isEmpty()) {
            Toast.makeText(this, "Please create a category first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int selectedCategoryPos = spinnerCategory.getSelectedItemPosition();
        if (selectedCategoryPos < 0 || selectedCategoryPos >= categoryList.size()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountStr);
            Category selectedCategory = categoryList.get(selectedCategoryPos);
            String note = etNote.getText().toString().trim();
            long dateTimestamp = selectedDate.getTimeInMillis();
            
            Transaction transaction = new Transaction(
                    selectedCategory.getId(),
                    amount,
                    transactionType,
                    dateTimestamp,
                    note
            );
            
            viewModel.insertTransaction(transaction);
            
            Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show();
            finish();
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
        }
    }
}
