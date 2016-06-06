package org.atlasapi.client;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.metabroadcast.common.url.QueryStringParameters;
import org.atlasapi.output.Annotation;

import java.util.Set;


public class EventQuery {

    private static final Joiner JOINER = Joiner.on(',');
    private static final String IDS_PARAMETER = "id";
    private static final String ANNOTATIONS_PARAMETER = "annotations";
    private static final String LIMIT = "limit";
    private static final String OFF_SET = "offset";

    private Set<Annotation> annotations;
    private Set<String> ids;
    private Integer limit;
    private Integer offset;

    private EventQuery(Iterable<String> ids, Iterable<Annotation> annotations, Integer limit, Integer offset) {
        this.ids = ImmutableSet.copyOf(ids);
        this.annotations = ImmutableSet.copyOf(annotations);
        this.limit = limit;
        this.offset = offset;
    }

    public static EventQueryBuilder builder() {
        return new EventQueryBuilder();
    }

    public QueryStringParameters toQueryStringParameters() {
        QueryStringParameters parameters = new QueryStringParameters();
        if (!ids.isEmpty()) {
            parameters.add(IDS_PARAMETER, JOINER.join(ids));
        }
        if (!annotations.isEmpty()) {
            parameters.add(ANNOTATIONS_PARAMETER, JOINER.join(Iterables.transform(annotations, Annotation.TO_KEY)));
        }

        if(limit != null) {
            parameters.add(LIMIT, limit.toString());
        }

        if(offset != null) {
            parameters.add(OFF_SET, offset.toString());
        }

        return parameters;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(EventQuery.class)
                .add(IDS_PARAMETER, ids)
                .add(ANNOTATIONS_PARAMETER, annotations)
                .add(LIMIT, limit.toString())
                .add(OFF_SET, offset.toString())
                .toString();
    }

    public static class EventQueryBuilder {
        ImmutableSortedSet.Builder<Annotation> annotations = ImmutableSortedSet.naturalOrder();
        Set<String> ids = Sets.newHashSet();
        Integer offset;
        Integer limit;
        public EventQueryBuilder withAnnotations(Iterable<Annotation> annotations) {
            this.annotations.addAll(annotations);
            return this;
        }

        public EventQueryBuilder withIds(Iterable<String> ids) {
            Iterables.addAll(this.ids, ids);
            return this;
        }

        public EventQueryBuilder withOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public EventQueryBuilder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public EventQuery build() {
            return new EventQuery(ids, annotations.build(), limit, offset);
        }
    }
}
