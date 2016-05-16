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

    public GsonContentGroupClient(HostSpecifier atlasHost, Optional<String> apiKey,
            GsonQueryClient client) {
        this.apiKey = apiKey;
        this.singleContentGroupQueryPattern = String.format("http://%s/3.0/content_groups/%%s.json", atlasHost);
        this.contentGroupsQuery = String.format("http://%s/3.0/content_groups.json", atlasHost);
        this.contentGroupContentQueryPattern = String.format("http://%s/3.0/content_groups/%%s/content.json", atlasHost);
        this.client = client;
    }
    
    @Override
    public ContentGroupQueryResult contentGroup(String id) {
        return this.contentGroup(id, apiKey);
    }

    @Override
    public ContentGroupQueryResult contentGroup(String id, Optional<String> apiKey) {
        return client.contentGroupQuery(appendApiKey(
                String.format(singleContentGroupQueryPattern, id),
                apiKey
        ));
    }
    
    @Override
    public ContentGroupQueryResult contentGroups() {
        return this.contentGroups(apiKey);
    }

    @Override
    public ContentGroupQueryResult contentGroups(Optional<String> apiKey) {
        return client.contentGroupQuery(appendApiKey(
                contentGroupsQuery,
                apiKey
        ));
    }
    
    @Override
    public ContentQueryResult contentFor(String contentGroupId, Optional<ContentQuery> contentQuery) {
        return this.contentFor(contentGroupId, contentQuery, apiKey);
    }

    @Override
    public ContentQueryResult contentFor(String contentGroupId, Optional<ContentQuery> contentQuery,
            Optional<String> apiKey) {
        String queryString = appendApiKey(
                String.format(contentGroupContentQueryPattern, contentGroupId),
                apiKey
        );
        return executeContentQuery(contentQuery, queryString);
    }

    private ContentQueryResult executeContentQuery(Optional<ContentQuery> contentQuery,
            String queryString) {
        if (contentQuery.isPresent()) {
            queryString = Urls.appendParameters(queryString, contentQuery.get().toQueryStringParameters());
        }
        return client.contentQuery(queryString);
    }

    private String appendApiKey(String uri, Optional<String> apiKey) {
        if (apiKey.isPresent()) {
            return Urls.appendParameters(uri, "apiKey", apiKey.get());
        }
        return uri;
    }
}
