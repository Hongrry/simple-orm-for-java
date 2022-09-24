package cn.hruit.mybatis.session;

/**
 * 会话工厂
 *
 * @author HONGRRY
 */
public interface SqlSessionFactory {
    /**
     * 开启会话
     *
     * @return sqlSession
     */
    SqlSession openSession();

    /**
     * 开启会话
     *
     * @param autoCommit 是否自动提交
     * @return SqlSession
     */
    SqlSession openSession(boolean autoCommit);

    /**
     * 获取全局配置
     *
     * @return 配置
     */
    Configuration getConfiguration();
}
