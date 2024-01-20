package mc.passcraft.utils.reflection;

import mc.passcraft.types.Filter;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FieldsUtil {

    public static class Types {

        public static class Parameterized {

            public static class Arguments {

                public static List<Type> all(Field field) {
                    List<Type> result = new ArrayList<>();

                    if(!(field.getGenericType() instanceof ParameterizedType))
                        return Collections.unmodifiableList(result);

                    ParameterizedType parameterized_type = (ParameterizedType) field.getGenericType();

                    Type[] argument_type_list = parameterized_type.getActualTypeArguments();

                    for(int i = 0; i < argument_type_list.length; ++i)
                        result.add(argument_type_list[i]);

                    return Collections.unmodifiableList(result);
                }

            }

        }

    }

    //

    public static Field get(Object target, String fieldname) {
        return get(target.getClass(), fieldname);
    }

    public static Field get(Object target, String fieldname, boolean declared) {
        return get(target.getClass(), fieldname, declared);
    }

    public static Field get(Class<?> target_class, String fieldname) {
        Field result = get(target_class, fieldname, false);

        if(result == null)
            result = get(target_class, fieldname, true);

        return result;
    }

    public static Field get(Class<?> target_class, String fieldname, boolean declared) {
        Field result = null;

        Field[] fields = declared ? target_class.getDeclaredFields() : target_class.getFields();

        Field field;
        for(int i = 0; i < fields.length && result == null; ++i) {
            field = fields[i];
            if(field.getName().equals(fieldname))
                result = field;
        }

        return result;
    }

    //

    public static <T> void changeFinal(Object target, String fieldname, T value) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        changeFinal(target, get(target.getClass(), fieldname), value);
    }

    public static <T> void changeFinal(Object target, Field field, T value) throws IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
//        field.setAccessible(true);
//
//        Object fieldBase = unsafe.staticFieldBase(field);
//        long fieldOffset = unsafe.staticFieldOffset(field);
//
//        unsafe.putObject(fieldBase, fieldOffset, value);

        //

        Method offset = Class.forName("jdk.internal.misc.Unsafe").getMethod("objectFieldOffset", Field.class);
        UnsafeUtil.unsafe().putBoolean(offset, 12, true);

        UnsafeUtil.unsafe().putObject(target, (long) offset.invoke(UnsafeUtil.internalUnsafe(), field), value);
    }

    //

    public static List<Field> filter(Object target, Filter<Field> filter) throws IllegalAccessException {
        return filter(target, filter, false);
    }

    public static List<Field> filter(Object target, Filter<Field> filter, boolean declared) throws IllegalAccessException {
        return filter(target.getClass(), filter, declared);
    }

    public static List<Field> filter(Class<?> target_class, Filter<Field> filter) throws IllegalAccessException {
        return filter(target_class, filter, false);
    }

    public static List<Field> filter(Class<?> target_class, Filter<Field> filter, boolean declared) throws IllegalAccessException {
        List<Field> result = new ArrayList<>();

        Field[] fields = declared ? target_class.getDeclaredFields() : target_class.getFields();

        for (Field field : fields)
            if (filter.filter(field))
                result.add(field);

        return result;
    }

}
