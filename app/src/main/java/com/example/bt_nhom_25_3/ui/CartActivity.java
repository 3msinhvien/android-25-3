package com.example.bt_nhom_25_3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt_nhom_25_3.R;
import com.example.bt_nhom_25_3.data.AppDatabase;
import com.example.bt_nhom_25_3.data.entity.CartItem;
import com.example.bt_nhom_25_3.data.entity.Order;
import com.example.bt_nhom_25_3.data.entity.OrderDetail;
import com.example.bt_nhom_25_3.data.entity.Product;
import com.example.bt_nhom_25_3.ui.adapter.CartAdapter;
import com.example.bt_nhom_25_3.utils.SessionManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView rvCart;
    private TextView tvCartTotalAmount;
    private Button btnCheckout;
    private CartAdapter adapter;
    private AppDatabase db;
    private SessionManager sessionManager;
    private DecimalFormat formatter = new DecimalFormat("###,###,###");
    private double currentTotal = 0;
    private int currentOrderId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCart = findViewById(R.id.rvCart);
        tvCartTotalAmount = findViewById(R.id.tvCartTotalAmount);
        btnCheckout = findViewById(R.id.btnCheckout);

        rvCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter();
        rvCart.setAdapter(adapter);

        db = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        loadCart();

        btnCheckout.setOnClickListener(v -> processCheckout());
    }

    private void loadCart() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Order pendingOrder = db.orderDao().getPendingOrder(sessionManager.getUserId());
            if (pendingOrder == null) {
                runOnUiThread(() -> {
                    Toast.makeText(CartActivity.this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                    btnCheckout.setEnabled(false);
                });
                return;
            }
            currentOrderId = pendingOrder.id;
            List<OrderDetail> details = db.orderDetailDao().getOrderDetails(pendingOrder.id);
            List<CartItem> cartItems = new ArrayList<>();
            double totalObj = 0;

            for (OrderDetail d : details) {
                Product p = db.productDao().getProductById(d.productId);
                String name = (p != null) ? p.name : "Sản phẩm ẩn";
                cartItems.add(new CartItem(name, d.quantity, d.price));
                totalObj += d.quantity * d.price;
            }

            currentTotal = totalObj;
            runOnUiThread(() -> {
                adapter.setCartItems(cartItems);
                tvCartTotalAmount.setText("Tổng thanh toán: " + formatter.format(currentTotal) + " VNĐ");
                btnCheckout.setEnabled(cartItems.size() > 0);
            });
        });
    }

    private void processCheckout() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Order order = db.orderDao().getPendingOrder(sessionManager.getUserId());
            if (order != null) {
                order.status = "Paid";
                order.totalAmount = currentTotal;
                order.orderDate = System.currentTimeMillis();
                db.orderDao().update(order);

                runOnUiThread(() -> {
                    Toast.makeText(CartActivity.this, "Nộp tiền thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CartActivity.this, InvoiceActivity.class);
                    intent.putExtra("ORDER_ID", order.id);
                    intent.putExtra("TOTAL_AMT", currentTotal);
                    startActivity(intent);
                    finish(); // Kết thúc CartActivity
                });
            }
        });
    }
}
