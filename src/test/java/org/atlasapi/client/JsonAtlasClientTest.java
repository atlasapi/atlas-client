package org.atlasapi.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.atlasapi.media.entity.Publisher;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Description;
import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.Location;
import org.atlasapi.media.entity.simple.Playlist;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.metabroadcast.common.query.Selection;

public class JsonAtlasClientTest {

    private final JsonAtlasClient client = new JsonAtlasClient("http://otter.atlasapi.org/3.0", null);

    @Test
    public void shouldGetEpisode() {
        ContentQueryResult content = client.content(ImmutableSet.of("http://www.bbc.co.uk/programmes/b00yb5kv"));
        assertFalse(content.getContents().isEmpty());

        Description desc = Iterables.getOnlyElement(content.getContents());
        assertNotNull(desc);

        assertEquals("http://www.bbc.co.uk/programmes/b00yb5kv", desc.getUri());
        assertEquals(Publisher.BBC, Publisher.fromKey(desc.getPublisher().getKey()).requireValue());

        Item item = (Item) desc;
        assertFalse(item.getBroadcasts().isEmpty());

        for (Location location : item.getLocations()) {
            assertNotNull(location.getAvailabilityStart());
        }
    }

    @Test
    public void shouldGetPlaylist() {
        ContentQueryResult content = client.content(ImmutableSet.of("http://www.bbc.co.uk/programmes/b00vsvv5"));
        assertFalse(content.getContents().isEmpty());

        boolean found = false;
        for (Description desc : content.getContents()) {
            assertNotNull(desc);

            Publisher publisher = Publisher.fromKey(desc.getPublisher().getKey()).requireValue();
            if (publisher == Publisher.BBC) {

                assertEquals("http://www.bbc.co.uk/programmes/b00vsvv5", desc.getUri());
                assertEquals(Publisher.BBC, publisher);
                assertEquals("The Trip", desc.getTitle());

                Playlist playlist = (Playlist) desc;
                assertFalse(playlist.getContent().isEmpty());

                for (Description description : playlist.getContent()) {
                    Item item = (Item) description;
                    assertFalse(item.getBroadcasts().isEmpty());

                    for (Location location : item.getLocations()) {
                        assertNotNull(location.getAvailabilityStart());
                    }
                }
                found = true;
            }
        }
        assertTrue(found);
    }
    
    @Test
    public void shouldSearch() {
        SearchQuery search = SearchQuery.builder().withPublishers(ImmutableSet.of(Publisher.BBC)).withQuery("The Trip").withSelection(new Selection(0, 10)).build();
        ContentQueryResult content = client.search(search);
        assertFalse(content.getContents().isEmpty());

        boolean found = false;
        for (Description desc : content.getContents()) {
            assertNotNull(desc);

            Publisher publisher = Publisher.fromKey(desc.getPublisher().getKey()).requireValue();
            if (publisher == Publisher.BBC) {

                assertEquals("http://www.bbc.co.uk/programmes/b00vsvv5", desc.getUri());
                assertEquals(Publisher.BBC, publisher);
                assertEquals("The Trip", desc.getTitle());
            }
        }
        assertTrue(found);
    }
}
