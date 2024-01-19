package raccoonman.reterraforged.data.worldgen.preset;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TemplateDecorator;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TemplateDecorators;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TreeContext;

public class PresetTemplateDecoratorLists {
	public static final List<TemplateDecorator<TreeContext>> BEEHIVE_RARITY_005 = ImmutableList.of(TemplateDecorators.tree(new BeehiveDecorator(0.05F)));
	public static final List<TemplateDecorator<TreeContext>> BEEHIVE_RARITY_005_AND_0002 = ImmutableList.of(TemplateDecorators.tree(new BeehiveDecorator(0.05F), new BeehiveDecorator(0.002F)));
	public static final List<TemplateDecorator<TreeContext>> BEEHIVE_RARITY_002_AND_005 = ImmutableList.of(TemplateDecorators.tree(new BeehiveDecorator(0.02F), new BeehiveDecorator(0.05F)));
	public static final List<TemplateDecorator<TreeContext>> BEEHIVE_RARITY_0002_AND_005 = ImmutableList.of(TemplateDecorators.tree(new BeehiveDecorator(0.002F), new BeehiveDecorator(0.05F)));
}
