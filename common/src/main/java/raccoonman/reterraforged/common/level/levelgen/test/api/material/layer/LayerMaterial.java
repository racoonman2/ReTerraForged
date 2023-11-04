package raccoonman.reterraforged.common.level.levelgen.test.api.material.layer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public class LayerMaterial {
    public static final BlockState NONE = Blocks.AIR.defaultBlockState();

    private final int min;
    private final int max;
    private final BlockState fullState;
    private final BlockState layerState;
    private final Property<Integer> layerProperty;

    private LayerMaterial(BlockState fullState, BlockState layerState, Property<Integer> layerProperty) {
        this.layerProperty = layerProperty;
        this.min = min(layerProperty);
        this.max = max(layerProperty);
        this.layerState = layerState;
        this.fullState = fullState;
    }

    public Block getLayerType() {
        return layerState.getBlock();
    }

    public BlockState getFull() {
        return fullState;
    }

    public BlockState getState(float value) {
        return getState(getLevel(value));
    }

    public BlockState getState(int level) {
        if (level < min) {
            return LayerMaterial.NONE;
        }
        if (level >= max) {
            return fullState;
        }
        return layerState.setValue(layerProperty, level);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getLevel(float depth) {
        if (depth > 1) {
            depth = getDepth(depth);
        } else if (depth < 0) {
            depth = 0;
        }
        return NoiseUtil.round(depth * max);
    }

    public float getDepth(float height) {
        return height - (int) height;
    }

    private static int min(Property<Integer> property) {
        return property.getPossibleValues().stream().min(Integer::compareTo).orElse(0);
    }

    private static int max(Property<Integer> property) {
        return property.getPossibleValues().stream().max(Integer::compareTo).orElse(0);
    }

    public static LayerMaterial of(Block block) {
        return of(block, BlockStateProperties.LAYERS);
    }

    public static LayerMaterial of(Block block, Property<Integer> property) {
        return of(block.defaultBlockState(), block.defaultBlockState(), property);
    }

    public static LayerMaterial of(Block full, Block layer) {
        return of(full.defaultBlockState(), layer.defaultBlockState());
    }

    public static LayerMaterial of(Block full, Block layer, Property<Integer> property) {
        return of(full.defaultBlockState(), layer.defaultBlockState(), property);
    }

    public static LayerMaterial of(BlockState layer) {
        return of(layer, BlockStateProperties.LAYERS);
    }

    public static LayerMaterial of(BlockState layer, Property<Integer> property) {
        return of(layer.setValue(property, max(property)), layer);
    }

    public static LayerMaterial of(BlockState full, BlockState layer) {
        return of(full, layer, BlockStateProperties.LAYERS);
    }

    public static LayerMaterial of(BlockState full, BlockState layer, Property<Integer> property) {
        return new LayerMaterial(full, layer, property);
    }
}