package com.example.home_budget_manager.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home_budget_manager.R;
import com.example.home_budget_manager.model.TransactionWithCategory;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    
    private List<TransactionWithCategory> transactions = new ArrayList<>();
    private OnTransactionClickListener listener;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    
    public interface OnTransactionClickListener {
        void onTransactionClick(TransactionWithCategory transaction);
        void onTransactionLongClick(TransactionWithCategory transaction);
    }
    
    public TransactionAdapter(OnTransactionClickListener listener) {
        this.listener = listener;
        // Set currency to Malaysian Ringgit (MYR)
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ms", "MY"));
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }
    
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionWithCategory item = transactions.get(position);
        holder.bind(item);
    }
    
    @Override
    public int getItemCount() {
        return transactions.size();
    }
    
    public void setTransactions(List<TransactionWithCategory> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }
    
    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private View colorIndicator;
        private TextView tvCategoryIcon, tvCategoryName, tvTransactionDate, tvTransactionNote, tvTransactionAmount;
        
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            colorIndicator = itemView.findViewById(R.id.categoryColorIndicator);
            tvCategoryIcon = itemView.findViewById(R.id.tvCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            tvTransactionNote = itemView.findViewById(R.id.tvTransactionNote);
            tvTransactionAmount = itemView.findViewById(R.id.tvTransactionAmount);
        }
        
        public void bind(TransactionWithCategory item) {
            // Set category info
            tvCategoryIcon.setText(item.categoryIcon);
            tvCategoryName.setText(item.categoryName);
            
            // Set color
            try {
                colorIndicator.setBackgroundColor(Color.parseColor(item.categoryColor));
            } catch (Exception e) {
                colorIndicator.setBackgroundColor(Color.GRAY);
            }
            
            // Set date
            Date date = new Date(item.transaction.getDate());
            tvTransactionDate.setText(dateFormat.format(date));
            
            // Set note
            String note = item.transaction.getNote();
            if (note != null && !note.isEmpty()) {
                tvTransactionNote.setVisibility(View.VISIBLE);
                tvTransactionNote.setText(note);
            } else {
                tvTransactionNote.setVisibility(View.GONE);
            }
            
            // Set amount with color
            double amount = item.transaction.getAmount();
            tvTransactionAmount.setText(currencyFormat.format(amount));
            
            if (item.transaction.getType().equals("INCOME")) {
                tvTransactionAmount.setTextColor(itemView.getContext().getResources()
                        .getColor(R.color.income_green, null));
            } else {
                tvTransactionAmount.setTextColor(itemView.getContext().getResources()
                        .getColor(R.color.expense_red, null));
            }
            
            // Click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTransactionClick(item);
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onTransactionLongClick(item);
                }
                return true;
            });
        }
    }
}
