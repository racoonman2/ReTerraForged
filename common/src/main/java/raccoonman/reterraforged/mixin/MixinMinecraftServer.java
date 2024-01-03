package raccoonman.reterraforged.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import raccoonman.reterraforged.concurrent.cache.CacheManager;

@Mixin(value =  { MinecraftServer.class, DedicatedServer.class })
public class MixinMinecraftServer {

	@Inject(method = "onServerExit()V", at = @At("TAIL"))
    public void onServerExit(CallbackInfo callback) {
		try {
			CacheManager.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
