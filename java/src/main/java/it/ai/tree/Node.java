package it.ai.tree;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Node<K, L> {
    @Getter(AccessLevel.PROTECTED)
    protected final K key;

    @Getter(AccessLevel.PROTECTED)
    protected final L link;

    @Getter
    protected final Node<K, L> parent;
    @Getter
    protected final Map<L, Child<K, L>> children = new HashMap<>();

    public Node(Node<K, L> parent, K key, L link) {
        this.parent = parent;
        this.key = key;
        this.link = link;
    }

    protected void addChild(L link) {
        children.put(link, new Child<>(link));
    }

    protected void addChild(Node<K, L> childNode) {
        children.put(childNode.getLink(), new Child<>(childNode.getLink(), childNode));
    }

    protected Child<K, L> getChild(L arcIndex) {
        return children.get(arcIndex);
    }

    protected boolean hasChild(L key) {
        return children.containsKey(key);
    }

    protected Stream<Child<K, L>> getChildren() {
        return children.values().stream();
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public static final class Child<K, L> {
        private final L link;
        private final Node<K, L> node;

        public Child(L link) {
            this(link, null);
        }

        public Child(L link, Node<K, L> node) {
            this.link = link;
            this.node = node;
        }

        public L getLink() {
            return this.link;
        }

        public Node<K, L> getNode() {
            return this.node;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (!(o instanceof Child)) return false;

            Child that = (Child) o;

            return new EqualsBuilder().append(link, that.link).append(node, that.node).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(link)
                    .append(node).toHashCode();
        }

        @Override
        public String toString() {
            return "MonteCarloChild{" +
                    "key=" + link +
                    ", node=" + node +
                    '}';
        }
    }
}
