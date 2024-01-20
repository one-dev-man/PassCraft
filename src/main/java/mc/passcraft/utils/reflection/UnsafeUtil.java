package mc.passcraft.utils.reflection;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * /!\ WARNING /!\
 *
 * The usage of this tool is risky but also most supported and very useful.
 * The choice was made to use it to inject some modifications into NMS classes.
 */
public class UnsafeUtil {

    private static Unsafe unsafe;
    private static Object internalUnsafe;

    //

    static{
        try{
            final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);

            final Field internalUnsafeField = Unsafe.class.getDeclaredField("theInternalUnsafe");
            internalUnsafeField.setAccessible(true);
            internalUnsafe = internalUnsafeField.get(null);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    //

    public static Unsafe unsafe() { return unsafe; }

    public static Object internalUnsafe() { return internalUnsafe; }

}
