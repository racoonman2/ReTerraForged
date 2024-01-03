package raccoonman.reterraforged.world.worldgen.feature.template.template;

import java.util.function.IntFunction;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public abstract class BakedTransform<T> {
    public static final int MIRRORS = Mirror.values().length;
    public static final int ROTATIONS = Rotation.values().length;

    private final T[] backing;

    public BakedTransform(IntFunction<T[]> array, T value) {
        this.backing = array.apply(MIRRORS * ROTATIONS);
        for (Mirror mirror : Mirror.values()) {
            for (Rotation rotation : Rotation.values()) {
                T result = apply(mirror, rotation, value);
                this.backing[indexOf(mirror, rotation)] = result;
            }
        }
    }

    public T get(Mirror mirror, Rotation rotation) {
        return this.backing[indexOf(mirror, rotation)];
    }

    protected abstract T apply(Mirror mirror, Rotation rotation, T value);

    private static int indexOf(Mirror mirror, Rotation rotation) {
        return mirror.ordinal() * ROTATIONS + rotation.ordinal();
    }
}
