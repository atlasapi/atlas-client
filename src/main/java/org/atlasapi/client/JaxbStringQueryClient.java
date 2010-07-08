package org.atlasapi.client;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.atlasapi.media.entity.simple.ContentQueryResult;

import com.metabroadcast.common.http.HttpStatusCodeException;
import com.metabroadcast.common.http.SimpleHttpClient;
import com.metabroadcast.common.http.SimpleHttpClientBuilder;

class JaxbStringQueryClient implements StringQueryClient {

	private static final String USER_AGENT = "Mozilla/5.0 (compatible; atlas-java-client/1.0; +http://atlasapi.org)";

	private static final int NOT_FOUND = 404;

	private final SimpleHttpClient httpClient = new SimpleHttpClientBuilder().withUserAgent(USER_AGENT).build();
	
	private final JAXBContext context;
	
	public JaxbStringQueryClient() {
		try {
			context = JAXBContext.newInstance(ContentQueryResult.class);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public ContentQueryResult query(String queryUri) {
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (ContentQueryResult) unmarshaller.unmarshal(new StringReader(httpClient.getContentsOf(queryUri)));
		}  catch (HttpStatusCodeException e) {
			if (NOT_FOUND == e.getStatusCode()) {
				return new ContentQueryResult();
			}
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
