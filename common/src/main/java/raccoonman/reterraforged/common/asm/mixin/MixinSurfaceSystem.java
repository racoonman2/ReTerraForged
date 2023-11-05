package raccoonman.reterraforged.common.asm.mixin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.asm.extensions.ContextExtension;
import raccoonman.reterraforged.common.asm.extensions.SurfaceSystemExtension;
import raccoonman.reterraforged.common.level.levelgen.surface.rule.ErosionRule;
import raccoonman.reterraforged.common.level.levelgen.surface.rule.StrataRule;
import raccoonman.reterraforged.common.registries.RTFRegistries;

@Mixin(SurfaceSystem.class)
@Implements(@Interface(iface = SurfaceSystemExtension.class, prefix = ReTerraForged.MOD_ID + "$SurfaceSystemExtension$"))
class MixinSurfaceSystem {
	private static final ResourceLocation GEOLOGY_RANDOM = RTFRegistries.resolve("geology");
	private RandomState randomState;
	private Map<ResourceLocation, List<List<StrataRule.Layer>>> strata;
	
	@Inject(
		at = @At("TAIL"),
		method = "<init>"
	)
    public void SurfaceSystem(RandomState randomState, BlockState blockState, int i, PositionalRandomFactory positionalRandomFactory, CallbackInfo callback) {
    	this.randomState = randomState;
    	this.strata = new ConcurrentHashMap<>();
	}
	
	public List<List<StrataRule.Layer>> reterraforged$SurfaceSystemExtension$getOrCreateStrata(ResourceLocation name, Function<RandomSource, List<List<StrataRule.Layer>>> strata) {
		return this.strata.computeIfAbsent(name, (k) -> {
			PositionalRandomFactory factory = this.randomState.getOrCreateRandomFactory(GEOLOGY_RANDOM);
			return strata.apply(factory.fromHashOf(k));
		});
	}
	
	@Inject(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/resources/ResourceKey;)Z",
			ordinal = 1
		),
		method = "buildSurface",
		locals = LocalCapture.CAPTURE_FAILHARD
	)
    public void buildSurface(RandomState randomState, BiomeManager biomeManager, Registry<Biome> registry, boolean bl, WorldGenerationContext worldGenerationContext, final ChunkAccess chunkAccess, NoiseChunk noiseChunk, SurfaceRules.RuleSource ruleSource, CallbackInfo callback, final BlockPos.MutableBlockPos pos, ChunkPos chunkPos, int chunkMinX, int chunkMinZ, BlockColumn column, SurfaceRules.Context context, SurfaceRules.SurfaceRule rule, BlockPos.MutableBlockPos pos2, int chunkX, int chunkZ) {
		if((Object) context instanceof ContextExtension extension) {
			ErosionRule.Rule erosionRule;
			if((erosionRule = extension.getErosionRule()) != null) {
				erosionRule.applyExtension(context.blockX, context.blockZ, chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkX, chunkZ), column);
			}
		} else {
			throw new IllegalStateException();
		}
    }
}
