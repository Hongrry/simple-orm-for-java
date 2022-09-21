package cn.hruit.mybatis.test.plugin;

import cn.hruit.mybatis.executor.Executor;
import cn.hruit.mybatis.plugin.Interceptor;
import cn.hruit.mybatis.plugin.Intercepts;
import cn.hruit.mybatis.plugin.Invocation;
import cn.hruit.mybatis.plugin.Signature;

import java.util.Properties;

/**
 * @author HONGRRY
 * @description
 * @date 2022/09/21 11:27
 **/
@Intercepts({@Signature(
        type = Executor.class,
        method = "commit",
        args = {boolean.class})})
public class TestPlugin implements Interceptor {
    private Properties props = new Properties();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("before");
        Object result = invocation.proceed();
        System.out.println("after");
        return result;
    }

    @Override
    public void setProperties(Properties properties) {
        this.props = properties;
    }
}
