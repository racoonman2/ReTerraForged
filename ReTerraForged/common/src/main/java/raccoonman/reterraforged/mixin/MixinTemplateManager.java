package raccoonman.reterraforged.mixin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import raccoonman.reterraforged.extensions.RTFTemplateManager;
import raccoonman.reterraforged.world.worldgen.feature.template.FeatureTemplate;

@Mixin(StructureTemplateManager.class)
class MixinTemplateManager implements RTFTemplateManager {
	@Shadow
	private ResourceManager resourceManager;
	@Shadow
	@Final
    private HolderGetter<Block> blockLookup;
	
	private Map<ResourceLocation, FeatureTemplate> cache = new ConcurrentHashMap<>();
	
	@Inject(
		method = "onResourceManagerReload",
		at = @At("TAIL")
	)
	public void onResourceManagerReload(CallbackInfo callback) {
		this.cache.clear();
    }
	
	@Override
	public FeatureTemplate loadFeatureTemplate(ResourceLocation location) {
		return this.cache.computeIfAbsent(location, this::readFeatureTemplate);
	}

	private FeatureTemplate readFeatureTemplate(ResourceLocation location) {
		return this.resourceManager.getResource(location).flatMap((resource) -> {
			try(InputStream stream = resource.open()) {
				return FeatureTemplate.load(this.blockLookup, stream);
			} catch (IOException e) {
				e.printStackTrace();
				return Optional.empty();
			}
		}).orElseGet(() -> new FeatureTemplate(ImmutableList.of()));
	}
}
