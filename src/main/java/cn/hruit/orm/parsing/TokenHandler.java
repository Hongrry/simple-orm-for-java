package cn.hruit.orm.parsing;

/**
 * 记号处理器
 *
 * @author HONGRRY
 */
public interface TokenHandler {

    /**
     * 处理标记
     *
     * @param content 待处理内容
     * @return 处理后的内容
     */
    String handleToken(String content);

}
