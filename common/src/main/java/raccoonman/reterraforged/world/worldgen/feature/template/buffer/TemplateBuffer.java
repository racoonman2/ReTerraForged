package raccoonman.reterraforged.world.worldgen.feature.template.buffer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import raccoonman.reterraforged.world.worldgen.feature.template.paste.PasteConfig;
import raccoonman.reterraforged.world.worldgen.feature.template.placement.TemplatePlacement;
import raccoonman.reterraforged.world.worldgen.feature.template.template.BlockInfo;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public class TemplateBuffer extends PasteBuffer {
    private LevelAccessor world;
    private BlockPos origin;
    private final BufferBitSet placementMask = new BufferBitSet();

    public TemplateBuffer() {
    	this.setRecording(true);
    }

    public TemplateBuffer init(LevelAccessor world, BlockPos origin, Vec3i p1, Vec3i p2) {
        super.clear();
        this.placementMask.clear();
        this.world = world;
        this.origin = origin;
        this.placementMask.set(p1.getX(), p1.getY(), p1.getZ(), p2.getX(), p2.getY(), p2.getZ());
        return this;
    }

    public void record(int i, BlockInfo block, BlockPos pastePos, TemplatePlacement<?> placement, PasteConfig config) {
        if (!config.replaceSolid() && !placement.canReplaceAt(world, pastePos)) {
        	this.placementMask.set(block.pos().getX(), block.pos().getY(), block.pos().getZ());
            return;
        }

        if (!config.pasteAir() && block.state().getBlock() == Blocks.AIR) {
            return;
        }

        record(i);
    }

    public boolean test(BlockPos pos) {
        int dx = pos.getX() - this.origin.getX();
        int dz = pos.getZ() - this.origin.getZ();
        if (dx == dz) {
            return test(pos, 1, 1);
        }
        if (Math.abs(dx) > Math.abs(dz)) {
            return test(pos, 1F, dz / (float) dx);
        } else {
            return test(pos, dx / (float) dz, 1F);
        }
    }

    private boolean test(BlockPos start, float dx, float dz) {
        int x = start.getX();
        int z = start.getZ();
        float px = x;
        float pz = z;
        int count = 0;
        while (x != origin.getX() && z != origin.getZ() && count < 10) {
            if (this.placementMask.test(x, start.getY(), z)) {
                return false;
            }
            px -= dx;
            pz -= dz;
            x = NoiseUtil.floor(px);
            z = NoiseUtil.floor(pz);
            count++;
        }
        return true;
    }
}
