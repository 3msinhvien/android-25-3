package com.example.bt_nhom_25_3.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt_nhom_25_3.R;
import com.example.bt_nhom_25_3.data.AppDatabase;
import com.example.bt_nhom_25_3.data.entity.Category;
import com.example.bt_nhom_25_3.ui.adapter.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(this, categoryList);
        rvCategories.setAdapter(adapter);

        db = AppDatabase.getDatabase(this);
        loadCategories();
    }

    private void loadCategories() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Category> categories = db.categoryDao().getAllCategories();
            runOnUiThread(() -> {
                if (categories != null && !categories.isEmpty()) {
                    categoryList.clear();
                    categoryList.addAll(categories);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Không có danh mục nào", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
