package org.atlasapi.client;

import java.util.List;

import org.atlasapi.client.query.AtlasQuery;
import org.atlasapi.media.entity.simple.ContentGroupQueryResult;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Description;
import org.atlasapi.media.entity.simple.DiscoverQueryResult;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.Person;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.net.HostSpecifier;
import com.metabroadcast.common.url.QueryStringParameters;
import com.metabroadcast.common.url.UrlEncoding;
import com.metabroadcast.common.url.Urls;

public class GsonAtlasClient implements AtlasClient, AtlasWriteClient {
    
    private final QueryStringBuilder queryStringBuilder = new QueryStringBuilder();
    private final GsonQueryClient client = new GsonQueryClient();
    private final Joiner joiner = Joiner.on(",");
    private final String baseUri;
    private final Optional<String> apiKey;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    
    public GsonAtlasClient(HostSpecifier atlasHost, Optional<String> apiKey) {
        this.apiKey = apiKey;
        this.baseUri = String.format("http://%s/3.0", atlasHost);
    }
    
    @Deprecated
    public GsonAtlasClient(String baseUri, String apiKey) {
        this.baseUri = baseUri;
        this.apiKey = Optional.fromNullable(apiKey);
        this.queryStringBuilder.setApiKey(apiKey);
    }

    @Override
    public ContentQueryResult content(Iterable<String> ids) {
        return client.contentQuery(baseUri + "/content.json?uri=" +  joiner.join(UrlEncoding.encode(ids)) + apiKeyQueryPart());
    }
    
    @Override
    public ContentQueryResult content(ContentQuery query) {
        return client.contentQuery(baseUri + "/content.json?" + withApiKey(query.toQueryStringParameters()).toQueryString());
    }
    
    @Override
    @Deprecated
    public ContentGroupQueryResult contentGroup(String id) {
        return client.contentGroupQuery(baseUri + "/content_groups/" + id + ".json?" + apiKeyQueryPart());
    }
    
    @Override
    @Deprecated
    public ContentGroupQueryResult contentGroups() {
        return client.contentGroupQuery(baseUri + "/content_groups.json?" +  apiKeyQueryPart());
    }
    
    private String apiKeyQueryPart() {
        if (apiKey.isPresent()) {
            return "&apiKey=" + apiKey.get();
        }
        return "";
    }
    
    public QueryStringParameters withApiKey(QueryStringParameters parameters) {
        if (apiKey.isPresent()) {
            parameters.add("apiKey", apiKey.get());
        }
        return parameters;
    }

    @Override
    public DiscoverQueryResult discover(AtlasQuery query) {
        List<Description> contents = client.contentQuery(baseUri + "/discover.json?" + queryStringBuilder.build(query.build())).getContents();
        return new DiscoverQueryResult(contents);
    }

    @Override
    public ScheduleQueryResult scheduleFor(ScheduleQuery query) {
        QueryStringParameters params = query.toParams();
        if (apiKey.isPresent()) {
            params.add("apiKey", apiKey.get());
        }
        return client.scheduleQuery(baseUri + "/schedule.json?" + params.toQueryString());
    }

    @Override
    public ContentQueryResult search(SearchQuery query) {
        QueryStringParameters params = query.toParams();
        if (apiKey.isPresent()) {
            params.add("apiKey", apiKey.get());
        }
        return client.contentQuery(baseUri + "/search.json?" + params.toQueryString());
    }

    @Override
    public PeopleQueryResult people(Iterable<String> uris) {
        
        return client.peopleQuery(baseUri + "/people.json?uri=" +  joiner.join(UrlEncoding.encode(uris)) + apiKeyQueryPart());
    }
    
    @Override
    public PeopleQueryResult people(PeopleQuery query) {
        return client.peopleQuery(baseUri + "/people.json?" + withApiKey(query.toQueryStringParameters()).toQueryString());
    }
    
    @Override
    public void writePerson(Person person) {
        QueryStringParameters queryParams = new QueryStringParameters();
        if (apiKey.isPresent()) {
            queryParams.add("apiKey", apiKey.get());
        }

        String queryString = Urls.appendParameters(baseUri + "/people.json?", queryParams);
        client.postTopic(queryString, person);
    }
    
}
