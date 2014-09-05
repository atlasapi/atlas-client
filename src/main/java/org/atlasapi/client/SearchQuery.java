package org.atlasapi.client;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.atlasapi.media.entity.Publisher;
import org.atlasapi.output.Annotation;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.metabroadcast.common.base.Maybe;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.url.QueryStringParameters;

public class SearchQuery {

    private static final String ANNOTATIONS_PARAMETER = "annotations";
    
    private final Joiner CSV = Joiner.on(',');
    private final NumberFormat fractionFormat = DecimalFormat.getNumberInstance();
    
    private final List<Publisher> publishers;
    private final String query;
    private final Selection selection;
    private final Maybe<Float> titleWeighting;
    private final Maybe<Float> broadcastWeighting;
    private final Maybe<Float> catchupWeighting;
    private final Maybe<String> type;
    private final Maybe<Boolean> topLevelOnly;
    private final Maybe<Boolean> currentBroadcastsOnly;
    private final Set<Annotation> annotations;
    private final Maybe<Float> priorityChannelWeighting;
    
    public SearchQuery(SearchQueryBuilder builder) {
        Preconditions.checkNotNull(builder.query, "Search query must not be null");
        fractionFormat.setMaximumFractionDigits(2);
        this.query = builder.query;
        this.publishers = ImmutableList.copyOf(builder.publishers);
        this.selection = builder.selection;
        this.titleWeighting = builder.titleWeighting;
        this.broadcastWeighting = builder.broadcastWeighting;
        this.catchupWeighting = builder.catchupWeighting;
        this.type = builder.type;
        this.topLevelOnly = builder.topLevelOnly;
        this.currentBroadcastsOnly = builder.currentBroadcastsOnly;
        this.annotations = builder.annotations;
        this.priorityChannelWeighting = builder.priorityChannelWeighting;
    }
    
    public QueryStringParameters toParams() {
        QueryStringParameters params = new QueryStringParameters();
        params.add("q", query);
        
        if (!publishers.isEmpty()) {
            params.add("publisher", CSV.join(Iterables.transform(publishers, Publisher.TO_KEY)));
        }
        if (selection != null) {
            if (selection.getLimit() != null) {
                params.add(Selection.LIMIT_REQUEST_PARAM, selection.getLimit().toString());
            }
            if (selection.getOffset() > 0) {
                params.add(Selection.START_INDEX_REQUEST_PARAM, String.valueOf(selection.getOffset()));
            }
        }
        if (titleWeighting.hasValue()) {
            params.add("titleWeighting", fractionFormat.format(titleWeighting.requireValue()));
        }
        if (broadcastWeighting.hasValue()) {
            params.add("broadcastWeighting", fractionFormat.format(broadcastWeighting.requireValue()));
        }
        if (catchupWeighting.hasValue()) {
            params.add("catchupWeighting", fractionFormat.format(catchupWeighting.requireValue()));
        }
        if (type.hasValue()) {
            params.add("type", type.requireValue());
        }
        if (currentBroadcastsOnly.hasValue()) {
            params.add("currentBroadcastsOnly", currentBroadcastsOnly.requireValue().toString());
        }
        if (topLevelOnly.hasValue()) {
            params.add("topLevelOnly", topLevelOnly.requireValue().toString());
        }
        if (priorityChannelWeighting.hasValue()) {
            params.add("priorityChannelWeighting", fractionFormat.format(priorityChannelWeighting.requireValue()));
        }
        if (!annotations.isEmpty()) {
            params.add(ANNOTATIONS_PARAMETER, CSV.join(Iterables.transform(annotations, Annotation.TO_KEY)));
        }
        
        return params;
    }

    public static final class SearchQueryBuilder {

        private LinkedHashSet<Publisher> publishers = Sets.newLinkedHashSet();
        private Selection selection;
        private String query;
        private Maybe<Float> titleWeighting = Maybe.nothing();
        private Maybe<Float> broadcastWeighting = Maybe.nothing();
        private Maybe<Float> catchupWeighting = Maybe.nothing();
        private Maybe<String> type = Maybe.nothing();
        private Maybe<Boolean> topLevelOnly = Maybe.nothing();
        private Maybe<Boolean> currentBroadcastsOnly = Maybe.nothing();
        private ImmutableSortedSet<Annotation> annotations = ImmutableSortedSet.of();
        private Maybe<Float> priorityChannelWeighting = Maybe.nothing();

        private SearchQueryBuilder() {
        }

        public SearchQuery build() {
            return new SearchQuery(this);
        }

        public SearchQueryBuilder withQuery(String query) {
            this.query = query;
            return this;
        }

        public SearchQueryBuilder withPublishers(Iterable<Publisher> publishers) {
            this.publishers = Sets.newLinkedHashSet(publishers);
            return this;
        }
        
        public SearchQueryBuilder withSelection(Selection selection) {
            this.selection = selection;
            return this;
        }
        
        public SearchQueryBuilder withTitleWeighting(float titleWeighting) {
            this.titleWeighting = Maybe.just(titleWeighting);
            return this;
        }
        
        public SearchQueryBuilder withBroadcastWeighting(float broadcastWeighting) {
            this.broadcastWeighting = Maybe.just(broadcastWeighting);
            return this;
        }
        
        public SearchQueryBuilder withCatchupWeighting(float catchupWeighting) {
            this.catchupWeighting = Maybe.just(catchupWeighting);
            return this;
        }
        
        public SearchQueryBuilder withType(String type) {
            this.type = Maybe.just(type);
            return this;
        }
        
        public SearchQueryBuilder withCurrentBroadcastsOnly(boolean currentBroadcastsOnly) {
            this.currentBroadcastsOnly = Maybe.just(currentBroadcastsOnly);
            return this;
        }
        
        public SearchQueryBuilder withTopLevelOnly(boolean topLevelOnly) {
            this.topLevelOnly = Maybe.just(topLevelOnly);
            return this;
        }

        public SearchQueryBuilder withAnnotations(Iterable<Annotation> annotations) {
            this.annotations = ImmutableSortedSet.copyOf(annotations);
            return this;
        }
        
        public SearchQueryBuilder withAnnotations(Annotation...annotations) {
            return withAnnotations(Arrays.asList(annotations));
        }

        public SearchQueryBuilder withAnnotations(ImmutableCollection.Builder<Annotation> annotations) {
            return withAnnotations(annotations.build());
        }

        public SearchQueryBuilder withPriorityChannelWeighting(float priorityChannelWeighting) {
            this.priorityChannelWeighting  = Maybe.just(priorityChannelWeighting);
            return this;
        }
        
    }

    public static SearchQueryBuilder builder() {
        return new SearchQueryBuilder();
    }
}
