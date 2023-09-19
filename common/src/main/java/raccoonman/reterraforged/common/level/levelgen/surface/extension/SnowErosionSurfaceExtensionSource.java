//package raccoonman.reterraforged.common.level.levelgen.surface.extension;
//
//import java.util.function.Predicate;
//
//import com.mojang.serialization.Codec;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.GrassBlock;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.chunk.BlockColumn;
//import net.minecraft.world.level.levelgen.Heightmap;
//import net.minecraft.world.level.levelgen.SurfaceRules.Context;
//
//public record SnowErosionSurfaceExtensionSource() implements SurfaceExtensionSource {
//
//	@Override
//	public Extension apply(Context ctx) {
//		return new Extension(ctx);
//	}
//
//	@Override
//	public Codec<SnowErosionSurfaceExtensionSource> codec() {
//		return null;
//	}
//	
//	private record Extension(Context ctx) implements SurfaceExtension {
//	    public static final float HEIGHT_MODIFIER = 6F / 255F;
//	    public static final float SLOPE_MODIFIER = 3F / 255F;
//
//		@Override
//	    public void apply(BlockColumn column) {
//	        int surface = chunk.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
//	        if (y - surface > 0) {
//	            if (y - surface > 4) {
//	                return;
//	            }
//	            y = surface;
//	        }
//
//	        if (context.biome.getTemperature(context.pos.set(x, y, z)) <= 0.25) {
//	            float var = -ErosionSurfaceExtensionSource.getNoise(x, z, seed1, 16, 0);
//	            float hNoise = rand.getValue(seed2, (float) x, (float) z) * HEIGHT_MODIFIER;
//	            float sNoise = rand.getValue(seed3, (float) x, (float) z) * SLOPE_MODIFIER;
//	            float vModifier = 0.0F;//context.cell.terrain == TerrainType.VOLCANO ? 0.15F : 0F;
//	            float height = context.cell.value + var + hNoise + vModifier;
//	            float steepness = context.cell.gradient + var + sNoise + vModifier;
//	            if (snowErosion(x, z, steepness, height)) {
//	                Predicate<BlockState> predicate = Heightmap.Types.MOTION_BLOCKING.isOpaque();
//	                for (int dy = 2; dy > 0; dy--) {
//	                    context.pos.set(x, y + dy, z);
//	                    BlockState state = chunk.getBlockState(context.pos);
//	                    if (!predicate.test(state) || state.getBlock() == Blocks.SNOW) {
//	                        erodeSnow(chunk, context.pos);
//	                    }
//	                }
//	            }
//	        }
//	    }
//
//	    private boolean snowErosion(float x, float z, float steepness, float height) {
//	        return steepness > ROCK_STEEPNESS
//	                || (steepness > SNOW_ROCK_STEEPNESS && height > SNOW_ROCK_HEIGHT)
//	                || super.erodeDirt(x, z, steepness, height);
//	    }
//
//	    private void erodeSnow(IChunk chunk, BlockPos.Mutable pos) {
//	        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
//
//	        if (pos.getY() > 0) {
//	            pos.setY(pos.getY() - 1);
//	            BlockState below = chunk.getBlockState(pos);
//	            if (below.hasProperty(GrassBlock.SNOWY)) {
//	                chunk.setBlockState(pos, below.setValue(GrassBlock.SNOWY, false), false);
//	            }
//	        }
//	    }
//	}
//}
