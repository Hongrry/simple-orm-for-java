package cn.hruit.mybatis.builder;

import cn.hruit.mybatis.session.Configuration;
import cn.hruit.mybatis.type.TypeAliasRegistry;
import cn.hruit.mybatis.type.TypeHandlerRegistry;

/**
 * @author HONGRRY
 * @description
 * @date 2022/08/23 11:11
 **/
public class BaseBuilder {
    protected Configuration configuration;
    /**
     * 别名注册器在Builder里面有什么用
     */
    protected final TypeAliasRegistry typeAliasRegistry;
    protected final TypeHandlerRegistry typeHandlerRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = configuration.getTypeAliasRegistry();
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    protected Class<?> resolveAlias(String alias) {
        return typeAliasRegistry.resolveAlias(alias);
    }
}
