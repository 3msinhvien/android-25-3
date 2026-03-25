package com.example.bt_nhom_25_3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt_nhom_25_3.R;
import com.example.bt_nhom_25_3.data.AppDatabase;
import com.example.bt_nhom_25_3.data.entity.Product;
import com.example.bt_nhom_25_3.ui.adapter.ProductAdapter;
import com.example.bt_nhom_25_3.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {
    private TextView tvProductTitle;
    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private List<Product> productList;
    private AppDatabase db;
    private SessionManager sessionManager;

    private int categoryId = -1;
    private String categoryName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        tvProductTitle = findViewById(R.id.tvProductTitle);
        rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        sessionManager = new SessionManager(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("CATEGORY_ID")) {
            categoryId = intent.getIntExtra("CATEGORY_ID", -1);
            categoryName = intent.getStringExtra("CATEGORY_NAME");
            tvProductTitle.setText("Sản phẩm thuộc: " + categoryName);
        }

        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList, product -> {
            Intent detailIntent = new Intent(ProductActivity.this, ProductDetailActivity.class);
            detailIntent.putExtra("PRODUCT_ID", product.id);
            startActivity(detailIntent);
        });
        rvProducts.setAdapter(adapter);

        db = AppDatabase.getDatabase(this);
        loadProducts();
    }

    private void loadProducts() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Product> products;
            if (categoryId != -1) {
                products = db.productDao().getProductsByCategory(categoryId);
            } else {
                products = db.productDao().getAllProducts();
            }

            runOnUiThread(() -> {
                if (products != null && !products.isEmpty()) {
                    productList.clear();
                    productList.addAll(products);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Không có sản phẩm nào", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
