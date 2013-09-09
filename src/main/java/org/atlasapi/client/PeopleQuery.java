package org.atlasapi.client;

import java.util.Set;

import org.atlasapi.media.entity.Publisher;
import org.atlasapi.output.Annotation;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.url.QueryStringParameters;

public class PeopleQuery {
    
    private static final Joiner JOINER = Joiner.on(',');
    
    private static final String URIS_PARAMETER = "uri";
    private static final String IDS_PARAMETER = "id";
    private static final String PUBLISHERS_PARAMETER = "publisher";
    private static final String ANNOTATIONS_PARAMETER = "annotations";
    
    private final Set<String> uris;
    private final Set<String> ids;
    private final Set<Publisher> publishers;
    private final Set<Annotation> annotations;
    private final Optional<Selection> selection;

    
    private PeopleQuery(Iterable<String> uris, Iterable<String> ids, Iterable<Publisher> publishers, 
            Iterable<Annotation> annotations, Optional<Selection> selection) {
        this.uris = ImmutableSet.copyOf(uris);
        this.ids = ImmutableSet.copyOf(ids);
        this.publishers = ImmutableSet.copyOf(publishers);
        this.annotations = ImmutableSet.copyOf(annotations);
        this.selection = selection;
    }
    
    public static PeopleQueryBuilder builder() {
        return new PeopleQueryBuilder();
    }
    
    public QueryStringParameters toQueryStringParameters() {
        QueryStringParameters parameters = new QueryStringParameters();
        
        if (!uris.isEmpty()) {
            parameters.add(URIS_PARAMETER, JOINER.join(uris));
        }
        if (!ids.isEmpty()) {
            parameters.add(IDS_PARAMETER, JOINER.join(ids));
        }
        if (!publishers.isEmpty()) {
            parameters.add(PUBLISHERS_PARAMETER, JOINER.join(publishers));
        }
        if (!annotations.isEmpty()) {
            parameters.add(ANNOTATIONS_PARAMETER, JOINER.join(Iterables.transform(annotations, Annotation.TO_KEY)));
        }
        if (selection.isPresent()) {
            parameters.addAll(selection.get().asQueryStringParameters());
        }
        
        return parameters;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(obj instanceof PeopleQuery) {
            PeopleQuery other = (PeopleQuery) obj;
            return Objects.equal(this.uris, other.uris) 
                    && Objects.equal(this.ids, other.ids)
                    && Objects.equal(this.publishers, other.publishers)
                    && Objects.equal(this.annotations, other.annotations);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(uris, ids, annotations, publishers);
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(PeopleQuery.class)
                    .add("uris", uris)
                    .add("annotations", annotations)
                    .add("publishers", publishers)
                    .toString();
    }
    
    public static class PeopleQueryBuilder {
        
        Set<String> urls = Sets.newHashSet();
        ImmutableSortedSet.Builder<Annotation> annotations = ImmutableSortedSet.naturalOrder();
        Set<String> ids = Sets.newHashSet();
        Optional<Selection> selection = Optional.absent();
        Set<Publisher> publishers = Sets.newHashSet();
        
        public PeopleQueryBuilder withUrls(Iterable<String> urls) {
            Preconditions.checkArgument(this.ids.isEmpty(), "Cannot set urls and ids on a PeopleQuery");
            Iterables.addAll(this.urls, urls);
            return this;
        }
        
        public PeopleQueryBuilder withUrls(String... urls) {
            return withUrls(ImmutableSet.copyOf(urls));
        }
        
        public PeopleQueryBuilder withAnnotations(Iterable<Annotation> annotations) {
            this.annotations.addAll(annotations);
            return this;
        }
        
        public PeopleQueryBuilder withAnnotations(Annotation... annotations) {
            return withAnnotations(ImmutableSet.copyOf(annotations));
        }
        
        public PeopleQueryBuilder withIds(Iterable<String> ids) {
            Preconditions.checkArgument(this.urls.isEmpty() && this.publishers.isEmpty(), 
                    "Can only set one of publishers, ids or urls on a PeopleQuery");
            Iterables.addAll(this.ids, ids);
            return this;
        }
        
        public PeopleQueryBuilder withPublishers(Iterable<Publisher> publishers) {
            Preconditions.checkArgument(this.urls.isEmpty() && this.ids.isEmpty(), 
                    "Can only set one of publishers, ids or urls on a PeopleQuery");
            Iterables.addAll(this.publishers, publishers);
            return this;
        }
        
        public PeopleQueryBuilder withIds(String... ids) {
            Preconditions.checkArgument(this.urls.isEmpty() && this.publishers.isEmpty(), 
                    "Can only set one of publishers, ids or urls on a PeopleQuery");
            return withIds(ImmutableSet.copyOf(ids));
        }
        
        public PeopleQueryBuilder withSelection(Selection selection) {
            this.selection = Optional.fromNullable(selection);
            return this;
        }

        public PeopleQuery build() {
            return new PeopleQuery(urls, ids, publishers, annotations.build(), selection);
        }
    }
    
}
