package raccoonman.reterraforged.concurrent;

import raccoonman.reterraforged.concurrent.cache.SafeCloseable;

public interface Resource<T> extends SafeCloseable {
    public static final Resource<?> NONE = new Resource<>() {
        @Override
        public Object get() {
            return null;
        }
        
        @Override
        public boolean isOpen() {
            return false;
        }
        
        @Override
        public void close() {
        }
    };
    
    T get();
    
    boolean isOpen();
    
    @SuppressWarnings("unchecked")
	public static <T> Resource<T> empty() {
        return (Resource<T>) Resource.NONE;
    }
}
