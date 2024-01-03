package raccoonman.reterraforged.data.worldgen.preset;

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
import raccoonman.reterraforged.data.worldgen.RTFBiomeData;
import raccoonman.reterraforged.data.worldgen.BiomeModifierData;
import raccoonman.reterraforged.data.worldgen.RTFConfiguredCarvers;
import raccoonman.reterraforged.data.worldgen.RTFConfiguredFeatures;
import raccoonman.reterraforged.data.worldgen.RTFDimensionTypes;
import raccoonman.reterraforged.data.worldgen.NoiseData;
import raccoonman.reterraforged.data.worldgen.RTFNoiseGeneratorSettings;
import raccoonman.reterraforged.data.worldgen.RTFNoiseRouterData;
import raccoonman.reterraforged.data.worldgen.RTFPlacedFeatures;
import raccoonman.reterraforged.registries.RTFRegistries;

public record Preset(WorldSettings world, CaveSettings caves, ClimateSettings climate, TerrainSettings terrain, RiverSettings rivers, FilterSettings filters, StructureSettings structures, MiscellaneousSettings miscellaneous) {
	public static final Codec<Preset> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		WorldSettings.CODEC.fieldOf("world").forGetter(Preset::world),
		CaveSettings.CODEC.optionalFieldOf("caves", new CaveSettings(0.0F, 1.5625F, 1.0F, 1.0F, 1.0F, 0.14285715F, 0.07F, 0.02F, true, false)).forGetter((o) -> o.caves),
		ClimateSettings.CODEC.fieldOf("climate").forGetter(Preset::climate),
		TerrainSettings.CODEC.fieldOf("terrain").forGetter(Preset::terrain),
		RiverSettings.CODEC.fieldOf("rivers").forGetter(Preset::rivers),
		FilterSettings.CODEC.fieldOf("filters").forGetter(Preset::filters),
		StructureSettings.CODEC.fieldOf("structures").forGetter(Preset::structures),
		MiscellaneousSettings.CODEC.fieldOf("miscellaneous").forGetter(Preset::miscellaneous)
	).apply(instance, Preset::new));
	
	@Deprecated
	public static final ResourceKey<Preset> KEY = RTFRegistries.createKey(RTFRegistries.PRESET, "preset");
	
	public Preset copy() {
		return new Preset(this.world.copy(), this.caves.copy(), this.climate.copy(), this.terrain.copy(), this.rivers.copy(), this.filters.copy(), this.structures.copy(), this.miscellaneous.copy());
	}

	public HolderLookup.Provider buildPatch(RegistryAccess registries) {
		RegistrySetBuilder builder = new RegistrySetBuilder();
		this.addPatch(builder, RTFRegistries.PRESET, (preset, ctx) -> ctx.register(KEY, preset));
		this.addPatch(builder, RTFRegistries.NOISE, NoiseData::bootstrap);
		this.addPatch(builder, RTFRegistries.BIOME_MODIFIER, BiomeModifierData::bootstrap);
		this.addPatch(builder, Registries.CONFIGURED_FEATURE, (preset, ctx) -> {
			RTFConfiguredFeatures.bootstrap(preset, ctx);
		});
		this.addPatch(builder, Registries.CONFIGURED_CARVER, (preset, ctx) -> {
			RTFConfiguredCarvers.bootstrap(preset, ctx);	
		});
		this.addPatch(builder, Registries.PLACED_FEATURE, RTFPlacedFeatures::bootstrap);
		this.addPatch(builder, Registries.BIOME, RTFBiomeData::bootstrap);
		this.addPatch(builder, Registries.DIMENSION_TYPE, RTFDimensionTypes::bootstrap);
		this.addPatch(builder, Registries.DENSITY_FUNCTION, RTFNoiseRouterData::bootstrap);
		this.addPatch(builder, Registries.NOISE_SETTINGS, RTFNoiseGeneratorSettings::bootstrap);
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
