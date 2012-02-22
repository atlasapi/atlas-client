package org.atlasapi.client;

import static com.google.common.base.Functions.toStringFunction;
import static com.metabroadcast.common.text.MoreStrings.TO_LOWER;

import java.util.List;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Topic;
import org.atlasapi.output.Annotation;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.net.HostSpecifier;

public class GsonTopicClient implements AtlasTopicClient {

    private final String topicPattern;
    private final String topicContentPattern;
    private final GsonQueryClient stringQueryClient;

    private final Joiner joiner = Joiner.on(',');

    public GsonTopicClient(HostSpecifier atlasHost) {
        this.topicPattern = String.format("http://%s/3.0/topics/%%s.json", atlasHost);
        this.topicContentPattern = String.format("http://%s/3.0/topics/%%s/content.json?annotations=%%s", atlasHost);
        this.stringQueryClient = new GsonQueryClient();
    }

    @Override
    public Topic topic(String topicId) {
        return Iterables.getOnlyElement(stringQueryClient.topicQuery(String.format(topicPattern, topicId)).getContents());
    }

    @Override
    public ContentQueryResult contentFor(String topicId, Annotation... annotations) {
        List<String> annotationStrings = Lists.transform(ImmutableList.copyOf(annotations), Functions.compose(TO_LOWER, toStringFunction()));
        String queryString = String.format(topicContentPattern, topicId, joiner.join(annotationStrings));
        return stringQueryClient.contentQuery(queryString);
    }
}
