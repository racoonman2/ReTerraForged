package raccoonman.reterraforged.data.preset.settings;

import com.mojang.serialization.Codec;

public class CaveSettings {
	public static final Codec<CaveSettings> CODEC = Codec.unit(new CaveSettings());
//	public static final Codec<CaveSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//		
//	).apply(instance, CaveSettings::new));

	public boolean largeOreVeins = true;

	public CaveSettings() {
	}
	
	public CaveSettings copy() {
		return new CaveSettings();
	}
	
//	public static class Pillar {
//		
//		public float multiplier;
//		
//		public Pillar copy() {
//			return new Pillar();
//		}
//	}
//	
	public static class CarverCave {
		public float probability;
	}
	
	public static class Canyon {
		
	}
}