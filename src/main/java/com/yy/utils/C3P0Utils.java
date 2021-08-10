package com.yy.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class C3P0Utils {
    private static ComboPooledDataSource ds;

    static {
        // 获取特性名称的连接池，mysql连接池配置在c3p0-config.xml文件中
        ds = new ComboPooledDataSource("mysql");
    }

    public static Connection getConnection() throws SQLException {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new SQLException("获取数据库连接出错：" + e);
        }
    }

    public static void closeAll(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
        // 先开后关闭
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                throw new SQLException(e);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    throw new SQLException(e);
                }
            }
        }

    }

}
