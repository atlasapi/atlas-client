package org.atlasapi.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.atlasapi.client.query.AtlasQuery;
import org.atlasapi.media.entity.Channel;
import org.atlasapi.media.entity.Publisher;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Description;
import org.atlasapi.media.entity.simple.DiscoverQueryResult;
import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.Location;
import org.atlasapi.media.entity.simple.Playlist;
import org.atlasapi.media.entity.simple.ScheduleChannel;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.time.DateTimeZones;

public class GsonAtlasClientTest {

    private static final Selection SELECTION = new Selection(0, 5);
    private final GsonAtlasClient client = new GsonAtlasClient("http://otter.atlasapi.org/3.0", null);

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
        SearchQuery search = SearchQuery.builder().withPublishers(ImmutableSet.of(Publisher.BBC)).withQuery("The Trip").withSelection(SELECTION).build();
        ContentQueryResult content = client.search(search);
        assertFalse(content.getContents().isEmpty());

        for (Description desc : content.getContents()) {
            assertNotNull(desc);

            assertTrue(desc.getTitle().contains("Trip"));
        }
    }
    
    @Test
    public void shouldRetrieveSchedule() {
        DateTime now = new DateTime(DateTimeZones.UTC);
        ScheduleQuery scheduleQuery = ScheduleQuery.builder().withChannels(Channel.BBC_ONE).withPublishers(ImmutableSet.of(Publisher.BBC)).withOnBetween(new Interval(now, now.plusHours(1))).build();
        ScheduleQueryResult schedule = client.scheduleFor(scheduleQuery);
        
        ScheduleChannel channel = Iterables.getOnlyElement(schedule.getChannels());
        assertEquals(Channel.BBC_ONE.key(), channel.getChannelKey());
        assertFalse(channel.getItems().isEmpty());
        for (Item item: channel.getItems()) {
            assertEquals(Publisher.BBC.key(), item.getPublisher().getKey());
            assertNotNull(item.getTitle());
        }
    }
    
    @Test
    public void shouldDiscover() {
        AtlasQuery query = AtlasQuery.filter().genres().equalTo("comedy").publisher().equalTo(Publisher.BBC).withSelection(SELECTION);
        DiscoverQueryResult result = client.discover(query);
        assertFalse(result.getResults().isEmpty());
        
        for (Description description: result.getResults()) {
            assertEquals(Publisher.BBC.key(), description.getPublisher().getKey());
            assertNotNull(description.getTitle());
            
            boolean found = false;
            for (String genre: description.getGenres()) {
                if (genre.contains("comedy")) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }
}
