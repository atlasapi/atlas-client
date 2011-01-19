package org.atlasapi.client;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import org.atlasapi.client.CachingJaxbAtlasClient;
import org.atlasapi.client.StringQueryClient;
import org.atlasapi.media.entity.simple.Description;
import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.Playlist;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@RunWith(JMock.class)
public class CachingJaxbUriplayClientTest {

	private final Mockery context = new JUnit4Mockery();
	private final StringQueryClient atlas = context.mock(StringQueryClient.class);
	private final CachingJaxbAtlasClient client = new CachingJaxbAtlasClient("atlas", atlas);
	
	@Test
	public void testBasicCachingOfIdentifierQueries() throws Exception {
		
		final Description item1 = new Item("1");
		item1.setCurie(":1");
		
		final Description playlist2 = new Playlist();
		playlist2.setUri("2");
		
		final Description item3 = new Item("3");
		
		context.checking(new Expectations() {{
			one(atlas).query("atlas/any.xml?uri=1,2"); will(returnValue(result(item1, playlist2)));
		}});
		
		assertThat(client.any(ImmutableList.of("1", "2")), is((Map<String, Description>) ImmutableMap.of("1", item1, "2", playlist2)));
		
		context.checking(new Expectations() {{
			never(atlas);
		}});
		
		// these next requests should be served from the cache
		assertThat(client.any(ImmutableList.of("1", "2")), is((Map<String, Description>) ImmutableMap.of("1", item1, "2", playlist2)));
		assertThat(client.any(ImmutableList.of("1")), is((Map<String, Description>) ImmutableMap.of("1", item1)));
		assertThat(client.any(ImmutableList.of(":1")), is((Map<String, Description>) ImmutableMap.of("1", item1)));
		assertThat(client.any(ImmutableList.of("2")), is((Map<String, Description>) ImmutableMap.of("2", playlist2)));
		
		// item1 and playlist 2 should be served from the cache, we only need to fetch item 3
		context.checking(new Expectations() {{
			one(atlas).query("atlas/any.xml?uri=3"); will(returnValue(result(item3)));
		}});
		
		assertThat(client.any(ImmutableList.of("1", "2", "3")), is((Map<String, Description>) ImmutableMap.of("1", item1, "2", playlist2, "3", item3)));
	}
	
	@Test
	public void testNegativeCaching() throws Exception {
		
		final Description exists = new Item("exists");
		
		context.checking(new Expectations() {{
			one(atlas).query("atlas/any.xml?uri=missing,exists"); will(returnValue(result(exists)));
		}});
		
		assertThat(client.any(ImmutableList.of("missing", "exists")), is((Map<String, Description>) ImmutableMap.of("exists", exists)));

		context.checking(new Expectations() {{
			never(atlas);
		}});
		
		// negative results should be served from the cache
		assertThat(client.any(ImmutableList.of("missing", "exists")), is((Map<String, Description>) ImmutableMap.of("exists", exists)));
		assertThat(client.any(ImmutableList.of("missing")), is((Map<String, Description>) ImmutableMap.<String, Description>of()));
	}

	protected ContentQueryResult result(Description... content) {
		ContentQueryResult result = new ContentQueryResult();
		for (Description description : content) {
			result.add(description);
		}
		return result;
	}
}
