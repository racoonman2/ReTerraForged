///*
//- * MIT License
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
//package raccoonman.reterraforged.common.level.levelgen.generator;
//
//import java.util.List;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Holder;
//import net.minecraft.server.level.WorldGenRegion;
//import net.minecraft.world.level.StructureManager;
//import net.minecraft.world.level.WorldGenLevel;
//import net.minecraft.world.level.biome.BiomeSource;
//import net.minecraft.world.level.chunk.ChunkAccess;
//import net.minecraft.world.level.levelgen.DensityFunction;
//import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
//import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
//import net.minecraft.world.level.levelgen.RandomState;
//
//public class RTFChunkGenerator extends NoiseBasedChunkGenerator {
//	public static final Codec<RTFChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//		BiomeSource.CODEC.fieldOf("biome_source").forGetter(RTFChunkGenerator::getBiomeSource),
//		NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(RTFChunkGenerator::generatorSettings)
//	).apply(instance, instance.stable(RTFChunkGenerator::new)));
//    
//    public RTFChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> generatorSettings) {
//        super(biomeSource, generatorSettings);
//    }
//
//    @Override
//    public Codec<RTFChunkGenerator> codec() {
//        return CODEC;
//    }
//
//	@Override
//    public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState state, ChunkAccess chunk) {
//		super.buildSurface(region, structures, state, chunk);
////    	Surface.apply(chunk, this, (TerrainCache) ((TerrainHolder) chunk).getTerrain());
//    }
//
//    @Override
//    public void applyBiomeDecoration(WorldGenLevel region, ChunkAccess chunk, StructureManager structures) {
////    	TerrainCache cache = (TerrainCache) ((TerrainHolder) chunk).getTerrain();
////    	Surface.smoothWater(chunk, region, cache);
////    	Surface.applyPost(chunk, cache, this);
//    	super.applyBiomeDecoration(region, chunk, structures);
//    }
//    
//    @Override
//    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos blockPos) {
//    	list.add("");
//    	list.add("[RTFChunkGenerator]");
//    	list.add("Final Density: " + randomState.router().finalDensity().compute(new DensityFunction.SinglePointContext(blockPos.getX(), blockPos.getY(), blockPos.getZ())));
//    }
//}
