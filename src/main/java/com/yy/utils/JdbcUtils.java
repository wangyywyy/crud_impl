package com.yy.utils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class JdbcUtils {
    private static Properties props;

    /**
     * 加载数据库驱动
     */
    static {
        props = new Properties();

        // NoClassDefFoundError
         InputStream in = JdbcUtils.class.getClassLoader().getResourceAsStream("jdbc.properties");
//        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.properties");
        try {
            props.load(in);
            Class.forName(props.getProperty("driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public static DataSource getDataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return JdbcUtils.getConnection();
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return null;
            }

            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                return null;
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                return false;
            }

            @Override
            public PrintWriter getLogWriter() throws SQLException {
                return null;
            }

            @Override
            public void setLogWriter(PrintWriter out) throws SQLException {

            }

            @Override
            public void setLoginTimeout(int seconds) throws SQLException {

            }

            @Override
            public int getLoginTimeout() throws SQLException {
                return 0;
            }

            @Override
            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                return null;
            }
        };
    }

    /**
     * 获取数据库连接
     *
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(props.getProperty("url"), props.getProperty("username"), props.getProperty("password"));
    }

    public static void closeAll(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
        // 先开后关闭
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }finally {
            try {
                if (stmt != null){
                    stmt.close();
                }
            } catch (SQLException e) {
                throw new SQLException(e);
            }finally {
                try {
                    if (conn != null){
                        conn.close();
                    }
                } catch (SQLException e) {
                    throw new SQLException(e);
                }
            }
        }

    }
}
