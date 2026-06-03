package raccoonman.reterraforged.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class CBiomeTags {
	public static final TagKey<Biome> AQUATIC = tag("aquatic");
	public static final TagKey<Biome> BEACH = tag("beach");
	public static final TagKey<Biome> COLD = tag("cold");
	public static final TagKey<Biome> CONIFEROUS = tag("coniferous");
	public static final TagKey<Biome> DENSE = tag("dense");
	public static final TagKey<Biome> HOT = tag("hot");
	public static final TagKey<Biome> DRY = tag("dry");
	public static final TagKey<Biome> ICY = tag("icy");
	public static final TagKey<Biome> PLAINS = tag("plains");
	public static final TagKey<Biome> PLATEAUS = tag("plateaus");
	public static final TagKey<Biome> RIVER = tag("river");
	public static final TagKey<Biome> SANDY = tag("sandy");
	public static final TagKey<Biome> SAVANNA = tag("savanna");
	public static final TagKey<Biome> SHORE = tag("shore");
	public static final TagKey<Biome> SNOWY = tag("snowy");
	public static final TagKey<Biome> SNOWY_FORESTS = tag("snowy_forests");
	public static final TagKey<Biome> SWAMPS = tag("swamps");
	public static final TagKey<Biome> TAIGAS = tag("taigas");
	public static final TagKey<Biome> WATER = tag("water");
	public static final TagKey<Biome> WET = tag("wet");

    private static TagKey<Biome> tag(String path) {
    	return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("c", path));
    }
}
