package org.atlasapi.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.atlasapi.media.entity.simple.ChannelGroupQueryResult;
import org.atlasapi.media.entity.simple.ChannelQueryResult;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;
import org.atlasapi.media.entity.simple.Topic;
import org.atlasapi.media.entity.simple.TopicQueryResult;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.metabroadcast.common.http.HttpException;
import com.metabroadcast.common.http.HttpResponsePrologue;
import com.metabroadcast.common.http.HttpResponseTransformer;
import com.metabroadcast.common.http.HttpStatusCode;
import com.metabroadcast.common.http.SimpleHttpClient;
import com.metabroadcast.common.http.SimpleHttpClientBuilder;
import com.metabroadcast.common.http.SimpleHttpRequest;
import org.atlasapi.media.entity.simple.ContentGroupQueryResult;

class JaxbStringQueryClient implements StringQueryClient {

    private static final String USER_AGENT = "Mozilla/5.0 (compatible; atlas-java-client/1.0; +http://atlasapi.org)";

    private final JAXBContext context;
    private final SimpleHttpClient httpClient;
    
    
    public JaxbStringQueryClient() {
        try {
            context = JAXBContext.newInstance(ContentQueryResult.class, ContentGroupQueryResult.class, ScheduleQueryResult.class, PeopleQueryResult.class, ChannelQueryResult.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        httpClient = new SimpleHttpClientBuilder()
            .withUserAgent(USER_AGENT)
            .withSocketTimeout(1, TimeUnit.MINUTES)
            .withRequestCompressedResponses()
            .withPoolConnections()
       .build();
    }
    
    private HttpResponseTransformer<Object> JAXB_TRANSFORMER = new HttpResponseTransformer<Object>() {
        @Override
        public Object transform(HttpResponsePrologue response, InputStream in) throws HttpException, IOException {
            int statusCode = response.statusCode();
            if (!HttpStatusCode.OK.is(statusCode)) {
                String body = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
                throw new HttpException(body, response);
            }
            try {
                Unmarshaller unmarshaller = context.createUnmarshaller();
                return unmarshaller.unmarshal(in);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    
    public Object queryInternal(String queryUri) {
        try {
            return httpClient.get(new SimpleHttpRequest<Object>(queryUri, JAXB_TRANSFORMER));
        } catch (Exception e) {
            throw new RuntimeException("Could not load " + queryUri + " from atlas", e);
        }
    }

    @Override
    public ScheduleQueryResult scheduleQuery(String queryUri) {
        return (ScheduleQueryResult) queryInternal(queryUri);
    }

    @Override
    public ContentQueryResult contentQuery(String queryUri) {
        return (ContentQueryResult) queryInternal(queryUri);
    }
    
    @Override
    public ContentGroupQueryResult contentGroupQuery(String queryUri) {
        return (ContentGroupQueryResult) queryInternal(queryUri);
    }

    @Override
    public PeopleQueryResult peopleQuery(String queryUri) {
        return (PeopleQueryResult) queryInternal(queryUri);
    }

    @Override
    public TopicQueryResult topicQuery(String queryUri) {
        return (TopicQueryResult) queryInternal(queryUri);
    }

    @Override
    public ChannelQueryResult channelQuery(String queryUri) {
        return (ChannelQueryResult) queryInternal(queryUri);
    }

    @Override
    public ChannelGroupQueryResult channelGroupQuery(String queryUri) {
        return (ChannelGroupQueryResult) queryInternal(queryUri);
    }

    @Override
    public void postTopic(String queryUri, Topic topic) {
        throw new UnsupportedOperationException("Topic POSTing not currently supported via XML");
    }
}
