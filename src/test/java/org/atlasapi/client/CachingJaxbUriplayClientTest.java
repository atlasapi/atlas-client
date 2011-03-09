package org.atlasapi.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Description;
import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.Playlist;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;

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
			one(atlas).query("atlas/content.xml?uri=1,2"); will(returnValue(result(item1, playlist2)));
		}});
		
		assertThat(client.content(ImmutableList.of("1", "2")).getContents(), hasItems(item1, playlist2));
		
		context.checking(new Expectations() {{
			never(atlas);
		}});
		
		// these next requests should be served from the cache
		assertThat(client.content(ImmutableList.of("1", "2")).getContents(), hasItems(item1, playlist2));
		assertThat(client.content(ImmutableList.of("1")).getContents(), hasItems(item1));
		assertThat(client.content(ImmutableList.of(":1")).getContents(), hasItems(item1));
		assertThat(client.content(ImmutableList.of("2")).getContents(), hasItems(playlist2));
		
		// item1 and playlist 2 should be served from the cache, we only need to fetch item 3
		context.checking(new Expectations() {{
			one(atlas).query("atlas/content.xml?uri=3"); will(returnValue(result(item3)));
		}});
		
		assertThat(client.content(ImmutableList.of("1", "2", "3")).getContents(), hasItems(item1, playlist2, item3));
	}
	
	@Test
	public void testNegativeCaching() throws Exception {
		
		final Description exists = new Item("exists");
		
		context.checking(new Expectations() {{
			one(atlas).query("atlas/content.xml?uri=missing,exists"); will(returnValue(result(exists)));
		}});
		
		assertThat(client.content(ImmutableList.of("missing", "exists")).getContents(), hasItems(exists));

		context.checking(new Expectations() {{
			never(atlas);
		}});
		
		// negative results should be served from the cache
		assertThat(client.content(ImmutableList.of("missing", "exists")).getContents(), hasItems(exists));
		assertEquals(ImmutableList.of(), client.content(ImmutableList.of("missing")).getContents());
	}

	protected ContentQueryResult result(Description... content) {
		ContentQueryResult result = new ContentQueryResult();
		for (Description description : content) {
			result.add(description);
		}
		return result;
	}
}
