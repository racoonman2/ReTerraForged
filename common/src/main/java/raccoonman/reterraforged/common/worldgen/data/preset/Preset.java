package raccoonman.reterraforged.common.worldgen.data.preset;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.RTFBiomeData;
import raccoonman.reterraforged.common.worldgen.data.RTFDimensionTypes;
import raccoonman.reterraforged.common.worldgen.data.RTFNoiseData;
import raccoonman.reterraforged.common.worldgen.data.RTFNoiseGeneratorSettings;
import raccoonman.reterraforged.common.worldgen.data.RTFNoiseRouterData;
import raccoonman.reterraforged.common.worldgen.data.RTFPlacedFeatures;
import raccoonman.reterraforged.common.worldgen.data.RTFStructureSets;

public record Preset(WorldSettings world, ClimateSettings climate, TerrainSettings terrain, RiverSettings rivers, FilterSettings filters, StructureSettings structures, MiscellaneousSettings miscellaneous) {
	public static final ResourceKey<Preset> KEY = ResourceKey.create(RTFRegistries.PRESET, RTFRegistries.resolve("preset"));
	public static final Codec<Preset> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		WorldSettings.CODEC.fieldOf("world").forGetter(Preset::world),
		ClimateSettings.CODEC.fieldOf("climate").forGetter(Preset::climate),
		TerrainSettings.CODEC.fieldOf("terrain").forGetter(Preset::terrain),
		RiverSettings.CODEC.fieldOf("rivers").forGetter(Preset::rivers),
		FilterSettings.CODEC.fieldOf("filters").forGetter(Preset::filters),
		StructureSettings.CODEC.fieldOf("structures").forGetter(Preset::structures),
		MiscellaneousSettings.CODEC.fieldOf("miscellaneous").forGetter(Preset::miscellaneous)
	).apply(instance, Preset::new));
	public static final Codec<Holder<Preset>> CODEC = RegistryFileCodec.create(RTFRegistries.PRESET, DIRECT_CODEC);
	
	public Preset copy() {
		return new Preset(this.world.copy(), this.climate.copy(), this.terrain.copy(), this.rivers.copy(), this.filters.copy(), this.structures.copy(), this.miscellaneous.copy());
	}

	@Deprecated // all the other makeDefault methods are deprecated too but im too lazy to annotate all of them
	public static Preset makeDefault() {
		return new Preset(WorldSettings.makeDefault(), ClimateSettings.makeDefault(), TerrainSettings.makeDefault(), RiverSettings.makeDefault(), FilterSettings.makeDefault(), StructureSettings.makeDefault(), MiscellaneousSettings.makeDefault()); 
	}
	
	public HolderLookup.Provider buildPatch(RegistryAccess.Frozen registries) {
		RegistrySetBuilder builder = new RegistrySetBuilder();	
		builder.add(RTFRegistries.PRESET, (ctx) -> ctx.register(KEY, this));
		builder.add(RTFRegistries.NOISE, (ctx) -> RTFNoiseData.bootstrap(ctx, this));
		builder.add(Registries.PLACED_FEATURE, (ctx) -> RTFPlacedFeatures.bootstrap(ctx, this));
		builder.add(Registries.STRUCTURE_SET, (ctx) -> RTFStructureSets.bootstrap(ctx, this));
		builder.add(Registries.BIOME, (ctx) -> RTFBiomeData.bootstrap(ctx, this));
		builder.add(Registries.DIMENSION_TYPE, (ctx) -> RTFDimensionTypes.bootstrap(ctx, this));
		builder.add(Registries.DENSITY_FUNCTION, (ctx) -> RTFNoiseRouterData.bootstrap(ctx, this));
		builder.add(Registries.NOISE_SETTINGS, (ctx) -> RTFNoiseGeneratorSettings.bootstrap(ctx, this));
		return builder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), registries);
	}
}
