package org.atlasapi.client;

import java.util.LinkedHashSet;
import java.util.List;

import org.atlasapi.media.entity.Publisher;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.metabroadcast.common.base.Maybe;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.url.QueryStringParameters;

public class SearchQuery {
    
    private final Joiner CSV = Joiner.on(',');
    
    private final List<Publisher> publishers;
    private final String query;
    private final Selection selection;
    private final Maybe<Float> titleWeighting;
    private final Maybe<Float> broadcastWeighting;
    private final Maybe<Float> catchupWeighting;
    
    public SearchQuery(SearchQueryBuilder builder) {
        Preconditions.checkNotNull(builder.query, "Search query must not be null");
        this.query = builder.query;
        this.publishers = ImmutableList.copyOf(builder.publishers);
        this.selection = builder.selection;
        this.titleWeighting = builder.titleWeighting;
        this.broadcastWeighting = builder.broadcastWeighting;
        this.catchupWeighting = builder.catchupWeighting;
    }
    
    QueryStringParameters toParams() {
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
            params.add("titleWeighting", String.valueOf(titleWeighting.requireValue()));
        }
        if (broadcastWeighting.hasValue()) {
            params.add("broadcastWeighting", String.valueOf(broadcastWeighting.requireValue()));
        }
        if (catchupWeighting.hasValue()) {
            params.add("catchupWeighting", String.valueOf(catchupWeighting.requireValue()));
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
    }

    public static SearchQueryBuilder builder() {
        return new SearchQueryBuilder();
    }
}
