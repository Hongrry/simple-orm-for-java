package cn.hruit.mybatis.reflection.invoker;

import cn.hruit.mybatis.reflection.Reflector;

import java.lang.reflect.Field;

/**
 * @author HONGRRY
 * @description 设置属性调用
 * @date 2022/08/31 21:14
 **/
public class SetFieldInvoker implements Invoker {
    private final Field field;

    public SetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        try {
            field.set(target, args[0]);
        } catch (IllegalAccessException e) {
            if (Reflector.canControlMemberAccessible()) {
                field.setAccessible(true);
                field.set(target, args[0]);
            } else {
                throw e;
            }
        }
        return null;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
