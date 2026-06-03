package raccoonman.reterraforged.mixin.compat;

import java.nio.file.Path;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldOptions;
import raccoonman.reterraforged.extensions.compat.TBClimateSampler;
import raccoonman.reterraforged.world.worldgen.compat.CompatUtil;

@Pseudo
@Mixin(targets = "caeruleusTait.world.preview.backend.worker.SampleUtils")
class MixinWPSampleUtils {
	@Shadow
	@Final
	private RandomState randomState;
	@Shadow
	@Final
    private RegistryAccess registryAccess;
	@Shadow
	@Final
    private ResourceKey<Level> dimension;
	
	@Inject(
		method = "<init>(Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/LayeredRegistryAccess;Lnet/minecraft/world/level/levelgen/WorldOptions;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/WorldDataConfiguration;Ljava/net/Proxy;Ljava/nio/file/Path;)V",
		at = @At("TAIL")
	)
	public void init(BiomeSource biomeSource, ChunkGenerator generator, LayeredRegistryAccess<RegistryAccess> registryAccess, WorldOptions options, LevelStem levelStem, LevelHeightAccessor heightAccessor, WorldDataConfiguration dataConfig, Object proxy, Path tempDataPackDir, CallbackInfo callback) {
		CompatUtil.setUniqueness(this.randomState, this.dimension, this.registryAccess);
	}
	
	@Inject(
		method = "<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/levelgen/WorldOptions;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/world/level/LevelHeightAccessor;)V",
		at = @At("TAIL")
	)
	public void init(MinecraftServer server, BiomeSource biomeSource, ChunkGenerator chunkGenerator, WorldOptions worldOptions, LevelStem levelStem, LevelHeightAccessor levelHeightAccessor, CallbackInfo callback) {
		CompatUtil.setUniqueness(this.randomState, this.dimension, this.registryAccess);
	}
	
	@Inject(
		method = "doSample",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;getNoiseBiome(Lnet/minecraft/world/level/biome/Climate$TargetPoint;)Lnet/minecraft/core/Holder;"
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void doSample(BlockPos pos, CallbackInfoReturnable<?> callback, Climate.Sampler sampler, DensityFunction.SinglePointContext context, double temperature, double humidity, double continentalness, double erosion, double depth, double weirdness, short[] noiseData, Climate.TargetPoint targetPoint) {
		TBClimateSampler.sample(sampler, targetPoint, context);
	}
}
