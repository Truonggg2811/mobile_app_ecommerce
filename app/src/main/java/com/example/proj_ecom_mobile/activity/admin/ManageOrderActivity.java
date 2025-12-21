package com.example.proj_ecom_mobile.activity.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proj_ecom_mobile.R;
import com.example.proj_ecom_mobile.model.Order;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ManageOrderActivity extends AppCompatActivity {

    private ListView listView;
    private FloatingActionButton fabAdd;
    private ImageView btnBack;
    private TextView tvTitle;
    private FirebaseFirestore db;
    private ArrayList<Order> orderList;
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

        tvTitle.setText("Quản lý Đơn hàng");
        fabAdd.setVisibility(View.GONE);

        orderList = new ArrayList<>();
        displayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        loadOrders();

        btnBack.setOnClickListener(v -> finish());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Order order = orderList.get(position);
            showStatusDialog(order);
        });
    }

    private void loadOrders() {
        db.collection("orders").orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    orderList.clear();
                    displayList.clear();
                    DecimalFormat formatter = new DecimalFormat("###,###,###");

                    for (DocumentSnapshot doc : value) {
                        Order o = doc.toObject(Order.class);
                        if (o != null) {
                            o.setId(doc.getId());
                            orderList.add(o);

                            String info = "Đơn: " + o.getId().substring(0, 6) + "...\n" +
                                    "KH: " + o.getUserEmail() + "\n" +
                                    "Tổng: " + formatter.format(o.getTotalPrice()) + "đ\n" +
                                    "Trạng thái: " + o.getStatus();
                            displayList.add(info);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void showStatusDialog(Order order) {
        String[] statuses = {"Đang xử lý", "Đang giao", "Đã giao", "Đã hủy"};
        new AlertDialog.Builder(this)
                .setTitle("Cập nhật trạng thái")
                .setItems(statuses, (dialog, which) -> {
                    updateStatus(order.getId(), statuses[which]);
                })
                .show();
    }

    private void updateStatus(String orderId, String newStatus) {
        db.collection("orders").document(orderId).update("status", newStatus)
                .addOnSuccessListener(v -> Toast.makeText(this, "Đã cập nhật trạng thái", Toast.LENGTH_SHORT).show());
    }
}