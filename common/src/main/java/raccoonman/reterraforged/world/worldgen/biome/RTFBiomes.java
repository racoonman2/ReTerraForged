package raccoonman.reterraforged.world.worldgen.biome;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.data.worldgen.RTFPlacements;

public class RTFBiomes {
    @Nullable
    private static final Music NORMAL_MUSIC = null;

    public static Biome coldMarshland() {
    	//TODO add shrubbery and stuff
    	return null;
    }

    public static Biome darkForest(HolderGetter<PlacedFeature> holderGetter, HolderGetter<ConfiguredWorldCarver<?>> holderGetter2) {
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals(spawnSettings);
        BiomeDefaultFeatures.commonSpawns(spawnSettings);
        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(holderGetter, holderGetter2);
        globalOverworldGeneration(generationSettings);
//        generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, RTFPlacements.FOREST_BUSH);
        generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, RTFPlacements.FOREST_GRASS);
        generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.DARK_FOREST_VEGETATION);
        BiomeDefaultFeatures.addForestFlowers(generationSettings);
        BiomeDefaultFeatures.addDefaultOres(generationSettings);
        BiomeDefaultFeatures.addDefaultSoftDisks(generationSettings);
        BiomeDefaultFeatures.addDefaultFlowers(generationSettings);
        BiomeDefaultFeatures.addForestGrass(generationSettings);
        BiomeDefaultFeatures.addDefaultMushrooms(generationSettings);
        BiomeDefaultFeatures.addDefaultExtraVegetation(generationSettings);
        Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FOREST);
        return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.7f).downfall(0.8f).specialEffects(new BiomeSpecialEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(0.7F)).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.DARK_FOREST).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(music).build()).mobSpawnSettings(spawnSettings.build()).generationSettings(generationSettings.build()).build();
    }
    
    public static Biome forest(HolderGetter<PlacedFeature> placeFeatures, HolderGetter<ConfiguredWorldCarver<?>> configuredWorldCarvers, boolean birch, boolean tallBirch, boolean flowerForest, boolean bush) {
        Music music;
        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(placeFeatures, configuredWorldCarvers);
        globalOverworldGeneration(generationSettings);
        if(bush) {
        	if(birch) {
                generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, RTFPlacements.BIRCH_BUSH);
        	} else if(!flowerForest) {
//                generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, RTFPlacements.FOREST_BUSH);
        	}
        }
        if(birch) {
        	generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, RTFPlacements.BIRCH_GRASS);
        } else {
        	generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, RTFPlacements.FOREST_GRASS);
        }
        
        if (flowerForest) {
            music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FLOWER_FOREST);
            generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FOREST_FLOWERS);
        } else {
            music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FOREST);
            BiomeDefaultFeatures.addForestFlowers(generationSettings);
        }
        BiomeDefaultFeatures.addDefaultOres(generationSettings);
        BiomeDefaultFeatures.addDefaultSoftDisks(generationSettings);
        if (flowerForest) {
            generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_FLOWER_FOREST);
            generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FLOWER_FOREST);
            BiomeDefaultFeatures.addDefaultGrass(generationSettings);
        } else {
            if (birch) {
                if (tallBirch) {
                    BiomeDefaultFeatures.addTallBirchTrees(generationSettings);
                } else {
                    BiomeDefaultFeatures.addBirchTrees(generationSettings);
                }
            } else {
                BiomeDefaultFeatures.addOtherBirchTrees(generationSettings);
            }
            BiomeDefaultFeatures.addDefaultFlowers(generationSettings);
            BiomeDefaultFeatures.addForestGrass(generationSettings);
        }
        BiomeDefaultFeatures.addDefaultMushrooms(generationSettings);
        BiomeDefaultFeatures.addDefaultExtraVegetation(generationSettings);
        
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals(spawns);
        BiomeDefaultFeatures.commonSpawns(spawns);
        if (flowerForest) {
            spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
        } else if (!birch) {
            spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
        }
        float skyColor = birch ? 0.6F : 0.7F;
        return biome(true, skyColor, birch ? 0.6F : 0.8F, spawns, generationSettings, music);
    }

    public static Biome savanna(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> configuredWorldCarvers, boolean shattered, boolean plateau) {
        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(placedFeatures, configuredWorldCarvers);
        globalOverworldGeneration(generationSettings);
        if (!shattered) {
            BiomeDefaultFeatures.addSavannaGrass(generationSettings);
        }
        BiomeDefaultFeatures.addDefaultOres(generationSettings);
        BiomeDefaultFeatures.addDefaultSoftDisks(generationSettings);
        if (shattered) {
            BiomeDefaultFeatures.addShatteredSavannaTrees(generationSettings);
            BiomeDefaultFeatures.addDefaultFlowers(generationSettings);
            BiomeDefaultFeatures.addShatteredSavannaGrass(generationSettings);
        } else {
            BiomeDefaultFeatures.addSavannaTrees(generationSettings);
            BiomeDefaultFeatures.addWarmFlowers(generationSettings);
            BiomeDefaultFeatures.addSavannaExtraGrass(generationSettings);
        }
        BiomeDefaultFeatures.addDefaultMushrooms(generationSettings);
        BiomeDefaultFeatures.addDefaultExtraVegetation(generationSettings);
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals(spawnSettings);
        spawnSettings.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 1, 2, 6)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 1));
        BiomeDefaultFeatures.commonSpawns(spawnSettings);
        if (plateau) {
            spawnSettings.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 8, 4, 4));
        }
        return biome(false, 2.0f, 0.0f, spawnSettings, generationSettings, NORMAL_MUSIC);
    }

    public static Biome meadowOrCherryGrove(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> configuredWorldCarvers, boolean isCherryGrove) {
        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(placedFeatures, configuredWorldCarvers);
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
        spawnSettings.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(isCherryGrove ? EntityType.PIG : EntityType.DONKEY, 1, 1, 2)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 2, 6)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 2, 2, 4));
        BiomeDefaultFeatures.commonSpawns(spawnSettings);
        globalOverworldGeneration(generationSettings);
        BiomeDefaultFeatures.addPlainGrass(generationSettings);
        BiomeDefaultFeatures.addDefaultOres(generationSettings);
        BiomeDefaultFeatures.addDefaultSoftDisks(generationSettings);
        if (isCherryGrove) {
            BiomeDefaultFeatures.addCherryGroveVegetation(generationSettings);
        } else {
            BiomeDefaultFeatures.addMeadowVegetation(generationSettings);
        }
        BiomeDefaultFeatures.addExtraEmeralds(generationSettings);
        BiomeDefaultFeatures.addInfestedStone(generationSettings);
        Music music = Musics.createGameMusic(isCherryGrove ? SoundEvents.MUSIC_BIOME_CHERRY_GROVE : SoundEvents.MUSIC_BIOME_MEADOW);
        if (isCherryGrove) {
            return biome(true, 0.5F, 0.8F, 6141935, 6141935, 11983713, 11983713, spawnSettings, generationSettings, music);
        }
        return biome(true, 0.5F, 0.8F, 937679, 329011, null, null, spawnSettings, generationSettings, music);
    }
    
    private static Biome biome(boolean hasPrecipitation, float skyColor, float downfall, MobSpawnSettings.Builder mobSpawnSettings, BiomeGenerationSettings.Builder generationSettings, @Nullable Music music) {
        return biome(hasPrecipitation, skyColor, downfall, 4159204, 329011, null, null, mobSpawnSettings, generationSettings, music);
    }

    private static Biome biome(boolean hasPrecipitation, float skyColor, float downfall, int waterColor, int waterFogColor, @Nullable Integer grassColorOverride, @Nullable Integer foliageColorOverride, MobSpawnSettings.Builder mobSpawnSettings, BiomeGenerationSettings.Builder generationSettings, @Nullable Music music) {
        BiomeSpecialEffects.Builder specialEffects = new BiomeSpecialEffects.Builder()
        	.waterColor(waterColor)
        	.waterFogColor(waterFogColor).fogColor(12638463).skyColor(calculateSkyColor(skyColor)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(music);
        if (grassColorOverride != null) {
            specialEffects.grassColorOverride(grassColorOverride);
        }
        if (foliageColorOverride != null) {
            specialEffects.foliageColorOverride(foliageColorOverride);
        }
        return new Biome.BiomeBuilder().hasPrecipitation(hasPrecipitation).temperature(skyColor).downfall(downfall).specialEffects(specialEffects.build()).mobSpawnSettings(mobSpawnSettings.build()).generationSettings(generationSettings.build()).build();
    }

    private static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder) {
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);
    }
    
    private static int calculateSkyColor(float f) {
        float g = f;
        g /= 3.0f;
        g = Mth.clamp(g, -1.0f, 1.0f);
        return Mth.hsvToRgb(0.62222224f - g * 0.05f, 0.5f + g * 0.1f, 1.0f);
    }
}
