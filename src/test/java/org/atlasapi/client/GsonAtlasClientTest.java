package org.atlasapi.client;

import java.io.Serializable;

import org.atlasapi.client.ContentQuery.ContentQueryBuilder;
import org.atlasapi.media.entity.Publisher;
import org.atlasapi.media.entity.simple.Broadcast;
import org.atlasapi.media.entity.simple.ContentIdentifier;
import org.atlasapi.media.entity.simple.ContentIdentifier.EpisodeIdentifier;
import org.atlasapi.media.entity.simple.ContentIdentifier.SeriesIdentifier;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Description;
import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.Location;
import org.atlasapi.media.entity.simple.Playlist;
import org.atlasapi.output.Annotation;

import com.metabroadcast.common.http.SimpleHttpClient;
import com.metabroadcast.common.http.SimpleHttpClientBuilder;
import com.metabroadcast.common.query.Selection;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
public class GsonAtlasClientTest {

    private static final Selection SELECTION = new Selection(0, 5);

    private GsonAtlasClient client ;

    @Before
    public void setUp() throws Exception {
        //This test is based on a live key (MetaBroadcast Office Equiv - d6d).
        //We query the application server so as not to expose the key in the code.
       SimpleHttpClient httpClient = new SimpleHttpClientBuilder().build();

        String json = httpClient.getContentsOf(
                "http://applications-service.production.svc.cluster.local/1/applications/d6d");
        Gson gson = new Gson();
        ApplicationWrapper application = gson.fromJson(json, ApplicationWrapper.class);

        this.client = new GsonAtlasClient("http://atlas.metabroadcast.com/3.0",
                application.getApplication().getApi_key());
    }

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

    /**
     * This class is here because this project does not have access to the Application. We use it
     * to serialize just the api_key from the applicationServer response, because thats the only
     * thing we'll be needing for this test.
     */
    private class ApplicationWrapper implements Serializable {
        public Application application;

        public Application getApplication() {
            return application;
        }

        public void setApplication(Application application) {
            this.application = application;
        }

        private class Application implements Serializable {
            String api_key;

            public String getApi_key() {
                return api_key;
            }

            public void setApi_key(String api_key) {
                this.api_key = api_key;
            }
        }
    }
    }
