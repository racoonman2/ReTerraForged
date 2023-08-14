///*
// * MIT License
// *
// * Copyright (c) 2021 TerraForged
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package raccoonman.reterraforged.common.registries.data;
//
//import net.minecraft.core.Holder;
//import net.minecraft.data.worldgen.BootstapContext;
//import net.minecraft.resources.ResourceKey;
//import raccoonman.reterraforged.common.ReTerraForged;
//import raccoonman.reterraforged.common.level.levelgen.biome.vegetation.VegetationConfig;
//import raccoonman.reterraforged.common.level.levelgen.biome.viability.BiomeEdgeViability;
//import raccoonman.reterraforged.common.level.levelgen.biome.viability.HeightViability;
//import raccoonman.reterraforged.common.level.levelgen.biome.viability.NoiseViability;
//import raccoonman.reterraforged.common.level.levelgen.biome.viability.SaturationViability;
//import raccoonman.reterraforged.common.level.levelgen.biome.viability.SlopeViability;
//import raccoonman.reterraforged.common.level.levelgen.biome.viability.SumViability;
//import raccoonman.reterraforged.common.level.levelgen.util.Seed;
//import raccoonman.reterraforged.common.noise.Source;
//import raccoonman.reterraforged.common.registries.RTFRegistries;
//import raccoonman.reterraforged.common.registries.data.tags.RTFBiomeTags;
//
//public final class RTFVegetation {
//	public static final ResourceKey<VegetationConfig> TREES_COPSE = resolve("trees_copse");
//	public static final ResourceKey<VegetationConfig> TREES_SPARSE = resolve("trees_sparse");
//	public static final ResourceKey<VegetationConfig> TREES_PATCHY = resolve("trees_patchy");
//	public static final ResourceKey<VegetationConfig> TREES_TEMPERATE = resolve("trees_temperate");
//	public static final ResourceKey<VegetationConfig> TREES_HARDY = resolve("trees_hardy");
//	public static final ResourceKey<VegetationConfig> TREES_HARDY_SLOPES = resolve("trees_hardy_slopes");
//	public static final ResourceKey<VegetationConfig> TREES_RAINFOREST = resolve("trees_rainforest");
//	public static final ResourceKey<VegetationConfig> TREES_SPARSE_RAINFOREST = resolve("trees_sparse_rainforest");
//		
//    public static void register(BootstapContext<VegetationConfig> ctx) {
//        var seed = new Seed(0);
//        ctx.register(TREES_COPSE, Factory.copse(seed));
//        ctx.register(TREES_SPARSE, Factory.sparse(seed));
//        ctx.register(TREES_PATCHY, Factory.patchy(seed));
//        ctx.register(TREES_TEMPERATE, Factory.temperate(seed));
//        ctx.register(TREES_HARDY, Factory.hardy(seed));
//        ctx.register(TREES_HARDY_SLOPES, Factory.hardySlopes(seed));
//        ctx.register(TREES_RAINFOREST, Factory.rainforest(seed));
//        ctx.register(TREES_SPARSE_RAINFOREST, Factory.sparseRainforest(seed));
//    }
//    
//    private static ResourceKey<VegetationConfig> resolve(String path) {
//		return ReTerraForged.resolve(RTFRegistries.VEGETATION, path);
//	}
//
//    private static final class Factory {
//
//        public static VegetationConfig copse(Seed seed) {
//            return new VegetationConfig(0.20F, 0.8F, 0.6F, RTFBiomeTags.COPSES, SumViability.builder(0F)
//                    .with(0.2F, new SaturationViability(0.7F, 1F))
//                    .with(-1.0F, new HeightViability(-100, 35, 150))
//                    .with(-0.5F, new SlopeViability(65, 0.55F))
//                    .with(1.0F, new NoiseViability(Holder.direct(Source.simplex(110, 2).shift(seed.next()).clamp(0.85, 0.95F).map(0, 1))))
//                    .build());
//        }
//
//        public static VegetationConfig hardy(Seed seed) {
//            return new VegetationConfig(0.22F, 0.8F, 0.7F, RTFBiomeTags.HARDY, SumViability.builder(0.5F)
//                    .with(0.2F, new SaturationViability(0.85F, 1F))
//                    .with(-1.0F, new HeightViability(-100, 40, 190))
//                    .with(-0.8F, new SlopeViability(55, 0.65F))
//                    .with(-0.8F, new BiomeEdgeViability(0.65F))
//                    .with(-0.4F, new NoiseViability(Holder.direct(Source.simplex(120, 2).shift(seed.next()).clamp(0.4, 0.8).map(0, 1))))
//                    .build());
//        }
//
//        public static VegetationConfig hardySlopes(Seed seed) {
//            return new VegetationConfig(0.20F, 0.8F, 0.7F, RTFBiomeTags.HARDY_SLOPES, SumViability.builder(0.2F)
//                    .with(+0.2F, new SaturationViability(0.8F, 1F))
//                    .with(-1.0F, new HeightViability(-100, 40, 150))
//                    .with(+1.0F, new SlopeViability(60, 0.5F))
//                    .with(-0.8F, new BiomeEdgeViability(0.65F))
//                    .with(-0.5F, new NoiseViability(Holder.direct(Source.simplex(140, 2).shift(seed.next()).clamp(0.2, 0.9).map(0, 1))))
//                    .build());
//        }
//
//        public static VegetationConfig sparse(Seed seed) {
//            return new VegetationConfig(0.15F, 0.75F, 0.35F, RTFBiomeTags.SPARSE, SumViability.builder(0F)
//                    .with(0.4F, new SaturationViability(0.95F, 1F))
//                    .with(-1.0F, new HeightViability(-100, 50, 175))
//                    .with(-1.0F, new SlopeViability(65, 0.6F))
//                    .with(1F, new NoiseViability(Holder.direct(Source.simplex(100, 3).shift(seed.next()).clamp(0.8, 0.85).map(0, 1))))
//                    .build());
//        }
//
//        public static VegetationConfig rainforest(Seed seed) {
//            return new VegetationConfig(0.35F, 0.75F, 0.7F, RTFBiomeTags.RAINFOREST, SumViability.builder(0.45F)
//                    .with(0.25F, new SaturationViability(0.7F, 1F))
//                    .with(-1.0F, new HeightViability(-100, 60, 180))
//                    .with(-0.5F, new SlopeViability(55, 0.65F))
//                    .with(-0.8F, new BiomeEdgeViability(0.7F))
//                    .with(-0.4F, new NoiseViability(Holder.direct(Source.simplex(100, 2).shift(seed.next()).clamp(0.7, 0.9).map(0, 1))))
//                    .build());
//        }
//
//        public static VegetationConfig sparseRainforest(Seed seed) {
//            return new VegetationConfig(0.15F, 0.8F, 0.45F, RTFBiomeTags.SPARSE_RAINFOREST, SumViability.builder(0.0F)
//                    .with(0.2F, new SaturationViability(0.65F, 1F))
//                    .with(-1.0F, new HeightViability(-100, 20, 150))
//                    .with(-0.5F, new SlopeViability(65, 0.75F))
//                    .with(0.5F, new NoiseViability(Holder.direct(Source.simplex(80, 2).shift(seed.next()).clamp(0.5, 0.7).map(0, 1))))
//                    .build());
//        }
//
//        public static VegetationConfig temperate(Seed seed) {
//            return new VegetationConfig(0.20F, 0.8F, 0.6F, RTFBiomeTags.TEMPERATE, SumViability.builder(0.7F)
//                    .with(0.25F, new SaturationViability(0.95F, 1F))
//                    .with(-1.0F, new HeightViability(-100, 45, 150))
//                    .with(-0.6F, new SlopeViability(55, 0.65F))
//                    .with(-0.8F, new BiomeEdgeViability(0.7F))
//                    .with(-0.5F, new NoiseViability(Holder.direct(Source.simplex(120, 2).shift(seed.next()).clamp(0.4, 0.6).map(0, 1))))
//                    .build());
//        }
//
//        public static VegetationConfig patchy(Seed seed) {
//            return new VegetationConfig(0.20F, 0.75F, 0.5F, RTFBiomeTags.PATCHY, SumViability.builder(0.65F)
//                    .with(0.2F, new SaturationViability(0.9F, 1F))
//                    .with(-1.0F, new HeightViability(-100, 40, 165))
//                    .with(-1.0F, new SlopeViability(60, 0.65F))
//                    .with(-0.75F, new BiomeEdgeViability(0.8F))
//                    .with(-0.45F, new NoiseViability(Holder.direct(Source.simplex(150, 3).shift(seed.next()).clamp(0.4, 0.7).map(0, 1))))
//                    .build());
//        }
//    }
//}
