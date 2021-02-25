package org.atlasapi.client;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.stream.MoreCollectors;
import com.metabroadcast.common.url.QueryStringParameters;
import org.atlasapi.media.entity.Publisher;
import org.atlasapi.output.Annotation;

import java.util.Set;

import javax.annotation.Nullable;

public class ChannelQuery {

    private static final Joiner JOINER = Joiner.on(',');

    private static final String PLATFORMS_PARAMETER = "platforms";
    private static final String REGIONS_PARAMETER = "regions";
    private static final String ANNOTATIONS_PARAMETER = "annotations";
    private static final String AVAILABLE_FROM_PARAMETER = "available_from";
    private static final String URI_PARAMETER = "uri";

    private final Set<String> platforms;
    private final Set<String> regions;
    private final Set<Annotation> annotations;
    private final Set<String> availableFrom;
    private final String uri;

    private final Optional<Selection> selection;

    private ChannelQuery(
            Iterable<String> platforms,
            Iterable<String> regions,
            Optional<Selection> selection,
            Set<Annotation> annotations,
            Set<Publisher> availableFrom,
            @Nullable String uri
    ) {
        this.platforms = ImmutableSet.copyOf(platforms);
        this.regions = ImmutableSet.copyOf(regions);
        this.selection = selection;
        this.annotations = ImmutableSortedSet.copyOf(annotations);
        this.availableFrom = availableFrom.stream()
                .map(Publisher::key)
                .collect(MoreCollectors.toImmutableSet());
        this.uri = uri;
    }

    public static ChannelQueryBuilder builder() {
        return new ChannelQueryBuilder();
    }

    public QueryStringParameters toQueryStringParameters() {
        QueryStringParameters parameters = new QueryStringParameters();

        if (!platforms.isEmpty()) {
            parameters.add(PLATFORMS_PARAMETER, JOINER.join(platforms));
        }
        if (!regions.isEmpty()) {
            parameters.add(REGIONS_PARAMETER, JOINER.join(regions));
        }

        if (selection.isPresent()) {
            parameters.addAll(selection.get().asQueryStringParameters());
        }
        
        if (!annotations.isEmpty()) {
            parameters.add(ANNOTATIONS_PARAMETER, JOINER.join(Iterables.transform(annotations, Annotation.TO_KEY)));
        }

        if (!availableFrom.isEmpty()) {
            parameters.add(AVAILABLE_FROM_PARAMETER, JOINER.join(availableFrom));
        }

        if (!Strings.isNullOrEmpty(uri)) {
            parameters.add(URI_PARAMETER, uri);
        }

        return parameters;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(ChannelQuery.class)
                .add(PLATFORMS_PARAMETER, platforms)
                .add(REGIONS_PARAMETER, regions)
                .add(ANNOTATIONS_PARAMETER, annotations)
                .add("selection", selection)
                .add(AVAILABLE_FROM_PARAMETER, availableFrom)
                .toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChannelQuery) {
            ChannelQuery other = (ChannelQuery) obj;
            return Objects.equal(this.platforms, other.platforms)
                    && Objects.equal(this.regions, other.regions)
                    && Objects.equal(this.annotations, other.annotations)
                    && Objects.equal(this.selection, other.selection)
                    && Objects.equal(this.availableFrom, other.availableFrom);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(platforms, regions, selection, availableFrom);
    }

    public static class ChannelQueryBuilder {
        
        private Set<String> platforms = Sets.newHashSet();
        private Set<String> regions = Sets.newHashSet();
        private ImmutableSortedSet<Annotation> annotations = ImmutableSortedSet.of();
        private Optional<Selection> selection = Optional.absent();
        private Set<Publisher> availableFrom = Sets.newHashSet();
        private String uri;

        public ChannelQueryBuilder withPlatforms(Iterable<String> platforms) {
            Iterables.addAll(this.platforms, platforms);
            return this;
        }

        public ChannelQueryBuilder withPlatforms(String... platforms) {
            return withPlatforms(ImmutableSet.copyOf(platforms));
        }

        public ChannelQueryBuilder withRegions(Iterable<String> regions) {
            Iterables.addAll(this.regions, regions);
            return this;
        }

        public ChannelQueryBuilder withRegions(String... regions) {
            return withRegions(ImmutableSet.copyOf(regions));
        }

        public ChannelQueryBuilder withSelection(Selection selection) {
            this.selection = Optional.fromNullable(selection);
            return this;
        }
        
        public ChannelQueryBuilder withAnnotations(Annotation...annotations) {
            return withAnnotations(ImmutableSortedSet.copyOf(annotations));
        }
        
        public ChannelQueryBuilder withAnnotations(Iterable<Annotation> annotations) {
            this.annotations = ImmutableSortedSet.copyOf(annotations);
            return this;
        }

        public ChannelQueryBuilder withAvailableFrom(Iterable<Publisher> publishers) {
            Iterables.addAll(this.availableFrom, publishers);
            return this;
        }

        public ChannelQueryBuilder withAvailableFrom(Publisher... publishers) {
            return withAvailableFrom(ImmutableSet.copyOf(publishers));
        }

        public ChannelQueryBuilder withUri(String uri) {
            this.uri = uri;
            return this;
        }

        public ChannelQuery build() {
            return new ChannelQuery(platforms, regions, selection, annotations, availableFrom, uri);
        }
    }
}
