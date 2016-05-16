package org.atlasapi.client;

import com.google.common.base.Optional;
import com.google.common.net.HostSpecifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GsonContentGroupClientTest {

    @Mock private GsonQueryClient client;

    private String id;
    private String contentGroupId;
    private HostSpecifier host;
    private Optional<String> defaultApiKey;
    private Optional<String> passedApiKey;
    private GsonContentGroupClient contentGroupClient;

    @Before
    public void setUp() throws Exception {
        this.id = "7es7";
        this.contentGroupId = "hafafa2";

        this.host = HostSpecifier.fromValid("atlas.metabroadcast.com");
        this.defaultApiKey = Optional.of("adu98asf91hurffh9afga9sf7as7fasfasf");
        this.passedApiKey = Optional.of("oadihf9dsvh9ads93rn23ofvd0vaaasdwdasd");
        this.contentGroupClient = new GsonContentGroupClient(host, defaultApiKey, client);
    }

    @Test
    public void gettingContentGroupQueryResultById() {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        contentGroupClient.contentGroup(id);

        verify(client).contentGroupQuery(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue(), "http://atlas.metabroadcast.com/3.0/content_groups/7es7.json?apiKey="
                + defaultApiKey.get());
    }

    @Test
    public void gettingContentGroupQueryResultByIdAndApiKey() {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        contentGroupClient.contentGroup(id, passedApiKey);

        verify(client).contentGroupQuery(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue(), "http://atlas.metabroadcast.com/3.0/content_groups/7es7.json?apiKey="
                + passedApiKey.get());
    }

    @Test
    public void gettingContentGroups() {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        contentGroupClient.contentGroups();

        verify(client).contentGroupQuery(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue(), "http://atlas.metabroadcast.com/3.0/content_groups.json?apiKey="
                + defaultApiKey.get());
    }

    @Test
    public void gettingContentGroupsForGivenApiKey() {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        contentGroupClient.contentGroups(passedApiKey);

        verify(client).contentGroupQuery(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue(), "http://atlas.metabroadcast.com/3.0/content_groups.json?apiKey="
                + passedApiKey.get());
    }

    @Test
    public void gettingContentGroupForGivenContentGroupId() {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        contentGroupClient.contentFor(contentGroupId, Optional.<ContentQuery>absent());

        verify(client).contentQuery(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue(), "http://atlas.metabroadcast.com/3.0/content_groups/hafafa2/content.json?apiKey="
                + defaultApiKey.get());
    }

    @Test
    public void gettingContentGroupForGivenContentGroupIdAndApiKey() {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        contentGroupClient.contentFor(contentGroupId, Optional.<ContentQuery>absent(), passedApiKey);

        verify(client).contentQuery(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue(), "http://atlas.metabroadcast.com/3.0/content_groups/hafafa2/content.json?apiKey="
                + passedApiKey.get());
    }
}