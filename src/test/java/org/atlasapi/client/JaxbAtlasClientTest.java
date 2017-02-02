package org.atlasapi.client;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Playlist;

import com.google.common.collect.Iterables;
import org.junit.Test;

import static org.atlasapi.output.Annotation.AVAILABLE_LOCATIONS;
import static org.atlasapi.output.Annotation.UPCOMING;
import static org.junit.Assert.assertNotNull;

public class JaxbAtlasClientTest {

    @Test
    public void testContentContentQuery() {
        ContentQuery contentQuery = ContentQuery.builder()
                .withUrls("http://pressassociation.com/brands/8306")
                .withAnnotations(UPCOMING, AVAILABLE_LOCATIONS)
                .build();
        
        AtlasClient client = new JaxbAtlasClient(
                "http://atlas.metabroadcast.com/3.0",
                "59be198386c143a7badb3b20a03ca042"
        );
        
        ContentQueryResult content = client.content(contentQuery);
        Playlist result = (Playlist) Iterables.getOnlyElement(content.getContents());
        assertNotNull(result);
    }

}
