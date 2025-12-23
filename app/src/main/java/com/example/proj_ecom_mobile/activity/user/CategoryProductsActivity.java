package com.example.proj_ecom_mobile.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proj_ecom_mobile.R;
import com.example.proj_ecom_mobile.adapter.ProductAdapter;
import com.example.proj_ecom_mobile.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class CategoryProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtTitle;
    private ImageView btnBack;
    private ProductAdapter adapter;
    private ArrayList<Product> productList;
    private FirebaseFirestore db;
    private String categoryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_products);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_category_products);
        txtTitle = findViewById(R.id.txt_category_title);
        btnBack = findViewById(R.id.btn_back);

        // Nhận dữ liệu từ SearchFragment
        if (getIntent().hasExtra("cat_code")) {
            categoryCode = getIntent().getStringExtra("cat_code");
            String catName = getIntent().getStringExtra("cat_name");
            txtTitle.setText(catName);
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        loadProductsByCategory();

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadProductsByCategory() {
        if (categoryCode == null) return;

        db.collection("products")
                .whereEqualTo("category", categoryCode)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        productList.clear();
                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            Product p = d.toObject(Product.class);
                            if (p != null) {
                                p.setId(d.getId());
                                productList.add(p);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Không có sản phẩm nào thuộc danh mục này", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show());
    }
}