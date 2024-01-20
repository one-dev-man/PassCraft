package mc.passcraft.authentication;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import mc.passcraft.authentication.events.SessionServiceProxyEventListener;
import mc.passcraft.types.events.Event;
import mc.passcraft.types.events.EventManagerInterface;

interface ISessionServiceProxy extends EventManagerInterface<Event, SessionServiceProxyEventListener> {

    MinecraftSessionService getOriginalSessionService();

    //

//    Formatter<GameProfile, GameProfile> getGameProfileFormatter();
//    void setGameProfileFormatter(Formatter<GameProfile, GameProfile> formatter);

}
