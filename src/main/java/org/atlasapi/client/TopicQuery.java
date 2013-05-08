package org.atlasapi.client;

import java.util.Collection;
import org.atlasapi.output.Annotation;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.metabroadcast.common.query.Selection;


public class TopicQuery {
    private final String topicId;
    private final Optional<String> uri;
    private final Optional<Selection> selection;
    private final Collection<Annotation> annotations;
    private final Collection<String> rawAnnotations;
    
    private TopicQuery(Builder builder) {
        this.topicId = builder.getTopicId();
        this.uri = Optional.fromNullable(builder.getUri());
        this.selection = builder.getSelection();
        this.annotations = builder.getAnnotations();
        this.rawAnnotations = builder.getRawAnnotations();
    }
    public String getTopicId() {
        return topicId;
    }
    
    public Optional<String> getUri() {
        return uri;
    }
    
    public Optional<Selection> getSelection() {
        return selection;
    }
    
    public Collection<Annotation> getAnnotations() {
        return annotations;
    }
    
    public Collection<String> getRawAnnotations() {
        return rawAnnotations;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String topicId;
        private String uri;
        private Optional<Selection> selection;
        private Collection<Annotation> annotations;
        private Collection<String> rawAnnotations;
        
        private Builder() {
           this.selection = Optional.absent();
           this.annotations = Sets.newHashSet();
           this.rawAnnotations = Sets.newHashSet();
        }
        
        public String getTopicId() {
            return topicId;
        }
        
        public String getUri() {
            return uri;
        }
        
        public Optional<Selection> getSelection() {
            return selection;
        }
        
        public Collection<Annotation> getAnnotations() {
            return annotations;
        }
        
        public Collection<String> getRawAnnotations() {
            return rawAnnotations;
        }
        
        public Builder withTopicId(String topicId) {
            this.topicId = topicId;
            return this;
        }
        
        public Builder withUri(String uri) {
            this.uri = uri;
            return this;
        }
        
        public Builder withSelection(Selection selection) {
            this.selection = Optional.of(selection);
            return this;
        }
        
        public Builder withAnnotations(Annotation... annotations) {
            this.annotations = ImmutableSet.copyOf(annotations);
            return this;
        }
        
        public Builder withRawAnnotations(String... rawAnnotations) {
            this.rawAnnotations = ImmutableSet.copyOf(rawAnnotations);
            return this;
        }
        
        public TopicQuery build() {
            return new TopicQuery(this);
        }
    }
}
