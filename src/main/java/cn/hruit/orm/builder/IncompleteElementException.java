package cn.hruit.orm.builder;

/**
 * @author HONGRRY
 * @description 未完成异常（条件不满足）
 * @date 2022/09/25 22:01
 **/
public class IncompleteElementException extends RuntimeException {
    private static final long serialVersionUID = -3697292286890900315L;

    public IncompleteElementException() {
        super();
    }

    public IncompleteElementException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncompleteElementException(String message) {
        super(message);
    }

    public IncompleteElementException(Throwable cause) {
        super(cause);
    }

}
