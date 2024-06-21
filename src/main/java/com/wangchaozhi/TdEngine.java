package com.wangchaozhi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class TdEngine {

    private static Connection conn;

    public static Connection getConnection() {
        return conn;
    }

    public static void setConnection(Connection newConn) {
        conn = newConn;
    }

}
