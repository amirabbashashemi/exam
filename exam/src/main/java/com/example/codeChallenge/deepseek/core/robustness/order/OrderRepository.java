package com.example.codeChallenge.deepseek.core.robustness.order;

import java.sql.*;

/*
Robustness (استحکام/مقاومت در برابر خطا) - Database Connection Pool

مشکلات فعلی:
هر بار یک اتصال جدید به دیتابیس باز می‌شود (هزینه بالا)
اگر دیتابیس در دسترس نباشد، کل سیستم کرش می‌کند
هیچ Retry mechanism برای خطاهای موقت وجود ندارد
منابع (Connection, Statement, ResultSet) ممکن است در صورت خطا بسته نشوند
هیچ Connection Pool برای مدیریت اتصالات وجود ندارد
اگر دیتابیس پاسخ ندهد، ترد برای همیشه بلوکه می‌شود
 */
public class OrderRepository {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/orders";
    private static final String DB_USER = "user";
    private static final String DB_PASSWORD = "password";

    public void saveOrder(Order order) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // ۱. اتصال به دیتابیس
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // ۲. ذخیره سفارش
            String sql = "INSERT INTO orders (id, customer_id, total, status) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, order.getId());
            stmt.setString(2, order.getCustomerId());
            stmt.setDouble(3, order.getTotal());
            stmt.setString(4, order.getStatus());
            stmt.executeUpdate();

            // ۳. ذخیره محصولات
            for (Product product : order.getProducts()) {
                String productSql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)";
                PreparedStatement productStmt = conn.prepareStatement(productSql);
                productStmt.setString(1, order.getId());
                productStmt.setString(2, product.getId());
                productStmt.setInt(3, product.getQuantity());
                productStmt.executeUpdate();
                productStmt.close();
            }

            System.out.println("Order saved: " + order.getId());

        } finally {
            // ۴. بستن منابع
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public Order findOrder(String orderId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT * FROM orders WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, orderId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Order order = new Order();
                order.setId(rs.getString("id"));
                order.setCustomerId(rs.getString("customer_id"));
                order.setTotal(rs.getDouble("total"));
                order.setStatus(rs.getString("status"));
                return order;
            }
            return null;

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
}