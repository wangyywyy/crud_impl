package com.yy.test;

import com.yy.DO.User;
import org.junit.Test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class DemoTest {
    /**
     * 写了一个Introspector类的小demo
     */
    @Test
    public void introspectorTest(){
        try {
            // Introspector类（内省操作）只针对JavaBean，只有符合JavaBean规则的类的成员才可以采用内省API
            // 获取User的所有属性、公开的方法和事件
            // 除了User的三个属性，还会包含父类Object的class属性
            BeanInfo beanInfo = Introspector.getBeanInfo(User.class);

            // 获取User的所有属性，不包括从父类的继承过来的属性，这里排除了Object的class属性
            // BeanInfo beanInfoExceptFather = Introspector.getBeanInfo(User.class, Object.class);

            // PropertyDescriptor类表示JavaBean类通过存储器导出一个属性。
            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();

            // 获取User类中每一个属性的类型名称
            for (int i = 0; i < props.length; i++) {
                // 获取属性类型名称，
                String className = props[i].getPropertyType().getName();

                // 获取写入属性的方法setXXX()
                Method method = props[i].getWriteMethod();
                if(method == null){
                    continue;
                }

                System.out.println(className + ": " + method.getName() + ": " + props[i].getName());
            }

        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
    }
}
