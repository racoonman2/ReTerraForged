package raccoonman.reterraforged.common.level.levelgen.test.world.biome.type;

import java.awt.Color;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;

public enum BiomeType {
    TROPICAL_RAINFOREST(7, 83, 48, new Color(7, 83, 48)), 
    SAVANNA(151, 165, 39, new Color(151, 165, 39)),
    DESERT(200, 113, 55, new Color(200, 113, 55)), 
    TEMPERATE_RAINFOREST(10, 84, 109, new Color(10, 160, 65)), 
    TEMPERATE_FOREST(44, 137, 160, new Color(50, 200, 80)), 
    GRASSLAND(179, 124, 6, new Color(100, 220, 60)), 
    COLD_STEPPE(131, 112, 71, new Color(175, 180, 150)), 
    STEPPE(199, 155, 60, new Color(200, 200, 120)), 
    TAIGA(91, 143, 82, new Color(91, 143, 82)), 
    TUNDRA(147, 167, 172, new Color(147, 167, 172)), 
    ALPINE(0, 0, 0, new Color(160, 120, 170));
    
    public static final int RESOLUTION = 256;
    public static final int MAX = 255;
    private static final BiomeType[] BIOMES = values();
    private Color lookup;
    private Color color;
    private float minTemp;
    private float maxTemp;
    private float minMoist;
    private float maxMoist;
    
    private BiomeType(int r, int g, int b, Color color) {
        this(new Color(r, g, b), color);
    }
    
    private BiomeType(Color lookup, Color color) {
        this.lookup = lookup;
        this.color = BiomeTypeColors.getInstance().getColor(this.name(), color);
    }
    
    Color getLookup() {
        return this.lookup;
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
    
    public static void apply(Cell cell) {
        applyCurve(cell);
    }
    
    public static void applyLinear(Cell cell) {
        cell.biome = get(cell.temperature, cell.moisture);
    }
    
    public static void applyCurve(Cell cell) {
        cell.biome = get(cell.temperature, cell.moisture);
    }
    
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
