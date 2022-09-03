package cn.hruit.mybatis.builder.xml;

import cn.hruit.mybatis.builder.BaseBuilder;
import cn.hruit.mybatis.datasource.DataSourceFactory;
import cn.hruit.mybatis.io.Resources;
import cn.hruit.mybatis.mapping.Environment;
import cn.hruit.mybatis.session.Configuration;
import cn.hruit.mybatis.transaction.TransactionFactory;
import cn.hruit.mybatis.type.TypeAliasRegistry;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author HONGRRY
 * @description XML配置构造器
 * @date 2022/08/23 11:19
 **/
public class XmlConfigBuilder extends BaseBuilder {
    private final static Pattern PARAMETER_PATTERN = Pattern.compile("(#\\{(.*?)})");

    private Element root;

    public XmlConfigBuilder(Reader reader) {
        super(new Configuration());
        // 处理xml配置文件，读取配置
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new InputSource(reader));
            root = document.getRootElement();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析配置文件
     *
     * @return 会话配置
     */
    public Configuration parse() {
        try {
            typeAliasesElement(root.element("typeAliases"));
            environmentsElement(root.element("environments"));
            mapperElement(root.element("mappers"));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
        return configuration;
    }

    private void typeAliasesElement(Element typeAliases) {
        TypeAliasRegistry registry = configuration.getTypeAliasRegistry();
        for (Element element : typeAliases.elements()) {
            String name = element.getName();
            if ("package".equals(name)) {
                String packageName = element.attributeValue("name");
                registry.registerAliases(packageName);
            } else {
                String type = element.attributeValue("type");
                String alias = element.attributeValue("alias");
                try {
                    Class<?> clazz = Resources.classForName(type);
                    if (alias == null) {
                        typeAliasRegistry.registerAlias(clazz);
                    } else {
                        typeAliasRegistry.registerAlias(alias, clazz);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Error registering typeAlias for '" + alias + "'. Cause: " + e, e);
                }
            }
        }
    }


    private void environmentsElement(Element environments) throws Exception {
        // 使用默认环境
        String defaultEnvName = environments.attributeValue("default");
        List<Element> environmentList = environments.elements("environment");
        for (Element environment : environmentList) {
            String id = environment.attributeValue("id");
            if (defaultEnvName.equals(id)) {
                TransactionFactory txFactory = transactionManagerElement(environment.element("transactionManager"));
                DataSourceFactory dsFactory = dataSourceElement(environment.element("dataSource"));

                // 构建环境
                Environment env = new Environment.Builder(id)
                        .transactionFactory(txFactory)
                        .dataSource(dsFactory.getDataSource())
                        .build();
                configuration.setEnvironment(env);
                break;
            }
        }
    }

    private DataSourceFactory dataSourceElement(Element context) throws InstantiationException, IllegalAccessException {
        TypeAliasRegistry aliasRegistry = configuration.getTypeAliasRegistry();
        String type = context.attributeValue("type");
        DataSourceFactory dsFactory = (DataSourceFactory) aliasRegistry.resolveAlias(type).newInstance();
        // 解析属性
        Properties props = new Properties();
        List<Element> propertyList = context.elements("property");
        for (Element property : propertyList) {
            String name = property.attributeValue("name");
            String value = property.attributeValue("value");
            props.setProperty(name, value);
        }

        dsFactory.setProperties(props);
        return dsFactory;
    }

    private TransactionFactory transactionManagerElement(Element context) throws Exception {
        TypeAliasRegistry aliasRegistry = configuration.getTypeAliasRegistry();
        String type = context.attributeValue("type");
        return (TransactionFactory) aliasRegistry.resolveAlias(type).newInstance();
    }

    /**
     * <mappers>
     * <mapper resource="mapper/User_Mapper.xml"/>
     * </mappers>
     *
     * @param mappers mappers
     * @throws Exception exception
     */
    private void mapperElement(Element mappers) throws Exception {
        List<Element> mapperList = mappers.elements("mapper");
        for (Element mapper : mapperList) {
            // package 和 class 怎么加载SQL语句
            String resource = mapper.attributeValue("resource");
            InputStream inputStream = Resources.getResourceAsStream(resource);
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource);
            mapperParser.parse();
        }
    }
}
