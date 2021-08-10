package com.yy.runner;

import com.yy.handler.ResultSetHandler;
import com.yy.utils.C3P0Utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class QueryRunner {
    private Properties props;

    public QueryRunner() {
        init();
    }

    private void init() {
        props = new Properties();
        // InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.properties");
        InputStream in = QueryRunner.class.getClassLoader().getResourceAsStream("jdbc.properties");
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

    public int insert(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = this.getConnection();
            stmt = conn.prepareStatement(sql);
            this.fillStatement(stmt, params);
            return stmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                //关闭资源
                this.closeAll(conn, stmt, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    public int update(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = this.getConnection();
            stmt = conn.prepareStatement(sql);
            this.fillStatement(stmt, params);
            return stmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                //关闭资源
                this.closeAll(conn, stmt, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    public int delete(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = this.getConnection();
            stmt = conn.prepareStatement(sql);
            this.fillStatement(stmt, params);
            return stmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                //关闭资源
                this.closeAll(conn, stmt, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    /**
     * 查询数据库，使用C3P0连接池
     * @param sql
     * @param rsh
     * @param params
     * @return
     */
    public Object query(String sql, ResultSetHandler rsh, Object... params) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Object result = null;
        try {
            // 获取数据库连接
//            conn = this.getConnection();
            // 使用C3P0获取连接
            conn = C3P0Utils.getConnection();

            // 获取preparedStatement
            stmt = conn.prepareStatement(sql);

            // 添加给stmt中添加参数
            this.fillStatement(stmt, params);

            // 执行查询
            resultSet = stmt.executeQuery();

            // 处理结果集，转化为对应的bean
            result = rsh.handler(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭资源
//                this.closeAll(conn, stmt, resultSet);
                C3P0Utils.closeAll(conn, stmt, resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;

    }

    private void closeAll(Connection conn, PreparedStatement stmt, ResultSet rs) throws SQLException {
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

    private void fillStatement(PreparedStatement stmt, Object... params) throws SQLException {
        // 对比stmt中的参数数量与params中的参数数量是否相等

        ParameterMetaData pmd = stmt.getParameterMetaData();
        int count = pmd.getParameterCount();
        if (count != params.length) {
            throw new SQLException("输入的参数数量(" + params.length + ")与sql语句中的参数数量(" + count + ")不一致！");
        }

        // 设置参数
        if (params != null) {
            for (int i = 0; i < count; i++) {
                if (params[i] != null) {
                    stmt.setObject(i + 1, params[i]);
                } else {
                    stmt.setNull(i + 1, pmd.getParameterType(i + 1));
                }
            }
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(props.getProperty("url"), props.getProperty("username"), props.getProperty("password"));
    }
}
