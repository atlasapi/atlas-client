package org.atlasapi.client;

import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.url.QueryStringParameters;

public class TopicQuery {
    private static final Joiner JOINER = Joiner.on(',');

    private static final String NAMESPACE_PARAMETER = "namespace";
    private static final String VALUE_PARAMETER = "value";

    private final Set<String> namespaces;
    private final Set<String> values;
    private final Optional<Selection> selection;

    private TopicQuery(Iterable<String> namespaces, Iterable<String> values, Optional<Selection> selection) {
        this.namespaces = ImmutableSet.copyOf(namespaces);
        this.values = ImmutableSet.copyOf(values);
        this.selection = selection;
    }

    public static TopicQueryBuilder builder() {
        return new TopicQueryBuilder();
    }
    
    public QueryStringParameters toQueryStringParameters() {
        QueryStringParameters parameters = new QueryStringParameters();
        
        if (!namespaces.isEmpty()) {
            parameters.add(NAMESPACE_PARAMETER, JOINER.join(namespaces));
        }
        if (!values.isEmpty()) {
            parameters.add(VALUE_PARAMETER, JOINER.join(values));
        }
        
        if (selection.isPresent()) {
            parameters.add(Selection.LIMIT_REQUEST_PARAM, "" + selection.get().getLimit());
            parameters.add(Selection.START_INDEX_REQUEST_PARAM, "" + selection.get().getOffset());
        }
        
        return parameters;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(TopicQuery.class).add("namespaces", namespaces).add("values", values).toString();
    }
    
    public static class TopicQueryBuilder {
        
        Set<String> namespaces = Sets.newHashSet();
        Set<String> values = Sets.newHashSet();
        Optional<Selection> selection = Optional.absent();
        
        public TopicQueryBuilder withNamespaces(Iterable<String> namespaces) {
            Iterables.addAll(this.namespaces, namespaces);
            return this;
        }
        
        public TopicQueryBuilder withValues(Iterable<String> values) {
            Iterables.addAll(this.values, values);
            return this;
        }
        
        public TopicQueryBuilder withNamespaces(String... namespaces) {
            return withNamespaces(ImmutableSet.copyOf(namespaces));
        }
        
        public TopicQueryBuilder withValues(String... values) {
            return withValues(ImmutableSet.copyOf(values));
        }
        
        public TopicQueryBuilder withSelection(Selection selection) {
            this.selection = Optional.fromNullable(selection);
            return this;
        }
        
        public TopicQuery build() {
            return new TopicQuery(namespaces, values, selection);
        }
    }
}
