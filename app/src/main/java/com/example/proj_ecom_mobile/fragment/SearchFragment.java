package com.example.proj_ecom_mobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proj_ecom_mobile.R;
import com.example.proj_ecom_mobile.activity.user.CategoryProductsActivity;
import com.example.proj_ecom_mobile.adapter.ProductAdapter;
import com.example.proj_ecom_mobile.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SearchFragment extends Fragment {

    private EditText edtSearch;
    private RecyclerView recyclerResults;
    private ScrollView layoutCategories;
    private ProductAdapter adapter;
    private ArrayList<Product> allProducts;

    private TextView tvTops, tvBottoms, tvOuter, tvAcc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        edtSearch = view.findViewById(R.id.edt_search);
        recyclerResults = view.findViewById(R.id.recycler_search_results);
        layoutCategories = view.findViewById(R.id.layout_categories);

        tvTops = view.findViewById(R.id.tv_cate_tops);
        tvBottoms = view.findViewById(R.id.tv_cate_bottoms);
        tvOuter = view.findViewById(R.id.tv_cate_outerwear);
        tvAcc = view.findViewById(R.id.tv_cate_acc);

        allProducts = new ArrayList<>();
        adapter = new ProductAdapter(getContext(), new ArrayList<>());
        recyclerResults.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerResults.setAdapter(adapter);

        loadAllProducts();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (keyword.isEmpty()) {
                    layoutCategories.setVisibility(View.VISIBLE);
                    recyclerResults.setVisibility(View.GONE);
                } else {
                    layoutCategories.setVisibility(View.GONE);
                    recyclerResults.setVisibility(View.VISIBLE);
                    filter(keyword);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Cập nhật sự kiện click: Chuyển sang Activity mới
        tvTops.setOnClickListener(v -> openCategoryPage("Ao", "Áo (Tops)"));
        tvBottoms.setOnClickListener(v -> openCategoryPage("Quan", "Quần (Bottoms)"));
        tvOuter.setOnClickListener(v -> openCategoryPage("AoKhoac", "Áo Khoác (Outerwear)"));
        tvAcc.setOnClickListener(v -> openCategoryPage("PhuKien", "Phụ Kiện (Accessories)"));

        return view;
    }

    private void loadAllProducts() {
        FirebaseFirestore.getInstance().collection("products").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allProducts.clear();
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        Product p = d.toObject(Product.class);
                        if (p != null) {
                            p.setId(d.getId());
                            allProducts.add(p);
                        }
                    }
                });
    }

    private void filter(String keyword) {
        ArrayList<Product> filteredList = new ArrayList<>();
        String normalizedKeyword = removeAccents(keyword.toLowerCase());

        for (Product p : allProducts) {
            String normalizedName = removeAccents(p.getName().toLowerCase());
            if (normalizedName.contains(normalizedKeyword)) {
                filteredList.add(p);
            }
        }
        adapter.updateList(filteredList);
    }

    // Hàm mới: Mở trang danh mục riêng
    private void openCategoryPage(String code, String name) {
        Intent intent = new Intent(getActivity(), CategoryProductsActivity.class);
        intent.putExtra("cat_code", code);
        intent.putExtra("cat_name", name);
        startActivity(intent);
    }

    public static String removeAccents(String str) {
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
        } catch (Exception e) {
            return str;
        }
    }
}