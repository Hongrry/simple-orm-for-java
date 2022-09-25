package cn.hruit.orm.test;

import cn.hruit.orm.io.Resources;
import cn.hruit.orm.session.SqlSession;
import cn.hruit.orm.session.SqlSessionFactory;
import cn.hruit.orm.session.SqlSessionFactoryBuilder;
import cn.hruit.orm.test.dao.IActivityDao;
import cn.hruit.orm.test.po.Activity;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author HONGRRY
 * @description
 * @date 2022/09/19 10:45
 **/
public class ActivityTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Test
    public void test_queryActivityById() throws IOException {
        // 1. 从SqlSessionFactory中获取SqlSession
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 2. 获取映射器对象
        IActivityDao dao = sqlSession.getMapper(IActivityDao.class);
        // 3. 测试验证
        Activity req = new Activity();
        req.setActivityId(100001L);
        Activity res = dao.queryActivityById(req);
        logger.info("测试结果：{}", JSON.toJSONString(res));
    }

}
