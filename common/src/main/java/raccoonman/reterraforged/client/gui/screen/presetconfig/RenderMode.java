package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.awt.Color;

import net.minecraft.world.level.levelgen.NoiseRouterData;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public enum RenderMode {
    BIOME_TYPE {
    	
        @Override
        public boolean handlesWater() {
            return true;
        }

        @Override
        public int getColor(Cell cell, Levels levels, float scale, float bias) {
            switch (cell.terrain.getCategory()) {
                case DEEP_OCEAN:
                    return rgba(0.63F, 0.65F, 0.8F);
                case SHALLOW_OCEAN:
                    return rgba(0.6F, 0.6F, 0.8F);
                case BEACH:
                    return rgba(0.2F, 0.4F, 0.75F);
                default:
                    if (cell.height < levels.water) {
                        return RenderMode.getWaterColor();
                    } else {
                        Color color = cell.biome.getColor();
                        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), new float[3]);
                        return rgba(hsb[0], hsb[1], (hsb[2] * scale) + bias);
                    }
            }
        }

		@Override
		public float getNoiseValue(Cell cell) {
			return cell.biomeRegionEdge;
		}
    },
    TRANSITION_POINTS {
    	
        @Override
        public boolean handlesWater() {
            return true;
        }

        @Override
        public int getColor(Cell cell, Levels levels, float scale, float bias) {
            switch (cell.terrain.getCategory()) {
                case DEEP_OCEAN:
                    return rgba(0.63F, 0.65F, 0.8F);
                case SHALLOW_OCEAN:
                    return rgba(0.6F, 0.6F, 0.8F);
                case BEACH:
                    return rgba(0.2F, 0.4F, 0.75F);
                case COAST:
                    return rgba(0.35F, 0.75F, 0.65F);
                default:
                    if (cell.terrain.isRiver() || cell.terrain.isWetland()) {
                        return rgba(0.6F, 0.6F, 0.8F);
                    }
                    return rgba(0.3F, 0.7F, 0.5F);
            }
        }

		@Override
		public float getNoiseValue(Cell cell) {
			return cell.continentalness;// cell.continentEdge;
		}
    },
    TEMPERATURE {
    	
        @Override
        public int getColor(Cell cell, Levels levels, float scale, float bias) {
            float saturation = 0.7F;
            float brightness = 0.8F;
            return rgba(step(1 - cell.regionTemperature, 8) * 0.65F, saturation, brightness);
        }

		@Override
		public float getNoiseValue(Cell cell) {
			return cell.regionTemperature;
		}
    },
    MOISTURE {
    	
        @Override
        public int getColor(Cell cell, Levels levels, float scale, float bias) {
            float saturation = 0.7F;
            float brightness = 0.8F;
            return rgba(step(cell.regionMoisture, 8) * 0.65F, saturation, brightness);
        }

		@Override
		public float getNoiseValue(Cell cell) {
			return cell.regionMoisture;
		}
    },
    BIOME {
    	
        @Override
        public int getColor(Cell cell, Levels levels, float scale, float bias) {
            float saturation = 0.7F;
            float brightness = 0.8F;
            return rgba(cell.biomeRegionId, saturation, brightness);
        }

		@Override
		public float getNoiseValue(Cell cell) {
			return cell.biomeRegionId;
		}
    },
    MACRO_NOISE {
    	
        @Override
        public int getColor(Cell cell, Levels levels, float scale, float bias) {
            float saturation = 0.7F;
            float brightness = 0.8F;
            return rgba(cell.macroBiomeId, saturation, brightness);
        }

		@Override
		public float getNoiseValue(Cell cell) {
			return cell.macroBiomeId;
		}
    },
    TERRAIN_REGION {
    	
        @Override
        public int getColor(Cell cell, Levels levels, float scale, float bias) {
            float saturation = 0.7F;
            float brightness = 0.8F;
            return rgba(cell.terrain.getRenderHue(), saturation, brightness);
        }

		@Override
		public float getNoiseValue(Cell cell) {
			return cell.terrainRegionId;
		}
    },
//    CONTINENTALNESS {
//    	
//        @Override
//        public int getColor(Cell cell, Levels levels, float scale, float bias) {
//        	float continentalness = this.getNoiseValue(cell);
//        	return rgba(continentalness, continentalness, continentalness);
//        }
//        
//        @Override
//        public boolean handlesWater() {
//            return true;
//        }
//
//		@Override
//		public float getNoiseValue(Cell cell) {
//			return cell.continentalness;
//		}
//    },
    EROSION {
    	
        @Override
        public int getColor(Cell cell, Levels levels, float scale, float bias) {
        	float erosion = this.getNoiseValue(cell);
        	return rgba(erosion, erosion, erosion);
        }

		@Override
		public float getNoiseValue(Cell cell) {
			return cell.erosion;
		}
    },
	WEIRDNESS {
    	
        @Override
        public int getColor(Cell cell, Levels levels, float scale, float bias) {
        	float weirdness = this.getNoiseValue(cell);
        	return rgba(weirdness, weirdness, weirdness);
        }

		@Override
		public float getNoiseValue(Cell cell) {
			return cell.weirdness;
		}
    },
	PEAKS_AND_VALLEYS {
    	
        @Override
        public int getColor(Cell cell, Levels levels, float scale, float bias) {
        	float peaksAndValleys = this.getNoiseValue(cell);
        	return rgba(peaksAndValleys, peaksAndValleys, peaksAndValleys);
        }

		@Override
		public float getNoiseValue(Cell cell) {
			return NoiseRouterData.peaksAndValleys(cell.weirdness);
		}
    };

    public int getColor(Cell cell, Levels levels) {
        if (!this.handlesWater() && cell.height < levels.water) {
            return getWaterColor();
        }
        float bands = 10.0F;
        float alpha = 0.2F;
        float elevation = (cell.height - levels.water) / (1.0F - levels.water);
        int band = NoiseUtil.round(elevation * bands);
        float scale = 1.0F - alpha;
        float bias = alpha * (band / bands);
        return this.getColor(cell, levels, scale, bias);
    }

    public abstract int getColor(Cell cell, Levels levels, float scale, float bias);

    public abstract float getNoiseValue(Cell cell);
    
    public boolean handlesWater() {
        return false;
    }

    private static int getWaterColor() {
        return rgba(40, 140, 200);
    }

    private static float step(float value, int steps) {
        return ((float) NoiseUtil.round(value * steps)) / steps;
    }

    private static int rgba(float h, float s, float b) {
        int argb = Color.HSBtoRGB(h, s, b);
        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue =  argb & 0xFF;
        return rgba(red, green, blue);
    }

    private static int rgba(int r, int g, int b) {
        return r + (g << 8) + (b << 16) + (255 << 24);
    }
}
