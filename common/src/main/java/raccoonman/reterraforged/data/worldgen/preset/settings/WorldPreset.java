package raccoonman.reterraforged.data.worldgen.preset.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.data.worldgen.compat.terrablender.TBNoiseRouterData;
import raccoonman.reterraforged.data.worldgen.preset.PresetBiomeData;
import raccoonman.reterraforged.data.worldgen.preset.PresetBiomeModifierData;
import raccoonman.reterraforged.data.worldgen.preset.PresetConfiguredCarvers;
import raccoonman.reterraforged.data.worldgen.preset.PresetConfiguredFeatures;
import raccoonman.reterraforged.data.worldgen.preset.PresetDimensionTypes;
import raccoonman.reterraforged.data.worldgen.preset.PresetNoiseData;
import raccoonman.reterraforged.data.worldgen.preset.PresetNoiseGeneratorSettings;
import raccoonman.reterraforged.data.worldgen.preset.PresetNoiseRouterData;
import raccoonman.reterraforged.data.worldgen.preset.PresetPlacedFeatures;
import raccoonman.reterraforged.data.worldgen.preset.PresetStructureRuleData;
import raccoonman.reterraforged.registries.RTFRegistries;

public record WorldPreset(WorldSettings world, SurfaceSettings surface, CaveSettings caves, ClimateSettings climate, TerrainSettings terrain, RiverSettings rivers, FilterSettings filters, StructureSettings structures, MiscellaneousSettings miscellaneous) {
	public static final Codec<WorldPreset> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		WorldSettings.CODEC.fieldOf("world").forGetter(WorldPreset::world),
		SurfaceSettings.CODEC.optionalFieldOf("surface", new SurfaceSettings(new SurfaceSettings.Erosion(30, 140, 40, 95, 95, 0.65F, 0.475F, 0.4F, 0.45F, 6.0F, 3.0F))).forGetter(WorldPreset::surface),
		CaveSettings.CODEC.optionalFieldOf("caves", new CaveSettings(0.0F, 1.5625F, 1.0F, 1.0F, 1.0F, 0.14285715F, 0.07F, 0.02F, true, false)).forGetter(WorldPreset::caves),
		ClimateSettings.CODEC.fieldOf("climate").forGetter(WorldPreset::climate),
		TerrainSettings.CODEC.fieldOf("terrain").forGetter(WorldPreset::terrain),
		RiverSettings.CODEC.fieldOf("rivers").forGetter(WorldPreset::rivers),
		FilterSettings.CODEC.fieldOf("filters").forGetter(WorldPreset::filters),
		StructureSettings.CODEC.fieldOf("structures").forGetter(WorldPreset::structures),
		MiscellaneousSettings.CODEC.fieldOf("miscellaneous").forGetter(WorldPreset::miscellaneous)
	).apply(instance, WorldPreset::new));
	
	@Deprecated
	public static final ResourceKey<WorldPreset> KEY = RTFRegistries.createKey(RTFRegistries.PRESET, "preset");
	
	public WorldPreset copy() {
		return new WorldPreset(this.world.copy(), this.surface.copy(), this.caves.copy(), this.climate.copy(), this.terrain.copy(), this.rivers.copy(), this.filters.copy(), this.structures.copy(), this.miscellaneous.copy());
	}

	public HolderLookup.Provider buildPatch(RegistryAccess registries) {
		RegistrySetBuilder builder = new RegistrySetBuilder();
		this.addPatch(builder, RTFRegistries.PRESET, (preset, ctx) -> ctx.register(KEY, preset));
		this.addPatch(builder, RTFRegistries.NOISE, PresetNoiseData::bootstrap);
		this.addPatch(builder, RTFRegistries.BIOME_MODIFIER, PresetBiomeModifierData::bootstrap);
		this.addPatch(builder, RTFRegistries.STRUCTURE_RULE, PresetStructureRuleData::bootstrap);
		this.addPatch(builder, Registries.CONFIGURED_FEATURE, (preset, ctx) -> {
			PresetConfiguredFeatures.bootstrap(preset, ctx);
		});
		this.addPatch(builder, Registries.CONFIGURED_CARVER, (preset, ctx) -> {
			PresetConfiguredCarvers.bootstrap(preset, ctx);	
		});
		this.addPatch(builder, Registries.PLACED_FEATURE, PresetPlacedFeatures::bootstrap);
		this.addPatch(builder, Registries.BIOME, PresetBiomeData::bootstrap);
		this.addPatch(builder, Registries.DIMENSION_TYPE, PresetDimensionTypes::bootstrap);
		this.addPatch(builder, Registries.DENSITY_FUNCTION, (preset, ctx) -> {
			PresetNoiseRouterData.bootstrap(preset, ctx);
			TBNoiseRouterData.bootstrap(ctx);
		});
		this.addPatch(builder, Registries.NOISE_SETTINGS, PresetNoiseGeneratorSettings::bootstrap);
		return builder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), registries);
	}
	
	private <T> void addPatch(RegistrySetBuilder builder, ResourceKey<? extends Registry<T>> key, Patch<T> patch) {
    	builder.add(key, (ctx) -> {
    		patch.apply(this, ctx);
    	});
    }
    
	private interface Patch<T> {
        void apply(WorldPreset preset, BootstapContext<T> ctx);
	}
}
