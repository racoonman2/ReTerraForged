package raccoonman.reterraforged.common.level.levelgen.test.world.heightmap;

import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;

public record ControlPoints(float deepOcean, float shallowOcean, float beach, float coast, float coastMarker, float inland) {
    
    public static ControlPoints make(WorldSettings.ControlPoints points) {
        if (!validate(points)) {
            points = WorldSettings.ControlPoints.makeDefault();
        }
        return new ControlPoints(points.deepOcean, points.shallowOcean, points.beach, points.coast, points.coast + (points.inland - points.coast) / 2.0F, points.inland);
    }
    
    public static boolean validate(WorldSettings.ControlPoints points) {
        return points.inland <= 1.0F && points.inland > points.coast && points.coast > points.beach && points.beach > points.shallowOcean && points.shallowOcean > points.deepOcean && points.deepOcean >= 0.0F;
    }
}
