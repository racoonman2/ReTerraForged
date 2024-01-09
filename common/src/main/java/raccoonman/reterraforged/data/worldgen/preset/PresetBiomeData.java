package raccoonman.reterraforged.data.worldgen.preset;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.worldgen.preset.settings.MiscellaneousSettings;
import raccoonman.reterraforged.data.worldgen.preset.settings.Preset;
import raccoonman.reterraforged.world.worldgen.biome.RTFBiomes;

public final class PresetBiomeData {
    public static final ResourceKey<Biome> BRYCE = createKey("bryce");
    public static final ResourceKey<Biome> COLD_STEPPE = createKey("cold_steppe");
    public static final ResourceKey<Biome> COLD_MARSHLAND = createKey("cold_marshland");
    public static final ResourceKey<Biome> FIR_FOREST = createKey("fir_forest");
    public static final ResourceKey<Biome> FLOWER_PLAINS = createKey("flower_plains");
    public static final ResourceKey<Biome> FROZEN_LAKE = createKey("frozen_lake");
    public static final ResourceKey<Biome> FROZEN_MARSH = createKey("frozen_marsh");
    public static final ResourceKey<Biome> LAKE = createKey("lake");
    public static final ResourceKey<Biome> MARSHLAND = createKey("marshland");
    public static final ResourceKey<Biome> SAVANNA_SCRUB = createKey("savanna_scrub");
    public static final ResourceKey<Biome> SHATTERED_SAVANNA_SCRUB = createKey("shattered_savanna_scrub");
    public static final ResourceKey<Biome> SNOWY_FIR_FOREST = createKey("snowy_fir_forest");
    public static final ResourceKey<Biome> SNOWY_TAIGA_SCRUB = createKey("snowy_taiga_scrub");
    public static final ResourceKey<Biome> STEPPE = createKey("steppe");
    public static final ResourceKey<Biome> STONE_FOREST = createKey("stone_forest");
    public static final ResourceKey<Biome> TAIGA_SCRUB = createKey("taiga_scrub");
    public static final ResourceKey<Biome> WARM_BEACH = createKey("warm_beach");
	
	public static void bootstrap(Preset preset, BootstapContext<Biome> ctx) {
		MiscellaneousSettings miscellaneousSettings = preset.miscellaneous();
		
		if(miscellaneousSettings.customBiomeFeatures) {
			HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
			HolderGetter<ConfiguredWorldCarver<?>> configuredWorldCarvers = ctx.lookup(Registries.CONFIGURED_CARVER);

		}
	}
	
    private static ResourceKey<Biome> createKey(String string) {
        return ResourceKey.create(Registries.BIOME, RTFCommon.location(string));
    }
}
