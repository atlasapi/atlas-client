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
        DateTime dateTime = new DateTime();
        GsonQueryClient.DateTimeDeserializer serializer = new GsonQueryClient.DateTimeDeserializer();
        JsonElement element = serializer.serialize(dateTime, DateTime.class, context);
        assertEquals(dateTime.toString(), element.getAsString());
    }
}
