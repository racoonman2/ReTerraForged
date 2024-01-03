package raccoonman.reterraforged.world.worldgen.feature.template;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.server.RTFMinecraftServer;
import raccoonman.reterraforged.world.worldgen.feature.template.TemplateFeature.Config;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.DecoratorConfig;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TemplateDecorator;
import raccoonman.reterraforged.world.worldgen.feature.template.paste.Paste;
import raccoonman.reterraforged.world.worldgen.feature.template.paste.PasteConfig;
import raccoonman.reterraforged.world.worldgen.feature.template.paste.PasteType;
import raccoonman.reterraforged.world.worldgen.feature.template.placement.TemplatePlacement;
import raccoonman.reterraforged.world.worldgen.feature.template.template.Dimensions;
import raccoonman.reterraforged.world.worldgen.feature.template.template.FeatureTemplate;
import raccoonman.reterraforged.world.worldgen.feature.template.template.TemplateContext;

public class TemplateFeature extends Feature<Config<?>> {

	public TemplateFeature(Codec<Config<?>> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<Config<?>> ctx) {
		RandomSource random = ctx.random();
		Config<?> config = ctx.config();
		
		Mirror mirror = nextMirror(random);
		Rotation rotation = nextRotation(random);
        return paste(ctx.level(), random, ctx.origin(), mirror, rotation, config, FeatureTemplate.WORLD_GEN);
	}

    public static <T extends TemplateContext> boolean paste(WorldGenLevel world, RandomSource rand, BlockPos pos, Mirror mirror, Rotation rotation, Config<T> config, PasteType pasteType) {
        return paste(world, rand, pos, mirror, rotation, config, pasteType, false);
    }

    public static <T extends TemplateContext> boolean paste(WorldGenLevel world, RandomSource rand, BlockPos pos, Mirror mirror, Rotation rotation, Config<T> config, PasteType pasteType, boolean modified) {
        if (config.templates().isEmpty()) {
            RTFCommon.LOGGER.warn("Empty template list for config");
            return false;
        }
        
        if(world.getServer() instanceof RTFMinecraftServer rtfMinecraftServer) {
	        DecoratorConfig<T> decoratorConfig = config.decorator();
	        
	        ResourceLocation templateName = nextTemplate(config.templates, rand);
	        FeatureTemplate template = rtfMinecraftServer.getFeatureTemplateManager().load(templateName);
	        
	        Dimensions dimensions = template.getDimensions(mirror, rotation);
	        TemplatePlacement<T> placement = config.placement();
	        if (!placement.canPlaceAt(world, pos, dimensions)) {
	            return false;
	        }
	
	        Paste paste = pasteType.get(template);
	        T buffer = placement.createContext();
	        if (paste.apply(world, buffer, pos, mirror, rotation, placement, config.paste())) {
	            ResourceLocation biome = world.getBiome(pos).unwrapKey().map(ResourceKey::registry).orElse(null);
	            for (TemplateDecorator<T> decorator : decoratorConfig.getDecorators(biome)) {
	                decorator.apply(world, buffer, rand, modified);
	            }
	            return true;
	        }
	
	        return false;
        } else {
        	throw new IllegalStateException();
        }
    }

	private static ResourceLocation nextTemplate(List<ResourceLocation> templates, RandomSource random) {
        return templates.get(random.nextInt(templates.size()));
    }

    private static Mirror nextMirror(RandomSource random) {
        return Mirror.values()[random.nextInt(Mirror.values().length)];
    }

    private static Rotation nextRotation(RandomSource random) {
        return Rotation.values()[random.nextInt(Rotation.values().length)];
    }
    
	public record Config<T extends TemplateContext>(List<ResourceLocation> templates, TemplatePlacement<T> placement, PasteConfig paste, DecoratorConfig<T> decorator) implements FeatureConfiguration {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static final Codec<Config<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.listOf().fieldOf("templates").forGetter(Config::templates),
			TemplatePlacement.CODEC.fieldOf("placement").forGetter(Config::placement),
			PasteConfig.CODEC.fieldOf("paste").forGetter(Config::paste),
			DecoratorConfig.CODEC.fieldOf("decorator").forGetter(Config::decorator)
		).apply(instance, (templates, placement, paste, decorator) -> new Config(templates, placement, paste, decorator)));
	}
}
