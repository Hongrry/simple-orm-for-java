package cn.hruit.orm.executor.result;

import cn.hruit.orm.reflection.factory.ObjectFactory;
import cn.hruit.orm.session.ResultContext;
import cn.hruit.orm.session.ResultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HONGRRY
 * @description
 * @date 2022/09/10 20:33
 **/
public class DefaultResultHandler implements ResultHandler {

    private final List<Object> list;

    public DefaultResultHandler() {
        this.list = new ArrayList<>();
    }

    /**
     * 通过 ObjectFactory 反射工具类，产生特定的 List
     */
    @SuppressWarnings("unchecked")
    public DefaultResultHandler(ObjectFactory objectFactory) {
        this.list = objectFactory.create(List.class);
    }

    @Override
    public void handleResult(ResultContext context) {
        list.add(context.getResultObject());
    }

    public List<Object> getResultList() {
        return list;
    }
}
