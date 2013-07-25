package org.atlasapi.client;

import static org.junit.Assert.*;

import org.atlasapi.client.SearchQuery.SearchQueryBuilder;
import org.atlasapi.output.Annotation;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.metabroadcast.common.url.QueryStringParameter;
import com.metabroadcast.common.url.QueryStringParameters;

public class SearchQueryTest {
    
    @Test
    public void testAddsSortedAnnotationsToParameters() {
        
        SearchQuery query = queryBuilder("q")
                .withAnnotations(Annotation.AUDIT, Annotation.DESCRIPTION)
                .build();
        hasParam(query, "annotations", "description,audit");
        
    }
    
    private void hasParam(SearchQuery query, String paramKey, String expectedValue) {
        QueryStringParameters actualParams = query.toParams();
        QueryStringParameter expectedParam = new QueryStringParameter(paramKey, expectedValue);
        assertTrue(
            String.format("expected params containing %s=%s but were %s", paramKey, expectedValue, actualParams),
            Iterables.contains(actualParams, expectedParam)
        );
    }
    
    private SearchQueryBuilder queryBuilder(String query) {
        return SearchQuery.builder().withQuery(query);
    }
    
}
