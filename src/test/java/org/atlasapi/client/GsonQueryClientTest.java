package org.atlasapi.client;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mock;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

public class GsonQueryClientTest {

    @Mock JsonSerializationContext context;

    @Test
    public void serializeDateTime() {
        DateTime dateTime = new DateTime(2016, 3, 9, 00, 00);
        GsonQueryClient.JodaDateTimeSerializer serializer = new GsonQueryClient.JodaDateTimeSerializer();
        JsonElement element = serializer.serialize(dateTime, DateTime.class, context);
        assertEquals("2016-03-09T00:00:00.000Z", element.getAsString());
    }
}
