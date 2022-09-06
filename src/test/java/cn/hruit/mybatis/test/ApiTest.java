package cn.hruit.mybatis.test;

import cn.hruit.mybatis.io.Resources;
import cn.hruit.mybatis.session.SqlSession;
import cn.hruit.mybatis.session.SqlSessionFactory;
import cn.hruit.mybatis.session.SqlSessionFactoryBuilder;
import cn.hruit.mybatis.test.dao.IUserMapper;
import cn.hruit.mybatis.test.po.User;
import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

/**
 * @author HONGRRY
 * @description
 * @date 2022/08/22 13:44
 **/
public class ApiTest {
    SqlSessionFactory sqlSessionFactory;

    @Before
    public void init() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

    }

    @Test
    public void queryUserInfoById() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        System.out.println(sqlSession.getConfiguration().isMapUnderscoreToCamelCase());
        IUserMapper userMapper = sqlSession.getMapper(IUserMapper.class);
        User user = userMapper.queryUserInfoById(1L);
        System.out.println(JSON.toJSONString(user));
    }

    @Test
    public void queryUserInfoByInfo() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        IUserMapper userMapper = sqlSession.getMapper(IUserMapper.class);
        User req = new User();
        req.setId(1L);
        req.setUserId("10001");
        User user = userMapper.queryUserInfoByInfo(req);
        System.out.println(JSON.toJSONString(user));
    }
}
