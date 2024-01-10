package raccoonman.reterraforged.data.worldgen.preset.settings;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.util.PosUtil;

//TODO fix this
public enum SpawnType implements StringRepresentable {
    CONTINENT_CENTER("CONTINENT_CENTER") {

		@Override
    	public BlockPos getSearchCenter(GeneratorContext ctx) {
    		long center = ctx.generator.getHeightmap().continent().getNearestCenter(0.0F, 0.0F);
    		return new BlockPos(PosUtil.unpackLeft(center), 0, PosUtil.unpackRight(center));
    	}
    	
		@Override
		public List<ParameterPoint> getParameterPoints() {
			return ImmutableList.of();
		}
	}, 
    ISLANDS("ISLANDS") {

		@Override
		public BlockPos getSearchCenter(GeneratorContext ctx) {
			return BlockPos.ZERO;
		}

		@Override
		public List<ParameterPoint> getParameterPoints() {
			return ImmutableList.of();
		}
	},
    WORLD_ORIGIN("WORLD_ORIGIN") {

		@Override
		public BlockPos getSearchCenter(GeneratorContext ctx) {
			return BlockPos.ZERO;
		}

		@Override
		public List<ParameterPoint> getParameterPoints() {
			return ImmutableList.of();
		}
	};
	
	public static final Codec<SpawnType> CODEC = StringRepresentable.fromEnum(SpawnType::values);

	private String name;
	
	private SpawnType(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
	public abstract BlockPos getSearchCenter(GeneratorContext ctx);
	
	public abstract List<ParameterPoint> getParameterPoints();
}