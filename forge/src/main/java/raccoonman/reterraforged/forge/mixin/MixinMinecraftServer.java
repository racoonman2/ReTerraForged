package raccoonman.reterraforged.forge.mixin;

import java.net.Proxy;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.DataFixer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import raccoonman.reterraforged.server.RTFMinecraftServer;
import raccoonman.reterraforged.world.worldgen.feature.template.template.FeatureTemplateManager;

@Implements(@Interface(iface = RTFMinecraftServer.class, prefix = "reterraforged$RTFMinecraftServer$"))
@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	private FeatureTemplateManager templateManager;

	@Inject(
		method = "<init>(Ljava/lang/Thread;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/server/WorldStem;Ljava/net/Proxy;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/server/Services;Lnet/minecraft/server/level/progress/ChunkProgressListenerFactory;)V",
		at = @At("TAIL")
	)
	public void MinecraftServer(Thread thread, LevelStorageSource.LevelStorageAccess arg2, PackRepository arg22, WorldStem arg3, Proxy proxy, DataFixer dataFixer, Services arg4, ChunkProgressListenerFactory arg5, CallbackInfo callback) {
		this.templateManager = new FeatureTemplateManager(this.getResourceManager());
	}
	
	public FeatureTemplateManager reterraforged$RTFMinecraftServer$getFeatureTemplateManager() {
		return this.templateManager;
	}

	@Inject(
		method = "lambda$reloadResources$27",
		require = 1,
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
