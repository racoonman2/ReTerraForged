package raccoonman.reterraforged.common.asm.mixin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.asm.extensions.ContextExtension;
import raccoonman.reterraforged.common.asm.extensions.SurfaceSystemExtension;
import raccoonman.reterraforged.common.level.levelgen.geology.Geology;
import raccoonman.reterraforged.common.level.levelgen.geology.Strata;

@Mixin(SurfaceSystem.class)
@Implements(@Interface(iface = SurfaceSystemExtension.class, prefix = ReTerraForged.MOD_ID + "$SurfaceSystemExtension$"))
class MixinSurfaceSystem {
	private static final ResourceLocation GEOLOGY_RANDOM = ReTerraForged.resolve("geology");
	private Map<ResourceLocation, Geology> geology = new ConcurrentHashMap<>();
	
	public Geology reterraforged$SurfaceSystemExtension$getOrCreateGeology(Strata strata, RandomState randomState) {
		ResourceLocation strataName = strata.name();
		return this.geology.computeIfAbsent(strataName, (name) -> {
			return strata.generateGeology(randomState.getOrCreateRandomFactory(GEOLOGY_RANDOM));
		});
	}
	
	@Inject(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/resources/ResourceKey;)Z",
			ordinal = 1
		),
		method = "buildSurface(Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;ZLnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/NoiseChunk;Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;)V",
		locals = LocalCapture.CAPTURE_FAILHARD
	)
    public void buildSurface(RandomState randomState, BiomeManager biomeManager, Registry<Biome> registry, boolean bl, WorldGenerationContext worldGenerationContext, ChunkAccess chunkAccess, NoiseChunk noiseChunk, SurfaceRules.RuleSource ruleSource, CallbackInfo callback, BlockPos.MutableBlockPos mutableBlockPos, ChunkPos chunkPos, int i, int j, BlockColumn column, SurfaceRules.Context context, SurfaceRules.SurfaceRule surfaceRule, BlockPos.MutableBlockPos mutableBlockPos2, int k, int l, int m, int n, int o, Holder<Biome> holder) {
    	if((Object) context instanceof ContextExtension ctx) {
    		ctx.applySurfaceExtensions(column);
    	} else {
    		throw new IllegalStateException();
    	}
    }
}
