package raccoonman.reterraforged.world;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import net.minecraft.world.phys.Vec3;
import raccoonman.reterraforged.extensions.compat.TBTargetPoint;

public record PointFinder(int step, int minRadius, int maxRadius, OptionalLong timeout) {
	public static final Codec<PointFinder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("step").forGetter(PointFinder::step),
		Codec.INT.fieldOf("min_radius").forGetter(PointFinder::minRadius),
		Codec.INT.fieldOf("max_radius").forGetter(PointFinder::maxRadius),
		Codec.LONG.optionalFieldOf("timeout").xmap((o) -> {
			if(o.isPresent()) {
				return OptionalLong.of(o.get());
			}
			return OptionalLong.empty();
		}, (l) -> {
			if(l.isPresent()) {
				return Optional.of(l.getAsLong());
			}
			return Optional.empty();
		}).forGetter(PointFinder::timeout)
	).apply(instance, PointFinder::new));
	
	public Optional<BlockPos> findPoint(List<Climate.ParameterPoint> parameterPoints, Climate.Sampler sampler) {
		if(parameterPoints.isEmpty()) {
			return Optional.of(BlockPos.ZERO);
		}
		
		return this.findPoint(Vec3.ZERO, (pos) -> {
			return fitness(sampler, parameterPoints, pos) == 0L;
		}, () -> true);
	}
	
	public Optional<BlockPos> findPoint(Vec3 origin, Predicate<BlockPos> test, BooleanSupplier running) {
	    int radius = this.maxRadius;
	    double minRadiusSq = this.minRadius * this.minRadius;
	    int x = 0;
	    int z = 0;
	    int dx = 0;
	    int dz = -1;
	    int size = radius * 2 + 1;
	    long max = (long) size * (long) size;
	    long timeOut = this.timeout.isPresent() ? System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(this.timeout.getAsLong()) : Long.MAX_VALUE;
	    BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
	    for (long i = 0; i < max; i++) {
	    	if (!running.getAsBoolean() || System.currentTimeMillis() > timeOut) {
	            break;
	        }
	
	        if ((-radius <= x) && (x <= radius) && (-radius <= z) && (z <= radius)) {
	            pos.set(origin.x() + (x * this.step), origin.y(), origin.z() + (z * this.step));
	            if (minRadiusSq == 0 || origin.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) >= minRadiusSq) {
	            	if(test.test(pos)) {
	            		return Optional.of(pos);
	            	}
	            }
	        }
	
	        if ((x == z) || ((x < 0) && (x == -z)) || ((x > 0) && (x == 1 - z))) {
	            size = dx;
	            dx = -dz;
	            dz = size;
	        }
	
	        x += dx;
	        z += dz;
	    }
	    return Optional.empty();
	}
    
    public static long fitness(Climate.Sampler sampler, List<Climate.ParameterPoint> points, BlockPos pos) {
	    Climate.TargetPoint target = sampler.sample(QuartPos.fromBlock(pos.getX()), 0, QuartPos.fromBlock(pos.getZ()));
	    Climate.TargetPoint noDepth = new TargetPoint(target.temperature(), target.humidity(), target.continentalness(), target.erosion(), 0L, target.weirdness());
	    TBTargetPoint.copy(target, noDepth);
	    long fitness = Long.MAX_VALUE;
	    for(Climate.ParameterPoint point : points) {
	    	fitness = Math.min(fitness, point.fitness(noDepth));
	    }
	    return fitness;
    }
    
    public static PointFinder of(int step, int minRadius, int maxRadius) {
    	return new PointFinder(step, minRadius, maxRadius, OptionalLong.empty());
    }
    
    public static PointFinder of(int step, int minRadius, int maxRadius, long timeout) {
    	return new PointFinder(step, minRadius, maxRadius, OptionalLong.of(timeout));
    }
}