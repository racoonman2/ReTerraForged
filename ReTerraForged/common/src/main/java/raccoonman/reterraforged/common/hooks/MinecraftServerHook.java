package raccoonman.reterraforged.common.hooks;

import net.minecraft.server.MinecraftServer;

@Deprecated(forRemoval = true)
public final class MinecraftServerHook {
	public static transient volatile MinecraftServer currentServer;
}
