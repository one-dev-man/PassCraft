package mc.passcraft.authentication;

import com.mojang.authlib.minecraft.MinecraftSessionService;

import java.lang.reflect.Proxy;

public interface SessionServiceProxy extends ISessionServiceProxy, MinecraftSessionService {

    static SessionServiceProxy create(MinecraftSessionService originalSessionService) {
        SessionServiceProxyInvocationHandler handler = new SessionServiceProxyInvocationHandler(originalSessionService);

        return (SessionServiceProxy) Proxy.newProxyInstance(
            SessionServiceProxy.class.getClassLoader(),
            new Class[] { SessionServiceProxy.class },
            handler
        );
    }

}
