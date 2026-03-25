package com.example.bt_nhom_25_3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bt_nhom_25_3.R;
import com.example.bt_nhom_25_3.data.AppDatabase;
import com.example.bt_nhom_25_3.data.entity.Order;
import com.example.bt_nhom_25_3.data.entity.OrderDetail;
import com.example.bt_nhom_25_3.data.entity.Product;
import com.example.bt_nhom_25_3.utils.SessionManager;

import java.text.DecimalFormat;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvDetailName, tvDetailPrice, tvDetailCategory;
    private Button btnAddToOrder;

    private AppDatabase db;
    private SessionManager sessionManager;
    private int productId = -1;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        btnAddToOrder = findViewById(R.id.btnAddToOrder);

        db = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("PRODUCT_ID")) {
            productId = intent.getIntExtra("PRODUCT_ID", -1);
        }

        if (productId != -1) {
            loadProductDetails();
        } else {
            Toast.makeText(this, "Lỗi khi tải thông tin sản phẩm!", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnAddToOrder.setOnClickListener(v -> handleAddToOrder());
    }

    private void loadProductDetails() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            currentProduct = db.productDao().getProductById(productId);
            runOnUiThread(() -> {
                if (currentProduct != null) {
                    tvDetailName.setText(currentProduct.name);
                    
                    DecimalFormat formatter = new DecimalFormat("###,###,###");
                    tvDetailPrice.setText("Giá: " + formatter.format(currentProduct.price) + " VNĐ");
                    
                    tvDetailCategory.setText("Mã danh mục: " + currentProduct.categoryId);
                }
            });
        });
    }

    private void handleAddToOrder() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        if (currentProduct == null) return;

        AppDatabase.databaseWriteExecutor.execute(() -> {
            int userId = sessionManager.getUserId();
            // 1. Tìm Order hiện tại chưa thanh toán (Pending)
            Order pendingOrder = db.orderDao().getPendingOrder(userId);
            
            long orderIdToUse;
            if (pendingOrder == null) {
                // Nếu chưa có, tạo Order mới
                Order newOrder = new Order(userId, "Pending", 0, System.currentTimeMillis());
                orderIdToUse = db.orderDao().insert(newOrder);
            } else {
                orderIdToUse = pendingOrder.id;
            }

            // 2. Kiểm tra xem sản phẩm đã có trong OrderDetail của Order này chưa
            OrderDetail existingDetail = db.orderDetailDao().getOrderDetail((int) orderIdToUse, currentProduct.id);

            if (existingDetail != null) {
                // Đã có -> tăng số lượng
                existingDetail.quantity += 1;
                db.orderDetailDao().update(existingDetail);
            } else {
                // Chưa có -> thêm mới
                OrderDetail newDetail = new OrderDetail((int) orderIdToUse, currentProduct.id, 1, currentProduct.price);
                db.orderDetailDao().insert(newDetail);
            }
            
            // Note: Normally we should trigger an update for order's totalAmount here. 
            // Simple approach for this task is calculating and updating immediately, although the instruction 
            // says "Chỉ làm product detail + add to order... Ưu tiên code rõ ràng".

            runOnUiThread(() -> {
                Toast.makeText(ProductDetailActivity.this, "Đã thêm " + currentProduct.name + " vào hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
            });
        });
    }
}
