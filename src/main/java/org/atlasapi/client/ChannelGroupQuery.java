package org.atlasapi.client;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.url.QueryStringParameters;

public class ChannelGroupQuery {
    
    private final Optional<Selection> selection;
    
    private ChannelGroupQuery(Optional<Selection> selection) {        
        this.selection = selection;
    }
    
    public static ChannelGroupQueryBuilder builder() {
        return new ChannelGroupQueryBuilder();
    }
    
    public QueryStringParameters toQueryStringParameters() {
        QueryStringParameters parameters = new QueryStringParameters();
                
        if (selection.isPresent()) {
            parameters.add(Selection.LIMIT_REQUEST_PARAM, "" + selection.get().getLimit());
            parameters.add(Selection.START_INDEX_REQUEST_PARAM, "" + selection.get().getOffset());
        }
        
        return parameters;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(ChannelGroupQuery.class).toString();
    }
    
    public static class ChannelGroupQueryBuilder {
        Optional<Selection> selection = Optional.absent();
        
        
        public ChannelGroupQueryBuilder withSelection(Selection selection) {
            this.selection = Optional.fromNullable(selection);
            return this;
        }

        public ChannelGroupQuery build() {
            return new ChannelGroupQuery(selection);
        }
    }
}
