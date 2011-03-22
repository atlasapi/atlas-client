package org.atlasapi.client;

import java.util.LinkedHashSet;
import java.util.List;

import org.atlasapi.media.entity.Publisher;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.url.QueryStringParameters;

public class SearchQuery {
    
    private final Joiner CSV = Joiner.on(',');
    
    private final List<Publisher> publishers;
    private final String query;
    private final Selection selection;
    
    public SearchQuery(SearchQueryBuilder builder) {
        Preconditions.checkNotNull(builder.query, "Search query must not be null");
        this.query = builder.query;
        this.publishers = ImmutableList.copyOf(builder.publishers);
        this.selection = builder.selection;
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
        return params;
    }

    public static final class SearchQueryBuilder {

        private LinkedHashSet<Publisher> publishers = Sets.newLinkedHashSet();
        private Selection selection;
        private String query;

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
    }

    public static SearchQueryBuilder builder() {
        return new SearchQueryBuilder();
    }
}
