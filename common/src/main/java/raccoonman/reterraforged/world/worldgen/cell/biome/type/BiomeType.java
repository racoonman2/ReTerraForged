package raccoonman.reterraforged.world.worldgen.cell.biome.type;

import java.awt.Color;

import com.mojang.datafixers.util.Pair;

import raccoonman.reterraforged.world.worldgen.biome.Humidity;
import raccoonman.reterraforged.world.worldgen.biome.Temperature;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;

public enum BiomeType {
    TROPICAL_RAINFOREST(7, 83, 48, new Color(7, 83, 48), constant(Temperature.LEVEL_3), constant(Humidity.LEVEL_4)), 
    SAVANNA(151, 165, 39, new Color(151, 165, 39), constant(Temperature.LEVEL_3), range(Humidity.LEVEL_0, Humidity.LEVEL_1)),
    DESERT(200, 113, 55, new Color(200, 113, 55), constant(Temperature.LEVEL_4), range(Humidity.LEVEL_0, Humidity.LEVEL_4)), 
    TEMPERATE_RAINFOREST(10, 84, 109, new Color(10, 160, 65), constant(Temperature.LEVEL_3), constant(Humidity.LEVEL_3)), 
    TEMPERATE_FOREST(44, 137, 160, new Color(50, 200, 80), constant(Temperature.LEVEL_2), range(Humidity.LEVEL_2, Humidity.LEVEL_4)), 
    GRASSLAND(179, 124, 6, new Color(100, 220, 60), range(Temperature.LEVEL_1, Temperature.LEVEL_2), range(Humidity.LEVEL_0, Humidity.LEVEL_1)), 
    COLD_STEPPE(131, 112, 71, new Color(175, 180, 150), constant(Temperature.LEVEL_0), constant(Humidity.LEVEL_4)), 
    STEPPE(199, 155, 60, new Color(200, 200, 120), range(Temperature.LEVEL_1, Temperature.LEVEL_2), range(Humidity.LEVEL_1, Humidity.LEVEL_2)), 
    TAIGA(91, 143, 82, new Color(91, 143, 82), range(Temperature.LEVEL_0, Temperature.LEVEL_1), constant(Humidity.LEVEL_4)), 
    TUNDRA(147, 167, 172, new Color(147, 167, 172), constant(Temperature.LEVEL_0), range(Humidity.LEVEL_0, Humidity.LEVEL_3)), 
    ALPINE(0, 0, 0, new Color(160, 120, 170), constant(Temperature.LEVEL_0), constant(Humidity.LEVEL_4));

    public static final int RESOLUTION = 256;
    public static final int MAX = 255;
    private static final BiomeType[] BIOMES = values();
    private Pair<Temperature, Temperature> temperatureRange;
    private Pair<Humidity, Humidity> moistureRange;
    private Color lookup;
    private Color color;
    private float minTemp;
    private float maxTemp;
    private float minMoist;
    private float maxMoist;

    private BiomeType(int r, int g, int b, Color color, Pair<Temperature, Temperature> temperatureRange, Pair<Humidity, Humidity> moistureRange) {
        this(new Color(r, g, b), color, temperatureRange, moistureRange);
    }
    
    private BiomeType(Color lookup, Color color, Pair<Temperature, Temperature> temperatureRange, Pair<Humidity, Humidity> moistureRange) {
        this.lookup = lookup;
        this.temperatureRange = temperatureRange;
        this.moistureRange = moistureRange;
        this.color = BiomeTypeColors.getInstance().getColor(this.name(), color);
    }
    
    Color getLookup() {
        return this.lookup;
    }

    private static final Temperature[] TEMPERATURE_LEVELS = Temperature.values();
    private static final Humidity[] MOISTURE_LEVELS = Humidity.values();

    public float getTemperature(float identity) {
    	return getRandom(identity, this.temperatureRange, TEMPERATURE_LEVELS).mid();
    }

    public float getMoisture(float identity) {
    	return getRandom(identity, this.moistureRange, MOISTURE_LEVELS).mid();
    }

    private static <E extends Enum<E>> E getRandom(float identity, Pair<E, E> range, E[] values) {
    	E min = range.getFirst();
    	E max = range.getSecond();
    	int minOrdinal = min.ordinal();
    	int count = max.ordinal() - minOrdinal;
        int index = NoiseUtil.round(count * identity);
        return values[minOrdinal + index];
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
