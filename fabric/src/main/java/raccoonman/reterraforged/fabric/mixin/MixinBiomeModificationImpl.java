package raccoonman.reterraforged.fabric.mixin;

import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.impl.biome.modification.BiomeModificationImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.biome.modifier.BiomeModifier;
import raccoonman.reterraforged.world.worldgen.biome.modifier.fabric.FabricBiomeModifier;

@Mixin(BiomeModificationImpl.class)
public class MixinBiomeModificationImpl {
	private List<Holder.Reference<BiomeModifier>> biomeModifiers;

	@Shadow(remap = false)
	private boolean modifiersUnsorted;
	
	@Inject(
		method = "finalizeWorldGen",
		at = @At("HEAD"),
		remap = false
	)
	public void finalizeWorldGen(RegistryAccess registries, CallbackInfo callback) {
		this.biomeModifiers = registries.lookupOrThrow(RTFRegistries.BIOME_MODIFIER).listElements().toList();
    }

	@Inject(
		method = "getSortedModifiers",
		at = @At("HEAD"),
		remap = false
	)
	private void getSortedModifiers(CallbackInfoReturnable<List<?>> callback) {
		if(this.modifiersUnsorted) {
			BiomeModificationImpl self = (BiomeModificationImpl) (Object) this;
			for(Holder.Reference<BiomeModifier> holder : this.biomeModifiers) {
				if(holder.value() instanceof FabricBiomeModifier modifier) {
					self.addModifier(holder.key().location(), ModificationPhase.POST_PROCESSING, (ctx) -> {
						Optional<HolderSet<Biome>> biomes = modifier.biomes();
						return biomes.isPresent() ? biomes.get().contains(ctx.getBiomeRegistryEntry()) : true;
					}, modifier::apply);
				}
			}
		}
	}
}
