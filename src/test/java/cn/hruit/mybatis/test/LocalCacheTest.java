package cn.hruit.mybatis.test;

import cn.hruit.mybatis.io.Resources;
import cn.hruit.mybatis.session.SqlSession;
import cn.hruit.mybatis.session.SqlSessionFactory;
import cn.hruit.mybatis.session.SqlSessionFactoryBuilder;
import cn.hruit.mybatis.test.dao.IUserMapper;
import cn.hruit.mybatis.test.po.User;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

/**
 * @author HONGRRY
 * @description
 * @date 2022/09/24 16:11
 **/

public class LocalCacheTest {
    SqlSessionFactory factory;

    @Before
    public void init() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        factory = new SqlSessionFactoryBuilder().build(reader);

    }

    @Test
    public void showDefaultCacheConfiguration() {
        System.out.println("本地缓存范围: " + factory.getConfiguration().getLocalCacheScope());
        System.out.println("二级缓存是否被启用: " + factory.getConfiguration().isCacheEnabled());
    }

    /**
     * <setting name="localCacheScope" value="SESSION"/>
     * <setting name="cacheEnabled" value="true"/>
     * 用例：在同一个会话且缓存作用域为会话的情况下，除了第一次以外，都命中缓存
     *
     * @throws Exception
     */
    @Test
    public void testLocalCache() throws Exception {
        SqlSession sqlSession = factory.openSession(true); // 自动提交事务
        IUserMapper userMapper = sqlSession.getMapper(IUserMapper.class);

        System.out.println(userMapper.queryUserInfoById(1L));
        System.out.println(userMapper.queryUserInfoById(1L));
        System.out.println(userMapper.queryUserInfoById(1L));

    }

    /**
     * <setting name="localCacheScope" value="SESSION"/>
     * <setting name="cacheEnabled" value="true"/>
     * 用例：在同一个会话且缓存作用域为会话的情况下，执行更新操作会使缓存失效
     *
     * @throws Exception
     */
    @Test
    public void testLocalCacheClear() throws Exception {
        SqlSession sqlSession = factory.openSession(true); // 自动提交事务
        IUserMapper userMapper = sqlSession.getMapper(IUserMapper.class);

        System.out.println(userMapper.queryUserInfoById(1L));
        System.out.println("增加了" + userMapper.insertUserInfo(new User()) + "个学生");
        System.out.println(userMapper.queryUserInfoById(1L));

    }

    /**
     * <setting name="localCacheScope" value="SESSION"/>
     * <setting name="cacheEnabled" value="true"/>
     * 用例：在两个会话都存在缓存的情况下，一个会话更新了数据，另一个缓存没有被更新，会出现脏读
     *
     * @throws Exception
     */
    @Test
    public void testLocalCacheScope() throws Exception {
        SqlSession sqlSession1 = factory.openSession(true); // 自动提交事务
        SqlSession sqlSession2 = factory.openSession(true); // 自动提交事务

        IUserMapper userMapper1 = sqlSession1.getMapper(IUserMapper.class);
        IUserMapper userMapper2 = sqlSession2.getMapper(IUserMapper.class);

        System.out.println(userMapper1.queryUserInfoById(1L));
        System.out.println(userMapper2.queryUserInfoById(1L));
        System.out.println("userMapper1修改" + userMapper1.updateUserInfo(new User(1L, "10001", "叮当猫")) + "个学生");
        System.out.println(userMapper1.queryUserInfoById(1L));
        System.out.println(userMapper2.queryUserInfoById(1L));

    }
}
