package org.atlasapi.client.query;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BroadcastAssertionsTest {

    @Test
    public void createAssertionsProducesExpectedString() throws Exception {
        String assertionString = BroadcastAssertions.builder()
                .withAssertion(
                        "uriA",
                        new DateTime(2016, 1, 1, 0, 0, 0, DateTimeZone.UTC),
                        new DateTime(2016, 1, 1, 12, 0, 0, DateTimeZone.UTC)
                )
                .withAssertion(
                        "uriB",
                        new DateTime(2016, 2, 1, 0, 0, 0, DateTimeZone.UTC),
                        new DateTime(2016, 2, 1, 12, 0, 0, DateTimeZone.UTC)
                )
                .build()
                .toString();

        assertThat(
                assertionString,
                is("\"uriA\"|\"2016-01-01T00:00:00.000Z\"|\"2016-01-01T12:00:00.000Z\","
                        + "\"uriB\"|\"2016-02-01T00:00:00.000Z\"|\"2016-02-01T12:00:00.000Z\"")
        );
    }
}
