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
import com.example.proj_ecom_mobile.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class ManageUserActivity extends AppCompatActivity {

    private ListView listView;
    private FloatingActionButton fabAdd;
    private ImageView btnBack;
    private TextView tvTitle;
    private FirebaseFirestore db;
    private ArrayList<User> userList;
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

        tvTitle.setText("Quản lý Tài khoản");
        fabAdd.setVisibility(View.GONE);

        userList = new ArrayList<>();
        displayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        loadUsers();

        btnBack.setOnClickListener(v -> finish());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            User user = userList.get(position);
            showOptions(user);
        });
    }

    private void loadUsers() {
        db.collection("Users").addSnapshotListener((value, error) -> {
            if (error != null) return;
            userList.clear();
            displayList.clear();
            for (DocumentSnapshot doc : value) {
                User u = doc.toObject(User.class);
                if (u != null) {
                    u.setUid(doc.getId());
                    userList.add(u);

                    String name = (u.getName() == null || u.getName().isEmpty()) ? "Chưa đặt tên" : u.getName();
                    String role = u.getRole();
                    String roleDisplay = "admin".equals(role) ? "QUẢN TRỊ VIÊN" : "Người dùng";

                    String info = "Tên: " + name + "\nEmail: " + u.getEmail() + "\nQuyền: " + roleDisplay;
                    displayList.add(info);
                }
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void showOptions(User user) {
        String[] options = {"Đặt làm Admin", "Đặt làm User", "Xóa tài khoản"};
        new AlertDialog.Builder(this)
                .setTitle(user.getEmail())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) updateUserRole(user.getUid(), "admin");
                    else if (which == 1) updateUserRole(user.getUid(), "user");
                    else deleteUser(user.getUid());
                })
                .show();
    }

    private void updateUserRole(String uid, String role) {
        db.collection("Users").document(uid).update("role", role)
                .addOnSuccessListener(v -> Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show());
    }

    private void deleteUser(String uid) {
        new AlertDialog.Builder(this)
                .setTitle("Cảnh báo")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    db.collection("Users").document(uid).delete();
                    Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}