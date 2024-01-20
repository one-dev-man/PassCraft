package mc.passcraft.utils.reflection;

import mc.passcraft.types.Filter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodsUtil {

    public static class Super {

        public static Method get(Object target, String method_name) {
            return get(target.getClass(), method_name);
        }

        public static Method get(Object target, String method_name, boolean declared) {
            return get(target.getClass(), method_name, declared);
        }

        public static Method get(Class<? extends Object> target_class, String method_name) {
            Class<? extends Object> super_class = target_class.getSuperclass();
            if(super_class == null)
                return null;

            return MethodsUtil.get(super_class, method_name);
        }

        public static Method get(Class<? extends Object> target_class, String method_name, boolean declared) {
            Class<? extends Object> super_class = target_class.getSuperclass();
            if(super_class == null)
                return null;

            return MethodsUtil.get(super_class, method_name, declared);
        }

        //

        public static <T> T invoke(Object target, String method_name, Object... args) throws InvocationTargetException, IllegalAccessException {
            return (T) MethodsUtil.invoke(target, get(target, method_name), args);
        }

    }

    public static Method get(Object target, String method_name) {
        return get(target.getClass(), method_name);
    }

    public static Method get(Object target, String method_name, boolean declared) {
        return get(target.getClass(), method_name, declared);
    }

    public static Method get(Class<?> target_class, String method_name) {
        Method method = get(target_class, method_name, false);

        if(method == null)
            method = get(target_class, method_name, true);

        return method;
    }

    public static Method get(Class<?> target_class, String method_name, boolean declared) {
        Method result = null;

        Method[] methods = declared ? target_class.getDeclaredMethods() : target_class.getMethods();

        Method m;
        for(int i = 0; i < methods.length && result == null; ++i) {
            m = methods[i];
            if(m.getName().equals(method_name))
                result = m;
        }

        return result;
    }

    //

    public static <T> T invoke(Object target, String method_name, Object... args) throws InvocationTargetException, IllegalAccessException {
        return (T) invoke(target, get(target, method_name), args);
    }

    public static <T> T invoke(Object target, Method method, Object... args) throws InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);
        return (T) method.invoke(target, args);
    }

    //

    public static List<Method> filter(Object target, Filter<Method> filter) {
        return filter(target, filter, false);
    }

    public static List<Method> filter(Object target, Filter<Method> filter, boolean declared) {
        return filter(target.getClass(), filter, declared);
    }

    public static List<Method> filter(Class<?> target_class, Filter<Method> filter) {
        return filter(target_class, filter, false);
    }

    public static List<Method> filter(Class<?> target_class, Filter<Method> filter, boolean declared) {
        List<Method> result = new ArrayList<>();

        Method[] methods = declared ? target_class.getDeclaredMethods() : target_class.getMethods();

        Method m;
        for (Method method : methods) {
            m = method;
            if (filter.filter(m))
                result.add(m);
        }

        return result;
    }

}
