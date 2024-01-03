package raccoonman.reterraforged.concurrent.cache;

public interface ExpiringEntry {
    long getTimestamp();
    
    default void close() {
    }
}
