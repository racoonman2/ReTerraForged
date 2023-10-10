//package raccoonman.reterraforged.common.level.levelgen.noise.continent.fancy;
//
//import java.util.List;
//import java.util.Random;
//
//import com.google.common.collect.ImmutableList;
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//
//import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
//import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
//import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
//import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings.ControlPoints;
//
////TODO
//public record FancyContinent(List<Island> islands) implements Noise {
////	public static final Codec<FancyContinent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
////		
////	).apply(instance, FancyContinent::new));
//	
//	public FancyContinent {
//		islands = ImmutableList.copyOf(islands);
//	}
//	
//	@Override
//	public Codec<FancyContinent> codec() {
//		throw new UnsupportedOperationException();//return CODEC;
//	}
//
//	@Override
//    public float compute(float x, float y, int seed) {
//        float value = 0.0F;
//        for (Island island : this.islands) {
//            float v = island.getEdgeValue(x, y);
//            value = Math.max(v, value);
//        }
//        return value;
//    }
//    
//	@Override
//	public Noise mapAll(Visitor visitor) {
//		return visitor.apply(new FancyContinent(this.islands));
//	}
//
//    private static Island[] generateIslands(ControlPoints controlPoints, int islandCount, int nodeCount, float radius, Random random) {
//        int dirs = 4;
//        Island main = generate(0, controlPoints, nodeCount, radius, random);
//        Island[] islands = new Island[1 + islandCount * dirs];
//        islands[0] = main;
//        int i = 1;
//        float yawStep = 1.0F / dirs * 6.2831855F;
//        for (int dir = 0; dir < dirs; ++dir) {
//            Island previous = main;
//            int nCount = Math.max(2, nodeCount - 1);
//            float r = radius * 0.5F;
//            float yaw = yawStep * dir + random.nextFloat() * yawStep;
//            for (int island = 0; island < islandCount; ++island) {
//                Island next = generate(i, controlPoints, nCount, r, random);
//                float y = yaw + nextFloat(random, -0.2F, 0.2F);
//                float distance = previous.radius();
//                float dx = NoiseUtil.sin(y * 6.2831855F) * distance;
//                float dz = NoiseUtil.cos(y * 6.2831855F) * distance;
//                float ox = previous.getCenter().x() + dx;
//                float oy = previous.getCenter().y() + dz;
//                next.translate(ox, oy);
//                nCount = Math.max(2, nCount - 1);
//                r *= 0.8F;
//                islands[i++] = next;
//                previous = next;
//            }
//        }
//        return islands;
//    }
//    
//    private static Island generate(int id, ControlPoints controlPoints, int nodes, float radius, Random random) {
//        float minScale = 0.75F;
//        float maxScale = 2.5F;
//        float minLen = radius * 1.5F;
//        float maxLen = radius * 3.5F;
//        float maxYaw = 1.5707964F;
//        float minYaw = -maxYaw;
//        Segment[] segments = new Segment[nodes - 1];
//        Vec2f pointA = new Vec2f(0.0F, 0.0F);
//        float scaleA = nextFloat(random, minScale, maxScale);
//        float previousYaw = nextFloat(random, 0.0F, 6.2831855F);
//        for (int i = 0; i < segments.length; ++i) {
//            float length = nextFloat(random, minLen, maxLen);
//            float yaw = previousYaw + nextFloat(random, minYaw, maxYaw);
//            float dx = NoiseUtil.sin(yaw) * length;
//            float dz = NoiseUtil.cos(yaw) * length;
//            Vec2f pointB = new Vec2f(pointA.x() + dx, pointA.y() + dz);
//            float scaleB = nextFloat(random, minScale, maxScale);
//            segments[i] = Segment.of(pointA, pointB, scaleA, scaleB);
//            previousYaw = yaw;
//            pointA = pointB;
//            scaleA = scaleB;
//        }
//        return new Island(id, segments, controlPoints, radius * 3.0F, radius * 1.25F, radius, radius * 0.975F);
//    }
//    
//    private static float nextFloat(Random random, float min, float max) {
//        return min + random.nextFloat() * (max - min);
//    }
//}
