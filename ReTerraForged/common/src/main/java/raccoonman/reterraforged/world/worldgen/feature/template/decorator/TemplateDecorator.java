package raccoonman.reterraforged.world.worldgen.feature.template.decorator;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import raccoonman.reterraforged.registry.RTFBuiltInRegistries;
import raccoonman.reterraforged.world.worldgen.feature.template.TemplateContext;

public interface TemplateDecorator<T extends TemplateContext> {
    public static final Codec<TemplateDecorator<?>> CODEC = RTFBuiltInRegistries.TEMPLATE_DECORATOR_TYPE.byNameCodec().dispatch(TemplateDecorator::codec, Function.identity());
    
    void apply(LevelAccessor level, T ctx, RandomSource random, boolean modified);
    
    MapCodec<? extends TemplateDecorator<T>> codec();
}
