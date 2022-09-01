package cn.hruit.mybatis.test;

import cn.hruit.mybatis.reflection.DefaultReflectorFactory;
import cn.hruit.mybatis.reflection.Reflector;
import cn.hruit.mybatis.reflection.invoker.Invoker;
import cn.hruit.mybatis.test.model.Entity;
import org.junit.Test;

/**
 * @author HONGRRY
 * @description
 * @date 2022/08/31 21:20
 **/

public class ReflectTest {
    @Test
    public void reflectTest() throws Exception {
        DefaultReflectorFactory factory = new DefaultReflectorFactory();
        Reflector reflector = factory.findForClass(Entity.class);
        Invoker invoker = reflector.getSetInvoker("id");
        Entity entity = new Entity();
        invoker.invoke(entity, new Object[]{"123456"});

        System.out.println(entity);

    }
}
