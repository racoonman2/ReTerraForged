package raccoonman.reterraforged.common.level.levelgen.test.concurrent;

public interface Disposable {
    void dispose();
    
    public interface Listener<T> {
        void onDispose(T ctx);
    }
}
