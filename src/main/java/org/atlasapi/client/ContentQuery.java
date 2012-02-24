package org.atlasapi.client;

import java.util.Set;

import org.atlasapi.output.Annotation;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.metabroadcast.common.url.QueryStringParameters;

public class ContentQuery {
    
    private static final Joiner JOINER = Joiner.on(',');
    
    private static final String URIS_PARAMETER = "uri";
    private static final String ANNOTATIONS_PARAMETER = "annotations";
    
    private final Set<String> uris;
    private final Set<Annotation> annotations;
    
    private ContentQuery(Iterable<String> uris, Iterable<Annotation> annotations) {
        Preconditions.checkArgument(!Iterables.isEmpty(uris));
        
        this.uris = ImmutableSet.copyOf(uris);
        this.annotations = ImmutableSet.copyOf(annotations);
    }
    
    public static ContentQueryBuilder builder() {
        return new ContentQueryBuilder();
    }
    
    public QueryStringParameters toQueryStringParameters() {
        QueryStringParameters parameters = new QueryStringParameters();
        
        if (!uris.isEmpty()) {
            parameters.add(URIS_PARAMETER, JOINER.join(uris));
        }
        if (!annotations.isEmpty()) {
            parameters.add(ANNOTATIONS_PARAMETER, JOINER.join(Iterables.transform(annotations, Annotation.TO_KEY)));
        }
        
        return parameters;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(ContentQuery.class).add("uris", uris).add("annotations", annotations).toString();
    }
    
    public static class ContentQueryBuilder {
        
        Set<String> urls = Sets.newHashSet();
        Set<Annotation> annotations = Sets.newHashSet();
        
        public ContentQueryBuilder withUrls(Iterable<String> urls) {
            Iterables.addAll(this.urls, urls);
            return this;
        }
        
        public ContentQueryBuilder withUrls(String... url) {
            this.urls.addAll(ImmutableSet.copyOf(url));
            return this;
        }
        
        public ContentQueryBuilder withAnnotations(Iterable<Annotation> annotations) {
            Iterables.addAll(this.annotations, annotations);
            return this;
        }
        
        public ContentQueryBuilder withAnnotations(Annotation... annotation) {
            this.annotations.addAll(ImmutableSet.copyOf(annotation));
            return this;
        }
        
        public ContentQuery build() {
            return new ContentQuery(urls, annotations);
        }
    }
    
}
