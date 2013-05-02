package org.atlasapi.client;

import java.util.List;
import java.util.Set;

import org.atlasapi.output.Annotation;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metabroadcast.common.url.QueryStringParameters;

public class ContentQuery {
    
    private static final Joiner JOINER = Joiner.on(',');
    
    private static final String URIS_PARAMETER = "uri";
    private static final String IDS_PARAMETER = "id";
    private static final String ANNOTATIONS_PARAMETER = "annotations";
    
    private final Set<String> uris;
    private final Set<String> ids;
    private final Set<Annotation> annotations;
    private final Set<String> rawAnnotations;
    
    private ContentQuery(Iterable<String> uris, Iterable<String> ids, Iterable<Annotation> annotations, Iterable<String> rawAnnotations) {
        this.uris = ImmutableSet.copyOf(uris);
        this.ids = ImmutableSet.copyOf(ids);
        this.annotations = ImmutableSet.copyOf(annotations);
        this.rawAnnotations = ImmutableSet.copyOf(rawAnnotations);
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
        if (!annotations.isEmpty() || !rawAnnotations.isEmpty()) {
            List<String> annotationStrings = Lists.newArrayList();
            annotationStrings.addAll(Lists.transform(ImmutableList.copyOf(annotations), Annotation.toKeyFunction()));
            annotationStrings.addAll(rawAnnotations);            
            parameters.add(ANNOTATIONS_PARAMETER, JOINER.join(annotationStrings));
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
                    && Objects.equal(this.annotations, other.annotations);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(uris, ids, annotations);
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(ContentQuery.class).add("uris", uris).add("annotations", annotations).toString();
    }
    
    public static class ContentQueryBuilder {
        
        Set<String> urls = Sets.newHashSet();
        Set<Annotation> annotations = Sets.newHashSet();
        Set<String> ids = Sets.newHashSet();
        Set<String> rawAnnotations = Sets.newHashSet();;
        
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
        
        public ContentQueryBuilder withRawAnnotations(String... rawAnnotations) {
            this.rawAnnotations = ImmutableSet.copyOf(rawAnnotations);
            return this;
        }
        
        public ContentQueryBuilder withIds(Iterable<String> ids) {
//            Preconditions.checkArgument(this.urls.isEmpty(), "Cannot set urls and ids on a ContentQuery");
            Iterables.addAll(this.ids, ids);
            return this;
        }
        
        public ContentQueryBuilder withIds(String... ids) {
            return withIds(ImmutableSet.copyOf(ids));
        }

        public ContentQuery build() {
            return new ContentQuery(urls, ids, annotations, rawAnnotations);
        }
    }
    
}
