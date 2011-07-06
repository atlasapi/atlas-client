package org.atlasapi.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.atlasapi.media.entity.Channel;
import org.atlasapi.media.entity.Publisher;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Description;
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
    private final GsonAtlasClient client = new GsonAtlasClient("http://owl.atlasapi.org/3.0", null);

    @Test
    public void shouldGetEpisode() {
        ContentQueryResult content = client.content(ImmutableSet.of("http://www.bbc.co.uk/programmes/b0074gdy"));
        assertFalse(content.getContents().isEmpty());

        Description desc = Iterables.getOnlyElement(content.getContents());
        assertNotNull(desc);

        assertEquals("http://www.bbc.co.uk/programmes/b0074gdy", desc.getUri());
        assertEquals(Publisher.BBC, Publisher.fromKey(desc.getPublisher().getKey()).requireValue());

        Item item = (Item) desc;
        assertFalse(item.getBroadcasts().isEmpty());

        for (Location location : item.getLocations()) {
            assertNotNull(location.getAvailabilityStart());
        }
    }

    @Test
    public void shouldGetPlaylist() {
        ContentQueryResult content = client.content(ImmutableSet.of("http://www.bbc.co.uk/programmes/b007sh7m"));
        assertFalse(content.getContents().isEmpty());

        boolean found = false;
        for (Description desc : content.getContents()) {
            assertNotNull(desc);

            Publisher publisher = Publisher.fromKey(desc.getPublisher().getKey()).requireValue();
            if ("http://www.bbc.co.uk/programmes/b007sh7m".equals(desc.getUri())) {

                assertEquals(Publisher.BBC, publisher);
                assertEquals("EastEnders Omnibus", desc.getTitle());

                Playlist playlist = (Playlist) desc;
                assertFalse(playlist.getContent().isEmpty());

                assertFalse(playlist.getContent().isEmpty());

                found = true;
            }
        }
        assertTrue(found);
    }
    
    @Test
    public void shouldSearch() {
        SearchQuery search = SearchQuery.builder().withPublishers(ImmutableSet.of(Publisher.BBC)).withQuery("EastEnders").withSelection(SELECTION).build();
        ContentQueryResult content = client.search(search);
        assertFalse(content.getContents().isEmpty());

        for (Description desc : content.getContents()) {
            assertNotNull(desc);

            assertTrue(desc.getTitle().contains("EastEnders"));
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

}
