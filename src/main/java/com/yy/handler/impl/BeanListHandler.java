package com.yy.handler.impl;

import com.yy.handler.ResultSetHandler;
import com.yy.processor.BeanProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BeanListHandler<T> implements ResultSetHandler {
    private Class type;
    private static BeanProcessor beanProcessor =new BeanProcessor();

    public BeanListHandler(Class type) {
        this.type = type;
    }

    @Override
    public List<T> handler(ResultSet rs) throws SQLException {
        return rs.next() ? this.beanProcessor.toBeanList(rs, type) : null;
    }
}
