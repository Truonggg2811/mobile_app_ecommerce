package com.example.proj_ecom_mobile.activity.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.proj_ecom_mobile.R;
import com.example.proj_ecom_mobile.model.Product;
import java.text.DecimalFormat;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgDetail, btnBack;
    private TextView txtName, txtPrice, txtDesc;
    private Button btnAddToCart;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        imgDetail = findViewById(R.id.detail_image);
        btnBack = findViewById(R.id.btn_back);
        txtName = findViewById(R.id.detail_name);
        txtPrice = findViewById(R.id.detail_price);
        txtDesc = findViewById(R.id.detail_desc);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);

        if (getIntent().hasExtra("product_item")) {
            product = (Product) getIntent().getSerializableExtra("product_item");

            txtName.setText(product.getName());
            txtDesc.setText(product.getDescription());

            DecimalFormat formatter = new DecimalFormat("###,###,###");
            txtPrice.setText(formatter.format(product.getPrice()) + "đ");

            Glide.with(this).load(product.getImageUrl()).into(imgDetail);
        }

        btnBack.setOnClickListener(v -> finish());

        btnAddToCart.setOnClickListener(v -> {
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }
}