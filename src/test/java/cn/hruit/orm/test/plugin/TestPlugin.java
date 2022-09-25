package cn.hruit.orm.test.plugin;

import cn.hruit.orm.executor.statement.StatementHandler;
import cn.hruit.orm.plugin.Interceptor;
import cn.hruit.orm.plugin.Intercepts;
import cn.hruit.orm.plugin.Invocation;
import cn.hruit.orm.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Properties;

/**
 * @author HONGRRY
 * @description
 * @date 2022/09/21 11:27
 **/
@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class})})
public class TestPlugin implements Interceptor {
    private final Logger logger = LoggerFactory.getLogger(TestPlugin.class);
    private Properties props = new Properties();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        logger.info(handler.getBoundSql().getSql());
        Object result = invocation.proceed();
        return result;
    }

    @Override
    public void setProperties(Properties properties) {
        this.props = properties;
    }
}
