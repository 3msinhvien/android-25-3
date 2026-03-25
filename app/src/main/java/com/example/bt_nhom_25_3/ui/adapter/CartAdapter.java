package com.example.bt_nhom_25_3.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt_nhom_25_3.R;
import com.example.bt_nhom_25_3.data.entity.CartItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> list = new ArrayList<>();
    private DecimalFormat formatter = new DecimalFormat("###,###,###");

    public void setCartItems(List<CartItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = list.get(position);
        holder.tvName.setText(item.productName);
        holder.tvQuantity.setText("SL: " + item.quantity);
        holder.tvPrice.setText("Đơn giá: " + formatter.format(item.price) + " đ");
        holder.tvSubTotal.setText("Thành tiền: " + formatter.format(item.subTotal) + " đ");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvPrice, tvSubTotal;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCartProductName);
            tvQuantity = itemView.findViewById(R.id.tvCartQuantity);
            tvPrice = itemView.findViewById(R.id.tvCartPrice);
            tvSubTotal = itemView.findViewById(R.id.tvCartSubTotal);
        }
    }
}
