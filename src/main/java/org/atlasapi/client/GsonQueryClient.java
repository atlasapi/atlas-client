package org.atlasapi.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.atlasapi.client.exception.BadResponseException;
import org.atlasapi.media.entity.simple.Broadcast;
import org.atlasapi.media.entity.simple.ChannelGroupQueryResult;
import org.atlasapi.media.entity.simple.ChannelQueryResult;
import org.atlasapi.media.entity.simple.ContentGroupQueryResult;
import org.atlasapi.media.entity.simple.ContentIdentifier;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Description;
import org.atlasapi.media.entity.simple.EventQueryResult;
import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.Person;
import org.atlasapi.media.entity.simple.Playlist;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;
import org.atlasapi.media.entity.simple.Topic;
import org.atlasapi.media.entity.simple.TopicQueryResult;

import com.metabroadcast.common.http.HttpException;
import com.metabroadcast.common.http.HttpResponse;
import com.metabroadcast.common.http.HttpResponsePrologue;
import com.metabroadcast.common.http.HttpResponseTransformer;
import com.metabroadcast.common.http.HttpStatusCodeException;
import com.metabroadcast.common.http.Payload;
import com.metabroadcast.common.http.SimpleHttpClient;
import com.metabroadcast.common.http.SimpleHttpClientBuilder;
import com.metabroadcast.common.http.SimpleHttpRequest;
import com.metabroadcast.common.http.StringPayload;
import com.metabroadcast.common.intl.Countries;
import com.metabroadcast.common.intl.Country;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class GsonQueryClient implements StringQueryClient {

    // We have suspiscions that Gson initialisation is not thread-safe. Making this a 
    // ThreadLocal for now to see if the intermittent, difficult to reproduce, problem
    // goes away. See https://groups.google.com/d/msg/google-gson/sWcCXP_oaIQ/1BGTeRtCW2AJ
    private static final ThreadLocal<Gson> gson = new ThreadLocal<Gson>() {

        @Override
        protected Gson initialValue() {
            return new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .registerTypeAdapter(Long.class, new LongDeserializer())
                    .registerTypeAdapter(Boolean.class, new BooleanDeserializer())
                    .registerTypeAdapter(Description.class, new DescriptionDeserializer())
                    .registerTypeAdapter(ContentIdentifier.class, new ContentIdentifierDeserializer())
                    .registerTypeAdapter(Country.class, new CountryDeserializer())
                    .registerTypeAdapter(DateTime.class, new JodaDateTimeSerializer())
                    .registerTypeAdapterFactory(new BroadcastFondlingTypeAdapterFactory())
                    .create();
        }
    };



    private static final String USER_AGENT = "Mozilla/5.0 (compatible; atlas-java-client/1.0; +http://atlasapi.org)";
    private static final int NOT_FOUND = 404;
    private static final String LOCATION = "Location";
    private final SimpleHttpClient httpClient = new SimpleHttpClientBuilder()
            .withUserAgent(USER_AGENT)
            .withRequestCompressedResponses()
            .withPoolConnections()
            .withSocketTimeout(4, TimeUnit.MINUTES)
            .build();

    @Override
    public ContentQueryResult contentQuery(String queryUri) {
        try {
            return gson.get().fromJson(httpClient.getContentsOf(queryUri), ContentQueryResult.class);
        } catch (HttpStatusCodeException e) {
            if (NOT_FOUND == e.getStatusCode()) {
                return new ContentQueryResult();
            }
            throw new RuntimeException("Problem with content query " + queryUri, e);
        } catch (Exception e) {
            throw new RuntimeException("Problem with content query " + queryUri, e);
        }
    }

    @Override
    public ContentGroupQueryResult contentGroupQuery(String queryUri) {
        try {
            return gson.get().fromJson(httpClient.getContentsOf(queryUri), ContentGroupQueryResult.class);
        } catch (HttpStatusCodeException e) {
            if (NOT_FOUND == e.getStatusCode()) {
                return new ContentGroupQueryResult();
            }
            throw new RuntimeException("Problem with content query " + queryUri, e);
        } catch (Exception e) {
            throw new RuntimeException("Problem with content query " + queryUri, e);
        }
    }

    @Override
    public ScheduleQueryResult scheduleQuery(String queryUri) {
        try {
            return gson.get().fromJson(httpClient.getContentsOf(queryUri), ScheduleQueryResult.class);
        } catch (HttpStatusCodeException e) {
            if (NOT_FOUND == e.getStatusCode()) {
                return new ScheduleQueryResult();
            }
            throw new RuntimeException("Problem with schedule query " + queryUri, e);
        } catch (Exception e) {
            throw new RuntimeException("Problem with schedule query " + queryUri, e);
        }
    }

    @Override
    public PeopleQueryResult peopleQuery(String queryUri) {
        try {
            return gson.get().fromJson(httpClient.getContentsOf(queryUri), PeopleQueryResult.class);
        } catch (HttpStatusCodeException e) {
            if (NOT_FOUND == e.getStatusCode()) {
                return new PeopleQueryResult();
            }
            throw new RuntimeException("Problem with people query " + queryUri, e);
        } catch (Exception e) {
            throw new RuntimeException("Problem with people query " + queryUri, e);
        }
    }

    @Override
    public TopicQueryResult topicQuery(String queryUri) {
        try {
            return httpClient.get(SimpleHttpRequest.httpRequestFrom(queryUri, new HttpResponseTransformer<TopicQueryResult>() {

                @Override
                public TopicQueryResult transform(HttpResponsePrologue prologue, InputStream body) throws HttpException, Exception {
                    return gson.get().fromJson(new InputStreamReader(body, Charsets.UTF_8), TopicQueryResult.class);
                }
            }));
        } catch (Exception e) {
            throw new RuntimeException("Problem with topic query " + queryUri, e);
        }
    }



    @Override public String postItem(String query, Item item) {
        try {
            String json = gson.get().toJson(item);
            Payload httpBody = new StringPayload(json);
            HttpResponse resp = httpClient.post(query, httpBody);
            if (resp.statusCode() >= 400) {
                throw new BadResponseException("Error POSTing item: HTTP " + resp.statusCode() + " received from Atlas");
            }
            Wrapper id = gson.get().fromJson(resp.body(), Wrapper.class);
            return id.getId().getId();
        } catch (HttpException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override public String putItem(String query, Item item) {
        try {
            String json = gson.get().toJson(item);
            Payload httpBody = new StringPayload(json);
            HttpResponse resp = httpClient.put(query, httpBody);
            if (resp.statusCode() >= 400) {
                throw new BadResponseException("Error PUTting item: HTTP " + resp.statusCode() + " received from Atlas");
            }
            Id id = gson.get().fromJson(resp.body(), Id.class);
            return id.getId();
        } catch (HttpException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public TopicResponse postTopicWithResponse(String queryUri, Topic topic) {
        try {
            Payload topicPayload = new StringPayload(gson.get().toJson(topic, Topic.class));
            HttpResponse response = httpClient.post(queryUri, topicPayload);
            if (response.statusCode() >= 400) {
                throw new BadResponseException("Error POSTing topic " + topic.getTitle() + " " + topic.getNamespace() + " " + topic.getValue() + " code: " + response.statusCode() + ", message: " + response.statusLine());
            }
            Id id = gson.get().fromJson(response.body(), Id.class);

            return new TopicResponse(
                    id.getId(),
                    response.header(LOCATION)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    @Override
    /**
     * This method has been deprecated as it doesn't return Topic ID.
     * Use {@link #postTopicWithResponse(String queryUri, Topic topic) postTopicWithResponse}
     * method instead of this method as the other method returns object that contains
     * Topic ID and location.
     */
    public String postTopic(String queryUri, Topic topic) {
        try {
            Payload topicPayload = new StringPayload(gson.get().toJson(topic, Topic.class));
            HttpResponse response = httpClient.post(queryUri, topicPayload);
            if (response.statusCode() >= 400) {
                throw new BadResponseException("Error POSTing topic " + topic.getTitle() + " " + topic.getNamespace() + " " + topic.getValue() + " code: " + response.statusCode() + ", message: " + response.statusLine());
            }
            return response.header(LOCATION);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChannelQueryResult channelQuery(String queryUri) {
        try {
            return httpClient.get(SimpleHttpRequest.httpRequestFrom(queryUri, new HttpResponseTransformer<ChannelQueryResult>() {

                @Override
                public ChannelQueryResult transform(HttpResponsePrologue prologue, InputStream body) throws HttpException, Exception {
                    return gson.get().fromJson(new InputStreamReader(body, Charsets.UTF_8), ChannelQueryResult.class);
                }
            }));
        } catch (Exception e) {
            throw new RuntimeException("Problem with channel query " + queryUri, e);
        }
    }

    @Override
    public ChannelGroupQueryResult channelGroupQuery(String queryUri) {
        try {
            return httpClient.get(SimpleHttpRequest.httpRequestFrom(queryUri, new HttpResponseTransformer<ChannelGroupQueryResult>() {

                @Override
                public ChannelGroupQueryResult transform(HttpResponsePrologue prologue, InputStream body) throws HttpException, Exception {
                    return gson.get().fromJson(new InputStreamReader(body, Charsets.UTF_8), ChannelGroupQueryResult.class);
                }
            }));
        } catch (Exception e) {
            throw new RuntimeException("Problem with channel group query " + queryUri, e);
        }
    }

    @Override
    public EventQueryResult eventQuery(String eventQueryUri) {
        try {
            return httpClient.get(SimpleHttpRequest.httpRequestFrom(eventQueryUri, new HttpResponseTransformer<EventQueryResult>() {
                @Override
                public EventQueryResult transform(HttpResponsePrologue httpResponsePrologue, InputStream inputStream) throws HttpException, Exception {
                    return gson.get().fromJson(new InputStreamReader(inputStream, Charsets.UTF_8), EventQueryResult.class);
                }
            }));
        } catch (Exception e) {
            throw new RuntimeException("Problem with event query" + eventQueryUri, e);
        }
    }

    public void postPerson(String queryString, Person person) {
        try {
            StringPayload data = new StringPayload(gson.get().toJson(person));
            HttpResponse response = httpClient.post(queryString, data);
            if (response.statusCode() >= 400) {
                throw new BadResponseException(String.format("POST %s %s: %s %s", person.getUri(), person.getPublisher(), response.statusCode(), response.statusLine()));
            }
        } catch (HttpException e) {
            throw new RuntimeException(String.format("%s %s %s", queryString, person.getUri(), person.getPublisher()), e);
        }
    }

    public void putPerson(String queryString, Person person) {
        try {
            StringPayload data = new StringPayload(gson.get().toJson(person));
            HttpResponse response = httpClient.put(queryString, data);
            if (response.statusCode() >= 400) {
                throw new BadResponseException(String.format("PUT %s %s: %s %s", person.getUri(), person.getPublisher(), response.statusCode(), response.statusLine()));
            }
        } catch (HttpException e) {
            throw new RuntimeException(String.format("%s %s %s", queryString, person.getUri(), person.getPublisher()), e);
        }
    }

    private static final class BroadcastFondlingTypeAdapterFactory implements TypeAdapterFactory {

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!type.getRawType().equals(Broadcast.class)) {
                return null;
            }
            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            return new TypeAdapter<T>() {

                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                @Override
                public T read(JsonReader in) throws IOException {
                    T read = delegate.read(in);
                    if (read instanceof Broadcast) {
                        Broadcast broadcast = (Broadcast) read;
                        if (broadcast.getChannel() != null && broadcast.getBroadcastOn() != null) {
                            broadcast.getChannel().setUri(broadcast.getBroadcastOn());
                        }
                    }
                    return read;
                }

            };
        }
    }

    public static final class JodaDateTimeSerializer implements JsonDeserializer<DateTime>, JsonSerializer<DateTime> {

        private static final DateTimeFormatter formatter
                = ISODateTimeFormat.dateTimeParser().withZoneUTC();
        private static final DateTimeFormatter printer = ISODateTimeFormat.dateTime();

        @Override
        public DateTime deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            return formatter.parseDateTime(json.getAsString());
        }

        @Override
        public JsonElement serialize(DateTime src, Type typeOfSrc,
                JsonSerializationContext context) {
            return new JsonPrimitive(printer.print(src));
        }
    }

    public static class DateDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {

        private static final DateTimeFormatter fmt = ISODateTimeFormat.dateTimeNoMillis();

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String jsonString = json.getAsJsonPrimitive().getAsString();
            if (Strings.isNullOrEmpty(jsonString)) {
                return null;
            }
            return fmt.parseDateTime(jsonString).toDate();
        }

        @Override public JsonElement serialize(Date date, Type type,
                JsonSerializationContext jsonSerializationContext) {
            DateTime dt = new DateTime(date);
            return new JsonPrimitive(dt.toString(fmt));
        }
    }

    public static class LongDeserializer implements JsonDeserializer<Long> {

        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String jsonString = json.getAsJsonPrimitive().getAsString();
            if (Strings.isNullOrEmpty(jsonString)) {
                return null;
            }
            return json.getAsLong();
        }
    }

    public static class BooleanDeserializer implements JsonDeserializer<Boolean> {

        private Map<String, Boolean> boolMap = ImmutableMap.of(
                "true", Boolean.TRUE,
                "false", Boolean.FALSE,
                "1", Boolean.TRUE,
                "0", Boolean.FALSE);

        @Override
        public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return boolMap.get(json.getAsJsonPrimitive().getAsString());
        }
    }

    public static class DescriptionDeserializer implements JsonDeserializer<Description> {

        @Override
        public Description deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObj = json.getAsJsonObject();
            if (jsonObj.has("locations") || jsonObj.has("broadcasts")) {
                return context.deserialize(json, Item.class);
            } else {
                return context.deserialize(json, Playlist.class);
            }
        }
    }

    public static class ContentIdentifierDeserializer implements JsonDeserializer<ContentIdentifier> {

        @Override
        public ContentIdentifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObj = json.getAsJsonObject();
            String uri = jsonObj.get("uri").getAsString();
            String type = jsonObj.get("type").getAsString();
            JsonElement idElement = jsonObj.get("id");
            String id = idElement != null ? idElement.getAsString() : null;

            if ("series".equals(type)) {
                JsonElement seriesElement = jsonObj.get("seriesNumber");
                Integer seriesNumber = seriesElement != null ? seriesElement.getAsInt() : null;
                return ContentIdentifier.seriesIdentifierFrom(uri, id, seriesNumber);
            } else {
                return ContentIdentifier.identifierFrom(id, uri, type);
            }
        }
    }

    public static class CountryDeserializer implements JsonDeserializer<Country> {

        @Override
        public Country deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Countries.fromCode(json.getAsJsonObject().get("code").getAsString());
        }
    }

    private class Wrapper {

        private final Id id;

        public Wrapper(Id id) {
            this.id = id;
        }

        public Id getId() {
            return id;
        }
    }

    private class Id {

        private final String id;

        public Id(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}