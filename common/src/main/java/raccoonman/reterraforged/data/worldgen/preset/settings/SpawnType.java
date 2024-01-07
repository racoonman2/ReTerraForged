package raccoonman.reterraforged.data.worldgen.preset.settings;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.util.PosUtil;

//TODO fix this
public enum SpawnType implements StringRepresentable {
    CONTINENT_CENTER("CONTINENT_CENTER") {
//    	private static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
//        private static final Climate.Parameter INLAND_CONTINENTALNESS = Climate.Parameter.point(SpawnType.CONTINENT_CENTER_NOISE_TARGET);
//		
//		@Override
//		public List<ParameterPoint> getParameterPoints() {
//			//FIXME this will spawn us on coasts as well
//		    return List.of(new Climate.ParameterPoint(FULL_RANGE, FULL_RANGE, INLAND_CONTINENTALNESS, FULL_RANGE, Climate.Parameter.point(0.0F), FULL_RANGE, 0L));
//		}
    	@Override
    	public BlockPos getSearchCenter(GeneratorContext ctx) {
    		long center = ctx.generator.getHeightmap().continent().getNearestCenter(0.0F, 0.0F);
    		return new BlockPos(PosUtil.unpackLeft(center), 0, PosUtil.unpackRight(center));
    	}
	}, 
    WORLD_ORIGIN("WORLD_ORIGIN") {

		@Override
		public BlockPos getSearchCenter(GeneratorContext ctx) {
			return BlockPos.ZERO;
		}
//		
//		@Override
//		public List<ParameterPoint> getParameterPoints() {
//			return ImmutableList.of();
//		}
	};
	
//	public static final float CONTINENT_CENTER_NOISE_TARGET = 1.0F;
	
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
	
//	public abstract List<ParameterPoint> getParameterPoints();
}