package raccoonman.reterraforged.common.asm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import raccoonman.reterraforged.common.hooks.MinecraftServerHook;

@Deprecated(forRemoval = true)
@Mixin(value = { 
	// people won't extend these, right??
	DedicatedServer.class, 
	IntegratedServer.class 
})
class MixinMinecraftServer {

	@Inject(
		at = @At("HEAD"),
		target = @Desc(
			value = "initServer",
			ret = boolean.class
		)
	)
    protected void initServer(CallbackInfoReturnable<Boolean> callback) {
		MinecraftServerHook.currentServer = (MinecraftServer) (Object) this;
    }
}
