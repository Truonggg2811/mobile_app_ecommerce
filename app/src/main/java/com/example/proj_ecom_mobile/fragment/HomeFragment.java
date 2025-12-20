package com.example.proj_ecom_mobile.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private ArrayList<Product> originalList;
    private FirebaseFirestore db;

    private Button btnAll, btnAo, btnQuan, btnKhoac, btnPK;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_products);
        btnCart = view.findViewById(R.id.btn_cart_home);

        btnAll = view.findViewById(R.id.cate_all);
        btnAo = view.findViewById(R.id.cate_ao);
        btnQuan = view.findViewById(R.id.cate_quan);
        btnKhoac = view.findViewById(R.id.cate_khoac);
        btnPK = view.findViewById(R.id.cate_pk);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productList = new ArrayList<>();
        originalList = new ArrayList<>();
        adapter = new ProductAdapter(getContext(), productList);
        recyclerView.setAdapter(adapter);

        loadProductsFromFirebase();

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CartActivity.class);
            startActivity(intent);
        });

        btnAll.setOnClickListener(v -> filterCategory("ALL", btnAll));
        btnAo.setOnClickListener(v -> filterCategory("Ao", btnAo));
        btnQuan.setOnClickListener(v -> filterCategory("Quan", btnQuan));
        btnKhoac.setOnClickListener(v -> filterCategory("AoKhoac", btnKhoac));
        btnPK.setOnClickListener(v -> filterCategory("PhuKien", btnPK));

        return view;
    }

    private void loadProductsFromFirebase() {
        db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        productList.clear();
                        originalList.clear();
                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            Product p = d.toObject(Product.class);
                            if (p != null) {
                                p.setId(d.getId());
                                productList.add(p);
                                originalList.add(p);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void filterCategory(String code, Button selectedBtn) {
        updateButtonUI(selectedBtn);

        if (code.equals("ALL")) {
            adapter.updateList(originalList);
            return;
        }

        ArrayList<Product> filteredList = new ArrayList<>();
        for (Product p : originalList) {
            if (p.getCategory() != null && p.getCategory().equals(code)) {
                filteredList.add(p);
            }
        }
        adapter.updateList(filteredList);
    }

    private void updateButtonUI(Button selected) {
        Button[] buttons = {btnAll, btnAo, btnQuan, btnKhoac, btnPK};
        for (Button btn : buttons) {
            btn.setBackgroundColor(Color.parseColor("#EEEEEE"));
            btn.setTextColor(Color.BLACK);
        }
        selected.setBackgroundColor(Color.BLACK);
        selected.setTextColor(Color.WHITE);
    }
}