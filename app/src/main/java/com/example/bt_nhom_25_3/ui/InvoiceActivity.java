package com.example.bt_nhom_25_3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bt_nhom_25_3.MainActivity;
import com.example.bt_nhom_25_3.R;
import java.text.DecimalFormat;

public class InvoiceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        TextView tvOrderId = findViewById(R.id.tvInvoiceOrderId);
        TextView tvTotal = findViewById(R.id.tvInvoiceTotal);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        int orderId = getIntent().getIntExtra("ORDER_ID", -1);
        double totalAmt = getIntent().getDoubleExtra("TOTAL_AMT", 0);

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        tvOrderId.setText("Mã hóa đơn: #" + orderId);
        tvTotal.setText("Đã thanh toán: " + formatter.format(totalAmt) + " VNĐ");

        btnBackHome.setOnClickListener(v -> {
            // Quay về MainActivity và xoá ngăn xếp activity cũ
            Intent intent = new Intent(InvoiceActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
