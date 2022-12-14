package cn.hruit.orm.test;

import cn.hruit.orm.io.Resources;
import cn.hruit.orm.session.SqlSession;
import cn.hruit.orm.session.SqlSessionFactory;
import cn.hruit.orm.session.SqlSessionFactoryBuilder;
import cn.hruit.orm.test.dao.IUserMapper;
import cn.hruit.orm.test.po.User;
import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.UUID;

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
    public void queryUserInfo() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        IUserMapper userMapper = sqlSession.getMapper(IUserMapper.class);
        User req = new User();
        req.setId(1L);
        req.setUserId("10001");
        User user = userMapper.queryUserInfo(req);
        System.out.println(JSON.toJSONString(user));
    }

    @Test
    public void queryUserInfoList() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        IUserMapper userMapper = sqlSession.getMapper(IUserMapper.class);
        List<User> users = userMapper.queryUserInfoList();
        for (User user : users) {
            System.out.println(JSON.toJSONString(user));
        }
    }

    @Test
    public void addUserTest() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        IUserMapper userMapper = sqlSession.getMapper(IUserMapper.class);
        User req = new User();
        req.setUserHead(UUID.randomUUID().toString().substring(0, 10));
        req.setUserName("Hongrry" + Math.random() * 100);
        req.setUserId(UUID.randomUUID().toString().replace("-", "").substring(0, 9));
        Integer integer = userMapper.insertUserInfo(req);
        System.out.println(integer);
        System.out.println(req.getId());
        sqlSession.commit();
    }

    @Test
    public void updateUserInfo() {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        IUserMapper userMapper = sqlSession.getMapper(IUserMapper.class);

        Integer integer = userMapper.updateUserInfo(new User(1L, "10001", "?????????"));
        System.out.println(integer);

    }

    @Test
    public void deleteUserInfoByUserId() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        IUserMapper userMapper = sqlSession.getMapper(IUserMapper.class);

        Integer integer = userMapper.deleteUserInfoByUserId("10002");
        System.out.println(integer);

        sqlSession.commit();
    }
}
