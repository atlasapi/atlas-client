package org.atlasapi.client;

import org.atlasapi.media.entity.simple.ContentGroupQueryResult;
import org.atlasapi.media.entity.simple.ContentQueryResult;

import com.google.common.base.Optional;
import com.google.common.net.HostSpecifier;
import com.metabroadcast.common.url.Urls;

public class GsonContentGroupClient implements AtlasContentGroupClient {

    private final Optional<String> apiKey;
    private final String singleContentGroupQueryPattern;
    private final String contentGroupsQuery;
    private final String contentGroupContentQueryPattern;
    private final GsonQueryClient client;

    public GsonContentGroupClient(HostSpecifier atlasHost, Optional<String> apiKey) {
        this.apiKey = apiKey;
        this.singleContentGroupQueryPattern = String.format("http://%s/3.0/content_groups/%%s.json", atlasHost);
        this.contentGroupsQuery = String.format("http://%s/3.0/content_groups.json", atlasHost);
        this.contentGroupContentQueryPattern = String.format("http://%s/3.0/content_groups/%%s/content.json", atlasHost);
        this.client = new GsonQueryClient();
    }
    
    @Override
    public ContentGroupQueryResult contentGroup(String id) {
        return client.contentGroupQuery(appendApiKey(String.format(singleContentGroupQueryPattern, id)));
    }
    
    @Override
    public ContentGroupQueryResult contentGroups() {
        return client.contentGroupQuery(appendApiKey(contentGroupsQuery));
    }
    
    @Override
    public ContentQueryResult contentFor(String contentGroupid, Optional<ContentQuery> contentQuery) {
        String queryString = appendApiKey(String.format(contentGroupContentQueryPattern, contentGroupid));
        if (contentQuery.isPresent()) {
            queryString = Urls.appendParameters(queryString, contentQuery.get().toQueryStringParameters());
        }
        return client.contentQuery(queryString);
    }
    
    private String appendApiKey(String uri) {
        if (apiKey.isPresent()) {
            return Urls.appendParameters(uri, "apiKey", apiKey.get());
        }
        return uri;
    }
   

}
