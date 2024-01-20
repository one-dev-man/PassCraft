package mc.passcraft.nms;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import mc.passcraft.utils.reflection.FieldsUtil;
import mc.passcraft.utils.reflection.MethodsUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

public class NMS {

    public static DedicatedServer getNativeServer() throws InvocationTargetException, IllegalAccessException {
        return getNativeServer(Bukkit.getServer());
    }

    public static DedicatedServer getNativeServer(Server server) throws InvocationTargetException, IllegalAccessException {
        return MethodsUtil.invoke(server, "getServer");
    }

    public static class NativeServer {

        public static net.minecraft.server.Services getServices() throws InvocationTargetException, IllegalAccessException {
            return getServices(getNativeServer());
        }

        public static net.minecraft.server.Services getServices(DedicatedServer nativeServer) throws IllegalAccessException {
            List<Field> candidate_fields_services = FieldsUtil.filter(
                    nativeServer.getClass().getSuperclass(),
                    field -> {
                        int modifiers = field.getModifiers();
                        return Modifier.isProtected(modifiers) && Modifier.isFinal(modifiers) && field.getType().equals(net.minecraft.server.Services.class);
                    },
                    true
            );
            Field field_services = candidate_fields_services.size() == 1 ? candidate_fields_services.get(0) : null;

            if(field_services == null) throw new RuntimeException("Field `services` not found.");

            field_services.setAccessible(true);
            return (net.minecraft.server.Services) field_services.get(nativeServer);
        }

        public static class Services {

            private static Field getSessionServiceField(net.minecraft.server.Services services) throws IllegalAccessException {
                List<Field> candidate_fields_sessionService = FieldsUtil.filter(services, f -> f.getType().equals(MinecraftSessionService.class), true);
                return candidate_fields_sessionService.size() == 1 ? candidate_fields_sessionService.get(0) : null;
            }

            //

            public static MinecraftSessionService getSessionService() throws InvocationTargetException, IllegalAccessException {
                return getSessionService(getServices());
            }

            public static MinecraftSessionService getSessionService(net.minecraft.server.Services services) throws IllegalAccessException {
                Field field_sessionService = getSessionServiceField(services);
                if(field_sessionService == null) throw new RuntimeException("Field `sessionService` not found.");

                field_sessionService.setAccessible(true);
                return (MinecraftSessionService) field_sessionService.get(services);
            }

            public static void setSessionService(MinecraftSessionService sessionService) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {
                setSessionService(getServices(), sessionService);
            }

            public static void setSessionService(net.minecraft.server.Services services, MinecraftSessionService sessionService) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
                Field field_sessionService = getSessionServiceField(services);
                if(field_sessionService == null) throw new RuntimeException("Field `sessionService` not found.");

                FieldsUtil.changeFinal(services, field_sessionService, sessionService);
            }

        }

    }

}
