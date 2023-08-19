//package raccoonman.reterraforged.common.registries;
//
//import com.mojang.serialization.Codec;
//
//import net.minecraft.resources.ResourceKey;
//import raccoonman.reterraforged.common.ReTerraForged;
//import raccoonman.reterraforged.common.level.levelgen.biome.viability.HeightViability;
//import raccoonman.reterraforged.common.level.levelgen.biome.viability.MulViability;
//import raccoonman.reterraforged.common.level.levelgen.biome.viability.NoiseViability;
//import raccoonman.reterraforged.common.level.levelgen.biome.viability.SaturationViability;
//import raccoonman.reterraforged.common.level.levelgen.biome.viability.SlopeViability;
//import raccoonman.reterraforged.common.level.levelgen.biome.viability.SumViability;
//import raccoonman.reterraforged.common.level.levelgen.biome.viability.Viability;
//import raccoonman.reterraforged.platform.registries.RegistryUtil;
//
//public final class RTFViabilities {
//	public static final ResourceKey<Codec<? extends Viability>> BIOME_EDGE = resolve("biome_edge");
//	public static final ResourceKey<Codec<? extends Viability>> HEIGHT = resolve("height");
//	public static final ResourceKey<Codec<? extends Viability>> MUL = resolve("mul");
//	public static final ResourceKey<Codec<? extends Viability>> NOISE = resolve("noise");
//	public static final ResourceKey<Codec<? extends Viability>> SATURATION = resolve("saturation");
//	public static final ResourceKey<Codec<? extends Viability>> SLOPE = resolve("slope");
//	public static final ResourceKey<Codec<? extends Viability>> SUM = resolve("sum");
//	
//	public static void register() {
////		RegistryUtil.register(BIOME_EDGE, () -> BiomeEdgeViability.CODEC);
//		RegistryUtil.register(HEIGHT, () -> HeightViability.CODEC);
//		RegistryUtil.register(MUL, () -> MulViability.CODEC);
//		RegistryUtil.register(NOISE, () -> NoiseViability.CODEC);
//		RegistryUtil.register(SATURATION, () -> SaturationViability.CODEC);
//		RegistryUtil.register(SLOPE, () -> SlopeViability.CODEC);
//		RegistryUtil.register(SUM, () -> SumViability.CODEC);
//	}
//	
//	private static ResourceKey<Codec<? extends Viability>> resolve(String path) {
//		return ReTerraForged.resolve(RTFRegistries.VIABILITY_TYPE, path);
//	}
//}
