/*
- * MIT License
 *
 * Copyright (c) 2021 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package raccoonman.reterraforged.common.level.levelgen.generator;

import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.asm.extensions.TerrainDataHolder;
import raccoonman.reterraforged.common.hooks.MinecraftServerHook;
import raccoonman.reterraforged.common.level.levelgen.biome.source.RTFBiomeSource;
import raccoonman.reterraforged.common.level.levelgen.biome.surface.Surface;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimateGroup;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimateNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseLevels;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseSample;
import raccoonman.reterraforged.common.level.levelgen.settings.Settings;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainBlender;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainData;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainGenerator;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainLevels;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainNoise;
import raccoonman.reterraforged.common.level.levelgen.terrain.erosion.ErosionFilter;
import raccoonman.reterraforged.common.level.levelgen.util.Seed;

public class RTFChunkGenerator extends NoiseBasedChunkGenerator {
	public static final Codec<RTFChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Settings.CODEC.fieldOf("settings").forGetter((g) -> g.settings),
		TerrainLevels.CODEC.fieldOf("levels").forGetter((g) -> g.levels),
		TerrainBlender.CODEC.fieldOf("terrain").forGetter((g) -> g.terrain),
		NoiseGeneratorSettings.CODEC.fieldOf("generator_settings").forGetter((g) -> g.generatorSettings),
    	ClimateGroup.LIST_CODEC.fieldOf("climate_groups").forGetter((g) -> g.climateGroups)
	).apply(instance, instance.stable(RTFChunkGenerator::create)));

	private final Settings settings;
	private final TerrainLevels levels;
	private final Holder<NoiseGeneratorSettings> generatorSettings;
    private final HolderSet<ClimateGroup> climateGroups;
    private final Supplier<ClimateNoise> climateNoise;
    private final Supplier<TerrainNoise> terrainNoise;
    private final TerrainBlender terrain;
    private final TerrainGenerator terrainGenerator;
    private final ErosionFilter erosion;
    
    public RTFChunkGenerator(
    	Settings settings,
    	TerrainLevels levels,
    	Holder<NoiseGeneratorSettings> generatorSettings,
    	HolderSet<ClimateGroup> climateGroups,
    	Supplier<TerrainNoise> terrainNoise,
    	Supplier<ClimateNoise> climateNoise,
    	TerrainGenerator terrainGenerator,
    	BiomeSource biomeSource,
    	TerrainBlender terrain
    ) {
        super(biomeSource, generatorSettings);
        this.settings = settings;
        this.levels = levels;
        this.generatorSettings = generatorSettings;
        this.terrainNoise = terrainNoise;
        this.climateNoise = climateNoise;
        this.terrainGenerator = terrainGenerator;
        this.terrain = terrain;
        this.climateGroups = climateGroups;
        this.erosion = new ErosionFilter(3 * 16 /*diameter * chunk size*/, settings.world().erosion());        
    }
    
    public ErosionFilter getErosionFilter() {
    	return this.erosion;
    }
    
    public TerrainGenerator getTerrainGenerator() {
    	return this.terrainGenerator;
    }
    
    public Supplier<TerrainNoise> getTerrainNoise() {
    	return this.terrainNoise;
    }
    
    public Supplier<ClimateNoise> getClimateNoise() {
    	return this.climateNoise;
    }

    @Override
    public Codec<RTFChunkGenerator> codec() {
        return CODEC;
    }

	@Override
    public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState state, ChunkAccess chunk) {
    	super.buildSurface(region, structures, state, chunk);
        Surface.apply(chunk, this);
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel region, ChunkAccess chunk, StructureManager structures) {
    	super.applyBiomeDecoration(region, chunk, structures);
    	TerrainData terrain = ((TerrainDataHolder) chunk).getTerrainData();
        Surface.smoothWater(chunk, region, terrain);
        Surface.applyPost(chunk, terrain, this);
        
        //FIXME!!!! chunks that don't reach Feature status will retain their TerrainData and cause a memory leak
        //MASSIVE ISSUE
        //FIX THIS IMMEDIATELY
        ((TerrainDataHolder) chunk).setTerrainData(null);
        this.getTerrainGenerator().restore(terrain);
    }

    @Override
    public void addDebugScreenInfo(List<String> lines, RandomState state, BlockPos pos) {
    	int x = pos.getX();
    	int z = pos.getZ();
    	NoiseSample sample = new NoiseSample();
    	this.climateNoise.get().sample(x, z, sample.climate);
    	this.terrainNoise.get().sample(x, z, sample.terrain);
    	
    	lines.add("");
    	lines.add("[TFChunkGenerator]");
    	lines.add("Biome: " + sample.climate.biomeNoise);
    	lines.add("Biome Edge: " + sample.climate.biomeEdgeNoise);
    	lines.add("Moisture: " + sample.climate.moisture);
    	lines.add("Temperature: " + sample.climate.temperature);
    	lines.add("Climate: " + sample.climate.climate);
    	lines.add("Base: " + sample.terrain.baseNoise);
    	lines.add("Height: " + sample.terrain.heightNoise);
    	lines.add("Continent: " + sample.terrain.continentNoise);
    	lines.add("River: " + sample.terrain.riverNoise);
    	lines.add("Depth: " + state.sampler().depth().compute(new DensityFunction.SinglePointContext(x, pos.getY(), z)));
    	lines.add("");
    }

    public static RTFChunkGenerator create(
    	Settings settings,
    	TerrainLevels terrainLevels,
    	TerrainBlender terrain,
    	Holder<NoiseGeneratorSettings> generatorSettings,
    	HolderSet<ClimateGroup> climateGroups
    ) {
    	Supplier<Seed> seed = Suppliers.memoize(() -> {
    	   	MinecraftServer server = MinecraftServerHook.currentServer;
    	   	int s = 0;
    	   	if(server != null) {
    	   		s = (int) server.getWorldData().worldGenOptions().seed();
    	    	ReTerraForged.LOGGER.info("Using seed {} for generator", s);
    	   	}
    	   	return new Seed(s);
    	});
    	NoiseLevels noiseLevels = terrainLevels.getNoiseLevels();
    	Supplier<TerrainNoise> terrainNoise = Suppliers.memoize(() -> {
        	return new TerrainNoise(seed.get(), settings.world(), noiseLevels, terrain);
    	});
    	Supplier<ClimateNoise> climateNoise = Suppliers.memoize(() -> {
    		return new ClimateNoise(seed.get(), settings.climate(), noiseLevels);
    	});
    	TerrainGenerator cache = new TerrainGenerator(terrainLevels, terrainNoise);
    	RTFBiomeSource biomeSource = new RTFBiomeSource(terrainNoise, climateNoise, climateGroups);
    	return new RTFChunkGenerator(settings, terrainLevels, generatorSettings, climateGroups, terrainNoise, climateNoise, cache, biomeSource, terrain);
    }
}
