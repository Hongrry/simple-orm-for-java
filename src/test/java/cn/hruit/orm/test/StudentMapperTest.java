package cn.hruit.orm.test;


import cn.hruit.orm.io.Resources;
import cn.hruit.orm.session.SqlSession;
import cn.hruit.orm.session.SqlSessionFactory;
import cn.hruit.orm.session.SqlSessionFactoryBuilder;
import cn.hruit.orm.test.entity.Class;
import cn.hruit.orm.test.entity.StudentEntity;
import cn.hruit.orm.test.mapper.ClassMapper;
import cn.hruit.orm.test.mapper.StudentMapper;
import org.junit.Before;
import org.junit.Test;

public class StudentMapperTest {

    private SqlSessionFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config.xml"));

    }

    @Test
    public void showDefaultCacheConfiguration() {
        System.out.println("本地缓存范围: " + factory.getConfiguration().getLocalCacheScope());
        System.out.println("二级缓存是否被启用: " + factory.getConfiguration().isCacheEnabled());
    }

    /**
     * <setting name="localCacheScope" value="SESSION"/>
     * <setting name="cacheEnabled" value="true"/>
     * 用例：在同一个会话且缓存作用域为会话的情况下，除了第一次以外，都命中缓存
     *
     * @throws Exception
     */
    @Test
    public void testLocalCache() throws Exception {
        SqlSession sqlSession = factory.openSession(true); // 自动提交事务
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
        System.out.println(studentMapper.getStudentById(1));
        System.out.println(studentMapper.getStudentById(1));
        System.out.println(studentMapper.getStudentById(1));
        sqlSession.close();
    }

    /**
     * <setting name="localCacheScope" value="SESSION"/>
     * <setting name="cacheEnabled" value="true"/>
     * 用例：在同一个会话且缓存作用域为会话的情况下，执行更新操作会使缓存失效
     *
     * @throws Exception
     */
    @Test
    public void testLocalCacheClear() throws Exception {
        SqlSession sqlSession = factory.openSession(true); // 自动提交事务
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        System.out.println(studentMapper.getStudentById(1));
        System.out.println("增加了" + studentMapper.addStudent(buildStudent()) + "个学生");
        System.out.println(studentMapper.getStudentById(1));

        sqlSession.close();
    }

    /**
     * <setting name="localCacheScope" value="SESSION"/>
     * <setting name="cacheEnabled" value="true"/>
     * 用例：在两个会话都存在缓存的情况下，一个会话更新了数据，另一个缓存没有被更新，会出现脏读
     *
     * @throws Exception
     */
    @Test
    public void testLocalCacheScope() throws Exception {
        SqlSession sqlSession1 = factory.openSession(true); // 自动提交事务
        SqlSession sqlSession2 = factory.openSession(true); // 自动提交事务

        StudentMapper studentMapper = sqlSession1.getMapper(StudentMapper.class);
        StudentMapper studentMapper2 = sqlSession2.getMapper(StudentMapper.class);
        StudentEntity student = new StudentEntity();
        student.setName("小岑");
        student.setId(1);

        System.out.println("studentMapper读取数据: " + studentMapper.getStudentById(1));
        System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentById(1));
        System.out.println("studentMapper2更新了" + studentMapper2.updateStudentName(student) + "个学生的数据");
        System.out.println("studentMapper读取数据: " + studentMapper.getStudentById(1));
        System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentById(1));

    }


    private StudentEntity buildStudent() {
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setName("明明");
        studentEntity.setAge(20);
        return studentEntity;
    }

    /**
     * <setting name="localCacheScope" value="SESSION"/>
     * <setting name="cacheEnabled" value="true"/>
     * 用例: 二级缓存，不提交事务、不会添加缓存
     *
     * @throws Exception
     */
    @Test
    public void testCacheWithoutCommitOrClose() throws Exception {
        SqlSession sqlSession1 = factory.openSession(true); // 自动提交事务
        SqlSession sqlSession2 = factory.openSession(true); // 自动提交事务

        StudentMapper studentMapper = sqlSession1.getMapper(StudentMapper.class);
        StudentMapper studentMapper2 = sqlSession2.getMapper(StudentMapper.class);

        System.out.println("studentMapper读取数据: " + studentMapper.getStudentById(1));
        System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentById(1));

    }

    /**
     * <setting name="localCacheScope" value="SESSION"/>
     * <setting name="cacheEnabled" value="true"/>
     * 用例: 二级缓存，提交事务，会话共享缓存
     *
     * @throws Exception
     */
    @Test
    public void testCacheWithCommitOrClose() throws Exception {
        SqlSession sqlSession1 = factory.openSession(true); // 自动提交事务
        SqlSession sqlSession2 = factory.openSession(true); // 自动提交事务

        StudentMapper studentMapper = sqlSession1.getMapper(StudentMapper.class);
        StudentMapper studentMapper2 = sqlSession2.getMapper(StudentMapper.class);

        System.out.println("studentMapper读取数据: " + studentMapper.getStudentById(1));
        sqlSession1.commit();
        System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentById(1));

    }

    /**
     * <setting name="localCacheScope" value="SESSION"/>
     * <setting name="cacheEnabled" value="true"/>
     * 用例: 二级缓存，执行更新指令后，刷新缓存
     *
     * @throws Exception
     */
    @Test
    public void testCacheWithUpdate() throws Exception {
        SqlSession sqlSession1 = factory.openSession(true); // 自动提交事务
        SqlSession sqlSession2 = factory.openSession(true); // 自动提交事务
        SqlSession sqlSession3 = factory.openSession(true); // 自动提交事务


        StudentMapper studentMapper = sqlSession1.getMapper(StudentMapper.class);
        StudentMapper studentMapper2 = sqlSession2.getMapper(StudentMapper.class);
        StudentMapper studentMapper3 = sqlSession3.getMapper(StudentMapper.class);


        System.out.println("studentMapper读取数据: " + studentMapper.getStudentById(1));
        sqlSession1.close();
        System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentById(1));

        StudentEntity student = new StudentEntity();
        student.setName("方方");
        student.setId(1);

        studentMapper3.updateStudentName(student);
        sqlSession3.commit();
        System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentById(1));
    }

    /**
     * <setting name="localCacheScope" value="SESSION"/>
     * <setting name="cacheEnabled" value="true"/>
     * 用例: 二级缓存，夸命名空间缓存，会出现脏数据
     *
     * @throws Exception
     */
    @Test
    public void testCacheWithDiffererntNamespace() throws Exception {
        SqlSession sqlSession1 = factory.openSession(true); // 自动提交事务
        SqlSession sqlSession2 = factory.openSession(true); // 自动提交事务
        SqlSession sqlSession3 = factory.openSession(true); // 自动提交事务


        StudentMapper studentMapper = sqlSession1.getMapper(StudentMapper.class);
        StudentMapper studentMapper2 = sqlSession2.getMapper(StudentMapper.class);
        ClassMapper classMapper = sqlSession3.getMapper(ClassMapper.class);


        System.out.println("studentMapper读取数据: " + studentMapper.getStudentByIdWithClassInfo(1));
        sqlSession1.close();

        System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentByIdWithClassInfo(1));
        Class aClass = new Class();
        aClass.setClassName("特色一班");
        aClass.setClassId(1);

        classMapper.updateClassName(aClass);
        sqlSession3.commit();

        System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentByIdWithClassInfo(1));
    }

    /**
     * <setting name="localCacheScope" value="SESSION"/>
     * <setting name="cacheEnabled" value="true"/>
     *
     * @throws Exception
     */
    @Test
    public void testCacheWithDiffererntNamespaceWithCacheRef() throws Exception {
        SqlSession sqlSession1 = factory.openSession(true); // 自动提交事务
        SqlSession sqlSession2 = factory.openSession(true); // 自动提交事务
        SqlSession sqlSession3 = factory.openSession(true); // 自动提交事务


        StudentMapper studentMapper = sqlSession1.getMapper(StudentMapper.class);
        StudentMapper studentMapper2 = sqlSession2.getMapper(StudentMapper.class);
        ClassMapper classMapper = sqlSession3.getMapper(ClassMapper.class);


        System.out.println("studentMapper读取数据: " + studentMapper.getStudentByIdWithClassInfo(1));
        sqlSession1.close();

        System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentByIdWithClassInfo(1));

        Class aClass = new Class();
        aClass.setClassName("特色一班");
        aClass.setClassId(1);
        classMapper.updateClassName(aClass);
        sqlSession3.commit();

        System.out.println("studentMapper2读取数据: " + studentMapper2.getStudentByIdWithClassInfo(1));
    }


}