package cn.hruit.orm.reflection.invoker;

import cn.hruit.orm.reflection.Reflector;

import java.lang.reflect.Field;

/**
 * @author HONGRRY
 * @description 获取属性调用
 * @date 2022/09/01 09:20
 **/
public class GetFieldInvoker implements Invoker {
    private final Field field;

    public GetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            if (Reflector.canControlMemberAccessible()) {
                field.setAccessible(true);
                return field.get(target);
            } else {
                throw e;
            }
        }
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
