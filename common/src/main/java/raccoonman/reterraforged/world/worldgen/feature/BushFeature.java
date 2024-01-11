package raccoonman.reterraforged.world.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import raccoonman.reterraforged.world.worldgen.feature.template.BlockUtils;

public class BushFeature extends Feature<BushFeature.Config> {
    private static final Vec3i[] LOG_POSITIONS = {
    	new Vec3i(+1, 0, +1),
    	new Vec3i(+1, 0, -1),
    	new Vec3i(-1, 0, -1),
    	new Vec3i(-1, 0, +1),

    	new Vec3i(+2, 0, +1),
    	new Vec3i(+2, 0, -1),
    	new Vec3i(-2, 0, +1),
    	new Vec3i(-2, 0, -1),

    	new Vec3i(+1, 0, +2),
    	new Vec3i(+1, 0, -2),
    	new Vec3i(-1, 0, +2),
    	new Vec3i(-1, 0, -2),
    };

    private static final Vec3i[] LEAF_POSITIONS = {
    	new Vec3i(0, 0, 1),
    	new Vec3i(0, 0, -1),
    	new Vec3i(1, 0, 0),
    	new Vec3i(-1, 0, 0),
    	new Vec3i(0, 1, 0),
    };
    
    public BushFeature(Codec<Config> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<Config> ctx) {
        BlockPos.MutableBlockPos log = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos leaf = new BlockPos.MutableBlockPos();
        WorldGenLevel level = ctx.level();
        RandomSource rand = ctx.random();
        BlockPos pos = ctx.origin();
        Config config = ctx.config();
        
        this.place(level, log.set(pos), leaf, rand, config);
        for (float chance = rand.nextFloat(); chance < config.sizeChance; chance += rand.nextFloat()) {
            addToMutable(log, LOG_POSITIONS[rand.nextInt(LOG_POSITIONS.length)]);

            if (!this.place(level, log, leaf, rand, config)) {
                break;
            }
        }
        return false;
	}

    private boolean place(LevelAccessor world, BlockPos.MutableBlockPos center, BlockPos.MutableBlockPos pos, RandomSource random, Config config) {
//        center.setY(world.getHeight(Heightmap.Types.WORLD_SURFACE, center.getX(), center.getZ()));

        // don't replace solid blocks
        if (!BlockUtils.canTreeReplace(world, center)) {
            return false;
        }

        // only place on dirt/grass/natural-stone
        center.move(Direction.DOWN, 1);
        if (!BlockUtils.isSoilOrRock(world, center)) {
            return false;
        }

        center.move(Direction.UP, 1);
        world.setBlock(center, config.trunk, 2);

        BlockState leaves = config.leavesWithDistance(1);
        BlockState leavesExtra = config.leavesWithDistance(2);
        for (Vec3i neighbour : BushFeature.LEAF_POSITIONS) {
            // randomly skip NESW neighbours
            if (neighbour.getY() == 0 && random.nextFloat() < config.airChance) {
                continue;
            }

            pos.set(center);
            addToMutable(pos, neighbour);

            if (BlockUtils.canTreeReplace(world, pos)) {
                world.setBlock(pos, leaves, 2);

                // randomly place extra leaves below if non-solid
                if (neighbour.getY() == 0 && random.nextFloat() < config.leafChance) {
                    pos.move(Direction.DOWN, 1);
                    if (BlockUtils.canTreeReplace(world, pos)) {
                        world.setBlock(pos, leavesExtra, 2);
                    }
                    pos.move(Direction.UP, 1);
                }

                // randomly place extra leaves above
                if (neighbour.getY() == 0 && random.nextFloat() < config.leafChance) {
                    pos.move(Direction.UP, 1);
                    if (BlockUtils.canTreeReplace(world, pos)) {
                        world.setBlock(pos, leavesExtra, 2);
                    }
                }
            }
        }

        return true;
    }

    private static void addToMutable(BlockPos.MutableBlockPos pos, Vec3i add) {
        pos.set(pos.getX() + add.getX(), pos.getY() + add.getY(), pos.getZ() + add.getZ());
    }

    public record Config(BlockState trunk, BlockState leaves, float airChance, float leafChance, float sizeChance) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        	BlockState.CODEC.fieldOf("trunk").forGetter(Config::trunk),
        	BlockState.CODEC.fieldOf("leaves").forGetter(Config::leaves),
        	Codec.FLOAT.optionalFieldOf("air_chance", 0.075F).forGetter(Config::airChance),
        	Codec.FLOAT.optionalFieldOf("leaf_chance", 0.075F).forGetter(Config::leafChance),
        	Codec.FLOAT.optionalFieldOf("size_chance", 0.75F).forGetter(Config::sizeChance)
        ).apply(instance, Config::new));

        public BlockState leavesWithDistance(int distance) {
            if (leaves.hasProperty(LeavesBlock.DISTANCE)) {
                return leaves.setValue(LeavesBlock.DISTANCE, distance);
            }
            return leaves;
        }
    }
}
