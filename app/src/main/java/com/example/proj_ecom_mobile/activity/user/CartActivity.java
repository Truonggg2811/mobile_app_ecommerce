package com.example.proj_ecom_mobile.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proj_ecom_mobile.R;
import com.example.proj_ecom_mobile.adapter.CartAdapter;
import com.example.proj_ecom_mobile.database.SQLHelper;
import com.example.proj_ecom_mobile.model.CartItem;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerCart;
    private TextView txtTotalPrice;
    private Button btnCheckout;
    private ImageView btnBack;
    private SQLHelper sqlHelper;
    private ArrayList<CartItem> cartList;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        sqlHelper = new SQLHelper(this);

        initView();
        loadCartData();

        btnBack.setOnClickListener(v -> finish());

        btnCheckout.setOnClickListener(v -> {
            if (cartList.size() > 0) {
                // Chuyển sang trang Thanh Toán (Code sau)
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        recyclerCart = findViewById(R.id.recycler_cart);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        btnCheckout = findViewById(R.id.btn_checkout);
        btnBack = findViewById(R.id.btn_back);
    }

    private void loadCartData() {
        cartList = sqlHelper.getCartItems();
        adapter = new CartAdapter(this, cartList);
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerCart.setAdapter(adapter);
        updateTotalPrice();
    }

    public void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartList) {
            total += item.getProductPrice() * item.getQuantity();
        }
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        txtTotalPrice.setText(formatter.format(total) + "đ");
    }
}