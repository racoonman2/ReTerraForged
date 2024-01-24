package raccoonman.reterraforged.data.preset;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CaveSettings {
	public static final Codec<CaveSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("surfaceDensityThreshold").forGetter((o) -> o.surfaceDensityThreshold),
		Codec.FLOAT.fieldOf("entranceCaveProbability").forGetter((o) -> o.entranceCaveProbability),
		Codec.FLOAT.fieldOf("cheeseCaveProbability").forGetter((o) -> o.cheeseCaveProbability),
		Codec.FLOAT.fieldOf("spaghettiCaveProbability").forGetter((o) -> o.spaghettiCaveProbability),
		Codec.FLOAT.fieldOf("noodleCaveProbability").forGetter((o) -> o.noodleCaveProbability),
		Codec.FLOAT.fieldOf("caveCarverProbability").forGetter((o) -> o.caveCarverProbability),
		Codec.FLOAT.fieldOf("deepCaveCarverProbability").forGetter((o) -> o.deepCaveCarverProbability),
		Codec.FLOAT.fieldOf("ravineCarverProbability").forGetter((o) -> o.ravineCarverProbability),
		Codec.BOOL.fieldOf("largeOreVeins").forGetter((o) -> o.largeOreVeins),
		Codec.BOOL.fieldOf("legacyCarverDistribution").forGetter((o) -> o.legacyCarverDistribution)
	).apply(instance, CaveSettings::new));

	public float surfaceDensityThreshold;
	public float entranceCaveProbability;
	public float cheeseCaveProbability;
	public float spaghettiCaveProbability;
	public float noodleCaveProbability;
	public float caveCarverProbability;
	public float deepCaveCarverProbability;
	public float ravineCarverProbability;
	public boolean largeOreVeins;
	public boolean legacyCarverDistribution;
	
	//TODO
	public boolean minCaveBiomeDepth;

	public CaveSettings(float surfaceDensityThreshold, float entranceCaveProbability, float cheeseCaveProbability, float spaghettiCaveProbability, float noodleCaveProbability, float caveCarverProbability, float deepCaveCarverProbability, float ravineProbability, boolean largeOreVeins, boolean legacyCarverDistribution) {
		this.surfaceDensityThreshold = surfaceDensityThreshold;
		this.entranceCaveProbability = entranceCaveProbability;
		this.cheeseCaveProbability = cheeseCaveProbability;
		this.spaghettiCaveProbability = spaghettiCaveProbability;
		this.noodleCaveProbability = noodleCaveProbability;
		this.caveCarverProbability = caveCarverProbability;
		this.deepCaveCarverProbability = deepCaveCarverProbability;
		this.ravineCarverProbability = ravineProbability;
		this.largeOreVeins = largeOreVeins;
		this.legacyCarverDistribution = legacyCarverDistribution;
	}
	
	public CaveSettings copy() {
		return new CaveSettings(this.surfaceDensityThreshold, this.entranceCaveProbability, this.cheeseCaveProbability, this.spaghettiCaveProbability, this.noodleCaveProbability, this.caveCarverProbability, this.deepCaveCarverProbability, this.ravineCarverProbability, this.largeOreVeins, this.legacyCarverDistribution);
	}
}