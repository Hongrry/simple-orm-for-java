package cn.hruit.mybatis.reflection;

import cn.hruit.mybatis.reflection.invoker.GetFiledInvoker;
import cn.hruit.mybatis.reflection.invoker.Invoker;
import cn.hruit.mybatis.reflection.invoker.MethodInvoker;
import cn.hruit.mybatis.reflection.invoker.SetFieldInvoker;
import cn.hruit.mybatis.reflection.property.PropertyNamer;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author HONGRRY
 * @description 反射器，将对象进行拆解
 * @date 2022/08/31 17:29
 **/
public class Reflector {
    private final Class<?> type;

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * get 属性列表
     */
    private String[] readablePropertyNames = EMPTY_STRING_ARRAY;
    /**
     * set 属性列表
     */
    private String[] writeablePropertyNames = EMPTY_STRING_ARRAY;
    /**
     * set 方法列表
     */
    private Map<String, Invoker> setMethods = new HashMap<>();
    /**
     * get 方法列表
     */
    private Map<String, Invoker> getMethods = new HashMap<>();
    /**
     * set 类型列表
     */
    private Map<String, Class<?>> setTypes = new HashMap<>();
    /**
     * get 类型列表
     */
    private Map<String, Class<?>> getTypes = new HashMap<>();
    /**
     * 默认构造函数
     */
    private Constructor<?> defaultConstructor;
    /**
     * 大小写格式不敏感的属性
     */
    private Map<String, String> caseInsensitivePropertyMap = new HashMap<>();

