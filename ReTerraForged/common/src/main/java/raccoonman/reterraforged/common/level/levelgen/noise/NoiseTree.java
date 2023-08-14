package raccoonman.reterraforged.common.level.levelgen.noise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;

// TODO rename this
public class NoiseTree {

	public static record Parameter(float min, float max) {
		public static final Codec<NoiseTree.Parameter> CODEC = ExtraCodecs.intervalCodec(
			Codec.floatRange(0.0F, 1.0F),
			"min", "max", (min, max) -> {
				return min.compareTo(max) > 0 ? DataResult.error(() -> {
					return "Cannon construct interval, min > max (" + min + " > " + max + ")";
				}) : DataResult.success(new NoiseTree.Parameter(min, max));
			}, (param) -> {
				return param.min();
			}, (max) -> {
				return max.max();
			}
		);

		public static NoiseTree.Parameter point(float value) {
			return span(value, value);
		}

		public static NoiseTree.Parameter span(float min, float max) {
			if (min > max) {
				throw new IllegalArgumentException("min > max: " + min + " " + max);
			} else {
				return new NoiseTree.Parameter(min, max);
			}
		}
		
		public static NoiseTree.Parameter min(float min) {
			return span(min, 1.0F);
		}
		
		public static NoiseTree.Parameter max(float max) {
			return span(0.0F, max);
		}
		
		public static NoiseTree.Parameter ignore() {
			return span(0.0F, 1.0F);
		}

		public static NoiseTree.Parameter span(NoiseTree.Parameter p1, NoiseTree.Parameter p2) {
			if (p1.min() > p2.max()) {
				throw new IllegalArgumentException("min > max: " + p1 + " " + p2);
			} else {
				return new NoiseTree.Parameter(p1.min(), p2.max());
			}
		}

		@Override
		public String toString() {
			return this.min == this.max ? String.format(Locale.ROOT, "%d", this.min)
					: String.format(Locale.ROOT, "[%d-%d]", this.min, this.max);
		}

		public float distance(float point) {
			float i = point - this.max;
			float j = this.min - point;
			return i > 0L ? i : Math.max(j, 0L);
		}
		
		private NoiseTree.Parameter span(@Nullable NoiseTree.Parameter parameter) {
			return parameter == null ? this : new NoiseTree.Parameter(Math.min(this.min, parameter.min()), Math.max(this.max, parameter.max()));
		}
	}

	public static class PointList<V, T extends Point<V>> {
		private final List<T> values;
		private final NoiseTree.RTree<V, T> index;

		public PointList(int paramCount, List<T> values) {
			this.values = values;
			this.index = NoiseTree.RTree.create(paramCount, values);
		}

		public List<T> values() {
			return this.values;
		}

		public V findValue(float... params) {
			return this.index.search(params);
		}
		
		public static <V, T extends Point<V>> Codec<PointList<V, T>> codec(Codec<T> pointCodec, int paramCount) {
			return ExtraCodecs.nonEmptyList(pointCodec.listOf()).xmap((values) -> new NoiseTree.PointList<>(paramCount, values), NoiseTree.PointList::values);
		}
	}
	
	protected static final class RTree<V, T extends Point<V>> {
		private final NoiseTree.RTree.Node<V, T> root;
		private final ThreadLocal<NoiseTree.RTree.Leaf<V, T>> lastResult = new ThreadLocal<>();

		private RTree(NoiseTree.RTree.Node<V, T> root) {
			this.root = root;
		}

		public static <V, T extends Point<V>> NoiseTree.RTree<V, T> create(int paramCount, List<T> points) {
			if (points.isEmpty()) {
				throw new IllegalArgumentException("Need at least one value to build the search tree.");
			} else {
				int i = points.get(0).parameterSpace().size();
				if (i != paramCount) {
					throw new IllegalStateException("Expecting parameter space to be " + paramCount + ", got " + i);
				} else {
					List<NoiseTree.RTree.Leaf<V, T>> list = points.stream().map((point) -> {
						return new NoiseTree.RTree.Leaf<>(paramCount, point);
					}).collect(Collectors.toCollection(ArrayList::new));
					return new NoiseTree.RTree<>(build(i, list));
				}
			}
		}

		private static <V, T extends Point<V>> NoiseTree.RTree.Node<V, T> build(int paramCount, List<? extends NoiseTree.RTree.Node<V, T>> nodes) {
			if (nodes.isEmpty()) {
				throw new IllegalStateException("Need at least one child to build a node");
			} else if (nodes.size() == 1) {
				return nodes.get(0);
			} else if (nodes.size() <= paramCount) {
				nodes.sort(Comparator.comparingLong((node) -> {
					long i1 = 0L;

					for (int j1 = 0; j1 < paramCount; ++j1) {
						NoiseTree.Parameter param = node.parameterSpace[j1];
						i1 += Math.abs((param.min() + param.max()) / 2L);
					}

					return i1;
				}));
				return new NoiseTree.RTree.SubTree<>(paramCount, nodes);
			} else {
				long i = Long.MAX_VALUE;
				int j = -1;
				List<NoiseTree.RTree.SubTree<V, T>> list = null;

				for (int k = 0; k < paramCount; ++k) {
					sort(nodes, paramCount, k, false);
					List<NoiseTree.RTree.SubTree<V, T>> list1 = bucketize(paramCount, nodes);
					long l = 0L;

					for (NoiseTree.RTree.SubTree<V, T> subtree : list1) {
						l += cost(subtree.parameterSpace);
					}

					if (i > l) {
						i = l;
						j = k;
						list = list1;
					}
				}

				sort(list, paramCount, j, true);
				return new NoiseTree.RTree.SubTree<>(paramCount, list.stream().map((tree) -> {
					return build(paramCount, Arrays.asList(tree.children));
				}).collect(Collectors.toList()));
			}
		}

