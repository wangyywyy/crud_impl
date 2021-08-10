package com.yy.handler.impl;

import com.yy.handler.ResultSetHandler;
import com.yy.processor.BeanProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BeanHandler<T> implements ResultSetHandler {
    private Class<T> type;
    private static BeanProcessor beanProcessor = new BeanProcessor();

    public BeanHandler(Class<T> type) {
        this.type = type;
    }

    /**
     * 处理结果集，转化为type类型的bean
     *
     * @param rs
     * @return
     */
    @Override
    public T handler(ResultSet rs) throws SQLException {
        // rs.next()是指让游标向下移动一行，在关闭ResultSet之前都是继续有效的。
        // 当第一次遍历rs.next()的时候，游标位置在表的最上方，即处于一个空的位置。
        // 但是如果没有rs.next()，游标的位置则为空。会报java.sql.SQLException: Before start of result set错误
        return rs.next() ? (T) this.beanProcessor.toBean(type, rs) : null;
    }


}
