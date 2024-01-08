package raccoonman.reterraforged.world.worldgen.cell.biome.type;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import raccoonman.reterraforged.world.worldgen.biome.Humidity;
import raccoonman.reterraforged.world.worldgen.biome.Temperature;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;

public enum BiomeType {
    TROPICAL_RAINFOREST(new Color(7, 83, 48), new Color(7, 83, 48), range(Temperature.LEVEL_3, Temperature.LEVEL_3, Humidity.LEVEL_3, Humidity.LEVEL_4)), 
    SAVANNA(new Color(151, 165, 39), new Color(151, 165, 39), range(Temperature.LEVEL_3, Temperature.LEVEL_3, Humidity.LEVEL_0, Humidity.LEVEL_1)),
    DESERT(new Color(200, 113, 55), new Color(200, 113, 55), range(Temperature.LEVEL_4, Temperature.LEVEL_4, Humidity.LEVEL_0, Humidity.LEVEL_4)), 
    TEMPERATE_RAINFOREST(new Color(10, 84, 109), new Color(10, 160, 65), constant(Temperature.LEVEL_2, Humidity.LEVEL_4)), 
    TEMPERATE_FOREST(new Color(44, 137, 160), new Color(50, 200, 80), range(Temperature.LEVEL_2, Temperature.LEVEL_2, Humidity.LEVEL_2, Humidity.LEVEL_3)), 
    GRASSLAND(new Color(179, 124, 6), new Color(100, 220, 60), range(Temperature.LEVEL_1, Temperature.LEVEL_2, Humidity.LEVEL_0, Humidity.LEVEL_1)), 
    COLD_STEPPE(new Color(131, 112, 71), new Color(175, 180, 150), constant(Temperature.LEVEL_0, Humidity.LEVEL_4)), 
    STEPPE(new Color(199, 155, 60), new Color(200, 200, 120), range(Temperature.LEVEL_1, Temperature.LEVEL_2, Humidity.LEVEL_1, Humidity.LEVEL_2)), 
    TAIGA(new Color(91, 143, 82), new Color(91, 143, 82), range(Temperature.LEVEL_1, Temperature.LEVEL_1, Humidity.LEVEL_3, Humidity.LEVEL_4)), 
    TUNDRA(new Color(147, 167, 172), new Color(147, 167, 172), range(Temperature.LEVEL_0, Temperature.LEVEL_0, Humidity.LEVEL_0, Humidity.LEVEL_3)), 
    ALPINE(new Color(0, 0, 0), new Color(160, 120, 170), constant(Temperature.LEVEL_1, Humidity.LEVEL_4));

    public static final int RESOLUTION = 256;
    public static final int MAX = 255;
    private static final BiomeType[] BIOMES = values();
    private List<Pair<Temperature, Humidity>> noisePairs;
    private Color lookup;
    private Color color;
    private float minTemp;
    private float maxTemp;
    private float minMoist;
    private float maxMoist;

    @SafeVarargs
	private BiomeType(Color lookup, Color color, Pair<Temperature, Humidity>... noisePairs) {
    	this(lookup, color, ImmutableList.copyOf(noisePairs));
    }
    
    private BiomeType(Color lookup, Color color, List<Pair<Temperature, Humidity>> noisePairs) {
        this.lookup = lookup;
        this.noisePairs = ImmutableList.copyOf(noisePairs);
        this.color = BiomeTypeColors.getInstance().getColor(this.name(), color);
    }
    
    Color getLookup() {
        return this.lookup;
    }

    public float getTemperature(float identity) {
    	return this.getRandomPair(identity).getFirst().mid();
    }

    public float getMoisture(float identity) {
    	return this.getRandomPair(identity).getSecond().mid();
    }
    
    private Pair<Temperature, Humidity> getRandomPair(float identity) {
    	int maxIndex = this.noisePairs.size() - 1;
    	return this.noisePairs.get(NoiseUtil.round(identity * maxIndex));
    }

    public float mapTemperature(float value) {
        return (value - this.minTemp) / (this.maxTemp - this.minTemp);
    }
    
    public float mapMoisture(float value) {
        return (value - this.minMoist) / (this.maxMoist - this.minMoist);
    }
    
    public int getId() {
        return this.ordinal();
    }
    
    public float getMinMoisture() {
        return this.minMoist;
    }
    
    public float getMaxMoisture() {
        return this.maxMoist;
    }
    
    public float getMinTemperature() {
        return this.minTemp;
    }
    
    public float getMaxTemperature() {
        return this.maxTemp;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public boolean isExtreme() {
        return this == BiomeType.TUNDRA || this == BiomeType.DESERT;
    }
    
    public static BiomeType get(int id) {
        return BiomeType.BIOMES[id];
    }
    
    public static BiomeType get(float temperature, float moisture) {
        return getCurve(temperature, moisture);
    }
    
    public static BiomeType getLinear(float temperature, float moisture) {
        int x = NoiseUtil.round(255.0f * temperature);
        int y = getYLinear(x, temperature, moisture);
        return getType(x, y);
    }
    
    public static BiomeType getCurve(float temperature, float moisture) {
        int x = NoiseUtil.round(255.0f * temperature);
        int y = getYCurve(x, temperature, moisture);
        return getType(x, y);
    }
    
//    public static void apply(Cell cell) {
//        applyCurve(cell);
//    }
    
//    public static void applyLinear(Cell cell) {
//        cell.biome = get(cell.regionTemperature, cell.regionMoisture);
//    }
//    
//    public static void applyCurve(Cell cell) {
//        cell.biome = get(cell.regionTemperature, cell.regionMoisture);
//    }
    
    private static BiomeType getType(int x, int y) {
        return BiomeTypeLoader.getInstance().getTypeMap()[y][x];
    }
    
    private static int getYLinear(int x, float temperature, float moisture) {
        if (moisture > temperature) {
            return x;
        }
        return NoiseUtil.round(255.0F * moisture);
    }
    
    private static int getYCurve(int x, float temperature, float moisture) {
        int max = x + (255 - x) / 2;
        int y = NoiseUtil.round(max * moisture);
        return Math.min(x, y);
    }
    
    private static List<Pair<Temperature, Humidity>> range(Temperature t0, Temperature t1, Humidity h0, Humidity h1) {
    	List<Pair<Temperature, Humidity>> set = new ArrayList<>();
    	
    	Temperature[] temperatures = Temperature.values();
    	Humidity[] humidities = Humidity.values();
    	for(int i = t0.ordinal(); i <= t1.ordinal(); i++) {
    		Temperature temperature = temperatures[i];
    		for(int j = h0.ordinal(); j <= h1.ordinal(); j++) {
    			Humidity humidity = humidities[j];
    			
    			set.add(Pair.of(temperature, humidity));
    		}
    	}
    	
    	return set;
    }
    
    private static Pair<Temperature, Humidity> constant(Temperature t, Humidity h) {
    	return Pair.of(t, h);
    }
    
    private static <T> Pair<T, T> range(T min, T max) {
    	return Pair.of(min, max);
    }

    private static <T> Pair<T, T> constant(T value)	{
    	return Pair.of(value, value);
    }
    
    private static void init() {
        for (BiomeType type : values()) {
            Vec2f[] ranges = BiomeTypeLoader.getInstance().getRanges(type);
            type.minTemp = ranges[0].x();
            type.maxTemp = ranges[0].y();
            type.minMoist = ranges[1].x();
            type.maxMoist = ranges[1].y();
        }
    }
    
    static {
        init();
    }
}
