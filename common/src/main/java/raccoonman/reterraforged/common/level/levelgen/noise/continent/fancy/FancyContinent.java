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
//import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;
//import raccoonman.reterraforged.common.level.levelgen.noise.util.Vec2f;
//
//public record FancyContinent(List<Island> islands) implements Noise {
//	public static final Codec<FancyContinent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//		Island.CODEC.listOf().fieldOf("islands").forGetter(FancyContinent::islands)
//	).apply(instance, FancyContinent::new));
//	
//	@Override
//	public Codec<FancyContinent> codec() {
//		return CODEC;
//	}
//
//	@Override
//	public float compute(float x, float y, int seed) {
//        float value = 0.0F;
//        for (Island island : this.islands) {
//            float v = island.getEdgeValue(x, y);
//            value = Math.max(v, value);
//        }
//        return value;
//	}
//
//	@Override
//	public Noise mapAll(Visitor visitor) {
//		return visitor.apply(new FancyContinent(this.islands));
//	}
//	
//	private static List<Island> generateIslands(ControlPoints controlPoints, int islandCount, int nodeCount, float radius, Random random) {
//        int dirs = 4;
//        Island main = generate(0, controlPoints, nodeCount, radius, random);
//        Island[] islands = new Island[1 + islandCount * dirs];
//        islands[0] = main;
//        int i = 1;
//        float yawStep = 1.0f / dirs * 6.2831855f;
//        for (int dir = 0; dir < dirs; ++dir) {
//            Island previous = main;
//            int nCount = Math.max(2, nodeCount - 1);
//            float r = radius * 0.5f;
//            float yaw = yawStep * dir + random.nextFloat() * yawStep;
//            for (int island = 0; island < islandCount; ++island) {
//                Island next = generate(i, controlPoints, nCount, r, random);
//                float y = yaw + nextFloat(random, -0.2f, 0.2f);
//                float distance = previous.radius();
//                float dx = NoiseUtil.sin(y * 6.2831855f) * distance;
//                float dz = NoiseUtil.cos(y * 6.2831855f) * distance;
//                float ox = previous.getCenter().x() + dx;
//                float oy = previous.getCenter().y() + dz;
//                next.translate(new Vec2f(ox, oy));
//                nCount = Math.max(2, nCount - 1);
//                r *= 0.8F;
//                islands[i++] = next;
//                previous = next;
//            }
//        }
//        return ImmutableList.copyOf(islands);
//    }
//    
//    private static Island generate(int id, ControlPoints controlPoints, int nodes, float radius, Random random) {
//        float minScale = 0.75f;
//        float maxScale = 2.5f;
//        float minLen = radius * 1.5f;
//        float maxLen = radius * 3.5f;
//        float maxYaw = 1.5707964f;
//        float minYaw = -maxYaw;
//        Segment[] segments = new Segment[nodes - 1];
//        Vec2f pointA = new Vec2f(0.0f, 0.0f);
//        float scaleA = nextFloat(random, minScale, maxScale);
//        float previousYaw = nextFloat(random, 0.0f, 6.2831855f);
//        for (int i = 0; i < segments.length; ++i) {
//            float length = nextFloat(random, minLen, maxLen);
//            float yaw = previousYaw + nextFloat(random, minYaw, maxYaw);
//            float dx = NoiseUtil.sin(yaw) * length;
//            float dz = NoiseUtil.cos(yaw) * length;
//            Vec2f pointB = new Vec2f(pointA.x() + dx, pointA.y() + dz);
//            float scaleB = nextFloat(random, minScale, maxScale);
//            segments[i] = Segment.create(pointA, pointB, scaleA, scaleB);
//            previousYaw = yaw;
//            pointA = pointB;
//            scaleA = scaleB;
//        }
//        return new Island(id, segments, controlPoints, radius * 3.0f, radius * 1.25f, radius, radius * 0.975f);
//    }
//    
//    private static float nextFloat(Random random, float min, float max) {
//        return min + random.nextFloat() * (max - min);
//    }
//}
