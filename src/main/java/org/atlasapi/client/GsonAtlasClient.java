package org.atlasapi.client;

import java.util.List;

import org.atlasapi.client.query.AtlasQuery;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Description;
import org.atlasapi.media.entity.simple.DiscoverQueryResult;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;

import com.google.common.base.Joiner;
import com.metabroadcast.common.url.QueryStringParameters;
import com.metabroadcast.common.url.UrlEncoding;

public class GsonAtlasClient implements AtlasClient {
    
    private final QueryStringBuilder queryStringBuilder = new QueryStringBuilder();
    private final GsonQueryClient client = new GsonQueryClient();
    private final Joiner joiner = Joiner.on(",");
    private final String baseUri;
    private final String apiKey;
    
    public GsonAtlasClient(String baseUri, String apiKey) {
        this.baseUri = baseUri;
        this.apiKey = apiKey;
        this.queryStringBuilder.setApiKey(apiKey);
    }

    @Override
    public ContentQueryResult content(Iterable<String> ids) {
        return client.contentQuery(baseUri + "/content.json?uri=" +  joiner.join(UrlEncoding.encode(ids)) + apiKeyQueryPart());
    }
    private String apiKeyQueryPart() {
        if (this.apiKey != null) {
            return "&apiKey="+this.apiKey;
        }
        return "";
    }

    @Override
    public DiscoverQueryResult discover(AtlasQuery query) {
        List<Description> contents = client.contentQuery(baseUri + "/discover.json?" + queryStringBuilder.build(query.build())).getContents();
        return new DiscoverQueryResult(contents);
    }

    @Override
    public ScheduleQueryResult scheduleFor(ScheduleQuery query) {
        QueryStringParameters params = query.toParams();
        if (apiKey != null) {
            params.add("apiKey", apiKey);
        }
        return client.scheduleQuery(baseUri + "/schedule.json?" + params.toQueryString());
    }

    @Override
    public ContentQueryResult search(SearchQuery query) {
        QueryStringParameters params = query.toParams();
        if (apiKey != null) {
            params.add("apiKey", apiKey);
        }
        return client.contentQuery(baseUri + "/search.json?" + params.toQueryString());
    }

    @Override
    public PeopleQueryResult people(Iterable<String> uris) {
        
        return client.peopleQuery(baseUri + "/people.json?uri=" +  joiner.join(UrlEncoding.encode(uris)) + apiKeyQueryPart());
    }
}
