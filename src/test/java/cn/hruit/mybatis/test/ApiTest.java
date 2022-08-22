package cn.hruit.mybatis.test;

import cn.hruit.mybatis.binding.MapperRegistry;
import cn.hruit.mybatis.session.SqlSession;
import cn.hruit.mybatis.session.defaults.DefaultSqlSessionFactory;
import cn.hruit.mybatis.test.dao.IDeptMapper;

/**
 * @author HONGRRY
 * @description
 * @date 2022/08/22 13:44
 **/
public class ApiTest {
    public static void main(String[] args) {
        MapperRegistry mapperRegistry = new MapperRegistry();
        mapperRegistry.addMappers("cn.hruit.mybatis.test.dao");

        DefaultSqlSessionFactory sessionFactory = new DefaultSqlSessionFactory(mapperRegistry);
        SqlSession sqlSession = sessionFactory.openSession();

        IDeptMapper mapper = sqlSession.getMapper(IDeptMapper.class);
        System.out.println(mapper.queryDeptName());
        Object hello = sqlSession.selectOne("xxxx", "Hello");
        System.out.println(hello);
    }
}
