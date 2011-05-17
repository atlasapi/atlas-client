package org.atlasapi.client;

import static org.junit.Assert.assertFalse;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.junit.Test;


public class XmlVsJsonSpeedTest {
    private final String urlBase = "http://otter.atlasapi.org/3.0/content.%s?uri=http://www.bbc.co.uk/programmes/b00vsvv5";
    private final GsonQueryClient jsonClient = new GsonQueryClient();
    private final JaxbStringQueryClient xmlClient = new JaxbStringQueryClient();
    
    @Test
    public void jsonTest() {
        String url = String.format(urlBase, "json");
        ContentQueryResult query = jsonClient.contentQuery(url);
        assertFalse(query.getContents().isEmpty());
        
        speedTest(jsonClient, url);
    }
    
    @Test
    public void xmlTest() {
        String url = String.format(urlBase, "xml");
        ContentQueryResult query = xmlClient.contentQuery(url);
        assertFalse(query.getContents().isEmpty());
        
        speedTest(xmlClient, url);
    }
    
    private void speedTest(StringQueryClient client, String url) {
        long currentTimeMillis = System.currentTimeMillis();
        for (int i=0; i<5; i++) {
            client.contentQuery(url);
        }
        long took = System.currentTimeMillis() - currentTimeMillis;
        System.out.println("Test took "+took+" millis");
    }
}
