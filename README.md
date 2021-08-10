# crud_impl
采用DBUtils的设计思路：

1.   QueryRunner：执行数据库操作CRUD

     -   获取数据库连接
     -   PreparedStatement创建和参数填充
     -   执行查询，获取结果集
     -   调用ResultSetHandler执行结果集处理

2.   ResultSetHandler：结果集处理器，调用BeanProcessor去实现结果集转换。

     实现类：

     -   BeanHandler
     -   BeanListHandler

3.   BeanProcessor：结果集封装为相应Bean对象。

     主要方法：

     -   toBean()
     -   toBeanList()
