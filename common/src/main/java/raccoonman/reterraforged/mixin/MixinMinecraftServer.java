package raccoonman.reterraforged.mixin;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.packs.resources.ResourceManager;
import raccoonman.reterraforged.concurrent.cache.CacheManager;
import raccoonman.reterraforged.server.RTFMinecraftServer;
import raccoonman.reterraforged.world.worldgen.feature.template.template.FeatureTemplateManager;

@Mixin(value =  { MinecraftServer.class, DedicatedServer.class })
class MixinMinecraftServer {
	
	@Inject(method = "onServerExit()V", at = @At("TAIL"))
    public void onServerExit(CallbackInfo callback) {
		try {
			CacheManager.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Implements(@Interface(iface = RTFMinecraftServer.class, prefix = "reterraforged$RTFMinecraftServer$"))
	@Mixin(MinecraftServer.class)
	static class MixinFeatureTemplateManager {
		private FeatureTemplateManager templateManager;

		@Inject(
			method = "<init>",
			at = @At("TAIL")
		)
		public void MinecraftServer(CallbackInfo callback) {
			this.templateManager = new FeatureTemplateManager(this.getResourceManager());
		}
		
		public FeatureTemplateManager reterraforged$RTFMinecraftServer$getFeatureTemplateManager() {
			return this.templateManager;
		}
		
		@Inject(
			method = "method_29440",
			at = @At("TAIL")
		)
		private void method_29440(CallbackInfo callback) {
			this.templateManager.onReload(this.getResourceManager());
		}
		
		@Shadow
		private ResourceManager getResourceManager() {
			throw new UnsupportedOperationException();
		}
	}
}
