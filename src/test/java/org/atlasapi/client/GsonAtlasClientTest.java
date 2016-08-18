package org.atlasapi.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.atlasapi.client.ContentQuery.ContentQueryBuilder;
import org.atlasapi.client.response.ContentResponse;
import org.atlasapi.media.entity.Channel;
import org.atlasapi.media.entity.Publisher;
import org.atlasapi.media.entity.simple.Broadcast;
import org.atlasapi.media.entity.simple.ContentGroup;
import org.atlasapi.media.entity.simple.ContentGroupQueryResult;
import org.atlasapi.media.entity.simple.ContentIdentifier;
import org.atlasapi.media.entity.simple.ContentIdentifier.EpisodeIdentifier;
import org.atlasapi.media.entity.simple.ContentIdentifier.SeriesIdentifier;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Description;
import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.Location;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.Playlist;
import org.atlasapi.media.entity.simple.PublisherDetails;
import org.atlasapi.media.entity.simple.ScheduleChannel;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;
import org.atlasapi.output.Annotation;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.net.HostSpecifier;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.time.DateTimeZones;

public class GsonAtlasClientTest {

    private static final Selection SELECTION = new Selection(0, 5);
    private final GsonAtlasClient client = new GsonAtlasClient("http://atlas.metabroadcast.com/3.0", null);

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
    public void shouldSetChannelUriOnChannelsOnBroadcastsOnItems() {
        ContentQuery query = new ContentQueryBuilder()
            .withAnnotations(Annotation.BROADCASTS)
            .withUrls("http://www.bbc.co.uk/programmes/b038lfjs")
            .build();
        ContentQueryResult content = client.content(query);
        assertFalse(content.getContents().isEmpty());

        Description desc = Iterables.getOnlyElement(content.getContents());
        assertNotNull(desc);

        assertEquals("http://www.bbc.co.uk/programmes/b038lfjs", desc.getUri());

        Item item = (Item) desc;
        assertFalse(item.getBroadcasts().isEmpty());

        for (Broadcast broadcast : item.getBroadcasts()) {
            org.atlasapi.media.entity.simple.Channel channel = broadcast.getChannel();
            assertNotNull(channel);
            assertEquals(broadcast.getBroadcastOn(), channel.getUri());
        }
    }

    
    @Test
    public void shouldDeserializeSeries() {
        ContentQuery query = new ContentQueryBuilder()
                .withAnnotations(Annotation.SERIES, Annotation.SUB_ITEMS)
                .withUrls("http://www.bbc.co.uk/programmes/b018ttws")
                .build();
        
        ContentQueryResult content = client.content(query);
        
        boolean foundSeries = false;
        for (Description desc : content.getContents()) {
            if (desc.getUri().equals("http://www.bbc.co.uk/programmes/b018ttws")) {
                Playlist playlist = (Playlist) desc;
                for (SeriesIdentifier series : playlist.getSeriesList()) {
                    if (series.getUri().equals("http://www.bbc.co.uk/programmes/b00t4pgh")) {
                        foundSeries = true;
                        assertEquals(Integer.valueOf(1), series.getSeriesNumber());
                    }
                }
                ContentIdentifier first = Iterables.getFirst(playlist.getContent(), null);
                assertThat(first, Matchers.instanceOf(EpisodeIdentifier.class));
            }
        }
        assertTrue(foundSeries);
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
        SearchQuery search = SearchQuery.builder()
            .withPublishers(ImmutableSet.of(Publisher.BBC))
            .withQuery("EastEnders")
            .withSelection(SELECTION)
            .withAnnotations(Annotation.DESCRIPTION)
            .build();
        ContentQueryResult content = client.search(search);
        assertFalse(content.getContents().isEmpty());

        for (Description desc : content.getContents()) {
            assertNotNull(desc);
        }
    }

}
