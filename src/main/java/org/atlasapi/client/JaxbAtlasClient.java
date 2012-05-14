package org.atlasapi.client;

import org.atlasapi.client.query.AtlasQuery;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.DiscoverQueryResult;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;

import com.google.common.base.Joiner;
import com.metabroadcast.common.url.QueryStringParameters;
import com.metabroadcast.common.url.UrlEncoding;
import org.atlasapi.media.entity.simple.ContentGroupQueryResult;

public class JaxbAtlasClient implements AtlasClient {
    private QueryStringBuilder queryStringBuilder = new QueryStringBuilder();
    private StringQueryClient queryClient;

    private final String baseUri;
    private String apiKey;

    public JaxbAtlasClient(String baseUri, StringQueryClient queryClient) {
        this.baseUri = baseUri;
        this.queryClient = queryClient;
    }
    
    public JaxbAtlasClient(String baseUri) {
        this(baseUri, new JaxbStringQueryClient());
    }
    
    public JaxbAtlasClient(String baseUri, String apiKey) {
        this(baseUri, new JaxbStringQueryClient());
        this.withApiKey(apiKey);
    }
    
    public JaxbAtlasClient withApiKey(String apiKey) {
        this.apiKey = apiKey;
        this.queryStringBuilder.setApiKey(apiKey);
        return this;
    }
    
    @Override
    public DiscoverQueryResult discover(AtlasQuery query) {
        return new DiscoverQueryResult(queryClient.contentQuery(baseUri + "/discover.xml?" + queryStringBuilder.build(query.build())).getContents());
    }

    private String apiKeyQueryPart() {
        if (this.apiKey != null) {
            return "&apiKey="+this.apiKey;
        }
        return "";
    }

    @Override
    public ScheduleQueryResult scheduleFor(ScheduleQuery query) {
        // avoid using the naive cache, schedules require day-binned caches provided by higher-layers.
        QueryStringParameters params = query.toParams();
        if (apiKey != null) {
            params.add("apiKey", apiKey);
        }
        return queryClient.scheduleQuery(baseUri + "/schedule.xml?" + params.toQueryString());
    }

    @Override
    public ContentQueryResult content(Iterable<String> uris) {
        return queryClient.contentQuery(baseUri + "/content.xml?uri=" +  Joiner.on(",").join(UrlEncoding.encode(uris)) + apiKeyQueryPart());
    }
    
    @Override
    public ContentQueryResult content(ContentQuery query) {
        return queryClient.contentQuery(baseUri + "/content.xml?" + withApiKey(query.toQueryStringParameters()).toQueryString()); 
    }
    
    @Override
    public ContentGroupQueryResult contentGroup(String id) {
        return queryClient.contentGroupQuery(baseUri + "/content_groups/" + id + ".xml?" + apiKeyQueryPart());
    }
    
    @Override
    public ContentGroupQueryResult contentGroups() {
        return queryClient.contentGroupQuery(baseUri + "/content_groups.xml?" +  apiKeyQueryPart());
    }

    @Override
    public ContentQueryResult search(SearchQuery query) {
        QueryStringParameters params = query.toParams();
        if (apiKey != null) {
            params.add("apiKey", apiKey);
        }
        return queryClient.contentQuery(baseUri + "/search.xml?" + params.toQueryString());
    }

    @Override
    public PeopleQueryResult people(Iterable<String> uris) {
        return queryClient.peopleQuery(baseUri + "/people.xml?uri=" + Joiner.on(",").join(UrlEncoding.encode(uris)) + apiKeyQueryPart());
    }
    
    public QueryStringParameters withApiKey(QueryStringParameters parameters) {
        if (apiKey != null) {
            parameters.add("apiKey", apiKey);
        }
        return parameters;
    }
}
