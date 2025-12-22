package com.example.proj_ecom_mobile.activity.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proj_ecom_mobile.R;
import com.example.proj_ecom_mobile.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageProductActivity extends AppCompatActivity {

    private ListView listView;
    private FloatingActionButton fabAdd;
    private ImageView btnBack;
    private TextView tvTitle;
    private FirebaseFirestore db;
    private ArrayList<Product> productList;
    private ArrayList<String> displayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_list);

        db = FirebaseFirestore.getInstance();
        listView = findViewById(R.id.list_view_data);
        fabAdd = findViewById(R.id.fab_add);
        btnBack = findViewById(R.id.btn_back_admin);
        tvTitle = findViewById(R.id.tv_header_title);

        tvTitle.setText("Quản lý Sản phẩm");

        productList = new ArrayList<>();
        displayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        loadProducts();

        btnBack.setOnClickListener(v -> finish());
        fabAdd.setOnClickListener(v -> showProductDialog(null));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product p = productList.get(position);
            showOptions(p);
        });
    }

    private void loadProducts() {
        db.collection("products").addSnapshotListener((value, error) -> {
            if (error != null) return;
            productList.clear();
            displayList.clear();
            for (DocumentSnapshot doc : value) {
                Product p = doc.toObject(Product.class);
                if (p != null) {
                    p.setId(doc.getId());
                    productList.add(p);
                    displayList.add(p.getName() + "\nTổng kho: " + p.getTotalStock() + " (S:" + p.getStockS() + ", M:" + p.getStockM() + ", L:" + p.getStockL() + ", XL:" + p.getStockXL() + ")");
                }
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void showOptions(Product p) {
        String[] options = {"Sửa", "Xóa"};
        new AlertDialog.Builder(this)
                .setTitle(p.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) showProductDialog(p);
                    else deleteProduct(p.getId());
                })
                .show();
    }

    private void showProductDialog(Product p) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null);
        builder.setView(view);

        EditText edtName = view.findViewById(R.id.edt_prod_name);
        EditText edtPrice = view.findViewById(R.id.edt_prod_price);
        EditText edtImg = view.findViewById(R.id.edt_prod_img);
        EditText edtDesc = view.findViewById(R.id.edt_prod_desc);
        Spinner spnCate = view.findViewById(R.id.spn_category);

        EditText edtS = view.findViewById(R.id.edt_stock_s);
        EditText edtM = view.findViewById(R.id.edt_stock_m);
        EditText edtL = view.findViewById(R.id.edt_stock_l);
        EditText edtXL = view.findViewById(R.id.edt_stock_xl);

        String[] categories = {"Ao", "Quan", "AoKhoac", "PhuKien"};
        ArrayAdapter<String> cateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spnCate.setAdapter(cateAdapter);

        if (p != null) {
            edtName.setText(p.getName());
            edtPrice.setText(String.valueOf(p.getPrice()));
            edtImg.setText(p.getImageUrl());
            edtDesc.setText(p.getDescription());

            edtS.setText(String.valueOf(p.getStockS()));
            edtM.setText(String.valueOf(p.getStockM()));
            edtL.setText(String.valueOf(p.getStockL()));
            edtXL.setText(String.valueOf(p.getStockXL()));

            for(int i=0; i<categories.length; i++) {
                if(categories[i].equals(p.getCategory())) spnCate.setSelection(i);
            }
        }

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            try {
                String name = edtName.getText().toString();
                double price = Double.parseDouble(edtPrice.getText().toString());
                String img = edtImg.getText().toString();
                String desc = edtDesc.getText().toString();
                String cate = spnCate.getSelectedItem().toString();

                int s = getIntFromEdt(edtS);
                int m = getIntFromEdt(edtM);
                int l = getIntFromEdt(edtL);
                int xl = getIntFromEdt(edtXL);

                Map<String, Object> data = new HashMap<>();
                data.put("name", name);
                data.put("price", price);
                data.put("imageUrl", img);
                data.put("description", desc);
                data.put("category", cate);
                data.put("stockS", s);
                data.put("stockM", m);
                data.put("stockL", l);
                data.put("stockXL", xl);

                if (p == null) {
                    db.collection("products").add(data);
                } else {
                    db.collection("products").document(p.getId()).update(data);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Vui lòng nhập đúng định dạng số", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private int getIntFromEdt(EditText edt) {
        String s = edt.getText().toString();
        return s.isEmpty() ? 0 : Integer.parseInt(s);
    }

    private void deleteProduct(String id) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                .setPositiveButton("Xóa", (dialog, which) -> db.collection("products").document(id).delete())
                .setNegativeButton("Hủy", null)
                .show();
    }
}