package raccoonman.reterraforged.data.worldgen;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TemplateDecorator;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TemplateDecorators;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TreeContext;

public class TemplateDecoratorLists {
	public static final List<TemplateDecorator<TreeContext>> BEEHIVE_RARITY_005 = ImmutableList.of(TemplateDecorators.tree(new BeehiveDecorator(0.05F)));
	
}
