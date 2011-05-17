package org.atlasapi.client;

import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;

import com.metabroadcast.common.http.HttpStatusCodeException;
import com.metabroadcast.common.http.SimpleHttpClient;
import com.metabroadcast.common.http.SimpleHttpClientBuilder;

class JaxbStringQueryClient implements StringQueryClient {

	private static final String USER_AGENT = "Mozilla/5.0 (compatible; atlas-java-client/1.0; +http://atlasapi.org)";

	private static final int NOT_FOUND = 404;

	private final SimpleHttpClient httpClient = new SimpleHttpClientBuilder().withUserAgent(USER_AGENT).withSocketTimeout(1, TimeUnit.MINUTES).build();
	
	private final JAXBContext context;
	
	public JaxbStringQueryClient() {
		try {
			context = JAXBContext.newInstance(ContentQueryResult.class, ScheduleQueryResult.class, PeopleQueryResult.class);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object queryInternal(String queryUri) {
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return unmarshaller.unmarshal(new StringReader(httpClient.getContentsOf(queryUri)));
		}  catch (HttpStatusCodeException e) {
			if (NOT_FOUND == e.getStatusCode()) {
				return new ContentQueryResult();
			}
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
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
