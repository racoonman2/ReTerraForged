package raccoonman.reterraforged.data.worldgen.preset.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CaveSettings {
	public static final Codec<CaveSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("entranceCaveProbability").forGetter((o) -> o.entranceCaveProbability),
		Codec.FLOAT.fieldOf("cheeseCaveDepthOffset").forGetter((o) -> o.cheeseCaveDepthOffset),
		Codec.FLOAT.fieldOf("cheeseCaveProbability").forGetter((o) -> o.cheeseCaveProbability),
		Codec.FLOAT.fieldOf("spaghettiCaveProbability").forGetter((o) -> o.spaghettiCaveProbability),
		Codec.FLOAT.fieldOf("noodleCaveProbability").forGetter((o) -> o.noodleCaveProbability),
		Codec.FLOAT.fieldOf("caveCarverProbability").forGetter((o) -> o.caveCarverProbability),
		Codec.FLOAT.fieldOf("deepCaveCarverProbability").forGetter((o) -> o.deepCaveCarverProbability),
		Codec.FLOAT.fieldOf("ravineCarverProbability").forGetter((o) -> o.ravineCarverProbability),
		Codec.BOOL.fieldOf("largeOreVeins").forGetter((o) -> o.largeOreVeins),
		Codec.BOOL.fieldOf("legacyCarverDistribution").forGetter((o) -> o.legacyCarverDistribution)
	).apply(instance, CaveSettings::new));

	public float entranceCaveProbability;
	public float cheeseCaveDepthOffset;
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

	public CaveSettings(float entranceCaveProbability, float cheeseCaveDepthOffset, float cheeseCaveProbability, float spaghettiCaveProbability, float noodleCaveProbability, float caveCarverProbability, float deepCaveCarverProbability, float ravineProbability, boolean largeOreVeins, boolean legacyCarverDistribution) {
		this.entranceCaveProbability = entranceCaveProbability;
		this.cheeseCaveDepthOffset = cheeseCaveDepthOffset;
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
		return new CaveSettings(this.entranceCaveProbability, this.cheeseCaveDepthOffset, this.cheeseCaveProbability, this.spaghettiCaveProbability, this.noodleCaveProbability, this.caveCarverProbability, this.deepCaveCarverProbability, this.ravineCarverProbability, this.largeOreVeins, this.legacyCarverDistribution);
	}
}