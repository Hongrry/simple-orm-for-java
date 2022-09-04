package cn.hruit.mybatis.type;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * JDBC类型枚举
 *
 * @author HONGRRY
 */
public enum JdbcType {

    /**
     * 整数类型
     */
    INTEGER(Types.INTEGER),
    /**
     * 单精度浮点数
     */
    FLOAT(Types.FLOAT),
    /**
     * 双精度浮点数
     */
    DOUBLE(Types.DOUBLE),
    /**
     * 十进制数
     */
    DECIMAL(Types.DECIMAL),
    /**
     * 动态字符串
     */
    VARCHAR(Types.VARCHAR),
    /**
     * 字符
     */
    CHAR(Types.CHAR),
    /**
     * 时间戳
     */
    TIMESTAMP(Types.TIMESTAMP),
    /**
     *
     */
    OTHER(Types.OTHER);
    public final int TYPE_CODE;
    private static Map<Integer, JdbcType> codeLookup = new HashMap<>();

    // 就将数字对应的枚举型放入 HashMap
    static {
        for (JdbcType type : JdbcType.values()) {
            codeLookup.put(type.TYPE_CODE, type);
        }
    }

    JdbcType(int code) {
        this.TYPE_CODE = code;
    }

    public static JdbcType forCode(int code) {
        return codeLookup.get(code);
    }

}
