package cn.hruit.orm.reflection.invoker;

import cn.hruit.orm.reflection.Reflector;

import java.lang.reflect.Method;

/**
 * @author HONGRRY
 * @description Getter/Setter方法调用
 * @date 2022/09/01 09:18
 **/
public class MethodInvoker implements Invoker {
    private final Class<?> type;
    private final Method method;

    public MethodInvoker(Method method) {
        this.method = method;
        if (method.getParameterCount() == 1) {
            // set 方法
            type = method.getParameterTypes()[0];
        } else {
            // get 方法
            type = method.getReturnType();
        }
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            if (Reflector.canControlMemberAccessible()) {
                method.setAccessible(true);
                return method.invoke(target, args);
            }
            throw e;
        }

    }

    @Override
    public Class<?> getType() {
        return type;
    }
}
