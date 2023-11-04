package raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache;

public interface SafeCloseable extends AutoCloseable {
    public static final SafeCloseable NONE = () -> {};
    
    void close();
}
