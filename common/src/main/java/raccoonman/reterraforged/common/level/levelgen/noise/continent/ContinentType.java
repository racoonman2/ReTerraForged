package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public enum ContinentType {
    MULTI(0) {
        
		@Override
		public Noise create(int seed) {
			throw new UnsupportedOperationException("TODO");
		}
    }, 
    SINGLE(1) {

		@Override
		public Noise create(int seed) {
			throw new UnsupportedOperationException("TODO");
		}
    }, 
    MULTI_IMPROVED(2) {

    	@Override
		public Noise create(int seed) {
			return null;
		}
    }, 
    EXPERIMENTAL(3) {
        
		@Override
		public Noise create(int seed) {
			throw new UnsupportedOperationException("TODO");
		}
    };
    
    public int index;
    
    private ContinentType(int index) {
        this.index = index;
    }
    
    public abstract Noise create(int seed);
}
