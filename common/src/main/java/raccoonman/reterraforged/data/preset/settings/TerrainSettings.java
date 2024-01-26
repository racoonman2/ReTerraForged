package raccoonman.reterraforged.data.preset.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TerrainSettings {
	public static final Codec<TerrainSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		General.CODEC.fieldOf("general").forGetter((o) -> o.general),
		Terrain.CODEC.fieldOf("steppe").forGetter((o) -> o.steppe),
		Terrain.CODEC.fieldOf("plains").forGetter((o) -> o.plains),
		Terrain.CODEC.fieldOf("hills").forGetter((o) -> o.hills),
		Terrain.CODEC.fieldOf("dales").forGetter((o) -> o.dales),
		Terrain.CODEC.fieldOf("plateau").forGetter((o) -> o.plateau),
		Terrain.CODEC.fieldOf("badlands").forGetter((o) -> o.badlands),
		Terrain.CODEC.fieldOf("torridonian").forGetter((o) -> o.torridonian),
		Terrain.CODEC.fieldOf("mountains").forGetter((o) -> o.mountains),
		Terrain.CODEC.fieldOf("volcano").forGetter((o) -> o.volcano)		
	).apply(instance, TerrainSettings::new));
	
    public General general;
    public Terrain steppe;
    public Terrain plains;
    public Terrain hills;
    public Terrain dales;
    public Terrain plateau;
    public Terrain badlands;
    public Terrain torridonian;
    public Terrain mountains;
    public Terrain volcano;
    
    public TerrainSettings(General general, Terrain steppe, Terrain plains, Terrain hills, Terrain dales, Terrain plateau, Terrain badlands, Terrain torridonian, Terrain mountains, Terrain volcano) {
    	this.general = general;
    	this.steppe = steppe;
    	this.plains = plains;
    	this.hills = hills;
    	this.dales = dales;
    	this.plateau = plateau;
    	this.badlands = badlands;
    	this.torridonian = torridonian;
    	this.mountains = mountains;
    	this.volcano = volcano;
    }
    
    public TerrainSettings copy() {
    	return new TerrainSettings(this.general.copy(), this.steppe.copy(), this.plains.copy(), this.hills.copy(), this.dales.copy(), this.plateau.copy(), this.badlands.copy(), this.torridonian.copy(), this.mountains.copy(), this.volcano.copy());
    }
    
    public static class General {
    	public static final Codec<General> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.INT.fieldOf("terrainSeedOffset").forGetter((o) -> o.terrainSeedOffset),
    		Codec.INT.fieldOf("terrainRegionSize").forGetter((o) -> o.terrainRegionSize),
    		Codec.FLOAT.fieldOf("globalVerticalScale").forGetter((o) -> o.globalVerticalScale),
    		Codec.FLOAT.fieldOf("globalHorizontalScale").forGetter((o) -> o.globalHorizontalScale),
    		Codec.BOOL.fieldOf("fancyMountains").forGetter((o) -> o.fancyMountains),
    		Codec.BOOL.optionalFieldOf("legacyWorldGen", true).forGetter((o) -> o.legacyWorldGen)
    	).apply(instance, General::new));
    	
        public int terrainSeedOffset;
        public int terrainRegionSize;
        public float globalVerticalScale;
        public float globalHorizontalScale;
        public boolean fancyMountains;
        public boolean legacyWorldGen;
        
        public General(int terrainSeedOffset, int terrainRegionSize, float globalVerticalScale, float globalHorizontalScale, boolean fancyMountains, boolean legacyWorldGen) {
        	this.terrainSeedOffset = terrainSeedOffset;
        	this.terrainRegionSize = terrainRegionSize;
        	this.globalVerticalScale = globalVerticalScale;
        	this.globalHorizontalScale = globalHorizontalScale;
        	this.fancyMountains = fancyMountains;
        	this.legacyWorldGen = legacyWorldGen;
        }
        
        public General copy() {
        	return new General(this.terrainSeedOffset, this.terrainRegionSize, this.globalVerticalScale, this.globalHorizontalScale, this.fancyMountains, this.legacyWorldGen);
        }
    }
    
    public static class Terrain {
    	public static final Codec<Terrain> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.FLOAT.fieldOf("weight").forGetter((o) -> o.weight),
    		Codec.FLOAT.fieldOf("baseScale").forGetter((o) -> o.baseScale),
    		Codec.FLOAT.fieldOf("verticalScale").forGetter((o) -> o.verticalScale),
    		Codec.FLOAT.fieldOf("horizontalScale").forGetter((o) -> o.horizontalScale)
    	).apply(instance, Terrain::new));
    	
        public float weight;
        public float baseScale;
        public float verticalScale;
        public float horizontalScale;
        
        public Terrain(float weight, float baseScale, float verticalScale, float horizontalScale) {
            this.weight = weight;
            this.baseScale = baseScale;
            this.verticalScale = verticalScale;
            this.horizontalScale = horizontalScale;
        }
        
        public Terrain copy() {
        	return new Terrain(this.weight, this.baseScale, this.verticalScale, this.horizontalScale);
        }
    }
}
