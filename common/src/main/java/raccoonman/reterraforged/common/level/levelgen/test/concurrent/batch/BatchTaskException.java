package raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch;

@SuppressWarnings("serial")
public class BatchTaskException extends RuntimeException {

	public BatchTaskException(String message) {
        super(message);
    }
    
    public BatchTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
