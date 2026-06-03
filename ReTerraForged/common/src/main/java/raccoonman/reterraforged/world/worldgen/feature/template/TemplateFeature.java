package raccoonman.reterraforged.world.worldgen.feature.template;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.extensions.RTFTemplateManager;
import raccoonman.reterraforged.world.worldgen.feature.template.TemplateFeature.Config;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.DecoratorConfig;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TemplateDecorator;
import raccoonman.reterraforged.world.worldgen.feature.template.paste.Paste;
import raccoonman.reterraforged.world.worldgen.feature.template.paste.PasteConfig;
import raccoonman.reterraforged.world.worldgen.feature.template.paste.PasteType;
import raccoonman.reterraforged.world.worldgen.feature.template.placement.TemplatePlacement;

public class TemplateFeature extends Feature<Config<?>> {
    private static final Vec3i[] TRANSFORM_OFFSETS = { new Vec3i(0, 0, 1), new Vec3i(1, 0, 1), new Vec3i(1, 0, 0) };
	
    public TemplateFeature(Codec<Config<?>> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<Config<?>> ctx) {
		WorldGenLevel level = ctx.level();
		RandomSource random = ctx.random();
		Config<?> config = ctx.config();
		
		Mirror mirror = nextMirror(random);
		Rotation rotation = nextRotation(random);
		BlockPos pos = ctx.origin();
		if(config.transform()) { 
			pos = pos.subtract(getTranslation(mirror, rotation));
		}
        return paste(level, random, pos, mirror, rotation, config, FeatureTemplate.WORLD_GEN);
	}

    private static <T extends TemplateContext> boolean paste(WorldGenLevel world, RandomSource rand, BlockPos pos, Mirror mirror, Rotation rotation, Config<T> config, PasteType pasteType) {
    	return paste(world, rand, pos, mirror, rotation, config, pasteType, false);
    }

    private static <T extends TemplateContext> boolean paste(WorldGenLevel world, RandomSource rand, BlockPos pos, Mirror mirror, Rotation rotation, Config<T> config, PasteType pasteType, boolean modified) {
        if (config.templates().isEmpty()) {
            RTFCommon.LOGGER.warn("Empty template list for config");
            return false;
        }
        
        if(world.getServer().getStructureManager() instanceof RTFTemplateManager rtfTemplateManager) {
	        DecoratorConfig<T> decoratorConfig = config.decorator();
	        
	        ResourceLocation templateName = nextTemplate(config.templates, rand);
	        FeatureTemplate template = rtfTemplateManager.loadFeatureTemplate(templateName);
	        
	        Dimensions dimensions = template.getDimensions(mirror, rotation);
	        TemplatePlacement<T> placement = config.placement();
	        if (!placement.canPlaceAt(world, pos, dimensions)) {
	            return false;
	        }
	
	        Paste paste = pasteType.get(template);
	        T buffer = placement.createContext();
	        if (paste.paste(world, buffer, pos, mirror, rotation, placement, config.paste())) {
	            ResourceKey<Biome> biome = world.getBiome(pos).unwrapKey().get();//1898y00
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

    private static Vec3i getTranslation(Mirror mirror, Rotation rotation) {
        if(mirror == Mirror.NONE && rotation == Rotation.NONE) {
            return Vec3i.ZERO;
        }
    	
        int minX = 0;
        int minZ = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (Vec3i vec : TRANSFORM_OFFSETS) {
            BlockPos dir = FeatureTemplate.transform(pos.set(vec), mirror, rotation);
            minX = Math.min(dir.getX(), minX);
            minZ = Math.min(dir.getZ(), minZ);
        }
        return new Vec3i(minX, 0, minZ);
    }
    
	public record Config<T extends TemplateContext>(List<ResourceLocation> templates, TemplatePlacement<T> placement, PasteConfig paste, DecoratorConfig<T> decorator, boolean transform) implements FeatureConfiguration {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static Codec<Config<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.listOf().fieldOf("templates").forGetter(Config::templates),
			TemplatePlacement.CODEC.fieldOf("placement").forGetter(Config::placement),
			PasteConfig.CODEC.fieldOf("paste").forGetter(Config::paste),
			DecoratorConfig.CODEC.fieldOf("decorator").forGetter(Config::decorator),
			Codec.BOOL.fieldOf("transform").forGetter(Config::transform)
		).apply(instance, (templates, placement, paste, decorator, transform) -> new Config(templates, placement, paste, decorator, transform)));
	}
}
