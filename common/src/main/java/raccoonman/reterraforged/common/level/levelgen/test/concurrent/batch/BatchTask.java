package raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch;

public interface BatchTask extends Runnable {
    public static final Notifier NONE = () -> {};
    
    void setNotifier(Notifier notifier);
    
    public interface Notifier {
        void markDone();
    }
}
