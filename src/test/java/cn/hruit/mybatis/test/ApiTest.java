package cn.hruit.mybatis.test;

import cn.hruit.mybatis.binding.MapperProxyFactory;
import cn.hruit.mybatis.test.dao.IUserMapper;

import java.util.HashMap;

/**
 * @author HONGRRY
 * @description
 * @date 2022/08/22 13:44
 **/
public class ApiTest {
    public static void main(String[] args) {
        HashMap<String, String> sqlSession = new HashMap<>();
        sqlSession.put("cn.hruit.mybatis.test.dao.IUserMapper.queryUserName", "模拟执行 Mapper.xml 中 SQL 语句的操作：查询用户姓名");
        sqlSession.put("cn.hruit.mybatis.test.dao.IUserMapper.queryUserAge", "模拟执行 Mapper.xml 中 SQL 语句的操作：查询用户年龄");


        MapperProxyFactory<IUserMapper> factory = new MapperProxyFactory<>(sqlSession, IUserMapper.class);
        IUserMapper IUserMapper = factory.newInstance();
        System.out.println(IUserMapper.queryUserAge());
        System.out.println(IUserMapper.queryUserName());
    }
}