    public Reflector(Class<?> clazz) {
        this.type = clazz;
        // 加入构造函数
        Method[] methods = getClassMethods(clazz);
        addDefaultConstructor(clazz);
        // 加入 getter
        addGetMethods(methods);
        // 加入 setter
        addSetMethods(methods);
        // 加入字段属性
        addFields(clazz);
        // 添加可读属性
        readablePropertyNames = getMethods.keySet().toArray(new String[0]);
        // 添加可写属性
        writeablePropertyNames = setMethods.keySet().toArray(new String[0]);
        // 添加大小写不敏感
        for (String propName : readablePropertyNames) {
            caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
        for (String propName : writeablePropertyNames) {
            caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
    }

    private void addFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!setMethods.containsKey(field.getName())) {
                int modifiers = field.getModifiers();
                if (!(Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers))) {
                    addSetField(field);
                }
            }
            if (!getMethods.containsKey(field.getName())) {
                addGetField(field);
            }
        }
        if (clazz.getSuperclass() != null) {
            addFields(clazz.getSuperclass());
        }
    }

    private void addGetField(Field field) {
        String fieldName = field.getName();
        if (isValidPropertyName(fieldName)) {
            getMethods.put(fieldName, new GetFiledInvoker(field));
            getTypes.put(fieldName, field.getType());
        }

    }

    private void addSetField(Field field) {
        String fieldName = field.getName();
        if (isValidPropertyName(fieldName)) {
            setMethods.put(fieldName, new SetFieldInvoker(field));
            setTypes.put(fieldName, field.getType());
        }
    }

    private boolean isValidPropertyName(String name) {
        return !(name.startsWith("$") || "serialVersionUID".equals(name) || "class".equals(name));
    }

    private void addGetMethods(Method[] methods) {
        HashMap<String, List<Method>> conflictingGetters = new HashMap<>();
        // getXxx 是没有参数的
        for (Method method : methods) {
            if (method.getParameterCount() == 0) {
                String methodName = method.getName();
                if (PropertyNamer.isGetter(methodName)) {
                    addMethodConflict(conflictingGetters, PropertyNamer.methodToProperty(methodName), method);
                }
            }
        }
        resolveGetterConflicts(conflictingGetters);

    }

    private void addSetMethods(Method[] methods) {
        HashMap<String, List<Method>> conflictingSetters = new HashMap<>();
        // setXxx 只有一个参数
        for (Method method : methods) {
            if (method.getParameterCount() == 1) {
                String methodName = method.getName();
                if (PropertyNamer.isSetter(methodName)) {
                    addMethodConflict(conflictingSetters, PropertyNamer.methodToProperty(methodName), method);
                }
            }
        }
        resolveSetterConflicts(conflictingSetters);

    }

    private void resolveGetterConflicts(HashMap<String, List<Method>> conflictingGetters) {
        // boolean 类型 优先选择 isxx
        // 继承多态 优先选择超类
        for (Map.Entry<String, List<Method>> entry : conflictingGetters.entrySet()) {
            Method winner = null;
            String propName = entry.getKey();
            boolean isAmbiguous = false;
            for (Method candidate : entry.getValue()) {
                if (winner == null) {
                    winner = candidate;
                    continue;
                }
                Class<?> winnerReturnType = winner.getReturnType();
                Class<?> candidateReturnType = candidate.getReturnType();
                if (winnerReturnType.equals(candidateReturnType)) {
                    if (!(boolean.class.equals(candidateReturnType))) {
                        // 方法名相同 参数相同 返回类型不同？
                        // 非法重载
                        isAmbiguous = true;
                        break;
                    } else if (candidate.getName().startsWith("is")) {
                        // 优先选择 isXxx
                        winner = candidate;
                    }
                } else if (candidateReturnType.isAssignableFrom(winnerReturnType)) {

                } else if (winnerReturnType.isAssignableFrom(candidateReturnType)) {
                    // 选择范围更大的超类
                    winner = candidate;
                } else {
                    isAmbiguous = true;
                    break;
                }
                addGetMethod(propName, winner, isAmbiguous);
            }
        }
    }

    private void resolveSetterConflicts(HashMap<String, List<Method>> conflictingSetters) {
        // set 参数类型 必须和 set 返回类型相同
        for (Map.Entry<String, List<Method>> entry : conflictingSetters.entrySet()) {
            String propName = entry.getKey();
            Method match = null;
            Class<?> expectType = setTypes.get(propName);
            for (Method method : entry.getValue()) {
                Class<?> parameterType = method.getParameterTypes()[0];
                if (parameterType.equals(expectType)) {
                    match = method;
                    break;
                }
                match = pickBetterSetter(match, method);
            }
            if (match != null) {
                addSetMethod(propName, match);
            }
        }
    }

    private Method pickBetterSetter(Method setter1, Method setter2) {
        if (setter1 == null) {
            return setter2;
        }
        Class<?> paramType1 = setter1.getParameterTypes()[0];
        Class<?> paramType2 = setter2.getParameterTypes()[0];

        if (paramType1.isAssignableFrom(paramType2)) {
            return setter2;
        } else if (paramType2.isAssignableFrom(paramType1)) {
            return setter1;
        }
        return null;
    }

    private void addSetMethod(String name, Method method) {
        setMethods.put(name, new MethodInvoker(method));
        setTypes.put(name, method.getParameterTypes()[0]);
    }

    private void addGetMethod(String name, Method method, boolean isAmbiguous) {
        if (isAmbiguous) {
            throw new RuntimeException("Illegal overloaded getter method with ambiguous type for property "
                    + name + " in class " + method.getDeclaringClass()
                    + ".  This breaks the JavaBeans " + "specification and can cause unpredicatble results.");
        }
        // 添加 getter 方法调用
        getMethods.put(name, new MethodInvoker(method));

        // 添加 getter 返回类型
        getTypes.put(name, method.getReturnType());
    }

    private void addMethodConflict(Map<String, List<Method>> conflictingMethods, String name, Method method) {
        List<Method> methods = conflictingMethods.computeIfAbsent(name, v -> new ArrayList<>());
        methods.add(method);
    }

    private Method[] getClassMethods(Class<?> clazz) {
        HashMap<String, Method> uniqueMethods = new HashMap<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && !Object.class.equals(currentClass)) {
            addUniqueMethods(uniqueMethods, currentClass.getMethods());
            // 获取接口方法（抽象类没有实现接口）
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> anInterfaces : interfaces) {
                addUniqueMethods(uniqueMethods, anInterfaces.getMethods());
            }
            currentClass = clazz.getSuperclass();
        }
        Collection<Method> methods = uniqueMethods.values();
        return methods.toArray(new Method[0]);
    }

    private void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] methods) {
        for (Method method : methods) {
            String signature = getSignature(method);
            // 优先选择被重写的方法
            if (!uniqueMethods.containsKey(signature)) {
                uniqueMethods.put(signature, method);
            }
        }
    }

    private String getSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        Class<?> returnType = method.getReturnType();
        sb.append(returnType.getName()).append('#');
        sb.append(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            sb.append(i == 0 ? ':' : ',').append(parameters[i].getName());
        }
        return sb.toString();
    }

    private void addDefaultConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Arrays.stream(constructors)
                .filter(constructor -> constructor.getParameters().length == 0)
                .findAny()
                .ifPresent(constructor -> defaultConstructor = constructor);
    }

    public Class<?> getType() {
        return type;
    }

    public Constructor<?> getDefaultConstructor() {
        if (defaultConstructor != null) {
            return defaultConstructor;
        } else {
            throw new RuntimeException("There is no default constructor for " + type);
        }
    }

    public boolean hasDefaultConstructor() {
        return defaultConstructor != null;
    }

    public Class<?> getSetterType(String propertyName) {
        Class<?> clazz = setTypes.get(propertyName);
        if (clazz == null) {
            throw new RuntimeException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }
        return clazz;
    }

    public Invoker getGetInvoker(String propertyName) {
        Invoker method = getMethods.get(propertyName);
        if (method == null) {
            throw new RuntimeException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
        }
        return method;
    }

    public Invoker getSetInvoker(String propertyName) {
        Invoker method = setMethods.get(propertyName);
        if (method == null) {
            throw new RuntimeException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }
        return method;
    }

    /*
     * Gets the type for a property getter
     *
     * @param propertyName - the name of the property
     * @return The Class of the propery getter
     */
    public Class<?> getGetterType(String propertyName) {
        Class<?> clazz = getTypes.get(propertyName);
        if (clazz == null) {
            throw new RuntimeException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
        }
        return clazz;
    }

    /*
     * Gets an array of the readable properties for an object
     *
     * @return The array
     */
    public String[] getGetablePropertyNames() {
        return readablePropertyNames;
    }

    /*
     * Gets an array of the writeable properties for an object
     *
     * @return The array
     */
    public String[] getSetablePropertyNames() {
        return writeablePropertyNames;
    }

    /*
     * Check to see if a class has a writeable property by name
     *
     * @param propertyName - the name of the property to check
     * @return True if the object has a writeable property by the name
     */
    public boolean hasSetter(String propertyName) {
        return setMethods.keySet().contains(propertyName);
    }

    /*
     * Check to see if a class has a readable property by name
     *
     * @param propertyName - the name of the property to check
     * @return True if the object has a readable property by the name
     */
    public boolean hasGetter(String propertyName) {
        return getMethods.keySet().contains(propertyName);
    }

    public String findPropertyName(String name) {
        return caseInsensitivePropertyMap.get(name.toUpperCase(Locale.ENGLISH));
    }

    public static boolean canControlMemberAccessible() {
        try {
            SecurityManager securityManager = System.getSecurityManager();
            if (null != securityManager) {
                securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
            }
        } catch (SecurityException e) {
            return false;
        }
        return true;
    }

}
