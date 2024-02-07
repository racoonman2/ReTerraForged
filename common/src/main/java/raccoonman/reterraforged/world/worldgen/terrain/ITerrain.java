package raccoonman.reterraforged.world.worldgen.terrain;

public interface ITerrain {
	
	default float erosionModifier() {
		return 1.0F;
	}

	default boolean isFlat() {
		return false;
	}

	default boolean isRiver() {
		return false;
	}

	default boolean isShallowOcean() {
		return false;
	}

	default boolean isDeepOcean() {
		return false;
	}

	default boolean isCoast() {
		return false;
	}

	default boolean isSubmerged() {
		return this.isDeepOcean() || this.isShallowOcean() || this.isRiver() || this.isLake();
	}

	default boolean isOverground() {
		return false;
	}

	default boolean overridesRiver() {
		return this.isDeepOcean() || this.isShallowOcean() || this.isCoast();
	}

	default boolean overridesCoast() {
		return this.isVolcano();
	}

	default boolean isLake() {
		return false;
	}

	default boolean isWetland() {
		return false;
	}

	default boolean isMountain() {
		return false;
	}

	default boolean isVolcano() {
		return false;
	}

	public interface Delegate extends ITerrain {
		ITerrain getDelegate();

		default float erosionModifier() {
			return this.getDelegate().erosionModifier();
		}

		default boolean isFlat() {
			return this.getDelegate().isFlat();
		}

		default boolean isRiver() {
			return this.getDelegate().isRiver();
		}

		default boolean isShallowOcean() {
			return this.getDelegate().isShallowOcean();
		}

		default boolean isDeepOcean() {
			return this.getDelegate().isDeepOcean();
		}

		default boolean isCoast() {
			return this.getDelegate().isCoast();
		}

		default boolean overridesRiver() {
			return this.getDelegate().overridesRiver();
		}

		default boolean overridesCoast() {
			return this.getDelegate().overridesCoast();
		}

		default boolean isLake() {
			return this.getDelegate().isLake();
		}

		default boolean isWetland() {
			return this.getDelegate().isWetland();
		}

		default boolean isOverground() {
			return this.getDelegate().isOverground();
		}

		default boolean isSubmerged() {
			return this.getDelegate().isSubmerged();
		}

		default boolean isMountain() {
			return this.getDelegate().isMountain();
		}

		default boolean isVolcano() {
			return this.getDelegate().isVolcano();
		}
	}
}
