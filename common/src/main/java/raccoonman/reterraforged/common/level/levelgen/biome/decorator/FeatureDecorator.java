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
//package raccoonman.reterraforged.common.level.levelgen.biome.decorator;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Holder;
//import net.minecraft.core.HolderSet;
//import net.minecraft.core.SectionPos;
//import net.minecraft.world.level.StructureManager;
//import net.minecraft.world.level.WorldGenLevel;
//import net.minecraft.world.level.biome.Biome;
//import net.minecraft.world.level.chunk.ChunkAccess;
//import net.minecraft.world.level.levelgen.GenerationStep;
//import net.minecraft.world.level.levelgen.LegacyRandomSource;
//import net.minecraft.world.level.levelgen.WorldgenRandom;
//import net.minecraft.world.level.levelgen.placement.PlacedFeature;
//import raccoonman.reterraforged.common.level.levelgen.biome.vegetation.BiomeVegetationManager;
//import raccoonman.reterraforged.common.level.levelgen.biome.vegetation.VegetationConfig;
//import raccoonman.reterraforged.common.level.levelgen.biome.vegetation.VegetationFeatures;
//import raccoonman.reterraforged.common.level.levelgen.generator.RTFChunkGenerator;
//import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainCache;
//
//public class FeatureDecorator {
//    public static final GenerationStep.Decoration[] STAGES = GenerationStep.Decoration.values();
//    private static final int MAX_DECORATION_STAGE = GenerationStep.Decoration.TOP_LAYER_MODIFICATION.ordinal();
//
//    private final BiomeVegetationManager vegetation;
//
//    public FeatureDecorator(HolderSet<VegetationConfig> vegetation) {
//        this.vegetation = new BiomeVegetationManager(vegetation);
//    }
//
//    public BiomeVegetationManager getVegetationManager() {
//        return vegetation;
//    }
//
//    public HolderSet<PlacedFeature> getStageFeatures(int stage, Biome biome) {
//        var stages = biome.getGenerationSettings().features();
//        if (stage >= stages.size()) return null;
//        return stages.get(stage);
//    }
//
//    public void decorate(ChunkAccess chunk,
//                         WorldGenLevel level,
//                         StructureManager structures,
//                         TerrainCache terrain,
//                         RTFChunkGenerator generator) {
//        var origin = getOrigin(level, chunk);
//        var biome = level.getBiome(origin);
//        var random = getRandom(level.getSeed());
//        long seed = random.setDecorationSeed(level.getSeed(), origin.getX(), origin.getZ());
//
//        decoratePre(seed, origin, biome, chunk, level, generator, random, structures);
//        decorateVegetation(seed, origin, biome, chunk, level, generator, random, terrain);
//        decoratePost(seed, origin, biome, chunk, level, generator, random, structures);
//    }
//
//    private void decoratePre(long seed,
//                             BlockPos origin,
//                             Holder<Biome> biome,
//                             ChunkAccess chunk,
//                             WorldGenLevel level,
//                             RTFChunkGenerator generator,
//                             WorldgenRandom random,
//                             StructureManager structureManager) {
//
//        VanillaDecorator.decorate(seed, 0, VegetationFeatures.STAGE - 1, origin, biome, chunk, level, generator, random, structureManager, this);
//    }
//
//    private void decoratePost(long seed,
//                              BlockPos origin,
//                              Holder<Biome> biome,
//                              ChunkAccess chunk,
//                              WorldGenLevel level,
//                              RTFChunkGenerator generator,
//                              WorldgenRandom random,
//                              StructureManager structureManager) {
//
//        VanillaDecorator.decorate(seed, VegetationFeatures.STAGE + 1, MAX_DECORATION_STAGE, origin, biome, chunk, level, generator, random, structureManager, this);
//    }
//
//    private void decorateVegetation(long seed,
//                                    BlockPos origin,
//                                    Holder<Biome> biome,
//                                    ChunkAccess chunk,
//                                    WorldGenLevel level,
//                                    RTFChunkGenerator generator,
//                                    WorldgenRandom random,
//                                    TerrainCache terrain) {
//
//        PositionSampler.placeVegetation(seed, origin, biome, chunk, level, generator, random, terrain, this);
//    }
//
//    private static BlockPos getOrigin(WorldGenLevel level, ChunkAccess chunk) {
//        var chunkPos = chunk.getPos();
//        var sectionPos = SectionPos.of(chunkPos, level.getMinSection());
//        return sectionPos.origin();
//    }
//
//    private static WorldgenRandom getRandom(long seed) {
//        return new WorldgenRandom(new LegacyRandomSource(seed));
//    }
//}
