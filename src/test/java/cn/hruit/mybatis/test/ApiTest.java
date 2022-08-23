package cn.hruit.mybatis.test;

import cn.hruit.mybatis.io.Resources;
import cn.hruit.mybatis.session.SqlSession;
import cn.hruit.mybatis.session.SqlSessionFactory;
import cn.hruit.mybatis.session.SqlSessionFactoryBuilder;
import cn.hruit.mybatis.test.dao.IUserMapper;

import java.io.IOException;
import java.io.Reader;

/**
 * @author HONGRRY
 * @description
 * @date 2022/08/22 13:44
 **/
public class ApiTest {
    public static void main(String[] args) throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        IUserMapper userMapper = sqlSession.getMapper(IUserMapper.class);
        System.out.println(userMapper.queryUserInfoById("134"));

    }
}
