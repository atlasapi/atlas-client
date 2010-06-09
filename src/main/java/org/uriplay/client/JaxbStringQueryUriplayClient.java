package org.uriplay.client;

import java.io.StringReader;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.uriplay.media.entity.simple.UriplayQueryResult;

import com.metabroadcast.common.http.HttpStatusCodeException;
import com.metabroadcast.common.http.SimpleHttpClient;
import com.metabroadcast.common.http.SimpleHttpClientBuilder;

class JaxbStringQueryUriplayClient implements StringQueryClient {

	private static final String USER_AGENT = "Mozilla/5.0 (compatible; uriplay/2.0; +http://uriplay.org)";

	private final SimpleHttpClient httpClient = new SimpleHttpClientBuilder().withUserAgent(USER_AGENT).build();
	
	private JAXBContext context;
	
	public JaxbStringQueryUriplayClient() {
		try {
			context = JAXBContext.newInstance(UriplayQueryResult.class);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public UriplayQueryResult query(String queryUri) {
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (UriplayQueryResult) unmarshaller.unmarshal(new StringReader(httpClient.get(queryUri)));
		}  catch (HttpStatusCodeException e) {
			if (HttpServletResponse.SC_NOT_FOUND == e.getStatusCode()) {
				return new UriplayQueryResult();
			}
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
