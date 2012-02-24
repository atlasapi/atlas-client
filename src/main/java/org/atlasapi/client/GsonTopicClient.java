package org.atlasapi.client;

import static com.google.common.base.Functions.toStringFunction;
import static com.metabroadcast.common.text.MoreStrings.TO_LOWER;

import java.util.List;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Topic;
import org.atlasapi.output.Annotation;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.net.HostSpecifier;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.url.Urls;

public class GsonTopicClient implements AtlasTopicClient {

    private final String topicPattern;
    private final String topicContentPattern;
    private final GsonQueryClient stringQueryClient;

    private final Joiner joiner = Joiner.on(',');
    private final Optional<String> apiKey;

    public GsonTopicClient(HostSpecifier atlasHost, Optional<String> apiKey) {
        this.apiKey = apiKey;
        this.topicPattern = String.format("http://%s/3.0/topics/%%s.json", atlasHost);
        this.topicContentPattern = String.format("http://%s/3.0/topics/%%s/content.json", atlasHost);
        this.stringQueryClient = new GsonQueryClient();
    }

    @Override
    public Topic topic(String topicId) {
        String queryString = apiKey.isPresent() ? Urls.appendParameters(String.format(topicPattern, topicId), "apiKey", apiKey.get()) : String.format(topicPattern, topicId);
        return Iterables.getOnlyElement(stringQueryClient.topicQuery(queryString).getContents());
    }

    @Override
    public ContentQueryResult contentFor(String topicId, Optional<Selection> selection, Annotation... annotations) {
        List<String> annotationStrings = Lists.transform(ImmutableList.copyOf(annotations), Functions.compose(TO_LOWER, toStringFunction()));
        String queryString = Urls.appendParameters(String.format(topicContentPattern, topicId), "annotations", joiner.join(annotationStrings));
        queryString = apiKey.isPresent() ? Urls.appendParameters(queryString, "apiKey", apiKey.get()) : queryString;
        queryString = selection.isPresent() ? selection.get().appendToUrl(queryString) : queryString;
        System.out.println(queryString);
        return stringQueryClient.contentQuery(queryString);
    }
    
}
