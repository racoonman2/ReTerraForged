package raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache;

public interface ExpiringEntry {
    long getTimestamp();
    
    default void close() {
    }
}
