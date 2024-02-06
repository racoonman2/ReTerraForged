package raccoonman.reterraforged.data.preset.settings;

import com.mojang.serialization.Codec;

public class CaveSettings {
	public static final Codec<CaveSettings> CODEC = Codec.unit(new CaveSettings(new Pillar()));
//	public static final Codec<CaveSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//		
//	).apply(instance, CaveSettings::new));

	public boolean largeOreVeins = true;
	
	public Pillar pillars;
	
	//TODO
	public boolean depthOffset;

	public CaveSettings(Pillar pillars) {
		this.pillars = pillars;
	}
	
	public CaveSettings copy() {
		return new CaveSettings(this.pillars.copy());
	}
	
	public static class Cheese {
		
		public float multiplier;
	}
	
	public static class Spaghetti {
		
		public float multiplier;
	}
	
	public static class Noodle {
		
		public float multiplier;
		
	}
	
	public static class Pillar {
		
		public float multiplier;
		
		public Pillar copy() {
			return new Pillar();
		}
	}
	
	public static class Carver {

	}
}