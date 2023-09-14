package raccoonman.reterraforged.common.data.preset;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MiscellaneousSettings {
	public static final Codec<MiscellaneousSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.BOOL.fieldOf("smoothLayerDecorator").forGetter((s) -> s.smoothLayerDecorator),
		Codec.INT.fieldOf("strataRegionSize").forGetter((s) -> s.strataRegionSize),
		Codec.BOOL.fieldOf("strataDecorator").forGetter((s) -> s.strataDecorator),
		Codec.BOOL.fieldOf("oreCompatibleStoneOnly").forGetter((s) -> s.oreCompatibleStoneOnly),
		Codec.BOOL.fieldOf("erosionDecorator").forGetter((s) -> s.erosionDecorator),
		Codec.BOOL.fieldOf("plainStoneErosion").forGetter((s) -> s.plainStoneErosion),
		Codec.BOOL.fieldOf("naturalSnowDecorator").forGetter((s) -> s.naturalSnowDecorator),
		Codec.BOOL.fieldOf("customBiomeFeatures").forGetter((s) -> s.customBiomeFeatures),
		Codec.BOOL.fieldOf("vanillaLakes").forGetter((s) -> s.vanillaLakes),
		Codec.BOOL.fieldOf("vanillaSprings").forGetter((s) -> s.vanillaSprings),
		Codec.BOOL.fieldOf("vanillaLavaLakes").forGetter((s) -> s.vanillaLavaLakes),
		Codec.BOOL.fieldOf("vanillaLavaSprings").forGetter((s) -> s.vanillaLavaSprings)
	).apply(instance, MiscellaneousSettings::new));
	
	public boolean smoothLayerDecorator;
	public int strataRegionSize;
	public boolean strataDecorator;
	public boolean oreCompatibleStoneOnly;
	public boolean erosionDecorator;
	public boolean plainStoneErosion;
	public boolean naturalSnowDecorator;
	public boolean customBiomeFeatures;
	public boolean vanillaLakes;
	public boolean vanillaSprings;
	public boolean vanillaLavaLakes;
	public boolean vanillaLavaSprings;
	
	public MiscellaneousSettings(
		boolean smoothLayerDecorator,
		int strataRegionSize,
		boolean strataDecorator,
		boolean oreCompatibleStoneOnly,
		boolean erosionDecorator,
		boolean plainStoneErosion,
		boolean naturalSnowDecorator,
		boolean customBiomeFeatures,
		boolean vanillaLakes,
		boolean vanillaSprings,
		boolean vanillaLavaLakes,
		boolean vanillaLavaSprings
	) {
		this.smoothLayerDecorator = smoothLayerDecorator;
		this.strataRegionSize = strataRegionSize;
		this.strataDecorator = strataDecorator;
		this.oreCompatibleStoneOnly = oreCompatibleStoneOnly;
		this.erosionDecorator = erosionDecorator;
		this.plainStoneErosion = plainStoneErosion;
		this.naturalSnowDecorator = naturalSnowDecorator;
		this.customBiomeFeatures = customBiomeFeatures;
		this.vanillaLakes = vanillaLakes;
		this.vanillaSprings = vanillaSprings;
		this.vanillaLavaLakes = vanillaLavaLakes;
		this.vanillaLavaSprings = vanillaLavaSprings;
	}
	
	public MiscellaneousSettings copy() {
		return new MiscellaneousSettings(this.smoothLayerDecorator, this.strataRegionSize, this.strataDecorator, this.oreCompatibleStoneOnly, this.erosionDecorator, this.plainStoneErosion, this.naturalSnowDecorator, this.customBiomeFeatures, this.vanillaLakes, this.vanillaSprings, this.vanillaLavaLakes, this.vanillaLavaSprings);
	}
	
	public static MiscellaneousSettings makeDefault() {
		return new MiscellaneousSettings(true, 600, true, true, true, false, true, true, false, true, true, true);
	}
}
