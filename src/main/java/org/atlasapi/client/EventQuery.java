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

    private Set<Annotation> annotations;
    private Set<String> ids;

    private EventQuery(Iterable<String> ids, Iterable<Annotation> annotations) {
        this.ids = ImmutableSet.copyOf(ids);
        this.annotations = ImmutableSet.copyOf(annotations);
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
        return parameters;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(EventQuery.class)
                .add(IDS_PARAMETER, ids)
                .add(ANNOTATIONS_PARAMETER, annotations)
                .toString();
    }

    public static class EventQueryBuilder {
        ImmutableSortedSet.Builder<Annotation> annotations = ImmutableSortedSet.naturalOrder();
        Set<String> ids = Sets.newHashSet();

        public EventQueryBuilder withAnnotations(Iterable<Annotation> annotations) {
            this.annotations.addAll(annotations);
            return this;
        }

        public EventQueryBuilder withIds(Iterable<String> ids) {
            Iterables.addAll(this.ids, ids);
            return this;
        }

        public EventQuery build() {
            return new EventQuery(ids, annotations.build());
        }
    }
}
