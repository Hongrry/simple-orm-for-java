package cn.hruit.mybatis.session;

/**
 * 结果处理器
 *
 * @author HONGRRY
 */
public interface ResultHandler {

    /**
     * 处理结果
     *
     * @param context 结果上下文
     */
    void handleResult(ResultContext context);
}
