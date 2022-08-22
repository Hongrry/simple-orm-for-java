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
}
