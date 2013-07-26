package org.atlasapi.client;

import static org.atlasapi.output.Annotation.AVAILABLE_LOCATIONS;
import static org.atlasapi.output.Annotation.UPCOMING;
import static org.junit.Assert.*;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.Playlist;
import org.junit.Test;

import com.google.common.collect.Iterables;

import java.util.Arrays;

import org.atlasapi.media.entity.simple.ContentGroup;
import org.atlasapi.media.entity.simple.ContentGroupQueryResult;

public class JaxbAtlasClientTest {

    @Test
    public void testContentContentQuery() {
        ContentQuery contentQuery = ContentQuery.builder().withUrls("http://pressassociation.com/brands/8306").withAnnotations(UPCOMING, AVAILABLE_LOCATIONS).build();
        
        AtlasClient client = new JaxbAtlasClient("http://stage.atlas.metabroadcast.com/3.0", "59be198386c143a7badb3b20a03ca042");
        
        ContentQueryResult content = client.content(contentQuery);
        Playlist result = (Playlist) Iterables.getOnlyElement(content.getContents());
        assertNotNull(result);
    }
    
    @Test
    public void testSingleContentGroupQuery() {
        AtlasClient client = new JaxbAtlasClient("http://stage.atlas.metabroadcast.com/3.0");
        
        ContentGroupQueryResult result = client.contentGroup("cbbn");
        ContentGroup group = (ContentGroup) Iterables.getOnlyElement(result.getContentGroups());
        assertNotNull(group);
    }
    
    @Test
    public void testManyContentGroupsQuery() {
        AtlasClient client = new JaxbAtlasClient("http://stage.atlas.metabroadcast.com/3.0");
        
        ContentGroupQueryResult result = client.contentGroups();
        assertNotNull(result);
        assertTrue(result.getContentGroups().size() > 0);
    }

    @Test
    public void testShouldGetPeople() {

        AtlasClient client = new JaxbAtlasClient("http://atlas.metabroadcast.com/3.0");
        
        String queryUri = "http://www.bbc.co.uk/people/84371";
        PeopleQuery query = PeopleQuery.builder()
                .withUrls(queryUri)
                .build();
        PeopleQueryResult people = client.people(query);
        assertNotNull(people);
        assertEquals(queryUri, Iterables.getOnlyElement(people.getPeople()).getUri());
    }
}
