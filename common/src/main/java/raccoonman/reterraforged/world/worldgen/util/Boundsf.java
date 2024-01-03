package raccoonman.reterraforged.world.worldgen.util;

public record Boundsf(float minX, float minY, float maxX, float maxY) {
    public static final Boundsf NONE = new Boundsf(1.0F, 1.0F, -1.0F, -1.0F);
    
    public boolean contains(float x, float y) {
        return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private float minX;
        private float minY;
        private float maxX;
        private float maxY;
        
        public Builder() {
            this.minX = Float.MAX_VALUE;
            this.minY = Float.MAX_VALUE;
            this.maxX = Float.MIN_VALUE;
            this.maxY = Float.MIN_VALUE;
        }
        
        public void record(float x, float y) {
            this.minX = Math.min(this.minX, x);
            this.minY = Math.min(this.minY, y);
            this.maxX = Math.max(this.maxX, x);
            this.maxY = Math.max(this.maxY, y);
        }
        
        public Boundsf build() {
            return new Boundsf(this.minX, this.minY, this.maxX, this.maxY);
        }
    }
}
