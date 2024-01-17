package raccoonman.reterraforged.data.worldgen.preset.tags;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import raccoonman.reterraforged.data.worldgen.preset.settings.MiscellaneousSettings;
import raccoonman.reterraforged.data.worldgen.preset.settings.WorldPreset;
import raccoonman.reterraforged.tags.RTFBiomeTags;

public class PresetBiomeTagsProvider extends TagsProvider<Biome> {
	private WorldPreset preset;
	
	public PresetBiomeTagsProvider(WorldPreset preset, PackOutput packOutput, CompletableFuture<Provider> completableFuture) {
		super(packOutput, Registries.BIOME, completableFuture);
		
		this.preset = preset;
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		MiscellaneousSettings miscellaneous = this.preset.miscellaneous();

		this.tag(RTFBiomeTags.HAS_SWAMP_GENERATION).add(Biomes.SWAMP);
		
		if(miscellaneous.customBiomeFeatures)	{
			this.tag(RTFBiomeTags.HAS_SWAMP_TREES).add(Biomes.SWAMP);
			this.tag(RTFBiomeTags.HAS_PLAINS_TREES).add(Biomes.RIVER, Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS);
			this.tag(RTFBiomeTags.HAS_FOREST_TREES).add(Biomes.FOREST);
			this.tag(RTFBiomeTags.HAS_FLOWER_FOREST_TREES).add(Biomes.FLOWER_FOREST);
			this.tag(RTFBiomeTags.HAS_BIRCH_FOREST_TREES).add(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST);
			this.tag(RTFBiomeTags.HAS_DARK_FOREST_TREES).add(Biomes.DARK_FOREST);
			this.tag(RTFBiomeTags.HAS_SAVANNA_TREES).add(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU);
			this.tag(RTFBiomeTags.HAS_MEADOW_TREES).add(Biomes.MEADOW);
			this.tag(RTFBiomeTags.HAS_FIR_FOREST_TREES).add(Biomes.WINDSWEPT_FOREST);
			this.tag(RTFBiomeTags.HAS_GROVE_TREES).add(Biomes.GROVE);
			this.tag(RTFBiomeTags.HAS_WINDSWEPT_HILLS_TREES).add(Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS);
			this.tag(RTFBiomeTags.HAS_PINE_FOREST_TREES).add(Biomes.TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA);
			this.tag(RTFBiomeTags.HAS_SPRUCE_FOREST_TREES).add(Biomes.SNOWY_TAIGA);
			this.tag(RTFBiomeTags.HAS_SPRUCE_TUNDRA_TREES).add(Biomes.SNOWY_PLAINS, Biomes.FROZEN_RIVER);
			this.tag(RTFBiomeTags.HAS_REDWOOD_FOREST_TREES).add(Biomes.OLD_GROWTH_PINE_TAIGA);
			this.tag(RTFBiomeTags.HAS_JUNGLE_TREES).add(Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE);
			this.tag(RTFBiomeTags.HAS_JUNGLE_EDGE_TREES).add(Biomes.SPARSE_JUNGLE);
			this.tag(RTFBiomeTags.HAS_BADLANDS_TREES).add(Biomes.WINDSWEPT_SAVANNA);
			this.tag(RTFBiomeTags.HAS_WOODED_BADLANDS_TREES).add(Biomes.WOODED_BADLANDS);
	
			this.tag(RTFBiomeTags.HAS_MARSH_BUSHES).add();
			this.tag(RTFBiomeTags.HAS_PLAINS_BUSHES).add(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.WINDSWEPT_HILLS, Biomes.MEADOW);
			this.tag(RTFBiomeTags.HAS_STEPPE_BUSHES).add(Biomes.SAVANNA, Biomes.WINDSWEPT_SAVANNA, Biomes.SAVANNA_PLATEAU);
			this.tag(RTFBiomeTags.HAS_COLD_STEPPE_BUSHES).add();
			this.tag(RTFBiomeTags.HAS_COLD_TAIGA_SCRUB_BUSHES).add(Biomes.SNOWY_PLAINS, Biomes.TAIGA, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_GRAVELLY_HILLS);
			
			this.tag(RTFBiomeTags.HAS_FOREST_GRASS).add(Biomes.FOREST, Biomes.DARK_FOREST);
			this.tag(RTFBiomeTags.HAS_BIRCH_FOREST_GRASS).add(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST);
		}
	}
}