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

public class ContentQuery {
    
    private static final Joiner JOINER = Joiner.on(',');
    
    private static final String URIS_PARAMETER = "uri";
    private static final String IDS_PARAMETER = "id";
    private static final String ANNOTATIONS_PARAMETER = "annotations";
    private static final String PUBLISHER_PARAMETER = "publisher";
    
    private final Set<String> uris;
    private final Set<String> ids;
    private final Set<Annotation> annotations;
    private final Optional<Selection> selection;
    private final Optional<Publisher> publisher;

    private ContentQuery(Publisher publisher, Iterable<Annotation> annotations,  Optional<Selection> selection) {
        this.publisher = Optional.of(publisher);
        this.uris = ImmutableSet.of();
        this.ids = ImmutableSet.of();
        this.annotations = ImmutableSet.copyOf(annotations);
        this.selection = selection;
    }

    private ContentQuery(Iterable<String> uris, Iterable<String> ids, Iterable<Annotation> annotations,
            Optional<Selection> selection) {
        this.publisher = Optional.absent();
        this.uris = ImmutableSet.copyOf(uris);
        this.ids = ImmutableSet.copyOf(ids);
        this.annotations = ImmutableSet.copyOf(annotations);
        this.selection = selection;
    }
    
    public static ContentQueryBuilder builder() {
        return new ContentQueryBuilder();
    }
    
    public QueryStringParameters toQueryStringParameters() {
        QueryStringParameters parameters = new QueryStringParameters();
        
        if (!uris.isEmpty()) {
            parameters.add(URIS_PARAMETER, JOINER.join(uris));
        }
        if (!ids.isEmpty()) {
            parameters.add(IDS_PARAMETER, JOINER.join(ids));
        }
        if (!annotations.isEmpty()) {
            parameters.add(ANNOTATIONS_PARAMETER, JOINER.join(Iterables.transform(annotations, Annotation.TO_KEY)));
        }
        if (publisher.isPresent()) {
            parameters.add(PUBLISHER_PARAMETER, publisher.get().key());
        }
        
        if (selection.isPresent()) {
            parameters.add(Selection.LIMIT_REQUEST_PARAM, "" + selection.get().getLimit());
            parameters.add(Selection.START_INDEX_REQUEST_PARAM, "" + selection.get().getOffset());
        }
        
        return parameters;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(obj instanceof ContentQuery) {
            ContentQuery other = (ContentQuery) obj;
            return Objects.equal(this.uris, other.uris) 
                    && Objects.equal(this.ids, other.ids)
                    && Objects.equal(this.annotations, other.annotations)
                    && Objects.equal(this.selection, other.selection);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(uris, ids, annotations, selection);
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(ContentQuery.class)
                .add("uris", uris)
                .add("annotations", annotations)
                .add("selection", selection).toString();
    }
    
    public static class ContentQueryBuilder {
        
        Set<String> urls = Sets.newHashSet();
        ImmutableSortedSet.Builder<Annotation> annotations = ImmutableSortedSet.naturalOrder();
        Set<String> ids = Sets.newHashSet();
        Optional<Selection> selection = Optional.absent();
        Optional<Publisher> publisher = Optional.absent();
        
        public ContentQueryBuilder withUrls(Iterable<String> urls) {
            Preconditions.checkArgument(this.ids.isEmpty() && !this.publisher.isPresent(), 
                    "Cannot set urls if ids or publisher are set, they're mutually exclusive");
            Iterables.addAll(this.urls, urls);
            return this;
        }
        
        public ContentQueryBuilder withUrls(String... urls) {
            return withUrls(ImmutableSet.copyOf(urls));
        }
        
        public ContentQueryBuilder withAnnotations(Iterable<Annotation> annotations) {
            this.annotations.addAll(annotations);
            return this;
        }
        
        public ContentQueryBuilder withAnnotations(Annotation... annotations) {
            return withAnnotations(ImmutableSet.copyOf(annotations));
        }
        
        public ContentQueryBuilder withIds(Iterable<String> ids) {
            Preconditions.checkArgument(this.urls.isEmpty() && !this.publisher.isPresent(), 
                    "Cannot set ids if publisher or urls are set, they're mutually exclusive");
            Iterables.addAll(this.ids, ids);
            return this;
        }
        
        public ContentQueryBuilder withIds(String... ids) {
            return withIds(ImmutableSet.copyOf(ids));
        }
        
        public ContentQueryBuilder withPublisher(Publisher publisher) {
            Preconditions.checkArgument(this.urls.isEmpty() && this.ids.isEmpty(),
                    "Cannot set publisher if urls or ids are set, they're mutually exclusive");
            this.publisher = Optional.fromNullable(publisher);
            return this;
        }
        
        public ContentQueryBuilder withSelection(Selection selection) {
            this.selection = Optional.fromNullable(selection);
            return this;
        }

        public ContentQuery build() {
            if (publisher.isPresent()) {
                return new ContentQuery(publisher.get(), annotations.build(), selection);
            }
            return new ContentQuery(urls, ids, annotations.build(), selection);
        }
    }
    
}
