package com.yy.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetHandler<T> {
    /**
     * 处理结果集，转化为对应类型的bean
     * @param rs
     * @return
     */
    T handler(ResultSet rs) throws SQLException;
}
