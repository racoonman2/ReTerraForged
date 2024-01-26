package raccoonman.reterraforged.data.preset.settings;

import com.mojang.serialization.Codec;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.data.preset.PresetBiomeData;
import raccoonman.reterraforged.data.preset.PresetBiomeModifierData;
import raccoonman.reterraforged.data.preset.PresetConfiguredCarvers;
import raccoonman.reterraforged.data.preset.PresetConfiguredFeatures;
import raccoonman.reterraforged.data.preset.PresetData;
import raccoonman.reterraforged.data.preset.PresetDimensionTypes;
import raccoonman.reterraforged.data.preset.PresetNoiseData;
import raccoonman.reterraforged.data.preset.PresetNoiseGeneratorSettings;
import raccoonman.reterraforged.data.preset.PresetNoiseRouterData;
import raccoonman.reterraforged.data.preset.PresetPlacedFeatures;
import raccoonman.reterraforged.data.preset.PresetStructureRuleData;
import raccoonman.reterraforged.data.preset.PresetStructureSets;
import raccoonman.reterraforged.integration.terrablender.TBNoiseRouterData;
import raccoonman.reterraforged.registries.RTFRegistries;

public record Preset(PresetVersion version, WorldSettings world, SurfaceSettings surface, CaveSettings caves, ClimateSettings climate, TerrainSettings terrain, RiverSettings rivers, FilterSettings filters, MiscellaneousSettings miscellaneous) {
	public static final Codec<Preset> CODEC = PresetVersion.CODEC.dispatch("version", Preset::version, PresetVersion::codec);
	
	public Preset copy() {
		return new Preset(this.version, this.world.copy(), this.surface.copy(), this.caves.copy(), this.climate.copy(), this.terrain.copy(), this.rivers.copy(), this.filters.copy(), /* this.structures.copy(), */this.miscellaneous.copy());
	}

	public HolderLookup.Provider buildPatch(RegistryAccess registries) {
		RegistrySetBuilder builder = new RegistrySetBuilder();
		this.addPatch(builder, RTFRegistries.PRESET, PresetData::bootstrap);
		this.addPatch(builder, RTFRegistries.NOISE, PresetNoiseData::bootstrap);
		this.addPatch(builder, RTFRegistries.BIOME_MODIFIER, PresetBiomeModifierData::bootstrap);
		this.addPatch(builder, RTFRegistries.STRUCTURE_RULE, PresetStructureRuleData::bootstrap);
		this.addPatch(builder, Registries.CONFIGURED_FEATURE, (preset, ctx) -> {
			PresetConfiguredFeatures.bootstrap(preset, ctx);
		});
		this.addPatch(builder, Registries.CONFIGURED_CARVER, (preset, ctx) -> {
			PresetConfiguredCarvers.bootstrap(preset, ctx);	
		});
		this.addPatch(builder, Registries.STRUCTURE_SET, PresetStructureSets::bootstrap);
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
        void apply(Preset preset, BootstapContext<T> ctx);
	}
}
