package com.example.proj_ecom_mobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proj_ecom_mobile.R;
import com.example.proj_ecom_mobile.activity.user.CartActivity;
import com.example.proj_ecom_mobile.adapter.ProductAdapter;
import com.example.proj_ecom_mobile.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageView btnCart;
    private ProductAdapter adapter;
    private ArrayList<Product> productList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_products);
        btnCart = view.findViewById(R.id.btn_cart_home);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productList = new ArrayList<>();
        adapter = new ProductAdapter(getContext(), productList);
        recyclerView.setAdapter(adapter);

        loadProductsFromFirebase();

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CartActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadProductsFromFirebase() {
        db = FirebaseFirestore.getInstance();

        db.collection("products")
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
                        Toast.makeText(getContext(), "Chưa có sản phẩm nào!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeFragment", "Lỗi tải dữ liệu: " + e.getMessage());
                    Toast.makeText(getContext(), "Lỗi kết nối Server!", Toast.LENGTH_SHORT).show();
                });
    }
}