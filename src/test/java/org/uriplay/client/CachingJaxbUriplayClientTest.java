package org.uriplay.client;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uriplay.media.entity.simple.Description;
import org.uriplay.media.entity.simple.Item;
import org.uriplay.media.entity.simple.Playlist;
import org.uriplay.media.entity.simple.UriplayQueryResult;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@RunWith(JMock.class)
public class CachingJaxbUriplayClientTest {

	private final Mockery context = new JUnit4Mockery();
	private final StringQueryClient uriplay = context.mock(StringQueryClient.class);
	private final CachingJaxbUriplayClient client = new CachingJaxbUriplayClient("uriplay", uriplay);
	
	@Test
	public void testBasicCachingOfIdentifierQueries() throws Exception {
		
		final Description item1 = new Item("1");
		item1.setCurie(":1");
		
		final Description playlist2 = new Playlist();
		playlist2.setUri("2");
		
		final Description item3 = new Item("3");
		
		context.checking(new Expectations() {{
			one(uriplay).query("uriplay/any.xml?uri=1,2"); will(returnValue(result(item1, playlist2)));
		}});
		
		assertThat(client.any(ImmutableList.of("1", "2")), is(ImmutableMap.of("1", item1, "2", playlist2)));
		
		context.checking(new Expectations() {{
			never(uriplay);
		}});
		
		// these next requests should be served from the cache
		assertThat(client.any(ImmutableList.of("1", "2")), is(ImmutableMap.of("1", item1, "2", playlist2)));
		assertThat(client.any(ImmutableList.of("1")), is(ImmutableMap.of("1", item1)));
		assertThat(client.any(ImmutableList.of(":1")), is(ImmutableMap.of("1", item1)));
		assertThat(client.any(ImmutableList.of("2")), is(ImmutableMap.of("2", playlist2)));
		
		// item1 and playlist 2 should be served from the cache, we only need to fetch item 3
		context.checking(new Expectations() {{
			one(uriplay).query("uriplay/any.xml?uri=3"); will(returnValue(result(item3)));
		}});
		
		assertThat(client.any(ImmutableList.of("1", "2", "3")), is(ImmutableMap.of("1", item1, "2", playlist2, "3", item3)));
	}
	
	@Test
	public void testNegativeCaching() throws Exception {
		
		final Description exists = new Item("exists");
		
		context.checking(new Expectations() {{
			one(uriplay).query("uriplay/any.xml?uri=missing,exists"); will(returnValue(result(exists)));
		}});
		
		assertThat(client.any(ImmutableList.of("missing", "exists")), is(ImmutableMap.of("exists", exists)));

		context.checking(new Expectations() {{
			never(uriplay);
		}});
		
		// negative results should be served from the cache
		assertThat(client.any(ImmutableList.of("missing", "exists")), is(ImmutableMap.of("exists", exists)));
		assertThat(client.any(ImmutableList.of("missing")), is(ImmutableMap.<String, Description>of()));
	}

	protected UriplayQueryResult result(Description... content) {
		UriplayQueryResult result = new UriplayQueryResult();
		for (Description description : content) {
			if (description instanceof Playlist) {
				result.addPlaylist((Playlist) description);
			} else {
				result.addItem((Item) description);
			}
		}
		return result;
	}
}
