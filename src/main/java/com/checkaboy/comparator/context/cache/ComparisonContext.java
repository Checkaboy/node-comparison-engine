package com.checkaboy.comparator.context.cache;

import com.checkaboy.comparator.path.DefaultComparisonPath;
import com.checkaboy.comparator.path.IComparisonPath;
import com.checkaboy.comparator.policy.IComparisonPolicy;
import com.checkaboy.comparator.registery.IComparatorRegistry;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Taras Shaptala
 */
public final class ComparisonContext
        implements IComparisonContext {

    private final Set<ComparisonPair> visited = new HashSet<>();
    private final IComparatorRegistry registry;
    private final IComparisonPolicy policy;
    private final Deque<IComparisonPath> paths = new ArrayDeque<>();

    public ComparisonContext(IComparatorRegistry registry, IComparisonPolicy policy) {
        this.registry = registry;
        this.policy = policy;
        this.paths.push(DefaultComparisonPath.ROOT);
    }

    @Override
    public boolean enter(Object left, Object right) {
        return visited.add(new ComparisonPair(left, right));
    }

    @Override
    public IComparatorRegistry registry() {
        return registry;
    }

    @Override
    public IComparisonPolicy policy() {
        return policy;
    }

    @Override
    public IComparisonPath path() {
        return paths.peek();
    }

    public void pushPath(IComparisonPath path) {
        paths.push(path);
    }

    public void popPath() {
        if (paths.size() > 1) paths.pop();
    }

    private static final class ComparisonPair {

        private final Object left;
        private final Object right;

        private ComparisonPair(Object left, Object right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ComparisonPair && ((ComparisonPair) o).left == left && ((ComparisonPair) o).right == right;
        }

        @Override
        public int hashCode() {
            return 31 * System.identityHashCode(left) + System.identityHashCode(right);
        }
    }

}
