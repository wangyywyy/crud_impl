package com.yy.test;

import com.yy.DO.User;
import com.yy.handler.impl.BeanHandler;
import com.yy.handler.impl.BeanListHandler;
import com.yy.runner.QueryRunner;
import org.junit.Test;

import java.util.List;

public class CrudTest {
    @Test
    public void queryTest() {
        QueryRunner qr = new QueryRunner();
        String sql = "select * from users where id=?";
        User user = (User) qr.query(sql, new BeanHandler(User.class), 1);
        System.out.println(user);
    }

    @Test
    public void querAllTest(){
        QueryRunner qr = new QueryRunner();
        String sql = "select * from users";
        List list = (List) qr.query(sql, new BeanListHandler(User.class));
        System.out.println(list);
    }

    @Test
    public void insertTest(){
        QueryRunner qr = new QueryRunner();
        String sql = "insert into users (account, user_id) values (?, ?)";
        Object[] params = new Object[]{"beautiful", "eyy78idjne"};
        int count =  qr.insert(sql, params);
        System.out.println(count);
    }

    @Test
    public void deleteTest(){
        QueryRunner qr = new QueryRunner();
        String sql = "delete from users where id=?";
        int count =  qr.insert(sql, 4);
        System.out.println(count);
    }

    @Test
    public void updateTest(){
        QueryRunner qr = new QueryRunner();
        String sql = "update users set user_id=? where id=?";
        Object[] params = new Object[]{"1233556479837", 3};
        int count =  qr.insert(sql, params);
        System.out.println(count);
    }
}
