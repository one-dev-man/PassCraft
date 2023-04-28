package org.onedevman.mc.plugins.passcraft.utils.reflection;

import org.onedevman.mc.plugins.passcraft.PluginMain;
import sun.misc.Unsafe;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class FieldsUtil {

    public interface FieldFilter {
        boolean filter(Field field) throws IllegalAccessException;
    }

    private static Unsafe unsafe;

    static{
        try{
            final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    //

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

    public static <T> void changeFinal(Object target, String fieldname, T value) throws NoSuchFieldException, IllegalAccessException {
        Field f = get(target.getClass(), fieldname);

        if(f == null)
            f = get(target.getClass(), fieldname, true);

        changeFinal(target, f, value);
    }

    public static <T> void changeFinal(Object target, Field field, T value) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);

        Object fieldBase = unsafe.staticFieldBase(field);
        long fieldOffset = unsafe.staticFieldOffset(field);

        unsafe.putObject(fieldBase, fieldOffset, value);
    }

    //

    public static List<Field> filter(Object target, FieldFilter filter) throws IllegalAccessException {
        return filter(target, filter, false);
    }

    public static List<Field> filter(Object target, FieldFilter filter, boolean declared) throws IllegalAccessException {
        return filter(target.getClass(), filter, declared);
    }

    public static List<Field> filter(Class<?> target_class, FieldFilter filter) throws IllegalAccessException {
        return filter(target_class, filter, false);
    }

    public static List<Field> filter(Class<?> target_class, FieldFilter filter, boolean declared) throws IllegalAccessException {
        List<Field> result = new ArrayList<>();

        Field[] fields = declared ? target_class.getDeclaredFields() : target_class.getFields();

        for (Field field : fields)
            if (filter.filter(field))
                result.add(field);

        return result;
    }

}
