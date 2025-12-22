package com.example.proj_ecom_mobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.proj_ecom_mobile.R;
import com.example.proj_ecom_mobile.activity.auth.LoginActivity;
import com.example.proj_ecom_mobile.activity.user.ChangePasswordActivity;
import com.example.proj_ecom_mobile.activity.user.OrderHistoryActivity;
import com.example.proj_ecom_mobile.activity.user.UserInfoActivity;
import com.example.proj_ecom_mobile.database.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvEmail;
    private LinearLayout layoutUserView, layoutGuestView;
    private LinearLayout btnOrderHistory, btnPersonalInfo, btnChangePassword;
    private Button btnLogout, btnLoginRequire;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initData();
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayUserInfo();
    }

    private void initData() {
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(getContext());
        db = FirebaseFirestore.getInstance();
    }

    private void initView(View view) {
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);

        layoutUserView = view.findViewById(R.id.layout_user_view);
        layoutGuestView = view.findViewById(R.id.layout_guest_view);

        btnPersonalInfo = view.findViewById(R.id.btn_personal_info);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnOrderHistory = view.findViewById(R.id.btn_order_history);

        btnLogout = view.findViewById(R.id.btn_logout);
        btnLoginRequire = view.findViewById(R.id.btn_login_require);

        btnLoginRequire.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));

        btnOrderHistory.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) startActivity(new Intent(getContext(), OrderHistoryActivity.class));
        });

        btnPersonalInfo.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) startActivity(new Intent(getContext(), UserInfoActivity.class));
        });

        btnChangePassword.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                startActivity(new Intent(getContext(), ChangePasswordActivity.class));
            } else {
                Toast.makeText(getContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            sessionManager.logoutUser();
            Toast.makeText(getContext(), "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
            displayUserInfo();
        });
    }

    private void displayUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            layoutUserView.setVisibility(View.VISIBLE);
            layoutGuestView.setVisibility(View.GONE);
            tvEmail.setText(user.getEmail());

            db.collection("Users").document(user.getUid()).get().addOnSuccessListener(doc -> {
                if(doc.exists()) {
                    String name = doc.getString("name");
                    if (name != null && !name.isEmpty()) {
                        tvUsername.setText(name);
                    } else {
                        tvUsername.setText("Thành viên");
                    }
                }
            });
        } else {
            layoutUserView.setVisibility(View.GONE);
            layoutGuestView.setVisibility(View.VISIBLE);
            tvUsername.setText("Khách");
            tvEmail.setText("Chưa đăng nhập");
        }
    }
}