package raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch;

@SuppressWarnings("serial")
public class BatchTimeoutException extends BatchTaskException {
	
    public BatchTimeoutException(String message) {
        super(message);
    }
}
