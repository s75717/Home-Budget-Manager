package com.example.home_budget_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.home_budget_manager.viewmodel.BudgetViewModel;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    private BudgetViewModel viewModel;
    private TextView tvCurrentMonth, tvTotalIncome, tvTotalExpense, tvBalance;
    private ImageButton btnPreviousMonth, btnNextMonth;
    private MaterialCardView cardAddIncome, cardAddExpense, cardViewTransactions, cardManageCategories;
    
    private Calendar currentCalendar;
    private SimpleDateFormat monthYearFormat;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Initialize
        initializeViews();
        initializeViewModel();
        setupListeners();
        
        // Setup date
        currentCalendar = Calendar.getInstance();
        monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        // Set currency to Malaysian Ringgit (MYR)
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ms", "MY"));
        
        updateMonthDisplay();
        loadMonthlyData();
    }
    
    private void initializeViews() {
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvBalance = findViewById(R.id.tvBalance);
        
        btnPreviousMonth = findViewById(R.id.btnPreviousMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        
        cardAddIncome = findViewById(R.id.cardAddIncome);
        cardAddExpense = findViewById(R.id.cardAddExpense);
        cardViewTransactions = findViewById(R.id.cardViewTransactions);
        cardManageCategories = findViewById(R.id.cardManageCategories);
    }
    
    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
    }
    
    private void setupListeners() {
        btnPreviousMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateMonthDisplay();
            loadMonthlyData();
        });
        
        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateMonthDisplay();
            loadMonthlyData();
        });
        
        cardAddIncome.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            intent.putExtra("TRANSACTION_TYPE", "INCOME");
            startActivity(intent);
        });
        
        cardAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            intent.putExtra("TRANSACTION_TYPE", "EXPENSE");
            startActivity(intent);
        });
        
        cardViewTransactions.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransactionListActivity.class);
            startActivity(intent);
        });
        
        cardManageCategories.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoryManagementActivity.class);
            startActivity(intent);
        });
    }
    
    private void updateMonthDisplay() {
        tvCurrentMonth.setText(monthYearFormat.format(currentCalendar.getTime()));
    }
    
    private void loadMonthlyData() {
        // Get start and end of current month
        Calendar startCal = (Calendar) currentCalendar.clone();
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        
        Calendar endCal = (Calendar) currentCalendar.clone();
        endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        
        long startDate = startCal.getTimeInMillis();
        long endDate = endCal.getTimeInMillis();
        
        // Observe income
        viewModel.getTotalIncomeByDateRange(startDate, endDate).observe(this, income -> {
            double incomeValue = income != null ? income : 0.0;
            tvTotalIncome.setText(currencyFormat.format(incomeValue));
            updateBalance();
        });
        
        // Observe expenses
        viewModel.getTotalExpenseByDateRange(startDate, endDate).observe(this, expense -> {
            double expenseValue = expense != null ? expense : 0.0;
            tvTotalExpense.setText(currencyFormat.format(expenseValue));
            updateBalance();
        });
    }
    
    private void updateBalance() {
        try {
            String incomeStr = tvTotalIncome.getText().toString().replaceAll("[^0-9.-]", "");
            String expenseStr = tvTotalExpense.getText().toString().replaceAll("[^0-9.-]", "");
            
            double income = incomeStr.isEmpty() ? 0 : Double.parseDouble(incomeStr);
            double expense = expenseStr.isEmpty() ? 0 : Double.parseDouble(expenseStr);
            double balance = income - expense;
            
            tvBalance.setText(currencyFormat.format(balance));
            
            // Change color based on balance
            if (balance > 0) {
                tvBalance.setTextColor(getResources().getColor(R.color.income_green, null));
            } else if (balance < 0) {
                tvBalance.setTextColor(getResources().getColor(R.color.expense_red, null));
            } else {
                tvBalance.setTextColor(getResources().getColor(R.color.text_primary, null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadMonthlyData();
    }
}