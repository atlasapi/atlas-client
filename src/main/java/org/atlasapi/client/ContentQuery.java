package org.atlasapi.client;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;

import org.atlasapi.output.Annotation;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.url.QueryStringParameters;

public class ContentQuery {
    
    private static final Joiner JOINER = Joiner.on(',');
    
    private static final String URIS_PARAMETER = "uri";
    private static final String IDS_PARAMETER = "id";
    private static final String ANNOTATIONS_PARAMETER = "annotations";
    
    private final Set<String> uris;
    private final Set<String> ids;
    private final Set<Annotation> annotations;
    private final Optional<Selection> selection;
    
    private ContentQuery(Iterable<String> uris, Iterable<String> ids, Iterable<Annotation> annotations, Optional<Selection> selection) {
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
        return Objects.toStringHelper(ContentQuery.class).add("uris", uris).add("annotations", annotations).toString();
    }
    
    public static class ContentQueryBuilder {
        
        Set<String> urls = Sets.newHashSet();
        Set<Annotation> annotations = Sets.newHashSet();
        Set<String> ids = Sets.newHashSet();
        Optional<Selection> selection = Optional.absent();
        
        public ContentQueryBuilder withUrls(Iterable<String> urls) {
            Preconditions.checkArgument(this.ids.isEmpty(), "Cannot set urls and ids on a ContentQuery");
            Iterables.addAll(this.urls, urls);
            return this;
        }
        
        public ContentQueryBuilder withUrls(String... urls) {
            return withUrls(ImmutableSet.copyOf(urls));
        }
        
        public ContentQueryBuilder withAnnotations(Iterable<Annotation> annotations) {
            Iterables.addAll(this.annotations, annotations);
            return this;
        }
        
        public ContentQueryBuilder withAnnotations(Annotation... annotations) {
            return withAnnotations(ImmutableSet.copyOf(annotations));
        }
        
        public ContentQueryBuilder withIds(Iterable<String> ids) {
            Preconditions.checkArgument(this.urls.isEmpty(), "Cannot set urls and ids on a ContentQuery");
            Iterables.addAll(this.ids, ids);
            return this;
        }
        
        public ContentQueryBuilder withIds(String... ids) {
            return withIds(ImmutableSet.copyOf(ids));
        }
        
        public ContentQueryBuilder withSelection(Selection selection) {
            this.selection = Optional.fromNullable(selection);
            return this;
        }

        public ContentQuery build() {
            return new ContentQuery(urls, ids, annotations, selection);
        }
    }
    
}
