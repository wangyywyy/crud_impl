package com.yy.processor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BeanProcessor<T> {

    public T toBean(Class<T> type, ResultSet rs) throws SQLException {
        // 获取bean信息
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            throw new SQLException("获取" + type.getName() + "属性失败！");
        }
        // 获取bean的属性信息
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        // 获得bean属性和resultSet列名的映射关系
        // 数组下标代表对应的属性位置，值代表列名位置
        int[] propertyToColumn = this.mapPropertyToColumn(propertyDescriptors, rs);

        // 创建并返回bean对象
        return this.createBean(propertyToColumn, type, rs, propertyDescriptors);
    }

    public <T> List<T> toBeanList(ResultSet rs, Class type) throws SQLException {
        // 获取bean信息
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            throw new SQLException("获取" + type.getName() + "属性失败！");
        }
        // 获取bean的属性信息
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        // 获得bean属性和resultSet列名的映射关系
        // 数组下标代表对应的属性位置，值代表列名位置
        int[] propertyToColumn = this.mapPropertyToColumn(propertyDescriptors, rs);

        List<T> results = new ArrayList<>();
        do {
            // 创建并返回bean对象
            results.add((T) this.createBean(propertyToColumn, type, rs, propertyDescriptors));
        } while (rs.next());

        return results;
    }

    private T createBean(int[] propertyToColumn, Class<T> type, ResultSet rs, PropertyDescriptor[] propertyDescriptors) {
        T instance = null;
        try {
            // 反射创建实例
            instance = type.newInstance();
            // 填充属性
            for (int i = 0; i < propertyToColumn.length; i++) {
                if (propertyToColumn[i] == -1) {
                    continue;
                }
                // 获取属性的类型
                Class<T> propertyType = (Class<T>) propertyDescriptors[i].getPropertyType();
                // 将rs中对应的值转换为propertyType
                Object value = this.convertToProperty(propertyType, propertyToColumn[i], rs);
//                Object value = processColumn(rs, propertyToColumn[i], propertyType);
                // 给instance填充属性
                this.fillProperties(value, propertyDescriptors[i], instance);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return (T) instance;
    }

    private void fillProperties(Object value, PropertyDescriptor descriptor, Object instance) {
        Method writeMethod = descriptor.getWriteMethod();
        Class[] params = writeMethod.getParameterTypes();
        String name = params[0].getName();

        // 日期和枚举单独处理
        // 代码来自DBUtils
        if (value instanceof Date) {
            String targetType = params[0].getName();
            if ("java.sql.Date".equals(targetType)) {
                value = new java.sql.Date(((Date) value).getTime());
            } else if ("java.sql.Time".equals(targetType)) {
                value = new Time(((Date) value).getTime());
            } else if ("java.sql.Timestamp".equals(targetType)) {
                Timestamp tsValue = (Timestamp) value;
                int nanos = tsValue.getNanos();
                value = new Timestamp(tsValue.getTime());
                ((Timestamp) value).setNanos(nanos);
            }
        } else if (value instanceof String && params[0].isEnum()) {
            value = Enum.valueOf(params[0].asSubclass(Enum.class), (String) value);
        }

        try {
            if (isCompatibleType(value, params[0])) {
                writeMethod.invoke(instance, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // DBUtils的方法
    private boolean isCompatibleType(Object value, Class<?> type) {
        if (value != null && !type.isInstance(value)) {
            if (type.equals(Integer.TYPE) && value instanceof Integer) {
                return true;
            } else if (type.equals(Long.TYPE) && value instanceof Long) {
                return true;
            } else if (type.equals(Double.TYPE) && value instanceof Double) {
                return true;
            } else if (type.equals(Float.TYPE) && value instanceof Float) {
                return true;
            } else if (type.equals(Short.TYPE) && value instanceof Short) {
                return true;
            } else if (type.equals(Byte.TYPE) && value instanceof Byte) {
                return true;
            } else if (type.equals(Character.TYPE) && value instanceof Character) {
                return true;
            } else {
                return type.equals(Boolean.TYPE) && value instanceof Boolean;
            }
        } else {
            return true;
        }
    }

    // DBUtils的方法
    /*protected Object processColumn(ResultSet rs, int index, Class<?> propType) throws SQLException {
        if (!propType.isPrimitive() && rs.getObject(index) == null) {
            return null;
        } else if (propType.equals(String.class)) {
            return rs.getString(index);
        } else if (!propType.equals(Integer.TYPE) && !propType.equals(Integer.class)) {
            if (!propType.equals(Boolean.TYPE) && !propType.equals(Boolean.class)) {
                if (!propType.equals(Long.TYPE) && !propType.equals(Long.class)) {
                    if (!propType.equals(Double.TYPE) && !propType.equals(Double.class)) {
                        if (!propType.equals(Float.TYPE) && !propType.equals(Float.class)) {
                            if (!propType.equals(Short.TYPE) && !propType.equals(Short.class)) {
                                if (!propType.equals(Byte.TYPE) && !propType.equals(Byte.class)) {
                                    if (propType.equals(Timestamp.class)) {
                                        return rs.getTimestamp(index);
                                    } else {
                                        return propType.equals(SQLXML.class) ? rs.getSQLXML(index) : rs.getObject(index);
                                    }
                                } else {
                                    return rs.getByte(index);
                                }
                            } else {
                                return rs.getShort(index);
                            }
                        } else {
                            return rs.getFloat(index);
                        }
                    } else {
                        return rs.getDouble(index);
                    }
                } else {
                    return rs.getLong(index);
                }
            } else {
                return rs.getBoolean(index);
            }
        } else {
            return rs.getInt(index);
        }
    }*/
    private Object convertToProperty(Class<T> propertyType, int i, ResultSet rs) throws SQLException {
        if (propertyType.equals(String.class)) {
            return rs.getString(i);
        } else if (propertyType.equals(Integer.class) || propertyType.equals(Integer.TYPE)) { //对于int基本类型数据，需要通过propertyType.equals(Integer.TYPE)方式去判断
            return rs.getInt(i);
        } else if (propertyType.equals(Long.class) || propertyType.equals(Long.TYPE)) {
            return rs.getLong(i);
        } else if (propertyType.equals(Short.class) || propertyType.equals(Short.TYPE)) {
            return rs.getShort(i);
        } else if (propertyType.equals(Byte.class) || propertyType.equals(Byte.TYPE)) {
            return rs.getByte(i);
        } else if (propertyType.equals(Boolean.class) || propertyType.equals(Boolean.TYPE)) {
            return rs.getBoolean(i);
        } else if (propertyType.equals(Double.class) || propertyType.equals(Double.TYPE)) {
            return rs.getDouble(i);
        } else if (propertyType.equals(Float.class) || propertyType.equals(Float.TYPE)) {
            return rs.getFloat(i);
        } else if (propertyType.equals(Timestamp.class)) {
            return rs.getTimestamp(i);
        }
        return null;
    }


    private int[] mapPropertyToColumn(PropertyDescriptor[] propertyDescriptors, ResultSet rs) throws SQLException {
        int[] propertyToColumn = new int[propertyDescriptors.length];
        Arrays.fill(propertyToColumn, -1);
        ResultSetMetaData metaData = rs.getMetaData();
        int columnNum = metaData.getColumnCount();

        for (int i = 0; i < propertyDescriptors.length; i++) {
            String propertyName = propertyDescriptors[i].getName();
            for (int j = 1; j <= columnNum; j++) {
                String columnName = metaData.getColumnName(j);
                if (columnName.equalsIgnoreCase(propertyName)) {
                    propertyToColumn[i] = j;
                }
            }
        }
        return propertyToColumn;
    }


}
