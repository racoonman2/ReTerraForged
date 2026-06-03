package raccoonman.reterraforged.world.worldgen.compat;

import java.util.List;

import terrablender.api.Region;

@Deprecated
public class TBRegionUtils {
	
	public static int getUniqueness(List<Region> regions, int weight) {
        for (int i = 0; i < regions.size(); i++) {
            if ((weight -= regions.get(i).getWeight()) >= 0) continue;
            return i;
        }
        return 0;
	}

	public static int getTotalWeight(List<Region> regions) {
        int total = 0;
        for (Region region : regions) {
            total += region.getWeight();
        }
        if (total > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Sum of weights must be <= " + Integer.MAX_VALUE);
        }
        return total;
	}
}
