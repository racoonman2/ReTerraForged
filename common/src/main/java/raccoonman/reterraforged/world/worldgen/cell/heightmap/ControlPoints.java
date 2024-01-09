package raccoonman.reterraforged.world.worldgen.cell.heightmap;

import raccoonman.reterraforged.data.worldgen.preset.settings.Presets;
import raccoonman.reterraforged.data.worldgen.preset.settings.WorldSettings;

@Deprecated
public record ControlPoints(float mushroomFieldsInland, float mushroomFieldsCoast, float deepOcean, float shallowOcean, float beach, float coast, float coastMarker, float inland) {
    
    public static ControlPoints make(WorldSettings.ControlPoints points) {
        if (!validate(points)) {
            points = Presets.makeLegacyDefault().world().controlPoints;
        }
        return new ControlPoints(points.mushroomFieldsInland, points.mushroomFieldsCoast, points.deepOcean, points.shallowOcean, points.beach, points.coast, points.coast + (points.inland - points.coast) / 2.0F, points.inland);
   }
    
    public static boolean validate(WorldSettings.ControlPoints points) {
        return points.inland <= 1.0F && points.inland > points.coast && points.coast > points.beach && points.beach > points.shallowOcean && points.shallowOcean > points.deepOcean && points.mushroomFieldsCoast >= points.mushroomFieldsInland && points.mushroomFieldsInland >= 0.0F;
    }
}
