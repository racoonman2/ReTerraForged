package raccoonman.reterraforged.world.worldgen.feature.template.decorator;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

record TreeDecorator(net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator decorator, net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator modifiedDecorator) implements TemplateDecorator<TreeContext> {
	public static final MapCodec<TreeDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator.CODEC.fieldOf("decorator").forGetter(TreeDecorator::decorator),
		net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator.CODEC.fieldOf("modified_decorator").forGetter(TreeDecorator::modifiedDecorator)
	).apply(instance, TreeDecorator::new));
	
    public net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator getDecorator(boolean modified) {
        return modified ? this.modifiedDecorator : this.decorator;
    }

    @Override
    public void apply(LevelAccessor level, TreeContext ctx, RandomSource random, boolean modified) {
    	Set<BlockPos> logs = ctx.logs();
    	Set<BlockPos> leaves = ctx.leaves();
    	
        if (logs.isEmpty() || leaves.isEmpty()) {
            return;
        }
        
        net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator.Context decoratorCtx = new net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator.Context(
        	level,
        	(pos, state) -> level.setBlock(pos, state, 19), 
        	random,
        	logs,
        	leaves,
        	ImmutableSet.of()
        );

        this.getDecorator(modified).place(decoratorCtx);
    }

	@Override
	public MapCodec<TreeDecorator> codec() {
		return CODEC;
	}
}
