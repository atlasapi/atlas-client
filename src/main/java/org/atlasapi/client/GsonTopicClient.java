package org.atlasapi.client;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Topic;
import org.atlasapi.media.entity.simple.TopicQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.net.HostSpecifier;
import com.metabroadcast.common.url.QueryStringParameters;
import com.metabroadcast.common.url.Urls;

public class GsonTopicClient implements AtlasTopicClient {

    private Logger log = LoggerFactory.getLogger(GsonTopicClient.class);
    
    private final String topicPattern;
    private final String topicsPattern;
    private final String topicContentPattern;
    private final GsonQueryClient stringQueryClient;

    private final Optional<String> apiKey;

    public GsonTopicClient(HostSpecifier atlasHost, Optional<String> apiKey) {
        this.apiKey = apiKey;
        this.topicPattern = String.format("http://%s/3.0/topics/%%s.json", atlasHost);
        this.topicsPattern = String.format("http://%s/3.0/topics.json", atlasHost);
        this.topicContentPattern = String.format("http://%s/3.0/topics/%%s/content.json", atlasHost);
        this.stringQueryClient = new GsonQueryClient();
    }

    @Override
    public Optional<Topic> topic(String topicId) {
        String queryString = apiKey.isPresent() ? Urls.appendParameters(String.format(topicPattern, topicId), "apiKey", apiKey.get()) : String.format(topicPattern, topicId);
        log.trace("Performing Atlas Topic query: " + queryString);
        return Optional.fromNullable(Iterables.getOnlyElement(stringQueryClient.topicQuery(queryString).getContents(), null));
    }

    @Override
    public TopicQueryResult topics(TopicQuery query) {
        QueryStringParameters queryParams = query.toQueryStringParameters();
        if (apiKey.isPresent()) {
            queryParams.add("apiKey", apiKey.get());
        }
        
        String queryString = Urls.appendParameters(topicsPattern, queryParams);
        log.trace("Performing Atlas Topics query: " + queryString);
        return stringQueryClient.topicQuery(queryString);
    }

    @Override
    public ContentQueryResult contentFor(String topicId, ContentQuery query) {
        String queryString = apiKey.isPresent() ? Urls.appendParameters(String.format(topicContentPattern, topicId), "apiKey", apiKey.get()) : String.format(topicContentPattern, topicId);
        queryString = Urls.appendParameters(queryString, query.toQueryStringParameters());
        log.trace("Performing Atlas Content for Topic query: " + queryString);
        return stringQueryClient.contentQuery(queryString);
    }

    @Override
    public String postTopic(Topic topic) {
        QueryStringParameters queryParams = new QueryStringParameters();
        if (apiKey.isPresent()) {
            queryParams.add("apiKey", apiKey.get());
        }
        
        String queryString = Urls.appendParameters(topicsPattern, queryParams);
        log.trace("POSTing Topic to Atlas, " + queryString, topic);
        return stringQueryClient.postTopic(queryString, topic);
    }

    @Override
    public TopicUpdateResponse postTopicWithResponse(Topic topic) {
        QueryStringParameters queryParams = new QueryStringParameters();
        if (apiKey.isPresent()) {
            queryParams.add("apiKey", apiKey.get());
        }

        String queryString = Urls.appendParameters(topicsPattern, queryParams);
        log.trace("POSTing Topic to Atlas, " + queryString, topic);
        return stringQueryClient.postTopicWithResponse(queryString, topic);
    }
    
}
