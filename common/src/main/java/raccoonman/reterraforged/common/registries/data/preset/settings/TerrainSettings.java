package raccoonman.reterraforged.common.registries.data.preset.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

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
	
	public static final TerrainSettings DEFAULT = new TerrainSettings(
		General.DEFAULT,
		new Terrain(1.0F, 1.0F, 1.0F, 1.0F), 
		new Terrain(2.0F, 1.0F, 1.0F, 1.0F), 
		new Terrain(2.0F, 1.0F, 1.0F, 1.0F),
		new Terrain(1.5F, 1.0F, 1.0F, 1.0F),
		new Terrain(1.5F, 1.0F, 1.0F, 1.0F), 
		new Terrain(1.0F, 1.0F, 1.0F, 1.0F), 
		new Terrain(2.0F, 1.0F, 1.0F, 1.0F), 
		new Terrain(2.5F, 1.0F, 1.0F, 1.0F),
		new Terrain(5.0F, 1.0F, 1.0F, 1.0F)
    );
	
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
    
    public static class General {
    	public static final Codec<General> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.INT.fieldOf("terrainSeedOffset").forGetter((o) -> o.terrainSeedOffset),
    		Codec.INT.fieldOf("terrainRegionSize").forGetter((o) -> o.terrainRegionSize),
    		Codec.FLOAT.fieldOf("globalVerticalScale").forGetter((o) -> o.globalVerticalScale),
    		Codec.FLOAT.fieldOf("globalHorizontalScale").forGetter((o) -> o.globalHorizontalScale),
    		Codec.BOOL.fieldOf("fancyMountains").forGetter((o) -> o.fancyMountains)
    	).apply(instance, General::new));
    	
    	public static final General DEFAULT = new General(0, 1200, 0.98F, 1.0F, true);
    	
        public int terrainSeedOffset;
        public int terrainRegionSize;
        public float globalVerticalScale;
        public float globalHorizontalScale;
        public boolean fancyMountains;
        
        public General(int terrainSeedOffset, int terrainRegionSize, float globalVerticalScale, float globalHorizontalScale, boolean fancyMountains) {
        	this.terrainSeedOffset = terrainSeedOffset;
        	this.terrainRegionSize = terrainRegionSize;
        	this.globalVerticalScale = globalVerticalScale;
        	this.globalHorizontalScale = globalHorizontalScale;
        	this.fancyMountains = fancyMountains;
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
        
        public Noise apply(double bias, double scale, Noise module) {
            double moduleBias = bias * this.baseScale;
            double moduleScale = scale * this.verticalScale;
            Noise outputModule = module.scale(moduleScale).bias(moduleBias);
            return clamp(outputModule);
        }
        
        private static Noise clamp(Noise module) {
            if (module.minValue() < 0.0F || module.maxValue() > 1.0F) {
                return module.clamp(0.0F, 1.0F);
            }
            return module;
        }
    }
}
