package com.example.home_budget_manager.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home_budget_manager.R;
import com.example.home_budget_manager.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    
    private List<Category> categories = new ArrayList<>();
    private OnCategoryActionListener listener;
    
    public interface OnCategoryActionListener {
        void onCategoryClick(Category category);
        void onCategoryDelete(Category category);
    }
    
    public CategoryAdapter(OnCategoryActionListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }
    
    @Override
    public int getItemCount() {
        return categories.size();
    }
    
    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }
    
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryIcon, tvCategoryName, tvCategoryType;
        private View categoryColorView;
        private ImageButton btnDeleteCategory;
        
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryIcon = itemView.findViewById(R.id.tvCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryType = itemView.findViewById(R.id.tvCategoryType);
            categoryColorView = itemView.findViewById(R.id.categoryColorView);
            btnDeleteCategory = itemView.findViewById(R.id.btnDeleteCategory);
        }
        
        public void bind(Category category) {
            tvCategoryIcon.setText(category.getIcon());
            tvCategoryName.setText(category.getName());
            tvCategoryType.setText(category.getType());
            
            // Set color with rounded shape
            try {
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.OVAL);
                shape.setColor(Color.parseColor(category.getColor()));
                categoryColorView.setBackground(shape);
            } catch (Exception e) {
                categoryColorView.setBackgroundColor(Color.GRAY);
            }
            
            // Click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
            
            btnDeleteCategory.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryDelete(category);
                }
            });
        }
    }
}
