package cn.hruit.orm.executor.keygen;

import cn.hruit.orm.executor.Executor;
import cn.hruit.orm.mapping.MappedStatement;

import java.sql.Statement;

/**
 * @author HONGRRY
 * @description 不使用键值生成器
 * @date 2022/09/18 15:43
 **/
public class NoKeyGenerator implements KeyGenerator {
    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // Do Nothing

    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // Do Nothing

    }
}
