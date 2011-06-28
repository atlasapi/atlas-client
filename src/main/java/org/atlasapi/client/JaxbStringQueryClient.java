package org.atlasapi.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;

import com.metabroadcast.common.http.HttpException;
import com.metabroadcast.common.http.HttpResponseTransformer;
import com.metabroadcast.common.http.HttpStatusCode;
import com.metabroadcast.common.http.SimpleHttpClient;
import com.metabroadcast.common.http.SimpleHttpClientBuilder;

class JaxbStringQueryClient implements StringQueryClient {

    private static final String USER_AGENT = "Mozilla/5.0 (compatible; atlas-java-client/1.0; +http://atlasapi.org)";

    private final JAXBContext context;
    private final SimpleHttpClient httpClient;
    
    
    public JaxbStringQueryClient() {
        try {
            context = JAXBContext.newInstance(ContentQueryResult.class, ScheduleQueryResult.class, PeopleQueryResult.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        httpClient = new SimpleHttpClientBuilder().withUserAgent(USER_AGENT).withSocketTimeout(1, TimeUnit.MINUTES).withTransformer(new JaxbTransformer()).build();
    }
    
    private class JaxbTransformer implements HttpResponseTransformer<Object> {
        @Override
        public Object transform(HttpResponse response) throws HttpException, IOException {
            int statusCode = response.getStatusLine().getStatusCode();
            if (!HttpStatusCode.OK.is(statusCode)) {
                String body = EntityUtils.toString(response.getEntity());
                throw new HttpException(body, new com.metabroadcast.common.http.HttpResponse(body, statusCode));
            }
            try {
                Unmarshaller unmarshaller = context.createUnmarshaller();
                return unmarshaller.unmarshal(response.getEntity().getContent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public Object queryInternal(String queryUri) {
        try {
            return httpClient.get(queryUri).transform();
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
    public PeopleQueryResult peopleQuery(String queryUri) {
        return (PeopleQueryResult) queryInternal(queryUri);
    }
}
