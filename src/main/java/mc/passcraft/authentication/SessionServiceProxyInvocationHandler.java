package mc.passcraft.authentication;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.ProfileResult;
import mc.passcraft.authentication.events.SessionServiceProxyAuthenticatePlayerEvent;
import mc.passcraft.authentication.events.SessionServiceProxyEventListener;
import mc.passcraft.types.events.Event;
import mc.passcraft.types.events.EventManager;
import mc.passcraft.utils.reflection.MethodsUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class SessionServiceProxyInvocationHandler extends EventManager<Event, SessionServiceProxyEventListener> implements ISessionServiceProxy, InvocationHandler {

    private interface ProxiedMethodHandler<ReturnType> {
        ReturnType handle(Object proxy, Method method, Object[] args);
    }

    private static final String ORIGINAL_SESSION_SERVICE_PROXIED_AUTH_METHOD_NAME = "hasJoinedServer";

    private final Map<String, ProxiedMethodHandler<?>> proxiedMethods = new HashMap<>();

    //

    private final MinecraftSessionService originalSessionService;

    //

    public SessionServiceProxyInvocationHandler(MinecraftSessionService originalSessionService) {
        this.originalSessionService = originalSessionService;

        //

        proxiedMethods.put(ORIGINAL_SESSION_SERVICE_PROXIED_AUTH_METHOD_NAME, (proxy, method, args) -> {
            ProfileResult initialProfileResult = null;
            String username = (String) args[0];

            try {
                initialProfileResult = MethodsUtil.invoke(this.getOriginalSessionService(), method, args);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            GameProfile profile = initialProfileResult == null ? null : initialProfileResult.profile();
//            UUID uuid = profile == null ? Players.getOfflineUUID(username) : profile.getId();
//
//            GameProfile intermediateProfile = new GameProfile(uuid, username);
//
//            if (profile != null) {
//                for (Map.Entry<String, Property> entry : profile.getProperties().entries()) {
//                    intermediateProfile.getProperties().put(entry.getKey(), entry.getValue());
//                }
//            }

            SessionServiceProxyAuthenticatePlayerEvent event = new SessionServiceProxyAuthenticatePlayerEvent(username, profile);
            boolean isCancelled = this.call(event);
            return new ProfileResult(isCancelled ? profile : event.getProfile());
        });

        proxiedMethods.put("getSecurePropertyValue", (proxy, method, args) -> {
            Property property = (Property) args[0];
            return property.value();
        });
    }

    //

    public MinecraftSessionService getOriginalSessionService() {
        return this.originalSessionService;
    }

    //

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;

        //

        String methodName = method.getName();

        ProxiedMethodHandler<?> methodHandler = this.proxiedMethods.get(methodName);

        if(methodHandler == null) {
            Object methodTarget = this;
            if (method.getDeclaringClass().equals(MinecraftSessionService.class)) methodTarget = this.getOriginalSessionService();

            result = MethodsUtil.invoke(methodTarget, method, args);
        }
        else result = methodHandler.handle(proxy, method, args);

        return result;
    }
}
