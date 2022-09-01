package cn.hruit.mybatis.reflection.invoker;

import cn.hruit.mybatis.reflection.Reflector;

import java.lang.reflect.Field;

/**
 * @author HONGRRY
 * @description 获取属性调用
 * @date 2022/09/01 09:20
 **/
public class GetFiledInvoker implements Invoker {
    private final Field field;

    public GetFiledInvoker(Field field) {
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
