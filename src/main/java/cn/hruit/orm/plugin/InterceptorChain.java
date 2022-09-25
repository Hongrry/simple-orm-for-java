package cn.hruit.orm.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author HONGRRY
 * @description 拦截链
 * @date 2022/09/21 10:20
 **/
public class InterceptorChain {
    private final List<Interceptor> interceptors = new ArrayList<>();

    public Object pluginAll(Object target) {
        for (Interceptor interceptor : interceptors) {
            target = interceptor.plugin(target);
        }
        return target;
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public List<Interceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }
}
