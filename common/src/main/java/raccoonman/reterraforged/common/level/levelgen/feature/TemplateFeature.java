package raccoonman.reterraforged.common.level.levelgen.feature;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import raccoonman.reterraforged.common.level.levelgen.feature.TemplateFeature.Config;

public class TemplateFeature extends Feature<Config> {

	public TemplateFeature(Codec<Config> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<Config> ctx) {
		WorldGenLevel level = ctx.level();
		RandomSource random = ctx.random();
		StructureTemplateManager structuretemplatemanager = level.getLevel().getServer().getStructureManager();
		Config config = ctx.config();
		ResourceLocation templateId = Util.getRandom(config.templates, random);
		StructureTemplate template = structuretemplatemanager.getOrCreate(templateId);
		BlockPos blockpos = ctx.origin();
		Rotation rotation = Rotation.getRandom(random);
		Vec3i vec3i = template.getSize(rotation);
		ChunkPos chunkpos = new ChunkPos(blockpos);
		BoundingBox boundingbox = new BoundingBox(chunkpos.getMinBlockX() - 16, level.getMinBuildHeight(), chunkpos.getMinBlockZ() - 16, chunkpos.getMaxBlockX() + 16, level.getMaxBuildHeight(), chunkpos.getMaxBlockZ() + 16);
		StructurePlaceSettings settings = (new StructurePlaceSettings()).setRotation(rotation).setBoundingBox(boundingbox).setRandom(random);
		BlockPos offsetPos = blockpos.offset(-vec3i.getX() / 2, 0, -vec3i.getZ() / 2);
		if(config.placementPredicate.test(level, offsetPos)) {
			return template.placeInWorld(level, offsetPos, offsetPos, settings, random, 2);
		} else {
			return false;
		}
	}

	public record Config(BlockPredicate placementPredicate, List<ResourceLocation> templates) implements FeatureConfiguration {
		public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BlockPredicate.CODEC.fieldOf("placement").forGetter(Config::placementPredicate),
			ResourceLocation.CODEC.listOf().fieldOf("templates").forGetter(Config::templates)
		).apply(instance, Config::new));
		
		public Config(BlockPredicate placementPredicate, ResourceLocation... templates) {
			this(placementPredicate, ImmutableList.copyOf(templates));
		}
		
		public Config {
			templates = ImmutableList.copyOf(templates);
		}
	}
}
