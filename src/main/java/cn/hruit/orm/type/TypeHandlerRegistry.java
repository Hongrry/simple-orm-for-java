package cn.hruit.orm.type;


import java.lang.reflect.Type;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 类型处理器注册器
 *
 * @author HONGRRY
 */
public final class TypeHandlerRegistry {

    private final Map<JdbcType, TypeHandler<?>> JDBC_TYPE_HANDLER_MAP = new EnumMap<>(JdbcType.class);
    /**
     * 为什么要二维map存储处理器
     */
    private final Map<Type, Map<JdbcType, TypeHandler<?>>> TYPE_HANDLER_MAP = new HashMap<>();
    /**
     * 存储所有的类型处理器
     */
    private final Map<Class<?>, TypeHandler<?>> ALL_TYPE_HANDLERS_MAP = new HashMap<>();
    /**
     * 未知类型处理器
     */
    private final TypeHandler<Object> unknownTypeHandler;

    public TypeHandlerRegistry() {
        unknownTypeHandler = new UnknownTypeHandler();
        register(Long.class, new LongTypeHandler());
        register(long.class, new LongTypeHandler());

        register(Integer.class, new IntegerTypeHandler());
        register(int.class, new IntegerTypeHandler());

        register(Object.class, unknownTypeHandler);
        register(Object.class, JdbcType.OTHER, unknownTypeHandler);

        register(String.class, new StringTypeHandler());
        register(String.class, JdbcType.CHAR, new StringTypeHandler());
        register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
        register(Date.class, new DateTypeHandler());
    }

    private <T> void register(Type javaType, TypeHandler<? extends T> typeHandler) {
        register(javaType, null, typeHandler);
    }

    private void register(Type javaType, JdbcType jdbcType, TypeHandler<?> handler) {
        if (null != javaType) {
            Map<JdbcType, TypeHandler<?>> map = TYPE_HANDLER_MAP.computeIfAbsent(javaType, k -> new HashMap<>());
            map.put(jdbcType, handler);
        }
        ALL_TYPE_HANDLERS_MAP.put(handler.getClass(), handler);
    }

    public <T> TypeHandler<T> getTypeHandler(Class<T> type, JdbcType jdbcType) {
        return getTypeHandler((Type) type, jdbcType);
    }

    public boolean hasTypeHandler(Class<?> javaType) {
        return hasTypeHandler(javaType, null);
    }

    public boolean hasTypeHandler(Class<?> javaType, JdbcType jdbcType) {
        return javaType != null && getTypeHandler((Type) javaType, jdbcType) != null;
    }

    private <T> TypeHandler<T> getTypeHandler(Type type, JdbcType jdbcType) {
        Map<JdbcType, TypeHandler<?>> jdbcHandlerMap = TYPE_HANDLER_MAP.get(type);
        TypeHandler<?> handler = null;
        if (jdbcHandlerMap != null) {
            handler = jdbcHandlerMap.get(jdbcType);
            if (handler == null) {
                handler = jdbcHandlerMap.get(null);
            }
        }
        // type drives generics here
        return (TypeHandler<T>) handler;
    }

    public TypeHandler<?> getMappingTypeHandler(Class<? extends TypeHandler<?>> typeHandlerType) {
        return ALL_TYPE_HANDLERS_MAP.get(typeHandlerType);
    }
}