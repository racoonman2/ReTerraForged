package raccoonman.reterraforged.data.preset.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CaveSettings {
	public static final Codec<CaveSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("surfaceDensityThreshold").forGetter((o) -> o.surfaceDensityThreshold),
		Codec.FLOAT.fieldOf("entranceCaveChance").forGetter((o) -> o.entranceCaveChance),
		Codec.FLOAT.fieldOf("cheeseCaveChance").forGetter((o) -> o.cheeseCaveChance),
		Codec.FLOAT.fieldOf("spaghettiCaveChance").forGetter((o) -> o.spaghettiCaveChance),
		Codec.FLOAT.fieldOf("noodleCaveChance").forGetter((o) -> o.noodleCaveChance),
		Codec.FLOAT.fieldOf("caveCarverChance").forGetter((o) -> o.caveCarverChance),
		Codec.FLOAT.fieldOf("deepCaveCarverChance").forGetter((o) -> o.deepCaveCarverChance),
		Codec.FLOAT.fieldOf("ravineCarverChance").forGetter((o) -> o.ravineCarverChance),
		Codec.BOOL.fieldOf("largeOreVeins").forGetter((o) -> o.largeOreVeins),
		Codec.BOOL.fieldOf("legacyCarverDistribution").forGetter((o) -> o.legacyCarverDistribution)
	).apply(instance, CaveSettings::new));

	public float surfaceDensityThreshold;
	public float entranceCaveChance;
	public float cheeseCaveChance;
	public float spaghettiCaveChance;
	public float noodleCaveChance;
	public float caveCarverChance;
	public float deepCaveCarverChance;
	public float ravineCarverChance;
	public boolean largeOreVeins;
	public boolean legacyCarverDistribution;
	
	//TODO
	public boolean minCaveBiomeDepth;

	public CaveSettings(float surfaceDensityThreshold, float entranceCaveChance, float cheeseCaveChance, float spaghettiCaveChance, float noodleCaveChance, float caveCarverChance, float deepCaveCarverChance, float ravineChance, boolean largeOreVeins, boolean legacyCarverDistribution) {
		this.surfaceDensityThreshold = surfaceDensityThreshold;
		this.entranceCaveChance = entranceCaveChance;
		this.cheeseCaveChance = cheeseCaveChance;
		this.spaghettiCaveChance = spaghettiCaveChance;
		this.noodleCaveChance = noodleCaveChance;
		this.caveCarverChance = caveCarverChance;
		this.deepCaveCarverChance = deepCaveCarverChance;
		this.ravineCarverChance = ravineChance;
		this.largeOreVeins = largeOreVeins;
		this.legacyCarverDistribution = legacyCarverDistribution;
	}
	
	public CaveSettings copy() {
		return new CaveSettings(this.surfaceDensityThreshold, this.entranceCaveChance, this.cheeseCaveChance, this.spaghettiCaveChance, this.noodleCaveChance, this.caveCarverChance, this.deepCaveCarverChance, this.ravineCarverChance, this.largeOreVeins, this.legacyCarverDistribution);
	}
}