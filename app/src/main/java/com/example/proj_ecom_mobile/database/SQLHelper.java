package com.example.proj_ecom_mobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.proj_ecom_mobile.model.CartItem;
import com.example.proj_ecom_mobile.model.Product;
import java.util.ArrayList;

public class SQLHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "EcomStore.db";
    private static final int DB_VERSION = 1;

    public SQLHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Bảng lưu sản phẩm (để xem offline)
        String createProductTable = "CREATE TABLE products (id TEXT PRIMARY KEY, name TEXT, price REAL, image TEXT, description TEXT, category TEXT)";
        db.execSQL(createProductTable);

        // 2. Bảng lưu giỏ hàng (QUAN TRỌNG: để đặt hàng)
        String createCartTable = "CREATE TABLE cart (productId TEXT PRIMARY KEY, productName TEXT, productPrice REAL, productImage TEXT, quantity INTEGER)";
        db.execSQL(createCartTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS cart");
        onCreate(db);
    }

    // --- PHẦN 1: QUẢN LÝ SẢN PHẨM OFFLINE (Giữ nguyên từ file cũ của bạn) ---
    public void syncProducts(ArrayList<Product> list) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM products");
            for (Product p : list) {
                ContentValues values = new ContentValues();
                values.put("id", p.getId());
                values.put("name", p.getName());
                values.put("price", p.getPrice());
                values.put("image", p.getImageUrl());
                values.put("description", p.getDescription());
                values.put("category", p.getCategory());
                db.insert("products", null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public ArrayList<Product> getOfflineProducts() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM products", null);
        if (c.moveToFirst()) {
            do {
                Product p = new Product();
                p.setId(c.getString(0));
                p.setName(c.getString(1));
                p.setPrice(c.getDouble(2));
                p.setImageUrl(c.getString(3));
                p.setDescription(c.getString(4));
                p.setCategory(c.getString(5));
                list.add(p);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    // --- PHẦN 2: QUẢN LÝ GIỎ HÀNG (PHẦN BẠN ĐANG THIẾU) ---
    // Hàm này dùng để thêm sản phẩm vào giỏ hoặc tăng số lượng nếu đã có
    public void addToCart(CartItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra xem sản phẩm này đã có trong giỏ chưa
        Cursor cursor = db.rawQuery("SELECT * FROM cart WHERE productId = ?", new String[]{item.getProductId()});

        if (cursor.moveToFirst()) {
            // Nếu có rồi -> Cộng dồn số lượng
            int currentQty = cursor.getInt(4); // Cột quantity là cột thứ 4 (tính từ 0)
            int newQty = currentQty + item.getQuantity();
            ContentValues values = new ContentValues();
            values.put("quantity", newQty);
            db.update("cart", values, "productId = ?", new String[]{item.getProductId()});
        } else {
            // Nếu chưa có -> Thêm mới
            ContentValues values = new ContentValues();
            values.put("productId", item.getProductId());
            values.put("productName", item.getProductName());
            values.put("productPrice", item.getProductPrice());
            values.put("productImage", item.getProductImage());
            values.put("quantity", item.getQuantity());
            db.insert("cart", null, values);
        }
        cursor.close();
        db.close();
    }

    // Hàm lấy danh sách để hiển thị lên màn hình CartActivity
    public ArrayList<CartItem> getCartItems() {
        ArrayList<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM cart", null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new CartItem(
                        cursor.getString(0), // productId
                        cursor.getString(1), // productName
                        cursor.getDouble(2), // productPrice
                        cursor.getString(3), // productImage
                        cursor.getInt(4)     // quantity
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Hàm xóa giỏ hàng (Dùng khi Đặt hàng thành công)
    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM cart");
        db.close();
    }
}