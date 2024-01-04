package raccoonman.reterraforged.forge.mixin;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import raccoonman.reterraforged.server.RTFMinecraftServer;
import raccoonman.reterraforged.world.worldgen.feature.template.template.FeatureTemplateManager;

@Implements(@Interface(iface = RTFMinecraftServer.class, prefix = "reterraforged$RTFMinecraftServer$"))
@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
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
		method = { "lambda$reloadResources$27" },
		require = 0,
		at = @At("TAIL")
	)
	private void lambda$reloadResources$27(CallbackInfo callback) {
		this.templateManager.onReload(this.getResourceManager());
	}
	
	@Shadow
	private ResourceManager getResourceManager() {
		throw new UnsupportedOperationException();
	}
}