		private static <V, T extends Point<V>> void sort(List<? extends NoiseTree.RTree.Node<V, T>> nodes, int count, int paramIndex, boolean abs) {
			Comparator<NoiseTree.RTree.Node<V, T>> comparator = comparator(paramIndex, abs);

			for (int i = 1; i < count; ++i) {
				comparator = comparator.thenComparing(comparator((paramIndex + i) % count, abs));
			}

			nodes.sort(comparator);
		}

		private static <V, T extends Point<V>> Comparator<NoiseTree.RTree.Node<V, T>> comparator(int paramIndex, boolean abs) {
			return Comparator.comparingDouble((node) -> {
				NoiseTree.Parameter param = node.parameterSpace[paramIndex];
				float i = (param.min() + param.max()) / 2F;
				return abs ? Math.abs(i) : i;
			});
		}

		private static <V, T extends Point<V>> List<NoiseTree.RTree.SubTree<V, T>> bucketize(int paramCount, List<? extends NoiseTree.RTree.Node<V, T>> nodes) {
			List<NoiseTree.RTree.SubTree<V, T>> list = Lists.newArrayList();
			List<NoiseTree.RTree.Node<V, T>> list1 = Lists.newArrayList();
			int i = (int) Math.pow(paramCount, Math.floor(Math.log((double) nodes.size() - 0.01D) / Math.log(paramCount)));

			for (NoiseTree.RTree.Node<V, T> node : nodes) {
				list1.add(node);
				if (list1.size() >= i) {
					list.add(new NoiseTree.RTree.SubTree<>(paramCount, list1));
					list1 = Lists.newArrayList();
				}
			}

			if (!list1.isEmpty()) {
				list.add(new NoiseTree.RTree.SubTree<>(paramCount, list1));
			}
			return list;
		}

		private static long cost(NoiseTree.Parameter[] params) {
			long i = 0L;

			for (NoiseTree.Parameter param : params) {
				i += Math.abs(param.max() - param.min());
			}

			return i;
		}

		static <V, T extends Point<V>> List<NoiseTree.Parameter> buildParameterSpace(int paramCount, List<? extends NoiseTree.RTree.Node<V, T>> nodes) {
			if (nodes.isEmpty()) {
				throw new IllegalArgumentException("SubTree needs at least one child");
			} else {
				List<NoiseTree.Parameter> list = Lists.newArrayList();

				for (int j = 0; j < paramCount; ++j) {
					list.add((NoiseTree.Parameter) null);
				}

				for (NoiseTree.RTree.Node<V, T> node : nodes) {
					for (int k = 0; k < paramCount; ++k) {
						list.set(k, node.parameterSpace[k].span(list.get(k)));
					}
				}

				return list;
			}
		}

		public V search(float[] params) {
			NoiseTree.RTree.Leaf<V, T> leaf = this.root.search(params, this.lastResult.get());
			this.lastResult.set(leaf);
			return leaf.value;
		}

		static final class Leaf<V, T extends Point<V>> extends NoiseTree.RTree.Node<V, T> {
			final V value;

			Leaf(int paramCount, T point) {
				super(paramCount, point.parameterSpace());
				this.value = point.value();
			}

			protected NoiseTree.RTree.Leaf<V, T> search(float[] params, @Nullable NoiseTree.RTree.Leaf<V, T> leaf) {
				return this;
			}
		}

		abstract static class Node<V, T extends Point<V>> {
			private int paramCount;
			protected final NoiseTree.Parameter[] parameterSpace;

			protected Node(int paramCount, List<NoiseTree.Parameter> paramSpace) {
				this.paramCount = paramCount;
				this.parameterSpace = paramSpace.toArray(new NoiseTree.Parameter[0]);
			}

			protected abstract NoiseTree.RTree.Leaf<V, T> search(float[] params, @Nullable NoiseTree.RTree.Leaf<V, T> leaf);

			protected float distance(float[] params) {
				float i = 0L;

				for (int j = 0; j < this.paramCount; ++j) {
					i += Mth.square(this.parameterSpace[j].distance(params[j]));
				}

				return i;
			}

			public String toString() {
				return Arrays.toString((Object[]) this.parameterSpace);
			}
		}

		static final class SubTree<V, T extends Point<V>> extends NoiseTree.RTree.Node<V, T> {
			final NoiseTree.RTree.Node<V, T>[] children;

			protected SubTree(int paramCount, List<? extends NoiseTree.RTree.Node<V, T>> nodes) {
				this(paramCount, NoiseTree.RTree.buildParameterSpace(paramCount, nodes), nodes);
			}

			@SuppressWarnings("unchecked")
			protected SubTree(int paramCount, List<NoiseTree.Parameter> params, List<? extends NoiseTree.RTree.Node<V, T>> nodes) {
				super(paramCount, params);
				this.children = nodes.toArray(new NoiseTree.RTree.Node[0]);
			}

			protected NoiseTree.RTree.Leaf<V, T> search(float[] params, @Nullable NoiseTree.RTree.Leaf<V, T> leaf) {
				float i = leaf == null ? Float.MAX_VALUE : leaf.distance(params);
				NoiseTree.RTree.Leaf<V, T> found = leaf;

				for (NoiseTree.RTree.Node<V, T> node : this.children) {
					float j = node.distance(params);
					if (i > j) {
						NoiseTree.RTree.Leaf<V, T> leaf1 = node.search(params, found);
						float k = node == leaf1 ? j : leaf1.distance(params);
						if (i > k) {
							i = k;
							found = leaf1;
						}
					}
				}

				return found;
			}
		}
	}
	
	public interface Point<V> {
		V value();
		
		List<NoiseTree.Parameter> parameterSpace();
	}
	
}