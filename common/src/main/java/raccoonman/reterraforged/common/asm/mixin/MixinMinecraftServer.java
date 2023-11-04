package raccoonman.reterraforged.common.asm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread.ThreadPools;

@Mixin(MinecraftServer.class)
class MixinMinecraftServer {

	@Inject(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/MinecraftServer;onServerExit()V"
		),
		method = "runServer"
	)
	protected void runServer(CallbackInfo callback) {
		if((Object) this instanceof DedicatedServer) {
			ThreadPools.shutdownAll();
		}
	}
}
