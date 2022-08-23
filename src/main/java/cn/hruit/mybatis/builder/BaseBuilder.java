package cn.hruit.mybatis.builder;

import cn.hruit.mybatis.session.Configuration;

/**
 * @author HONGRRY
 * @description
 * @date 2022/08/23 11:11
 **/
public class BaseBuilder {
    protected Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
