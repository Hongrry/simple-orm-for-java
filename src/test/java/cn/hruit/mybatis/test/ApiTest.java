package cn.hruit.mybatis.test;

import cn.hruit.mybatis.io.Resources;
import cn.hruit.mybatis.session.SqlSession;
import cn.hruit.mybatis.session.SqlSessionFactory;
import cn.hruit.mybatis.session.SqlSessionFactoryBuilder;
import cn.hruit.mybatis.test.dao.IUserMapper;
import cn.hruit.mybatis.test.po.User;
import com.alibaba.fastjson.JSON;

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
        User user = userMapper.queryUserInfoById(1L);
        System.out.println(JSON.toJSONString(user));
    }
}
