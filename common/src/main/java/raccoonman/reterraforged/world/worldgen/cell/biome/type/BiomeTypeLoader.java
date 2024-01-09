package raccoonman.reterraforged.world.worldgen.cell.biome.type;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;

public class BiomeTypeLoader {
    private static BiomeTypeLoader instance;
    private BiomeType[][] map;
    
    public BiomeTypeLoader() {
        this.map = new BiomeType[256][256];
        this.generateTypeMap();
    }
    
    public BiomeType[][] getTypeMap() {
        return this.map;
    }
    
    public Vec2f[] getRanges(BiomeType type) {
        float minTemp = 1.0F;
        float maxTemp = 0.0F;
        float minMoist = 1.0F;
        float maxMoist = 0.0F;
        for (int moist = 0; moist < this.map.length; ++moist) {
            BiomeType[] row = this.map[moist];
            for (int temp = 0; temp < row.length; ++temp) {
                BiomeType t = row[temp];
                if (t == type) {
                    float temperature = temp / (float)(row.length - 1);
                    float moisture = moist / (float)(this.map.length - 1);
                    minTemp = Math.min(minTemp, temperature);
                    maxTemp = Math.max(maxTemp, temperature);
                    minMoist = Math.min(minMoist, moisture);
                    maxMoist = Math.max(maxMoist, moisture);
                }
            }
        }
        return new Vec2f[] { new Vec2f(minTemp, maxTemp), new Vec2f(minMoist, maxMoist) };
    }
    
    private void generateTypeMap() {
        try {
            BufferedImage image = ImageIO.read(BiomeType.class.getResourceAsStream("/biomes.png"));
            float xf = image.getWidth() / 256.0f;
            float yf = image.getHeight() / 256.0f;
            for (int y = 0; y < 256; ++y) {
                for (int x = 0; x < 256; ++x) {
                    if (255 - y > x) {
                        this.map[255 - y][x] = BiomeType.ALPINE;
                    } else {
                        int ix = NoiseUtil.round(x * xf);
                        int iy = NoiseUtil.round(y * yf);
                        int argb = image.getRGB(ix, iy);
                        Color color = fromARGB(argb);
                        this.map[255 - y][x] = forColor(color);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static BiomeType forColor(Color color) {
        BiomeType type = null;
        int closest = Integer.MAX_VALUE;
        for (BiomeType t : BiomeType.values()) {
            int distance2 = getDistance2(color, t.getLookup());
            if (distance2 < closest) {
                closest = distance2;
                type = t;
            }
        }
        if (type == null) {
            return BiomeType.GRASSLAND;
        }
        return type;
    }
    
    private static int getDistance2(Color a, Color b) {
        int dr = a.getRed() - b.getRed();
        int dg = a.getGreen() - b.getGreen();
        int db = a.getBlue() - b.getBlue();
        return dr * dr + dg * dg + db * db;
    }
    
    private static Color fromARGB(int argb) {
        int b = argb & 0xFF;
        int g = argb >> 8 & 0xFF;
        int r = argb >> 16 & 0xFF;
        return new Color(r, g, b);
    }
    
    public static BiomeTypeLoader getInstance() {
        if (BiomeTypeLoader.instance == null) {
            BiomeTypeLoader.instance = new BiomeTypeLoader();
        }
        return BiomeTypeLoader.instance;
    }
}
