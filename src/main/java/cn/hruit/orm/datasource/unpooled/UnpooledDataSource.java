package cn.hruit.orm.datasource.unpooled;

import cn.hruit.orm.io.Resources;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author HONGRRY
 * @description 无池化数据源
 * @date 2022/08/25 10:21
 **/
public class UnpooledDataSource implements DataSource {

    /**
     * 驱动类加载器
     */
    private ClassLoader driverClassLoader;
    /**
     * 驱动配置信息
     */
    private Properties driverProperties;
    /**
     * 注册的驱动器
     */
    private static Map<String, Driver> registeredDrivers = new ConcurrentHashMap<>();

    /**
     * 驱动
     */
    private String driver;
    /**
     * 连接URL
     */
    private String url;
    /**
     * 数据库用户名
     */
    private String username;
    /**
     * 数据库密码
     */
    private String password;
    /**
     * 自动提交
     */
    private Boolean autoCommit;
    /**
     * 事务隔离级别
     */
    private Integer defaultTransactionIsolationLevel;
    /**
     * 默认网络超时时间
     */
    private Integer defaultNetworkTimeout;

    // 可能其他类加载过了驱动，或者通过SPI机制已经加载过驱动了
    static {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            registeredDrivers.put(driver.getClass().getName(), driver);
        }
    }

    public UnpooledDataSource() {
    }

    public UnpooledDataSource(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public UnpooledDataSource(String driver, String url, Properties driverProperties) {
        this.driver = driver;
        this.url = url;
        this.driverProperties = driverProperties;
    }

    public UnpooledDataSource(ClassLoader driverClassLoader, String driver, String url, String username, String password) {
        this.driverClassLoader = driverClassLoader;
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public UnpooledDataSource(ClassLoader driverClassLoader, String driver, String url, Properties driverProperties) {
        this.driverClassLoader = driverClassLoader;
        this.driver = driver;
        this.url = url;
        this.driverProperties = driverProperties;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return doGetConnection(username, password);
    }


    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return doGetConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) throws SQLException {
        DriverManager.setLogWriter(logWriter);

    }

    @Override
    public void setLoginTimeout(int loginTimeout) throws SQLException {
        DriverManager.setLoginTimeout(loginTimeout);

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    private Connection doGetConnection(String username, String password) throws SQLException {
        Properties props = new Properties();
        if (null != driverProperties) {
            props.putAll(driverProperties);
        }
        if (null != username) {
            props.put("user", username);
        }
        if (null != password) {
            props.put("password", password);
        }

        return doGetConnection(props);
    }

    private Connection doGetConnection(Properties props) throws SQLException {
        // 初始化驱动
        initializeDriver();
        // 获取连接
        Connection connection = DriverManager.getConnection(url, props);
        // 配置连接属性
        configureConnection(connection);
        return connection;
    }

    private synchronized void initializeDriver() throws SQLException {
        if (!registeredDrivers.containsKey(driver)) {
            Class<?> driverType;
            try {
                // 加载驱动
                if (driverClassLoader == null) {
                    driverType = Resources.classForName(driver);
                } else {
                    driverType = Class.forName(driver, true, driverClassLoader);
                }
                // 创建驱动实例
                Driver driverInstance = (Driver) driverType.getDeclaredConstructor().newInstance();
                // 注册驱动
                registeredDrivers.put(driver, new ProxyDriver(driverInstance));
            } catch (Exception e) {
                throw new SQLException("Error setting driver on UnpooledDataSource. Cause: " + e);
            }
        }
    }

    private void configureConnection(Connection conn) throws SQLException {
        // 配置网络超时时间
        if (null != defaultNetworkTimeout) {
            conn.setNetworkTimeout(Executors.newSingleThreadExecutor(), defaultNetworkTimeout);
        }
        // 配置连接事务隔离级别
        if (null != defaultTransactionIsolationLevel) {
            conn.setTransactionIsolation(defaultTransactionIsolationLevel);
        }
        // 配置是否自动提交
        if (null != autoCommit) {
            conn.setAutoCommit(autoCommit);
        }
    }

    public ClassLoader getDriverClassLoader() {
        return driverClassLoader;
    }

    public void setDriverClassLoader(ClassLoader driverClassLoader) {
        this.driverClassLoader = driverClassLoader;
    }

    public Properties getDriverProperties() {
        return driverProperties;
    }

    public void setDriverProperties(Properties driverProperties) {
        this.driverProperties = driverProperties;
    }

    public static Map<String, Driver> getRegisteredDrivers() {
        return registeredDrivers;
    }

    public static void setRegisteredDrivers(Map<String, Driver> registeredDrivers) {
        UnpooledDataSource.registeredDrivers = registeredDrivers;
    }

    public String getDriver() {
        return driver;
    }

    public synchronized void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public Integer getDefaultTransactionIsolationLevel() {
        return defaultTransactionIsolationLevel;
    }

    public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel) {
        this.defaultTransactionIsolationLevel = defaultTransactionIsolationLevel;
    }

    /**
     * 代理驱动
     * 为什么需要代理驱动？
     * 静态代理 封装日志
     */
    private static class ProxyDriver implements Driver {
        private Driver driver;

        public ProxyDriver(Driver driver) {
            this.driver = driver;
        }

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            return driver.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return driver.acceptsURL(url);
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return driver.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return driver.getMinorVersion();
        }

        @Override
        public boolean jdbcCompliant() {
            return driver.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() {
            return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        }
    }
}
